INSERT INTO typ_platby (ID, MD, D, POPIS, SMER) VALUES
  (36, '221000', '261000', 'RB příjem z účtu czela.net', NULL),
  (37, '261000', '221000', 'RB výdaj na účet czela.net', NULL);

CREATE TABLE bankovni_ucet (
  id        TINYINT UNSIGNED PRIMARY KEY AUTO_INCREMENT
  COMMENT 'Interní identifikátor čísla účtu',
  nazev     VARCHAR(50)  NOT NULL UNIQUE
  COMMENT 'Naše označení účtu',
  banka     VARCHAR(100) NOT NULL
  COMMENT 'Název banky',
  predcisli DECIMAL(6)   NULL
  COMMENT 'Předčíslí čísla účtu (pokud existuje)',
  cislo     DECIMAL(10)  NOT NULL
  COMMENT 'Číslo účtu',
  kod_banky DECIMAL(4)   NOT NULL
  COMMENT 'Čtyřmístný kód banky'
)
  COMMENT 'Čísla bankovních účtů czela.net z. s.';

INSERT INTO bankovni_ucet (id, nazev, banka, predcisli, cislo, kod_banky) VALUES
  (1, 'Raiffeisenbank', 'Raiffeisenbank a.s.', NULL, 1222733001, 5500),
  (2, 'Fio', 'Fio banka, a.s.', NULL, 2600392940, 2010);

ALTER TABLE uploadovane_vypisy
  ADD COLUMN banka_id TINYINT REFERENCES bankovni_ucet (id);

UPDATE uploadovane_vypisy
SET banka_id = 1
WHERE banka = 'Raiffeisenbank a.s.';
UPDATE uploadovane_vypisy
SET banka_id = 2
WHERE banka = 'FIO';

ALTER TABLE uploadovane_vypisy
  MODIFY COLUMN banka_id TINYINT NOT NULL REFERENCES bankovni_ucet (id)
  COMMENT 'Číslo účtu czela.net, ke kterému se výpis vztahuje';

ALTER TABLE typ_platby
  ADD COLUMN banka_id TINYINT REFERENCES bankovni_ucet (id);

ALTER TABLE typ_platby
  MODIFY COLUMN banka_id TINYINT NOT NULL
  COMMENT 'Číslo účtu czela.net, ke kterému se typ platby vztahuje';

DELIMITER |
CREATE PROCEDURE kontrola_vypisu(pbanka_id TINYINT UNSIGNED, pdatum_od DATE, pdatum_do DATE)
READS SQL DATA
  COMMENT 'Zkontroluje návaznost bankovních výpisů v zadaném období.'
  BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_zustatek DECIMAL(10, 2);
    DECLARE v_pocatecni_zustatek DECIMAL(10, 2);
    DECLARE v_koncovy_zustatek DECIMAL(10, 2);
    DECLARE v_obdobi_od DATE;
    DECLARE v_cislo_vypisu VARCHAR(25);
    DECLARE msg VARCHAR(1000);
    DECLARE cur CURSOR FOR SELECT
                             cislo_vypisu,
                             uv.obdobi_od,
                             pocatecni_zustatek,
                             koncovy_zustatek
                           FROM uploadovane_vypisy uv
                           WHERE uv.banka_id = pbanka_id AND uv.obdobi_od >= pdatum_od AND uv.obdobi_do <= pdatum_do
                           ORDER BY uv.obdobi_od, uv.cislo_vypisu;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    FETCH cur
    INTO v_cislo_vypisu, v_obdobi_od, v_pocatecni_zustatek, v_koncovy_zustatek;
    SET v_zustatek = v_koncovy_zustatek;

    read_loop: LOOP
      FETCH cur
      INTO v_cislo_vypisu, v_obdobi_od, v_pocatecni_zustatek, v_koncovy_zustatek;
      IF done
      THEN
        LEAVE read_loop;
      END IF;
      IF v_pocatecni_zustatek != v_zustatek
      THEN
        SET msg = CONCAT('Vypis c. ', v_cislo_vypisu, ' z banky ', pbanka_id, ' v obdobi od ', v_obdobi_od, ' nenavazuje na predchozi vypis!');
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = msg;
      END IF;
      SET v_zustatek = v_koncovy_zustatek;
    END LOOP;

    CLOSE cur;
  END;
|

CREATE PROCEDURE urcit_typ_platby(IN p_id INT UNSIGNED, OUT p_typ_platby INT UNSIGNED, OUT p_user_id SMALLINT UNSIGNED)
  BEGIN
    DECLARE v_banka_id TINYINT UNSIGNED;
    DECLARE v_castka DECIMAL(10, 2);
    DECLARE v_vs VARCHAR(10);
    DECLARE v_cislo_protiuctu VARCHAR(25);
    DECLARE v_poznamka VARCHAR(255);
    DECLARE v_zprava VARCHAR(255);
    DECLARE v_user_id SMALLINT UNSIGNED;

    SELECT
      uv.banka_id,
      pv.castka,
      COALESCE(pv.opraveny_vs, pv.vs),
      pv.cislo_protiuctu,
      pv.poznamka,
      pv.zprava
    INTO v_banka_id, v_castka, v_vs, v_cislo_protiuctu, v_poznamka, v_zprava
    FROM parsovane_vypisy pv
      JOIN uploadovane_vypisy uv ON (pv.id_vypisu = uv.id)
    WHERE pv.id = p_id;

    IF v_vs = 0
    THEN SET v_vs = NULL;
    END IF;

    SELECT u.id
    INTO v_user_id
    FROM users u
    WHERE u.vs = v_vs;

    CASE v_banka_id
    -- Raiffeisenbank
      WHEN 1
      THEN
        IF v_user_id IS NOT NULL
        THEN
          IF v_castka >= 0
          THEN
            BEGIN
              SET p_typ_platby = 5; -- členský příspěvek
              SET p_user_id = v_user_id;
            END;
          ELSE
            BEGIN
              SET p_typ_platby = 17; -- vrácení přeplatku
              SET p_user_id = v_user_id;
            END;
          END IF;
        ELSEIF LENGTH(v_vs) = 4
          THEN
            IF v_castka >= 0
            THEN
              SET p_typ_platby = 6; -- členský příspěvek neznámého člena
            ELSE
              SET p_typ_platby = 17; -- vrácení přeplatku
            END IF;
        ELSEIF v_cislo_protiuctu = '2600392940/2010'
          THEN
            IF v_castka >= 0
            THEN
              SET p_typ_platby = 36; -- příjem z jiného účtu czela.net
            ELSE
              SET p_typ_platby = 37; -- výdej na jiný účet czela.net
            END IF;
        ELSEIF v_poznamka LIKE 'Úrok%'
          THEN
            SET p_typ_platby = 7; -- úrok z BÚ
        ELSEIF v_poznamka = 'Připsání úroku TV'
          THEN
            SET p_typ_platby = 16; -- úroky z revolvingu
        ELSEIF v_poznamka LIKE 'Automat. výběr z IRTV%'
          THEN
            IF v_castka >= 0
            THEN
              SET p_typ_platby = 12; -- příjem z revolvingu na BÚ
            ELSE
              SET p_typ_platby = 15; -- výdej z revolvingu na BÚ
            END IF;
        ELSEIF v_poznamka LIKE 'Automat. vklad na IRTV%'
          THEN
            IF v_castka >= 0
            THEN
              SET p_typ_platby = 14; -- příjem na revolving z BÚ
            ELSE
              SET p_typ_platby = 13; -- výdej na revolving z BÚ
            END IF;
        ELSEIF v_zprava LIKE '%Poplatek za generování výpisů%'
          THEN
            SET p_typ_platby = 1; -- bankovní poplatek
        ELSE
          IF v_castka >= 0
          THEN
            SET p_typ_platby = 10; -- nejasný příjem
          ELSE
            SET p_typ_platby = 2; -- platba faktury
          END IF;
        END IF;
    -- Fio
      WHEN 2
      THEN
        IF v_user_id IS NOT NULL
        THEN
          IF v_castka >= 0
          THEN
            BEGIN
              SET p_typ_platby = 22; -- členský příspěvek
              SET p_user_id = v_user_id;
            END;
          ELSE
            BEGIN
              SET p_typ_platby = 34; -- vrácení přeplatku
              SET p_user_id = v_user_id;
            END;
          END IF;
        ELSEIF LENGTH(v_vs) = 4
          THEN
            IF v_castka >= 0
            THEN
              SET p_typ_platby = 23; -- členský příspěvek neznámého člena
            ELSE
              SET p_typ_platby = 34; -- vrácení přeplatku
            END IF;
        ELSEIF v_cislo_protiuctu = '1222733001/5500'
          THEN
            IF v_castka >= 0
            THEN
              SET p_typ_platby = 29; -- příjem z jiného účtu czela.net
            ELSE
              SET p_typ_platby = 30; -- výdej na jiný účet czela.net
            END IF;
        ELSE
          IF v_castka >= 0
          THEN
            SET p_typ_platby = 27; -- nejasný příjem
          ELSE
            SET p_typ_platby = 19; -- platba faktury
          END IF;
        END IF;
    END CASE;
  END;
|

CREATE PROCEDURE zapsat_do_deniku(IN p_id INT UNSIGNED)
  BEGIN
    DECLARE v_datum DATE;
    DECLARE v_typ_platby INT UNSIGNED;
    DECLARE v_cislo_vypisu INT UNSIGNED;
    DECLARE v_castka DECIMAL(10, 2);
    DECLARE v_poplatek DECIMAL(10, 2);
    DECLARE v_vs VARCHAR(10);
    DECLARE v_user_id SMALLINT UNSIGNED;
    DECLARE v_nazev_protiuctu VARCHAR(20);
    DECLARE v_cislo_protiuctu VARCHAR(25);
    DECLARE v_poznamka VARCHAR(255);
    DECLARE v_zprava VARCHAR(255);
    DECLARE v_porad_cislo BIGINT UNSIGNED;
    DECLARE v_cislo2 BIGINT UNSIGNED;
    DECLARE v_banka_id TINYINT UNSIGNED;
    DECLARE v_md VARCHAR(20);
    DECLARE v_d VARCHAR(20);
    DECLARE v_popis_platby VARCHAR(100);
    DECLARE v_doklad VARCHAR(15);
    DECLARE v_uzivatel VARCHAR(101);
    DECLARE v_obsah VARCHAR(100);
    DECLARE v_pozn VARCHAR(100);

    SELECT
      pv.datum,
      pv.porad_cislo,
      pv.typ_platby,
      pv.castka,
      pv.poplatek,
      COALESCE(pv.opraveny_vs, pv.vs) AS vs,
      pv.user_ID,
      pv.nazev_protiuctu,
      pv.cislo_protiuctu,
      pv.poznamka,
      pv.zprava,
      uv.cislo_vypisu,
      uv.banka_id
    INTO v_datum, v_porad_cislo, v_typ_platby, v_castka, v_poplatek, v_vs, v_user_id, v_nazev_protiuctu, v_cislo_protiuctu, v_poznamka, v_zprava, v_cislo_vypisu, v_banka_id
    FROM parsovane_vypisy pv
      JOIN uploadovane_vypisy uv ON (pv.ID_VYPISU = uv.id)
    WHERE pv.id = p_id;

    SELECT
      tp.md,
      tp.d,
      tp.popis
    INTO v_md, v_d, v_popis_platby
    FROM typ_platby tp
    WHERE tp.id = v_typ_platby;

    IF v_user_id IS NOT NULL
    THEN
      SELECT CONCAT(prijmeni, ' ', jmeno)
      INTO v_uzivatel
      FROM users
      WHERE id = v_user_id;
    END IF;

    IF v_banka_id = 2 /*Fio*/
    THEN
      SET v_cislo2 = v_porad_cislo;
      SET v_doklad = CONCAT('B', LPAD(MONTH(v_datum), 6, '0'));
    ELSEIF v_banka_id = 1 /*Raiffeisenbank a.s.*/
      THEN
        SET v_doklad = CONCAT('B', YEAR(v_datum) MOD 100, LPAD(v_cislo_vypisu, 4, '0'));
    END IF;

    SET v_obsah = v_popis_platby;

    CASE v_typ_platby
      WHEN 2
      THEN
        SET v_obsah = CONCAT(v_poznamka, ' ', v_zprava);
      WHEN 4
      THEN
        IF v_uzivatel IS NOT NULL
        THEN
          SET v_obsah = CONCAT(v_uzivatel, ' – platba členovi');
        END IF;
      WHEN 5
      THEN
        SET v_obsah = CONCAT(v_uzivatel, ' – platba členského příspěvku');
      WHEN 9
      THEN
        SET v_obsah = CONCAT(v_uzivatel, ' – vratka zálohy');
      WHEN 19
      THEN
        SET v_obsah = CONCAT(v_poznamka, ' ', v_zprava);
      WHEN 22
      THEN
        SET v_obsah = CONCAT(v_uzivatel, ' – platba členského příspěvku');
      WHEN 26
      THEN
        SET v_obsah = CONCAT(v_uzivatel, ' – vratka zálohy');
    ELSE
      SET v_pozn = CONCAT(v_poznamka, ' ', v_zprava, ' ', v_nazev_protiuctu, ' ', v_cislo_protiuctu);
    END CASE;

    IF v_obsah IS NULL
    THEN
      SET v_obsah = CONCAT(v_poznamka, ' ', v_zprava, ' ', v_nazev_protiuctu, ' ', v_cislo_protiuctu);
    END IF;

    IF v_castka != 0
    THEN
      INSERT INTO denik (datum, doklad, cislo2, obsah, MD, D, cena, vs, pozn)
      VALUES (DATE_FORMAT(v_datum, '%d.%m.%Y'), v_doklad, v_cislo2, v_obsah, v_md, v_d, ABS(v_castka), v_vs, v_pozn);
    END IF;
    IF v_poplatek IS NOT NULL
    THEN
      IF v_banka_id = 2 /* Fio */
      THEN
        SET v_d = '223000';
      ELSEIF v_banka_id = 1 /* Raiffeisenbank a.s. */
        THEN
          SET v_d = '221000';
      END IF;
      INSERT INTO denik (datum, doklad, cislo2, obsah, MD, D, cena, vs, pozn)
      VALUES (DATE_FORMAT(v_datum, '%d.%m.%Y'), v_doklad, v_cislo2, 'Bankovní poplatek', '568000', v_d, ABS(v_poplatek), v_vs, v_pozn);
    END IF;
    UPDATE parsovane_vypisy
    SET zauctovano = 1
    WHERE id = p_id;
  END;
|

CREATE PROCEDURE urcit_typy_plateb()
  BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_id INT UNSIGNED;
    DECLARE v_typ_platby INT UNSIGNED;
    DECLARE v_user_id SMALLINT UNSIGNED;
    DECLARE cur CURSOR FOR
      SELECT pv.id
      FROM parsovane_vypisy pv
      WHERE pv.typ_platby IS NULL;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
      FETCH cur
      INTO v_id;
      IF done
      THEN
        LEAVE read_loop;
      END IF;
      CALL urcit_typ_platby(v_id, v_typ_platby, v_user_id);
      IF v_typ_platby IS NOT NULL
      THEN
        UPDATE parsovane_vypisy
        SET typ_platby = v_typ_platby, user_ID = v_user_id
        WHERE id = v_id;
        CALL zapsat_do_deniku(v_id);
      END IF;
    END LOOP;
  END;
|
DELIMITER ;

GRANT EXECUTE ON PROCEDURE netadmin.kontrola_vypisu  TO 'accounting'@'localhost';
GRANT EXECUTE ON PROCEDURE netadmin.kontrola_vypisu  TO 'accounting'@'10.93.%';
GRANT EXECUTE ON PROCEDURE netadmin.urcit_typ_platby  TO 'accounting'@'localhost';
GRANT EXECUTE ON PROCEDURE netadmin.urcit_typ_platby  TO 'accounting'@'10.93.%';
GRANT EXECUTE ON PROCEDURE netadmin.zapsat_do_deniku  TO 'accounting'@'localhost';
GRANT EXECUTE ON PROCEDURE netadmin.zapsat_do_deniku  TO 'accounting'@'10.93.%';
GRANT EXECUTE ON PROCEDURE netadmin.urcit_typy_plateb  TO 'accounting'@'localhost';
GRANT EXECUTE ON PROCEDURE netadmin.urcit_typy_plateb  TO 'accounting'@'10.93.%';
/*
CALL urcit_typ_platby(57513, @typ_platby, @user_id);
SELECT @typ_platby, pv.typ_platby, pv.typ_platby = @typ_platby, @user_id, pv.user_id, @user_id = pv.user_id FROM parsovane_vypisy pv WHERE id = 57513;
*/
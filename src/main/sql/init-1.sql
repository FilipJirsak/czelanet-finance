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

DELIMITER |
CREATE PROCEDURE kontrola_vypisu(pbanka VARCHAR(20), pdatum_od DATE, pdatum_do DATE)
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
		                       WHERE uv.banka = pbanka AND uv.obdobi_od >= pdatum_od AND uv.obdobi_do <= pdatum_do
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
				SET msg = CONCAT('Vypis c. ', v_cislo_vypisu, ' z banky ', pbanka, ' v obdobi od ', v_obdobi_od, ' nenavazuje na predchozi vypis!');
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
		DECLARE v_banka VARCHAR(20);
		DECLARE v_castka DECIMAL(10, 2);
		DECLARE v_vs VARCHAR(10);
		DECLARE v_cislo_protiuctu VARCHAR(25);
		DECLARE v_poznamka VARCHAR(255);
		DECLARE v_zprava VARCHAR(255);
		DECLARE v_user_id SMALLINT UNSIGNED;

		SELECT
			uv.banka,
			pv.castka,
			COALESCE(pv.opraveny_vs, pv.vs),
			pv.cislo_protiuctu,
			pv.poznamka,
			pv.zprava
		INTO v_banka, v_castka, v_vs, v_cislo_protiuctu, v_poznamka, v_zprava
		FROM parsovane_vypisy pv
			JOIN uploadovane_vypisy uv ON (pv.id_vypisu = uv.id)
		WHERE pv.id = p_id;

		IF v_vs = 0 THEN SET v_vs = NULL;

		SELECT u.id
		INTO v_user_id
		FROM users u
		WHERE u.vs = v_vs;

		CASE v_banka
			WHEN 'Raiffeisenbank a.s.'
			THEN -- Raiffeisenbank
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
			WHEN 'FIO'
			THEN -- Fio
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
DELIMITER ;

/*
CALL urcit_typ_platby(57513, @typ_platby, @user_id);
SELECT @typ_platby, pv.typ_platby, pv.typ_platby = @typ_platby, @user_id, pv.user_id, @user_id = pv.user_id FROM parsovane_vypisy pv WHERE id = 57513;
*/
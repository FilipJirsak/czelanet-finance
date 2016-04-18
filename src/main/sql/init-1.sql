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
  MODIFY COLUMN banka_id TINYINT NOT NULL REFERENCES bankovni_ucet (id) COMMENT 'Číslo účtu czela.net, ke kterému se výpis vztahuje';

delimiter |
CREATE TRIGGER parsovane_vypisy_before_insert BEFORE INSERT ON parsovane_vypisy FOR EACH ROW
	BEGIN
		DECLARE banka_id TINYINT;
		DECLARE user_id INT UNSIGNED;
		DECLARE vs SMALLINT UNSIGNED;
		SELECT banka_id INTO banka_id FROM uploadovane_vypisy WHERE uploadovane_vypisy.id = NEW.id_vypisu;

		IF NEW.typ_platby IS NOT NULL THEN
			LEAVE;
		END IF;


		SET vs = COALESCE(NEW.opraveny_VS, NEW.VS);
		SELECT id INTO user_id FROM users WHERE users.vs = vs;

		CASE banka_id
			WHEN 1 THEN -- Raiffeisenbank
				IF user_id IS NOT NULL THEN
					IF castka >= 0 THEN
						BEGIN
							SET NEW.typ_platby = 5; -- členský příspěvek
							SET NEW.user_id = user_id;
						END;
					ELSE
						BEGIN
							SET NEW.typ_platby = 17; -- vrácení přeplatku
							SET NEW.user_id = user_id;
						END;
					END IF;
				ELSEIF LENGTH(vs) = 4 THEN
					IF castka >= 0 THEN
						SET NEW.typ_platby = 6; -- členský příspěvek neznámého člena
					ELSE
						SET NEW.typ_platby = 17; -- vrácení přeplatku
					END IF;
				ELSEIF NEW.cislo_protiuctu = '2600392940/2010' THEN
					IF castka >= 0 THEN
						SET NEW.typ_platby = 36; -- příjem z jiného účtu czela.net
					ELSE
						SET NEW.typ_platby = 37; -- výdej na jiný účet czela.net
					END IF;
				ELSEIF NEW.poznamka LIKE 'Úrok%' THEN
					SET NEW.typ_platby = 7; -- úrok z BÚ
				ELSEIF NEW.poznamka = 'Připsání úroku TV' THEN
					SET NEW.typ_platby = 16; -- úroky z revolvingu
				ELSEIF NEW.poznamka LIKE 'Automat. výběr z IRTV%' THEN
					IF castka >= 0 THEN
						SET NEW.typ_platby = 12; -- příjem z revolvingu na BÚ
					ELSE
						SET NEW.typ_platby = 15; -- výdej z revolvingu na BÚ
					END IF;
				ELSEIF NEW.poznamka LIKE 'Automat. vklad na IRTV%' THEN
					IF castka >= 0 THEN
						SET NEW.typ_platby = 14; -- příjem na revolving z BÚ
					ELSE
						SET NEW.typ_platby = 13; -- výdej na revolving z BÚ
					END IF;
				ELSEIF NEW.zprava LIKE '%Poplatek za generování výpisů%' THEN
					SET NEW.typ_platby = 1; -- bankovní poplatek
				ELSE
					IF castka >= 0 THEN
						SET NEW.typ_platby = 10; -- nejasný příjem
					ELSE
						SET NEW.typ_platby = 2; -- platba faktury
					END IF;
				END IF;
			WHEN 2 THEN -- Fio
			IF user_id IS NOT NULL THEN
				IF castka >= 0 THEN
					BEGIN
						SET NEW.typ_platby = 22; -- členský příspěvek
						SET NEW.user_id = user_id;
					END;
				ELSE
					BEGIN
						SET NEW.typ_platby = 34; -- vrácení přeplatku
						SET NEW.user_id = user_id;
					END;
				END IF;
			ELSEIF LENGTH(vs) = 4 THEN
				IF castka >= 0 THEN
					SET NEW.typ_platby = 23; -- členský příspěvek neznámého člena
				ELSE
					SET NEW.typ_platby = 34; -- vrácení přeplatku
				END IF;
			ELSEIF NEW.cislo_protiuctu = '1222733001/5500' THEN
				IF castka >= 0 THEN
					SET NEW.typ_platby = 29; -- příjem z jiného účtu czela.net
				ELSE
					SET NEW.typ_platby = 30; -- výdej na jiný účet czela.net
				END IF;
			ELSE
				IF castka >= 0 THEN
					SET NEW.typ_platby = 27; -- nejasný příjem
				ELSE
					SET NEW.typ_platby = 19; -- platba faktury
				END IF;
			END IF;
	END CASE;
	END;|
delimiter ;
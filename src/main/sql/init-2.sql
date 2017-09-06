DELIMITER |

DROP PROCEDURE urcit_typ_platby|

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
						  IF v_cislo_protiuctu IS NULL AND v_vs = 9545
						  THEN
  							SET p_typ_platby = 28; -- platba kartou
						  ELSE
  							SET p_typ_platby = 34; -- vrácení přeplatku
						  END IF;
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

GRANT EXECUTE ON PROCEDURE netadmin.urcit_typ_platby TO 'accounting'@'localhost'|
GRANT EXECUTE ON PROCEDURE netadmin.urcit_typ_platby TO 'accounting'@'10.93.%'|

DELIMITER ;

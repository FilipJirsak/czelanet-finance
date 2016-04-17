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

INSERT INTO bankovni_ucet (id, nazev, banka, predcisli, cislo, kod_banky) VALUES (1, 'Raiffeisenbank', 'Raiffeisenbank a.s.', NULL, 1222733001, 5500);
INSERT INTO bankovni_ucet (id, nazev, banka, predcisli, cislo, kod_banky) VALUES (2, 'Fio', 'Fio banka, a.s.', NULL, 2600392940, 2010);

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

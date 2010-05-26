-- Virtualiosios lenteles
-- Rungtyniu rezultatai 
CREATE VIEW tuba7791.Rezultatai (Namai, Varzovas, Imesta, Praleista)
AS SELECT B.Pavadinimas as Namai, C.Pavadinimas as Sveciai, Imesta, Praleista
FROM tuba7791.Rungtynes as A, tuba7791.Komanda as C, tuba7791.Komanda as B
WHERE B.Nr = A.Komanda and C.Nr = A.Varzovas;

-- Zaideju perziura pagal rezultatyvuma
CREATE VIEW tuba7791.Rezultatyvumai (Vardas, Pavarde, Komanda, Rezultatyvumas)
AS SELECT Vardas, Pavarde, Pavadinimas, DECIMAL(AVG(DECIMAL(Taskai, 5, 2)), 4, 2) Rezultatyvumas
FROM tuba7791.Komanda as K, tuba7791.Zaidejas as Z, tuba7791.Statistika as S
WHERE Z.AK = S.AK and Z.Komanda = K.Nr
GROUP BY Vardas, Pavarde, Pavadinimas;

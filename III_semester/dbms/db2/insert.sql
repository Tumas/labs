-- Testiniai duomenys
-- Lentelei Komanda
INSERT INTO tuba7791.Komanda VALUES (1, 'Broliai Saunuoliai');
INSERT INTO tuba7791.Komanda VALUES (2, 'Cempionai');
INSERT INTO tuba7791.Komanda VALUES (3, 'Antra Religija');

--Lentelei Zaidejas
INSERT INTO tuba7791.Zaidejas VALUES ('38904011123', 4, 'Petras', 'Petraitis', 1.89, 82, 1);
INSERT INTO tuba7791.Zaidejas VALUES ('38112657789', 12, 'Petras', 'Petrauskas', 1.99, 90, 1);
INSERT INTO tuba7791.Zaidejas VALUES ('38908119876', 46, 'Jonas', 'Jonaitis', 2.09, 120, 1);
INSERT INTO tuba7791.Zaidejas VALUES ('38912019912', 65, 'Romanas', 'Jankauskas', 1.84, 80, 1);
INSERT INTO tuba7791.Zaidejas VALUES ('39112306542', 87, 'Povilas', 'Dulke', 1.74, 72, 1);

INSERT INTO tuba7791.Zaidejas VALUES ('38901011111', 7, 'Saulius', 'Mikalauskas', 1.92, 100, 2);
INSERT INTO tuba7791.Zaidejas VALUES ('37706177862', 8, 'Jonas', 'Ziedas', 1.77, 72, 2);
INSERT INTO tuba7791.Zaidejas VALUES ('39010016543', 10, 'Marius', 'Kriptosavicius', 1.79, 84, 2);
INSERT INTO tuba7791.Zaidejas VALUES ('38807150100', 0, 'Petras', 'Riedulys', 1.84, 85, 2);
INSERT INTO tuba7791.Zaidejas VALUES ('38004281192', 11, 'Tadas', 'Rimkauskas', 1.94, 90, 2);

INSERT INTO tuba7791.Zaidejas VALUES ('38712041120', 91, 'Tomas', 'Paukste', 1.96, 99, 3);
INSERT INTO tuba7791.Zaidejas VALUES ('38906066623', 19, 'Mindaugas', 'Milosevicius', 2.04, 101, 3);
INSERT INTO tuba7791.Zaidejas VALUES ('38903031234', 66, 'Maksim', 'Dulko', 1.94, 87, 3);
INSERT INTO tuba7791.Zaidejas VALUES ('38910100120', 6, 'Povilas', 'Jarelavicius', 1.84, 72, 3);
INSERT INTO tuba7791.Zaidejas VALUES ('38904179828', 4, 'Dovydas', 'Smigelskas', 1.82, 77, 3);

--Lentelei Rungtynes
INSERT INTO tuba7791.Rungtynes VALUES (1, 1, 2, 87, 99, '2009-11-01');
INSERT INTO tuba7791.Rungtynes VALUES (2, 3, 2, 67, 102, '2009-11-02');
INSERT INTO tuba7791.Rungtynes VALUES (3, 3, 1, 76, 52, '2009-11-03');
INSERT INTO tuba7791.Rungtynes VALUES (4, 2, 3, 87, 88, '2009-11-04');
INSERT INTO tuba7791.Rungtynes VALUES (5, 2, 1, 98, 64, '2009-11-05');
INSERT INTO tuba7791.Rungtynes VALUES (6, 1, 3, 73, 71, '2009-11-06');

-- Lentelei Statistika
-- AK Rungtynes AKM RP prazangos Kl BL PK TSK
-- 3 komandos zaidejai
INSERT INTO tuba7791.Statistika VALUES('38712041120', 6, 11,  3, 4, 7, 1, 1, 30);
INSERT INTO tuba7791.Statistika VALUES('38906066623', 6,  3,  0, 1, 1, 0, 0, 15);
INSERT INTO tuba7791.Statistika VALUES('38903031234', 6,  6,  1, 3, 0, 0, 1, 20);
INSERT INTO tuba7791.Statistika VALUES('38910100120', 6,  4,  4, 2, 1, 0, 3,  5);
INSERT INTO tuba7791.Statistika VALUES('38904179828', 6,  1,  6, 1, 0, 2, 0,  3);

INSERT INTO tuba7791.Statistika VALUES('38712041120', 4, 10,  3, 0, 0, 1, 0, 16);
INSERT INTO tuba7791.Statistika VALUES('38906066623', 4,  1,  3, 1, 1, 0, 4, 15);
INSERT INTO tuba7791.Statistika VALUES('38903031234', 4, 16,  1, 1, 4, 2, 1, 20);
INSERT INTO tuba7791.Statistika VALUES('38910100120', 4,  5,  2, 0, 1, 1, 2, 15);
INSERT INTO tuba7791.Statistika VALUES('38904179828', 4, 11,  0, 1, 0, 2, 0, 21);

INSERT INTO tuba7791.Statistika VALUES('38712041120', 3,  1,  3, 0, 2, 1, 0, 15);
INSERT INTO tuba7791.Statistika VALUES('38906066623', 3,  2,  3, 2, 1, 0, 2, 15);
INSERT INTO tuba7791.Statistika VALUES('38903031234', 3, 11,  1, 1, 8, 1, 1, 10);
INSERT INTO tuba7791.Statistika VALUES('38910100120', 3,  4,  2, 0, 1, 0, 3, 15);
INSERT INTO tuba7791.Statistika VALUES('38904179828', 3, 10,  0, 1, 0, 2, 0, 21);

INSERT INTO tuba7791.Statistika VALUES('38712041120', 2,  0,  3, 1, 4, 0, 1, 20);
INSERT INTO tuba7791.Statistika VALUES('38906066623', 2,  0,  9, 2, 1, 0, 4, 20);
INSERT INTO tuba7791.Statistika VALUES('38903031234', 2, 10,  0, 1, 7, 3, 1, 10);
INSERT INTO tuba7791.Statistika VALUES('38910100120', 2,  2,  6, 4, 1, 0, 0, 15);
INSERT INTO tuba7791.Statistika VALUES('38904179828', 2, 10,  1, 1, 3, 1, 0,  9);

-- 2 komandos zaidejai
INSERT INTO tuba7791.Statistika VALUES('38004281192', 5, 10,  9, 1, 1, 0, 1, 19);
INSERT INTO tuba7791.Statistika VALUES('38807150100', 5,  1,  3, 2, 3, 0, 4, 17);
INSERT INTO tuba7791.Statistika VALUES('39010016543', 5,  1,  5, 1, 2, 2, 0, 20);
INSERT INTO tuba7791.Statistika VALUES('37706177862', 5,  1,  4, 0, 0, 0, 0, 20);
INSERT INTO tuba7791.Statistika VALUES('38901011111', 5,  2,  1, 1, 9, 0, 3, 23);

INSERT INTO tuba7791.Statistika VALUES('38004281192', 4, 22, 1, 1, 1, 2, 6, 17);
INSERT INTO tuba7791.Statistika VALUES('38807150100', 4, 1,  3, 4, 3, 0, 2, 17);
INSERT INTO tuba7791.Statistika VALUES('39010016543', 4, 12, 5, 0, 0, 2, 0, 10);
INSERT INTO tuba7791.Statistika VALUES('37706177862', 4, 9,  4, 3, 0, 0, 1, 20);
INSERT INTO tuba7791.Statistika VALUES('38901011111', 4, 2, 11, 1, 4, 0, 2, 23);

INSERT INTO tuba7791.Statistika VALUES('38004281192', 2, 22, 1, 1, 1, 2, 6, 37);
INSERT INTO tuba7791.Statistika VALUES('38807150100', 2, 1,  3, 4, 3, 0, 2, 9);
INSERT INTO tuba7791.Statistika VALUES('39010016543', 2, 12, 5, 0, 0, 2, 0, 0);
INSERT INTO tuba7791.Statistika VALUES('37706177862', 2, 9,  4, 3, 0, 0, 1, 33);
INSERT INTO tuba7791.Statistika VALUES('38901011111', 2, 2, 11, 1, 4, 0, 2, 23);

INSERT INTO tuba7791.Statistika VALUES('38004281192', 1, 2, 1, 3, 2, 7, 0, 33);
INSERT INTO tuba7791.Statistika VALUES('38807150100', 1, 12, 3, 4, 2, 4, 2, 10);
INSERT INTO tuba7791.Statistika VALUES('39010016543', 1, 6, 0, 0, 5, 2, 0, 0);
INSERT INTO tuba7791.Statistika VALUES('37706177862', 1, 2, 7, 3, 4, 0, 1, 33);
INSERT INTO tuba7791.Statistika VALUES('38901011111', 1, 2, 1, 1, 2, 1, 2, 23);
-- 1 Komandos zaidejai
INSERT INTO tuba7791.Statistika VALUES('39112306542', 1, 2, 1, 3, 2, 0, 2, 36);
INSERT INTO tuba7791.Statistika VALUES('39112306542', 3, 2, 4, 3, 7, 2, 0, 6);
INSERT INTO tuba7791.Statistika VALUES('39112306542', 5, 2, 7, 5, 0, 2, 2, 12);
INSERT INTO tuba7791.Statistika VALUES('39112306542', 6, 4, 8, 5, 2, 1, 3, 29);
INSERT INTO tuba7791.Statistika VALUES('38912019912', 1, 12, 1, 3, 2, 0, 2, 7);

INSERT INTO tuba7791.Statistika VALUES('38912019912', 3, 2, 8, 0, 0, 0, 1, 4);
INSERT INTO tuba7791.Statistika VALUES('38912019912', 5, 2, 4, 3, 6, 0, 1, 3);
INSERT INTO tuba7791.Statistika VALUES('38912019912', 6, 7, 10, 2, 2, 0, 5, 7);
INSERT INTO tuba7791.Statistika VALUES('38908119876', 1, 12, 1, 3, 2, 1, 1, 7);
INSERT INTO tuba7791.Statistika VALUES('38908119876', 3, 10, 0, 0, 2, 3, 0, 7);

INSERT INTO tuba7791.Statistika VALUES('38908119876', 5, 5, 3, 1, 2, 1, 3, 18);
INSERT INTO tuba7791.Statistika VALUES('38908119876', 6, 8, 4, 0, 2, 0, 0, 4);
INSERT INTO tuba7791.Statistika VALUES('38904011123', 1, 2, 0, 2, 8, 2, 0, 17);
INSERT INTO tuba7791.Statistika VALUES('38904011123', 3, 10, 2, 0, 0, 0, 4, 12);
INSERT INTO tuba7791.Statistika VALUES('38904011123', 5, 8, 0, 2, 3, 0, 2, 16);

INSERT INTO tuba7791.Statistika VALUES('38904011123', 6, 4, 1, 4, 2, 2, 0, 16);
INSERT INTO tuba7791.Statistika VALUES('38112657789', 1, 2, 4, 2, 0, 0, 0, 20);
INSERT INTO tuba7791.Statistika VALUES('38112657789', 3, 4, 0, 4, 0, 0, 0, 22);
INSERT INTO tuba7791.Statistika VALUES('38112657789', 5, 5, 2, 2, 4, 1, 0, 13);
INSERT INTO tuba7791.Statistika VALUES('38112657789', 6, 2, 0, 0, 2, 1, 0, 17);

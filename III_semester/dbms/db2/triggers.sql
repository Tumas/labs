-- trigerris tikrina ar komandoje nera uzregistruota daugiau nei 12 zaideju
CREATE TRIGGER tuba7791.Zaideju_sk
  NO CASCADE BEFORE INSERT ON tuba7791.Zaidejas
  REFERENCING NEW AS Naujas
  FOR EACH ROW MODE DB2SQL
  WHEN ((SELECT COUNT(*) FROM tuba7791.Zaidejas AS Z
    WHERE Naujas.Komanda = Z.Komanda) >= 12)
  SIGNAL SQLSTATE '88888'('Vienoje komandoje galima registruoti nedaugiau kaip 12 zaideju!');

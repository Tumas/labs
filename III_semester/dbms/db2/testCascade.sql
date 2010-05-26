-- test cascade
select * from tuba7791.Rezultatyvumai order by Rezultatyvumas desc;
delete from tuba7791.Zaidejas where Pavarde = 'Ziedas';
select * from tuba7791.Rezultatyvumai order by Rezultatyvumas desc;

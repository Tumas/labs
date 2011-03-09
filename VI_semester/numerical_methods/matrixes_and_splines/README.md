Task 1

Sudarykite perkelties metodo programą triįstrižainės lygčių sistemos atveju. 
Pateikite savo testinius uždavinius. 
Sulyginkite gautą rezultatą su tiksliu sprendiniu.

Task 2

Sudarykite programą, randančią kubinio splaino koeficientus, kai duota funkcijos reikšmių lentelė.
Tiesinių lygčių sistemos sprendimui panaudokite perkelties metodą.
Išspręskite individualią užduotį.

2.1 

Raskite kubinį splainą, intervale [a, b] aproksimuojantį funkciją y = f (x) (reikšmių lentelės žingsnis [a, b]/10 ).
Šiuo splainu apskaičiuokite funkcijos artinį taške x iš intervalo [a, b].
Nubraižykite kubinio splaino grafiką ir palyginkite su y = f (x) grafiku.

2.2

Raskite kubinį splainą, intervale [a, b] aproksimuojantį funkciją y = f (x), kuri užduota reikšmių lentele.
Šiuo splainu apskaičiuokite funkcijos artinį duotajame taške x. 
Pakartokite uždavinio sprendimą panaudojant specialias komandas vienoje iš matematinių sistemų (MATLAB, MAPLE ir t.t.).
Palyginkite rezultatus.

** Kvadratiniu splainu egzamine nebus, info ten siaip bendro pobudzio. Nelabai naudojami kvadratiniai splainai.

== Octave Code ==
http://www.obihiro.ac.jp/~suzukim/masuda/octave/html3/octave_148.html
x = [0:3] 
y = [0, 0.5, 2, 1.5]
cub = interp(x, y, x, 'cubic')
plot(x, cub, 's')

spline(x, y, pt)

x = [1:5]
y = [0, 0, 0, 209, 0]
spline(x, y, )

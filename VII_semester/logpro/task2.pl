% Tumas Bajoras PS3, 4 kursas
% 1a, 3b 

% Duotas miestus jungiančių kelių tinklas. Keliai vienakrypčiai,
% NESUDARANTYS ciklų. Žinomi atstumai tarp miestų. Tai išreiškiama faktais
%    kelias (Miestas1, Miestas2, Atstumas).

% Apibrėžti predikatą "galima pervažiuoti  iš miesto X i miestą Y,
%       a) kad bendras nuvažiuotas atstumas neviršytų L (duotas) kilometrų;

%kelias(a, b, 10).
%kelias(a, h, 5).
%kelias(b, c, 5).
%kelias(b, f, 3).
%kelias(f, g, 5).
%kelias(b, d, 15).
%kelias(d, e, 4).

% http://flylib.com/books/2/264/1/html/2/images/figu418_1.jpg
kelias(a, b, 16).
kelias(b, c, 5).
kelias(b, d, 6).
kelias(d, e, 4).
kelias(b, f, 11).

go(X, Y, L) :- kelias(X, Y, LS), LS =< L.
go(X, Y, L) :- kelias(X, S, LS), go(S, Y, L-LS).

% 3. Faktais užrašyta logikos algebros konjunkcijos bei neiginio reikšmių lentelė. Apibrėžti predikatus
% b) "dizjunkcija",  -> OR

konjunkcija(true, true, true).
konjunkcija(true, false, false).
konjunkcija(false, true, false).
konjunkcija(false, false, false).

neig(true, false).
neig(false, true).

% q || p = !(!q && !p)
disjunkcija(Q, P, Z) :- neig(Q, NQ), neig(P, NP), konjunkcija(NQ, NP, K), neig(K, Z).


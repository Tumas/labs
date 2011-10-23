% Tumas Bajoras PS3, 4 kursas (UNR = 7791)

% 1.1
%did(S):
% duoto sveikųjų skaičių sąrašo S elementai išdėstyti didėjimo tvarka.
% Pvz.  goal: did([4,18,23,100]).
%       true

did([]).
did([_]).
did([H,H1|T]) :- H =< H1, did([H1|T]).

% 2.8
%dvej_skaic(Sar, Skaic):
% Skaičius Skaic atitinka duotą dvejetainį pavidalą Sar.
% Pvz. goal: dvej_skaic ([1,0,0,1],Skaic).
% Skaic =9.

bin([], _, N, N).
bin([H|T], P, N, S) :- P1 is P+1, N1 is N+(2**P)*H, bin(T, P1, N1, S).
ds(L, S) :- reverse(L, RL), bin(RL, 0, 0, S).

% 3.4
% dubl_trigub(S,R):
% sąrašas R gaunamas iš S, pastarojo teigiamus elementus dubliuojant du kartus,
% o neteigiamus- tris kartus.
% Pvz. goal: dubl_trigub([-3,2,0],R).
% R = [-3,-3,-3,2,2,0,0,0].

dt([], []).
dt([H|T], [H,H|T1])   :- H > 0, dt(T, T1).
dt([H|T], [H,H,H|T1]) :- H =< 0, dt(T, T1).

% 4.7
% keisti (A, K, R):
% Duotas sąrašas A. Duotas sąrašas K nusako keitinį ir
% susideda iš elementų pavidalo k (KeiciamasSimbolis, PakeistasSimbolis).
% R - rezultas, gautas pritaikius sąrašui A keitinį K.
%	Pvz. goal:keisti ([a,c,b], [k(a,x), k(b,y)],R).
%	R= [x,c,y].

lookup(A, [], A). 
lookup(A, [k(A, R)|_], R) :- !.
lookup(A, [_|T], R) :- lookup(A, T, R).

keisti([], _, []).
keisti([H|T], K, [V|R]) :- lookup(H,K,V), keisti(T,K,R).

% Tumas Bajoras PS3, 4 kursas
% 17, 31

% FAKTAI 

asmuo(asta, m, kepti_blynus).
asmuo(ramunas, v, zvejoti).
asmuo(greta, m, zvejoti).
asmuo(tumas, v, zaisti_krepsini).
asmuo(vaidis, v, zaisti_krepsini).
asmuo(milda, m, gauti_dovanu).
asmuo(brigita, m, gauti_dovanu).
asmuo(aldona, m, mokytis).

mama(aldona, ramunas).
mama(aldona, vaidis).
mama(asta, greta).
mama(asta, tomas).
mama(brigita, milda).

pora(ramunas, asta).
pora(vaidis, brigita).

% TAISYKLES

mergina(X) :- asmuo(X, m, _).

megsta(P, X) :- asmuo(X, _, P).

yra_bendru_tevu(X, Y) :- mama(M, Y), mama(M, X).
yra_bendru_tevu(X, Y) :- mama(M, Y), mama(M1, X), pora(T, M), pora(T, M1).

%tevas

% pussesere(X, Y)

pussesere(X, Y) :- mergina(X), mama(MX, X), mama(MY, Y), yra_bendru_tevu(MX, MY).
pussesere(X, Y) :- mergina(X), mama(MX, X), mama(MY, Y), pora(TY, MY), yra_bendru_tevu(MX, TY).
pussesere(X, Y) :- mergina(X), mama(MX, X), mama(MY, Y), pora(TX, MX), yra_bendru_tevu(MY, TX).
pussesere(X, Y) :- mergina(X), mama(MX, X), mama(MY, Y), pora(TX, MX), pora(TY, MY), yra_bendru_tevu(TY, TX).

% paveldejo(X, P) - "X turi tokį patį pomėgį P kaip ir vienas tevų"

paveldejo(X, P) :- mama(M, X), megsta(P, M), megsta(P, X).
paveldejo(X, P) :- mama(M, X), pora(T, M), megsta(P, T), megsta(P, X).

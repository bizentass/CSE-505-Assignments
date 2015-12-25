boy(ali).
boy(bing).
boy(charles).
boy(dani). 
girl(kari).
girl(lola).
girl(mary).
girl(nina).

biking(Boy1, Girl1) :- biking(boy(x),girl(x)).
hiking(Boy2, Girl2) :- hiking(boy(x),girl(x)).
running(Boy3, Girl3) :- running(boy(x),girl(x)).
surfing(Boy4, Girl4) :- surfing(boy(x),girl(x)).

member(X, [X | _]).
member(X, [_ | T]) :- member(X,T).
 
constrainta(Answer) :- member(biking(ali,_), Answer), member(hiking(_,mary), Answer).

constraintb(Answer) :- \+ member(running(bing,_), Answer), \+ member(running(charles,_), Answer), 
					   \+ member(running(_,kari), Answer), \+ member(running(_,lola), Answer).

constraintc(Answer) :- \+ member(surfing(_,nina), Answer).

constraintd(Answer) :- member(biking(charles,lola), Answer); member(hiking(charles,lola), Answer); 
					   member(running(charles,lola), Answer); member(surfing(charles,lola), Answer).

constrainte(Answer) :- \+ member(biking(dani,mary), Answer); \+ member(hiking(dani,mary), Answer); 
					   \+ member(running(dani,mary), Answer); \+ member(surfing(dani,mary), Answer).


assumptions(Answer) :- 	boy(B1),
 						boy(B2),B1 \== B2,
 						boy(B3),B1 \== B3, B2 \== B3,
 						boy(B4),B1 \== B4, B2 \== B4,B3 \== B4,
						girl(G1),
						girl(G2),G1 \== G2,
						girl(G3),G1 \== G3,G2 \== G3,
						girl(G4),G1 \== G4, G2 \== G4,G3 \== G4,
						
						Answer = [ biking(B1, G1), running(B2, G2),
								   hiking(B3, G3), surfing(B4, G4)].

solve(Answer) :- assumptions(Answer), constrainta(Answer),
				 constraintb(Answer), constraintc(Answer), 
				 constraintd(Answer), constrainte(Answer),!.


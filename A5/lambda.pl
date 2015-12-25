% Occurs Free Definitions
occurs_free_in(X, v(X)).    

occurs_free_in(X, l(Y, T)) :-
		X \== Y,
		occurs_free_in(X, T).
	 
occurs_free_in(X, a(T1, T2)) :-
		occurs_free_in(X, T1);
		occurs_free_in(X, T2).

% Beta-redex
% (Lx.T1 T2)
reducible(a(l(_,_), _)).

% Eta-redex
% Lx.(T x)
reducible(l(x, a(T, v(x)))) :- \+ occurs_free_in(x,T).

% Other cases
reducible(l(_,T)) :- reducible(T).
reducible(a(T1, T2)) :- reducible(T1); reducible(T2).
	
% Relationship between norm and reducible is opposite.
norm(T) :- \+ reducible(T).
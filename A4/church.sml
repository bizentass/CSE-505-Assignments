Control.Print.printDepth := 100;
Control.Print.printLength := 1000; 

datatype 'a inf_list = lcons of 'a * (unit -> 'a inf_list)

fun church(n) = let 
					fun thk() = church("(f " ^ n ^ ")")
				in 
					lcons("Lf.Lx.(f " ^ n ^ ")", thk)
				end;


fun take(0, _) = []
	| take(n, lcons(h, thk)) = h :: take(n-1, thk());

take(20,church("x"))

(*take(4,church("xyz"))*)
(*take(3,church("9"))*)


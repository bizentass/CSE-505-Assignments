Control.Print.printDepth := 10;
Control.polyEqWarn := false;

datatype 'a ntree = leaf of 'a | node of 'a ntree list;

fun map(f, [ ]) = []
  | map(f,x::t) = f(x) :: map(f,t);
  
fun reduce(f,b, [ ]) = b 
  |  reduce(f,b, x::t) = f(x, reduce(f,b,t));

fun subst(leaf(x), v1, v2) =  
		    if x = v1 then 
          leaf(v2)
			  else 
          leaf(x)
		    
  | subst(node(n), v1, v2) = 
        let 
            fun d(tr) = subst(tr, v1, v2)
  			 in 
            node(map(d,n))
  			end;
 				
fun toString(leaf(value)) = value
  | toString(node(n)) =
        let 
            fun d(tr,acc_string) =  if acc_string = "" then 
                                      toString(tr)
                                    else 
                                       toString(tr) ^ " " ^ acc_string;
  			 in 
            reduce(d,"",n)
  			end;
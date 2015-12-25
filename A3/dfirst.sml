Control.Print.printDepth := 10;
Control.polyEqWarn := false;

datatype 'a tree = leaf | node of 'a * 'a tree * 'a tree;

fun insert(i, leaf) = node(i,leaf,leaf)
      | insert(i, node(v,left,right)) =  	if i < v then 
 							    node(v,insert(i,left),right) 
							else 
							     if i > v then 
									node(v,left,insert(i,right)) 
								else 
									node(i,left,right);
                                                      

fun testcase() =
            let 
                  val t1 = node(100,leaf,leaf);
                  val t2 = insert(50, t1);
                  val t3 = insert(150, t2);
                  val t4 = insert(200, t3);
                  val t5 = insert(125, t4);
                  val t6 = insert(175, t5);
                  val t7 = insert(250, t6);
                  val t8 = insert(25, t7);
                  val root = insert(75, t8)
            in root
end;

fun dfirst2(tr)  = let  fun df([],acc) =  acc
                        | df(leaf::t, acc) =  df(t, acc)                        				 
                        | df(node(n,leaf,r)::t, acc) =  df([r]@t, acc@[n])
                        | df(node(n,l,r)::t, acc) =  df([l,node(n,leaf,leaf),r]@t, acc)
                   in
                        df([tr], [])
                   end;

fun test_dfirst2() = dfirst2(testcase());








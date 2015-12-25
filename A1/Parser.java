import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @authors szaidi2,lalithvi
 *
 */

public class Parser {
	
	public static HashMap<Character, Integer> hashmap = new HashMap<Character,Integer>();
	public static int index = 1;
	public static ArrayList<Character> identi = new ArrayList<Character>();
	public static int identi_count = 0;
	public static ArrayList<Character> op_rexp = new ArrayList<Character>();
	public static int rexp_count = 0;
	public static int loop_counter = 0;
	public static String[] incre_store = new String[100];
	public static String[] stmt_store = new String[100];
	
	public static void main(String[] args) {
		System.out.println("Enter your program, finish it with 'end'!\n");
		Lexer.lex();
		new Program();
		Code.output();
	}

}

class Program{  // program -> decls stmts end
	
	Decls d;
	Stmts s;
	
	public Program(){
		d = new Decls();
		s =  new Stmts();
		if(Lexer.nextToken == Token.KEY_END){
			Code.gen("return");
		}
	}
}

class Decls{  // decls -> int idlist ';'
	
	Idlist idl;
	int k;
	
	public Decls(){
		if(Lexer.nextToken == Token.KEY_INT){
			idl = new Idlist();
			
		}
	}
}

class Idlist{ // idlist -> id [',' idlist]
	
	Idlist idlist;
	
	public Idlist(){
		Lexer.lex();
		if(Lexer.nextToken == Token.ID){
			Parser.hashmap.put(Lexer.ident, Parser.index);
			Parser.index++;
			Lexer.lex(); //to get the comma operator
			if(Lexer.nextToken == Token.COMMA){
				idlist = new Idlist();
			}
			if(Lexer.nextToken == Token.SEMICOLON) {
				Lexer.lex();
			}
		}
	}
}

class Stmts{  // stmts -> stmt [ stmts ]
	
	Stmts stmts;
	Stmt stmt;
	 
	 public Stmts() {
		 
		 stmt = new Stmt();
		 
		 if(Lexer.nextToken != Token.RIGHT_BRACE && Lexer.nextToken != Token.KEY_END) {			 
			 stmts = new Stmts();
		 }
	}
}


class Stmt{  // stmt -> assign ';' | cmpd | cond | loop
	
	Assign assign;
	Cmpd cmpd;
	Cond cond;
	Loop loop;
	Stmt stmt;
	int k;
	
	public Stmt(){
		
		if(Lexer.nextToken == Token.ID ) {
			Lexer.lex(); // to get the next variable for comparing assigning 
		}
		switch(Lexer.nextToken) {
			case Token.ASSIGN_OP:
				assign = new Assign();
				Lexer.lex();
				if(Lexer.nextToken == Token.SEMICOLON){
					Lexer.lex();
				}
				break;
			case Token.LEFT_BRACE:
				cmpd = new Cmpd();
				break;
			case Token.KEY_IF:
				cond = new Cond();
				break;
			case Token.KEY_FOR:
				loop = new Loop();
				break;
			default:
				break;
		}
	}
}

class Assign{ // assign -> id '=' expr
	Expr expr;
		
	public Assign(){
		
		if(Lexer.nextToken == Token.ID){
			Lexer.lex();
		}
		
		if(Lexer.nextToken == Token.ASSIGN_OP){
			Parser.identi.add((Lexer.ident));
			Lexer.lex();
			expr = new Expr();
			
			if(Parser.hashmap.containsKey(Parser.identi.get(Parser.identi_count))){
				Code.gen(Code.storecode(Parser.hashmap.get(Parser.identi.get(Parser.identi_count))));
				Code.lg_identifier_check(Parser.hashmap.get(Parser.identi.get(Parser.identi_count)));
				Parser.identi_count++;
			}
		}
		
		else if(Lexer.nextToken == Token.ADD_OP) {
			expr = new Expr();
		}
	}
}

class Cmpd{  // cmpd -> '{' stmts '}'
	Stmts  stmts;
	
	public Cmpd(){
		if(Lexer.nextToken == Token.LEFT_BRACE){
			Lexer.lex();
			stmts = new Stmts();
		}
		if(Lexer.nextToken == Token.RIGHT_BRACE){
			Lexer.lex();
		}
	}
	
}


class Cond{  // cond -> if '(' rexp ')' stmt [ else stmt ]
	Rexp rexp;
	Stmt stmt1, stmt2;
	int goto_index; //keeps track of the goto index
	
	public Cond(){
		
		if(Lexer.nextToken == Token.KEY_IF){
			Lexer.lex();
			if(Lexer.nextToken == Token.LEFT_PAREN){
				rexp = new Rexp();
				if(Lexer.nextToken == Token.RIGHT_PAREN){
					Lexer.lex();
					stmt1 = new Stmt();
				}
			}
		}
		if(Lexer.nextToken == Token.KEY_ELSE){
			//System.out.println("goto index is " + goto_index);  // prints 14
			Code.insert_goto();// returns "goto" string 
			goto_index = Code.getCodePtr() - 1;
			//System.out.println("goto index now is " + goto_index);  // prints 15
			Code.double_byte(); //increases the codeptr by 2
			//System.out.println("rexp.if_cmpindex is " + rexp.if_cmpindex); //print 8
			Code.indexOfEnd(rexp.if_cmpindex); //set the goto for 'else' condition
			//Code.code[rexp.if_cmpindex] = Code.code[rexp.if_cmpindex] + " " + Code.getCodePtr();
			Lexer.lex();
			stmt2 = new Stmt();
			Code.indexOfEnd(goto_index);
			//Code.code[goto_index] = Code.code[goto_index] + " " + Code.getCodePtr();
		}
		else {
			 Code.indexOfEnd(rexp.if_cmpindex);	//set the goto for 'if' condition
	    }	
	}
}

class Loop{ // loop -> for '(' [assign] ';' [rexp] ';' [assign] ')' stmt
	Assign assign1, assign2;
	Rexp rexp;
	Stmt stmt;
    //int goto_index;
    int compare_start;
    int i;
	int incre_cnter_bfr;
	int incre_cnter_aftr;
	int stmt_cnter_bfr;
	int stmt_cnter_aftr;
	int incre_stmt_diff;
	
	public Loop(){
		
		//should store increment values and put them after statement
		Parser.loop_counter += 1;
		System.out.println("Loop counter is "+Parser.loop_counter);
		incre_cnter_bfr = 0;
		incre_cnter_aftr = 0;
		stmt_cnter_bfr = 0;
		//String[] incre_store_1 = new String[100];
		//String[] stmt_store_1 = new String[100];
		stmt_cnter_aftr = 0;
		incre_stmt_diff = 0;
		
		//compare_start = Code.getCodePtr();
		if(Lexer.nextToken == Token.KEY_FOR){
			Lexer.lex();
			if(Lexer.nextToken == Token.LEFT_PAREN){
				Lexer.lex();
				if(Lexer.nextToken == Token.ID) {
					assign1 = new Assign();
					Lexer.lex(); //to move past the semicolon that comes with the assignment statement
					if(Lexer.nextToken == Token.ID){
                    	
                    	compare_start = Code.getCodePtr();
                        i = Lexer.ident;
                        if(Parser.hashmap.containsKey(Lexer.ident)){
                            Code.gen(Code.loadcode(Parser.hashmap.get(Lexer.ident)));
                            Code.lg_identifier_check(Parser.hashmap.get(Lexer.ident));
                        }
                        
						rexp = new Rexp();
						Lexer.lex(); //to move past the semicolon that comes with the regular expression
						if(Lexer.nextToken == Token.ID){
							incre_cnter_bfr = Code.getCodePtr();
							System.out.println("incre_cnter_bfr is" +incre_cnter_bfr);
							assign2 = new Assign();
							incre_cnter_aftr = Code.getCodePtr();
							System.out.println("incre_cnter_aftr is" +incre_cnter_aftr);
							Parser.incre_store = Arrays.copyOfRange(Code.code, incre_cnter_bfr, incre_cnter_aftr);
							System.out.println("Incre_Store_Array is "+Arrays.toString(Parser.incre_store));
							if(Lexer.nextToken == Token.RIGHT_PAREN){
								Lexer.lex();
								stmt_cnter_bfr = Code.getCodePtr();
								System.out.println("stmt_cnter_before is" +stmt_cnter_bfr);
								stmt = new Stmt();
								stmt_cnter_aftr = Code.getCodePtr();
								System.out.println("stmt_cnter_aftr is" +stmt_cnter_aftr);
								Parser.stmt_store = Arrays.copyOfRange(Code.code, stmt_cnter_bfr, stmt_cnter_aftr);
								System.out.println("Stmt_Store_Array is "+Arrays.toString(Parser.stmt_store));
								swapIncreStmt(1);
                                Code.gen("goto "+ compare_start);
                                Code.double_byte();
                                Code.indexOfEnd(rexp.if_cmpindex);
							}
						}
						else if(Lexer.nextToken == Token.RIGHT_PAREN){
							Lexer.lex();
							stmt = new Stmt();
						}
					}
					else if(Lexer.nextToken == Token.SEMICOLON){
							Lexer.lex();
							if(Lexer.nextToken == Token.ID) {
								incre_cnter_bfr = Code.getCodePtr();
								assign2 = new Assign();
								incre_cnter_aftr = Code.getCodePtr();
								Parser.incre_store = Arrays.copyOfRange(Code.code, incre_cnter_bfr, incre_cnter_aftr);
								if(Lexer.nextToken == Token.RIGHT_PAREN){
									Lexer.lex();
									stmt_cnter_bfr = Code.getCodePtr();
									stmt = new Stmt();
									stmt_cnter_aftr = Code.getCodePtr();
									Parser.stmt_store = Arrays.copyOfRange(Code.code, stmt_cnter_bfr, stmt_cnter_aftr);
									swapIncreStmt(2);
									//should we add this?
	                                //Code.gen("goto "+ compare_start);
	                                //Code.double_byte();
	                                //Code.indexOfEnd(rexp.if_cmpindex);
								}
							}
							else if(Lexer.nextToken == Token.RIGHT_PAREN){
								Lexer.lex();
								stmt = new Stmt();
							}
					}
				}
				else if(Lexer.nextToken == Token.SEMICOLON){
					Lexer.lex();
					if(Lexer.nextToken == Token.ID){
						compare_start = Code.getCodePtr();
                        i = Lexer.ident;
                        if(Parser.hashmap.containsKey(Lexer.ident)){
                            Code.gen(Code.loadcode(Parser.hashmap.get(Lexer.ident)));
                            Code.lg_identifier_check(Parser.hashmap.get(Lexer.ident));
                        }
						rexp = new Rexp();
                        //compare_start = Code.getCodePtr();
                        //Code.sipushinc();
                        //goto_index = Code.getCodePtr();
						//Code.insert_goto();
                        //Code.indexOfEnd(goto_index);
                        
						Lexer.lex(); //to move past the semicolon that comes with the regular expression
						if(Lexer.nextToken == Token.ID) {
								incre_cnter_bfr = Code.getCodePtr();
								System.out.println("incre_cnter_bfr is" +incre_cnter_bfr);
								assign2 = new Assign();
								incre_cnter_aftr = Code.getCodePtr();
								System.out.println("incre_cnter_aftr is" +incre_cnter_aftr);
								Parser.incre_store = Arrays.copyOfRange(Code.code, incre_cnter_bfr, incre_cnter_aftr);
								if(Lexer.nextToken == Token.RIGHT_PAREN){
									Lexer.lex();
									stmt_cnter_bfr = Code.getCodePtr();
									stmt = new Stmt();
									stmt_cnter_aftr = Code.getCodePtr();
									System.out.println("stmt_cnter_aftr is" +stmt_cnter_aftr);
									Parser.stmt_store = Arrays.copyOfRange(Code.code, stmt_cnter_bfr, stmt_cnter_aftr);
									swapIncreStmt(3);
									//should we add this?
//	                                Code.gen("goto "+ compare_start);
//	                                Code.double_byte();
//	                                Code.indexOfEnd(rexp.if_cmpindex);
								}
						}
						else if(Lexer.nextToken == Token.RIGHT_PAREN) {
							Lexer.lex();
							stmt = new Stmt();
							Code.gen("goto "+ compare_start);
                            Code.double_byte();
                            Code.indexOfEnd(rexp.if_cmpindex);
						}
					}
					else if(Lexer.nextToken == Token.SEMICOLON){
						Lexer.lex();
						if(Lexer.nextToken == Token.ID) {
								incre_cnter_bfr = Code.getCodePtr();
								assign2 = new Assign();
								incre_cnter_aftr = Code.getCodePtr();
								Parser.incre_store = Arrays.copyOfRange(Code.code, incre_cnter_bfr, incre_cnter_aftr);
								if(Lexer.nextToken == Token.RIGHT_PAREN){
									Lexer.lex();
									stmt_cnter_bfr = Code.getCodePtr();
									stmt = new Stmt();
									stmt_cnter_aftr = Code.getCodePtr();
									Parser.stmt_store = Arrays.copyOfRange(Code.code, stmt_cnter_bfr, stmt_cnter_aftr);
									swapIncreStmt(4);
								}
						}
						else if(Lexer.nextToken == Token.RIGHT_PAREN) {
							Lexer.lex();
							stmt = new Stmt();
						}
					}
				}
			}
		}
	}
	
	public void swapIncreStmt(int num) {
		System.out.println("entered "+num+" time");
		System.out.println("Stmt_store length is "+Parser.stmt_store.length);
		System.out.println("Incre_store length is "+Parser.incre_store.length);
		if (Parser.stmt_store.length > Parser.incre_store.length) {
			incre_stmt_diff = Parser.stmt_store.length - Parser.incre_store.length;
			System.out.println("It cmoes here with incre_stmt_diff"+incre_stmt_diff);
			ArrayList<String> incre_list = new ArrayList<String>(Arrays.asList(Parser.incre_store));
			//int incre_last_index = Parser.incre_store.length - 1;
			System.out.println("Incre_store length is "+Parser.incre_store.length);
			for(int i=0; i<incre_stmt_diff; i++){
				//Parser.incre_store[incre_last_index] = "Random";
				incre_list.add(null);
			}
			//String[] incre_store = new String[incre_list.size()];
			System.out.println("Incre_list.size is"+incre_list.size());
			Parser.incre_store = incre_list.toArray(new String[incre_list.size()]);
			System.out.println("Incre_Store_Array is "+Arrays.toString(Parser.incre_store));
		}
		else if (Parser.incre_store.length > Parser.stmt_store.length){
			incre_stmt_diff = Parser.incre_store.length - Parser.stmt_store.length;
			ArrayList<String> stmt_list = new ArrayList<String>(Arrays.asList(Parser.stmt_store));
			for(int i=0; i<incre_stmt_diff; i++){
				stmt_list.add("NULL");
			}
			//String[] stmt_store = new String[stmt_list.size()];
			Parser.stmt_store = stmt_list.toArray(new String[stmt_list.size()]);
			System.out.println("Stmt_Store_Array is "+Arrays.toString(Parser.stmt_store));
		}
		System.out.println("Incre_Store_length is "+Parser.incre_store.length);
		System.out.println("Stmt_Store_length is "+Parser.stmt_store.length);
		System.arraycopy(Parser.stmt_store, 0, Code.code, incre_cnter_bfr, Parser.incre_store.length);
		System.arraycopy(Parser.incre_store, 0, Code.code, stmt_cnter_bfr, Parser.stmt_store.length);
		//Parser.stmt_store = new String[100];
		//Parser.incre_store = new String[100];
		//Arrays.fill(Parser.stmt_store, null);
		//Arrays.fill(Parser.incre_store, null);
		//System.out.println("Incre_store array is null?"+Arrays.toString(Parser.incre_store));
		//System.out.println("Incre_store new length is "+Parser.incre_store.length);
		System.out.println("Code array is"+Arrays.toString(Code.code));
		System.out.println("successful "+num+" time");
	}
}

class Rexp{ //  rexp -> expr('<' | '>' | '==' | '!=') expr
	Expr expr1;
	Expr expr2;
	char op;
	int if_cmpindex; //keeps track of the index of if_cmp
	
	public Rexp(){
		Lexer.lex();
		expr1 = new Expr();
		if(Lexer.nextToken == Token.LESSER_OP || Lexer.nextToken == Token.GREATER_OP || Lexer.nextToken == Token.EQ_OP
				|| Lexer.nextToken == Token.NOT_EQ) {
			op = Lexer.nextChar;
			Parser.op_rexp.add(Lexer.nextChar);
			Lexer.lex();
			expr2 = new Expr();
			//System.out.println("current pointer is at " + Code.getCodePtr());  // current pointer is at 8
			Code.gen(Code.opcode(op)); //when gen is called codeptr is increased by 1
			//System.out.println("current pointer is at " + Code.getCodePtr()); // current pointer is at 9
			if_cmpindex = Code.getCodePtr()-1; //to get the current index of if_cmpindex
			Code.double_byte(); //increases codeptr by 2
		}
	}
}


class Expr   {  // expr -> term [ ('+' | '-') expr ]
	
	Term t;
	Expr e;
	char op;

	public Expr() {
		t = new Term();
		if (Lexer.nextToken == Token.ADD_OP || Lexer.nextToken == Token.SUB_OP) {
			op = Lexer.nextChar;
			Lexer.lex();
			e = new Expr();
			Code.gen(Code.opcode(op));	 
		}
	}
}

class Term{  //term -> factor [ ('*' | '/') term ]
	
	Factor f;
	Term t;
	char op;

	public Term() {
		f = new Factor();
		if (Lexer.nextToken == Token.MULT_OP || Lexer.nextToken == Token.DIV_OP) {
			op = Lexer.nextChar;
			Lexer.lex();
			t = new Term();
			Code.gen(Code.opcode(op));
		}
	}
	
}

class Factor{   //factor -> int_lit | id | '(' expr ')'
	Expr e;
	int i;
	char c;

	public Factor() {
		//System.out.println("Lexer.nextToken at factor is"+Lexer.nextToken);
		switch (Lexer.nextToken) {
		case Token.INT_LIT: // number
			i = Lexer.intValue;
			Code.gen(Code.intcode(i));
			if(i > 127){
				Code.sipushinc();
			}
			else if(i > 5){
				Code.bipushinc();
			}
			Lexer.lex();
			break;
		case Token.ID: 
			i = Lexer.ident;
			if(Parser.hashmap.containsKey(Lexer.ident)){
				//System.out.println("Code Pointer before load is: " +Code.getCodePtr());
				//System.out.println("Parser.hashmap is: " +Parser.hashmap.get(Lexer.ident));
				Code.gen(Code.loadcode(Parser.hashmap.get(Lexer.ident)));
				Code.lg_identifier_check(Parser.hashmap.get(Lexer.ident));
				//System.out.println("Code Pointer after load is: " +Code.getCodePtr());
			}
			Lexer.lex();
			break;
		case Token.LEFT_PAREN: // '('
			Lexer.lex();
			e = new Expr();
			Lexer.lex(); // skip over ')'
			break;
		default:
			break;
		}
	}
}


class Code{
	
	static String[] code = new String[100];
	private static int codeptr = 0;
	static int k = 0;
	
	public static void gen(String s) {
		code[codeptr] = s;
		codeptr+=1;
	}
	
	public static int getCodePtr() {
		return codeptr;
	}
	
	public static void bipushinc() {
		codeptr+=1;
	}
	
	public static void sipushinc() {
		codeptr+=2;
	}
	
	public static void double_byte() {
		codeptr+=2;
	}
	
	public static String intcode(int i) {
		if (i > 127){
			String si = "sipush " + i;
			return si;	
		}
		if (i > 5){
			String bi = "bipush " + i;
			
			return bi;
		}
		return "iconst_" + i;
	}
	
	public static String loadcode(int loadint){
		if(loadint > 3) 
			return "iload " +loadint;
		
		return "iload_" + loadint;
	}
	
	public static void lg_identifier_check(int lg_identifier_index){ //Checks if the identifier has an index greater than 3
		if(lg_identifier_index > 3) 
			codeptr+=1;
	}
	
	public static String storecode(int storeint){
		if(storeint > 3) 
			return "istore " +storeint;
		
		return "istore_" + storeint;
	}
	
	public static void skipblock(int skip_to_ptr, int op_code_ptr){
		code[op_code_ptr] = code[op_code_ptr] + " " +Integer.toString(skip_to_ptr);
	}
	
	// returns goto 
	public static void insert_goto(){
		Code.gen("goto");
		//codeptr += 2;
	}
	
	
	public static String opcode(char op) {
		switch(op) {
		case '+' : return "iadd";
		case '-':  return "isub";
		case '*':  return "imul";
		case '/':  return "idiv";
		case '>':  return "if_icmple";
		case '<':  return "if_icmpge";
		case '!': return "if_icmpeq";
		case '=': return "if_icmpne";
		default: return "";
		}
	}
	
	//** My work
	public static void indexOfEnd(int ifcmp_index) {
		//System.out.println("ifcmpindex is " + ifcmp_index); // prints 8, prints 15
		//System.out.println("Code.code[ifcmp_index] is " + Code.code[ifcmp_index]); //prints "if_cmple", prints "goto"
		//System.out.println("codeptr is " + codeptr); //prints 18, prints 22
		Code.code[ifcmp_index] = Code.code[ifcmp_index] + " " + codeptr;
	}
	
	public static void output() {
		for (int i=0; i<codeptr; i++){
			if(code[i] != null){
				System.out.println(i + ":" + code[i]);
			}
		}
	}
}


	public class Delegation {
		public static void main(String args[]) {
			C c = new C();
			System.out.println(c.r());
			D d = new D();
			System.out.println(d.r());
			
			C2 c2 = new C2();
			System.out.println(c2.r());
			D2 d2 = new D2();
			System.out.println(d2.r());	
		}
	}

	 abstract class A {
		int a1 = 1;
		int a2 = 2;

		public int f() {
			return a1 + p(100) + q(100);
		}

        protected abstract int p(int m);
        protected abstract int q(int m);
	 }
	 
	 class B extends A {
		int b1 = 10;
		int b2 = 20;

		public int g() {
			return f() + this.q(200);
		}

		public int p(int m) {
			return m + b1;
		}

		public int q(int m) {
			return m + b2;
		}
	}
	 
	 
	class C extends B {
		int c1 = 100;
		int c2 = 200;

		public int r() {
			return f() + g() + c1;
			}
		
		public int p(int m) {
			return super.p(m) + super.q(m) + c2;
		}
		
		public int q(int m) {
			return m + a2 + b2 + c2;
		}
	}

	class D extends B {
		int d1 = 300;
		int d2 = 400;
		
		public int p(int m) {
			return m + a1 + b1 + d1;
			
		}
		public int r() {
			return f() + g() + d2;
		}

	}
	
// ---------------------------------------------------- //

	interface IA {
		int f();
		int p(int m);
		int q(int m);
	}
	
	interface IB extends IA {
		int g();
		int p(int m);
		int q(int m);
	}
	
	interface IC extends IB {
		int r();
		int p(int m);
		int q(int m);
	}
	
	interface ID extends IB {
		int r();
		int p(int m);
	}
	
	class A2 implements IA {
		
		int a1_i = 1;
		int a2_i = 2;
		
		static IA this_a;
		
		public A2(IA obj){
			this_a = obj;
		}
		
		@Override
		public int f() {
			return a1_i + p(100) + q(100);
		}

		@Override
		public int p(int m) {
			return this_a.p(m);
		}

		@Override
		public int q(int m) {
			return this_a.q(m);
		}
		
	}
	
	class B2 implements IB {
		
		int b1_i = 10;
		int b2_i = 20;
		
		A2 super_a;
		IB this_b;
		
		public B2(IB obj){
			super_a = new A2(this);
			this_b = obj;
		}
		
		@Override
		public int f() {
			return super_a.f();
		}

		@Override
		public int g() {
			return f() + this_b.q(200);
		}

		@Override
		public int p(int m) {
			return m + b1_i;
		}

		@Override
		public int q(int m) {
			return m + b2_i;
		}
	}
	
	class C2 implements IC {
		
		int c1_i = 100;
		int c2_i = 200;
		
		B2 super_b;
		A2 super_a;
		
		public C2(){
			super_b = new B2(this);
			super_a = new A2(this);
		}
		
		@Override
		public int g() {
			return 0;
		}

		@Override
		public int f() {
			return super_b.f();
		}

		@Override
		public int r() {
			return f() + super_b.g()+ c1_i;
		}

		@Override
		public int p(int m) {
			return super_b.p(m) + super_b.q(m)+ c2_i;
		}

		@Override
		public int q(int m) {
			return m + super_a.a2_i + super_b.b2_i + c2_i;
		}
		
	}
	
	class D2 implements ID {
		
		int d1_i = 300;
		int d2_i = 400;
		
		B2 super_b;
		A2 super_a;
		
		public D2() {
			super_b = new B2(this);
			super_a = new A2(this);
		}
		
		@Override
		public int g() {
			return 0;
		}

		@Override
		public int q(int m) {
			return super_b.q(m);
		}

		@Override
		public int f() {
			return super_b.f();
		}

		@Override
		public int r() {
			return f() + super_b.g() + d2_i;
		}

		@Override
		public int p(int m) {
			return m + super_a.a1_i + super_b.b1_i + d1_i;
		}
	}
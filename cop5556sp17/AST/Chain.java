package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;

public abstract class Chain extends Statement {
	
	public Chain(Token firstToken) {
		super(firstToken);
	}
	
	private TypeName t;
	boolean b;
	public boolean getB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}

	Dec dec;
	
	public Dec getDec() {
		return dec;
	}
	public void setDec(Dec dec) {
		this.dec = dec;
	}
	public TypeName getTypeName() {
		return t;
	}
	public void setTypeName(TypeName t1) {
		t = t1;
	}
}

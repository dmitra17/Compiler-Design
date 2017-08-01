package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;


public class Dec extends ASTNode {
	
	private int current_slot;
	final Token ident;	
	private TypeName t;	
	public int get_current_slot() {
		return current_slot;
	}
	public void set_current_slot(int current_slot) {
		this.current_slot = current_slot;
	}	
	public TypeName getTypeName(){
		return t;
	}
	public void setTypeName(TypeName t1){
		t=t1;
	}
	public Dec(Token firstToken, Token ident) {
		super(firstToken);
		this.ident = ident;
	}
	public Token getType() {
		return firstToken;
	}
	public Token getIdent() {
		return ident;
	}
	@Override
	public String toString() {
		return "Dec [ident=" + ident + ", firstToken=" + firstToken + "]";
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((ident == null) ? 0 : ident.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (!(obj instanceof Dec)) {
			return false;
		}
		Dec other = (Dec) obj;
		if (ident == null) {
			if (other.ident != null) {
				return false;
			}
		} else if (!ident.equals(other.ident)) {
			return false;
		}
		return true;
	}

	@Override
	public Object visit(ASTVisitor v, Object arg) throws Exception {
		return v.visitDec(this,arg);
	}

}

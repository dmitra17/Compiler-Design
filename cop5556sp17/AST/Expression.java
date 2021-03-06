package cop5556sp17.AST;

import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.Type.TypeName;


public abstract class Expression extends ASTNode {
	
	protected Expression(Token firstToken) {
		super(firstToken);
	}
	
	private TypeName t;
	public TypeName getTypeName(){
		return t;
	}
	public void setTypeName(TypeName t1){
		t=t1;
	}

	@Override
	abstract public Object visit(ASTVisitor v, Object arg) throws Exception;

}

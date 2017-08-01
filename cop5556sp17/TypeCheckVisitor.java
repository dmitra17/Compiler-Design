package cop5556sp17;
import cop5556sp17.AST.ASTVisitor;
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.AssignmentStatement;
import cop5556sp17.AST.BinaryChain;
import cop5556sp17.AST.BinaryExpression;
import cop5556sp17.AST.Block;
import cop5556sp17.AST.BooleanLitExpression;
import cop5556sp17.AST.ConstantExpression;
import cop5556sp17.AST.Dec;
import cop5556sp17.AST.Expression;
import cop5556sp17.AST.FilterOpChain;
import cop5556sp17.AST.FrameOpChain;
import cop5556sp17.AST.IdentChain;
import cop5556sp17.AST.IdentExpression;
import cop5556sp17.AST.IdentLValue;
import cop5556sp17.AST.IfStatement;
import cop5556sp17.AST.ImageOpChain;
import cop5556sp17.AST.IntLitExpression;
import cop5556sp17.AST.ParamDec;
import cop5556sp17.AST.Program;
import cop5556sp17.AST.SleepStatement;
import cop5556sp17.AST.Statement;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;
import cop5556sp17.AST.Type;
import java.util.List;
import cop5556sp17.Scanner.Token;
import static cop5556sp17.AST.Type.TypeName.*;
import static cop5556sp17.Scanner.Kind.ARROW;
import static cop5556sp17.Scanner.Kind.KW_HIDE;
import static cop5556sp17.Scanner.Kind.KW_MOVE;
import static cop5556sp17.Scanner.Kind.KW_SHOW;
import static cop5556sp17.Scanner.Kind.KW_XLOC;
import static cop5556sp17.Scanner.Kind.KW_YLOC;
import static cop5556sp17.Scanner.Kind.OP_BLUR;
import static cop5556sp17.Scanner.Kind.OP_CONVOLVE;
import static cop5556sp17.Scanner.Kind.OP_GRAY;
import static cop5556sp17.Scanner.Kind.OP_HEIGHT;
import static cop5556sp17.Scanner.Kind.OP_WIDTH;
import static cop5556sp17.Scanner.Kind.*;

public class TypeCheckVisitor implements ASTVisitor {

	@SuppressWarnings("serial")
	public static class TypeCheckException extends Exception {
		TypeCheckException(String message) {
			super(message);
		}
	}

	SymbolTable symtab = new SymbolTable();

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName t0 = (TypeName)binaryChain.getE0().visit(this, arg);
		TypeName t1 = (TypeName)binaryChain.getE1().visit(this, arg);
		Token op = binaryChain.getArrow();
		
		if(binaryChain.getE1() instanceof FrameOpChain){
			
		}
		
		if(op.isKind(ARROW)){
			if(t0.equals(URL) && t1.equals(IMAGE)){
				binaryChain.setTypeName(IMAGE);
			}
			else if(t0.equals(FILE) && t1.equals(IMAGE)){
				binaryChain.setTypeName(IMAGE);
			}
			else if(t0.equals(FRAME) && (binaryChain.getE1() instanceof FrameOpChain) && 
					(binaryChain.getE1().getFirstToken().isKind(KW_XLOC) || binaryChain.getE1().
							getFirstToken().isKind(KW_YLOC))){
				binaryChain.setTypeName(INTEGER);
			}
			else if(t0.equals(FRAME) && (binaryChain.getE1() instanceof FrameOpChain) && 
					(binaryChain.getE1().getFirstToken().isKind(KW_SHOW) || binaryChain.getE1().
							getFirstToken().isKind(KW_HIDE) || binaryChain.getE1().getFirstToken().isKind(KW_MOVE))){
				binaryChain.setTypeName(FRAME);
			}
			else if(t0.equals(IMAGE) && (binaryChain.getE1() instanceof ImageOpChain) && 
					(binaryChain.getE1().getFirstToken().isKind(OP_WIDTH) || binaryChain.getE1().
							getFirstToken().isKind(OP_HEIGHT))){
				binaryChain.setTypeName(INTEGER);
			}
			else if(t0.equals(IMAGE) && t1.equals(FRAME)){
				binaryChain.setTypeName(FRAME);
			}
			else if(t0.equals(IMAGE) && t1.equals(FILE)){
				binaryChain.setTypeName(NONE);
			}
			else if(t0.equals(IMAGE) && (binaryChain.getE1() instanceof FilterOpChain) && 
					(binaryChain.getE1().getFirstToken().isKind(OP_GRAY) || binaryChain.getE1().
							getFirstToken().isKind(OP_BLUR) || binaryChain.getE1().getFirstToken().
							isKind(OP_CONVOLVE))){
				binaryChain.setTypeName(IMAGE);
			}
			else if(t0.equals(IMAGE) && (binaryChain.getE1() instanceof ImageOpChain) && 
					(binaryChain.getE1().getFirstToken().isKind(KW_SCALE) )){
				binaryChain.setTypeName(IMAGE);
			}
			else if(t0.equals(IMAGE) && (binaryChain.getE1() instanceof IdentChain) ){
				binaryChain.setTypeName(IMAGE);
			}
			else if(t0.equals(IMAGE) && (binaryChain.getE1() instanceof IdentChain) && (t1.equals(IMAGE)) ){
				binaryChain.setTypeName(IMAGE);	
			}
			else if(t0.equals(INTEGER) && (binaryChain.getE1() instanceof IdentChain) && (t1.equals(INTEGER)) ){
				binaryChain.setTypeName(INTEGER);
			}
			else{
				throw new TypeCheckException("BinaryChain Error, Token="+binaryChain.getFirstToken().
						getText()+" Global Position="+binaryChain.getFirstToken().pos+" Line Pos="+ binaryChain.
						getFirstToken().getLinePos());
			}			
		}
		else if(op.isKind(BARARROW)){
			if(t0.equals(IMAGE) && (binaryChain.getE1() instanceof FilterOpChain) && 
					(binaryChain.getE1().getFirstToken().isKind(OP_GRAY) || binaryChain.getE1().
							getFirstToken().isKind(OP_BLUR) || binaryChain.getE1().getFirstToken().
							isKind(OP_CONVOLVE))){
				binaryChain.setTypeName(IMAGE);
			}
			else{
				throw new TypeCheckException("BinaryChain Error, Token="+binaryChain.getFirstToken().
						getText()+" Global Position="+binaryChain.getFirstToken().pos+" Line Pos="+ binaryChain.
						getFirstToken().getLinePos());
			}
		}
		else{
			throw new TypeCheckException("BinaryChain Error, Token="+binaryChain.getFirstToken().
					getText()+" Global Position="+binaryChain.getFirstToken().pos+" Line Pos="+ binaryChain.
					getFirstToken().getLinePos());
		}		
		return binaryChain.getTypeName();		
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName t0 = (TypeName) binaryExpression.getE0().visit(this, arg);
        TypeName t1 = (TypeName) binaryExpression.getE1().visit(this, arg);
        
        Scanner.Kind opkind = binaryExpression.getOp().kind;
        
        if(t0.equals(INTEGER) && (opkind.equals(PLUS)||opkind.equals(MINUS)) && t1.equals(INTEGER)){
        	binaryExpression.setTypeName(INTEGER);
        }
        else if(t0.equals(IMAGE) && (opkind.equals(PLUS)||opkind.equals(MINUS)) && t1.equals(IMAGE)){
        	binaryExpression.setTypeName(IMAGE);
        }
        else if(t0.equals(INTEGER) && (opkind.equals(TIMES)||opkind.equals(DIV)) && t1.equals(INTEGER)){
        	binaryExpression.setTypeName(INTEGER);
        }
        else if(t0.equals(INTEGER) && (opkind.equals(TIMES)) && t1.equals(IMAGE)){
        	binaryExpression.setTypeName(IMAGE);
        }
        else if(t0.equals(IMAGE) && (opkind.equals(TIMES)) && t1.equals(INTEGER)){
        	binaryExpression.setTypeName(IMAGE);
        }
        else if(t0.equals(INTEGER) && (opkind.equals(LT)||opkind.equals(GT)||opkind.equals(LE)||
        		opkind.equals(GE)) && t1.equals(INTEGER)){
        	binaryExpression.setTypeName(BOOLEAN);
        }
        else if(t0.equals(BOOLEAN) && (opkind.equals(LT)||opkind.equals(GT)||opkind.equals(LE)||
        		opkind.equals(GE)) && t1.equals(BOOLEAN)){
        	binaryExpression.setTypeName(BOOLEAN);
        }
        else if(t0.equals(BOOLEAN) && (opkind.equals(AND)||opkind.equals(OR)) && t1.equals(BOOLEAN)){
        	binaryExpression.setTypeName(BOOLEAN);
        }
        
        else if(t0.equals(IMAGE) && (opkind.equals(DIV)) && t1.equals(INTEGER)){	//HW6
        	binaryExpression.setTypeName(IMAGE);
        }
        else if(t0.equals(IMAGE) && (opkind.equals(MOD)) && t1.equals(INTEGER)){	//HW6
        	binaryExpression.setTypeName(IMAGE);
        }
        else if(t0.equals(INTEGER) && (opkind.equals(MOD)) && t1.equals(INTEGER)){	//HW6
        	binaryExpression.setTypeName(INTEGER);
        }
        
        else if(t0.equals(t1) && (opkind.equals(AND)||opkind.equals(OR)) ){
        	binaryExpression.setTypeName(BOOLEAN);	//HW6
        }        
        else if(opkind.equals(EQUAL)||opkind.equals(NOTEQUAL)){
        	if(t0.equals(t1)){
        		binaryExpression.setTypeName(BOOLEAN);
        	}        	
        	else{
        		throw new TypeCheckException("BinaryExpression Error, Token="+binaryExpression.
        				getFirstToken().getText()+" Global Position="+binaryExpression.getFirstToken().pos+" Line Pos="+
        				binaryExpression.getFirstToken().getLinePos());
        	}
        }
        else{
        	throw new TypeCheckException("BinaryExpression Error, Token="+binaryExpression.
        			getFirstToken().getText()+" Global Position="+binaryExpression.getFirstToken().pos+" Line Pos="+
        			binaryExpression.getFirstToken().getLinePos());
        }                 
        return binaryExpression.getTypeName();		
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		// TODO Auto-generated method stub
		symtab.enterScope();
		for(Dec d : block.getDecs()){
			d.visit(this, arg);
		}
		for(Statement s : block.getStatements()){
			s.visit(this, arg);
		}
		symtab.leaveScope();
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		booleanLitExpression.setTypeName(BOOLEAN);
		return booleanLitExpression.getTypeName();		
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		int i = filterOpChain.getArg().getExprList().size();
		if(i!=0){
			throw new TypeCheckException("FilterOpChain Error, Token="+filterOpChain.
					getFirstToken().getText()+" Global Position="+filterOpChain.getFirstToken().pos+" Line Pos="+
					filterOpChain.getFirstToken().getLinePos());
		}		
		filterOpChain.getArg().visit(this, arg);
		filterOpChain.setTypeName(IMAGE);		
		return filterOpChain.getTypeName();		
	}
	
	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token t = frameOpChain.getFirstToken();
		if(t.isKind(KW_SHOW) || t.isKind(KW_HIDE)){
			if(frameOpChain.getArg().getExprList().size() != 0){
				throw new TypeCheckException("Length of FrameOpChain not 0, Token="+frameOpChain.
						getFirstToken().getText()+" Global Position="+frameOpChain.getFirstToken().pos+" Line Pos="+
						frameOpChain.getFirstToken().getLinePos());
			}
			frameOpChain.setTypeName(NONE);
		}
		else if(t.isKind(KW_XLOC) || t.isKind(KW_YLOC)){
			if(frameOpChain.getArg().getExprList().size() != 0){
				throw new TypeCheckException("Length of FrameOpChain not 0, Token="+frameOpChain.
						getFirstToken().getText()+" Global Position="+frameOpChain.getFirstToken().pos+" Line Pos="+
						frameOpChain.getFirstToken().getLinePos());
			}
			frameOpChain.setTypeName(INTEGER);
		}
		else if(t.isKind(KW_MOVE)){
			if(frameOpChain.getArg().getExprList().size() != 2){
				throw new TypeCheckException("Length of FrameOpChain not 2, Token="+frameOpChain.
						getFirstToken().getText()+" Global Position="+frameOpChain.getFirstToken().pos+" Line Pos="+
						frameOpChain.getFirstToken().getLinePos());
			}
			frameOpChain.setTypeName(NONE);
		}
		else{
			throw new TypeCheckException("FrameOpChain Operator not correct, Token="+frameOpChain.
					getFirstToken().getText()+" Global Position="+frameOpChain.getFirstToken().pos+" Line Pos="+
					frameOpChain.getFirstToken().getLinePos());
		}		
		frameOpChain.getArg().visit(this, arg);		
		return frameOpChain.getTypeName();		
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec d = symtab.lookup(identChain.getFirstToken().getText());
		if(d==null){
			throw new TypeCheckException("IdentChain Lookup Fail, Token="+identChain.
					getFirstToken().getText()+"Global Position="+identChain.getFirstToken().pos+" Line Pos="+ identChain.
					getFirstToken().getLinePos());
		}
		identChain.setDec(d);
		identChain.setTypeName(d.getTypeName());		
		return identChain.getTypeName();		
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec d = symtab.lookup(identExpression.getFirstToken().getText());
		if(d==null){
			throw new TypeCheckException("IdentExpression Lookup Fail, Token="+identExpression.
					getFirstToken().getText()+" Global Position="+identExpression.getFirstToken().pos+" Line Pos="+
					identExpression.getFirstToken().getLinePos());
		}
		identExpression.setTypeName(d.getTypeName());
		identExpression.setDec(d);		
		return identExpression.getTypeName();		
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName tn = (TypeName)ifStatement.getE().visit(this, arg);
		if(!tn.equals(BOOLEAN)){
			throw new TypeCheckException("IfStatement Error, Token="+ifStatement.
					getFirstToken().getText()+" Global Position="+ifStatement.getFirstToken().pos+" Line Pos="+
					ifStatement.getFirstToken().getLinePos());
		}
		ifStatement.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		// TODO Auto-generated method stub
		intLitExpression.setTypeName(INTEGER);
		return intLitExpression.getTypeName();
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName tn = (TypeName)sleepStatement.getE().visit(this, arg);
		if(!tn.equals(INTEGER)){
			throw new TypeCheckException("SleepStatement Error, Token="+sleepStatement.
					getFirstToken().getText()+" Global Position="+sleepStatement.getFirstToken().pos+" Line Pos="+
					sleepStatement.getFirstToken().getLinePos());
		}
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName tn = (TypeName)whileStatement.getE().visit(this, arg);
		if(!tn.equals(BOOLEAN)){
			throw new TypeCheckException("WhileStatement Error, Token="+whileStatement.
					getFirstToken().getText()+" Global Position="+whileStatement.getFirstToken().pos+" Line Pos="+ 
					whileStatement.getFirstToken().getLinePos());
		}
		whileStatement.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		// TODO Auto-generated method stub
		boolean b = symtab.insert(declaration.getIdent().getText(), declaration);
		if(!b){
			throw new TypeCheckException("visitDec Error, Token="+declaration.
					getFirstToken().getText()+" Global Position="+declaration.getFirstToken().pos+" Line Pos="+ 
					declaration.getFirstToken().getLinePos());
		}
		declaration.setTypeName(Type.getTypeName(declaration.getFirstToken()));
		return null;
	}

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		// TODO Auto-generated method stub
		for(ParamDec p : program.getParams()){
			p.visit(this, arg);
		}
		program.getB().visit(this, arg);
		return null;
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		// TODO Auto-generated method stub
		TypeName t0 = (TypeName)assignStatement.getVar().visit(this, arg);
		TypeName t1 = (TypeName)assignStatement.getE().visit(this, arg);
		
		if(!t0.equals(t1)){
			throw new TypeCheckException("Assignment Statement error, Token="+assignStatement.
					getFirstToken().getText()+" Global Position="+assignStatement.getFirstToken().pos+" Line Pos="+ 
					assignStatement.getFirstToken().getLinePos());
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Dec d = symtab.lookup(identX.getText());
		if(d==null){
			throw new TypeCheckException("IdentLValue Lookup Fail, Token="+identX.getFirstToken().
					getText()+" Global Position="+identX.getFirstToken().pos+" Line Pos="+ identX.
					getFirstToken().getLinePos());
		}		
		identX.setDec(d);
		return d.getTypeName();
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		// TODO Auto-generated method stub
		boolean b = symtab.insert(paramDec.getIdent().getText(), paramDec);
		if(!b){
			throw new TypeCheckException("ParamDec error, Token="+paramDec.getFirstToken().
					getText()+" Global Position="+paramDec.getFirstToken().pos+" Line Pos="+ paramDec.
					getFirstToken().getLinePos());
		}
		paramDec.setTypeName(Type.getTypeName(paramDec.getFirstToken()));
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {
		// TODO Auto-generated method stub
		constantExpression.setTypeName(INTEGER);
		return constantExpression.getTypeName();
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Token t = imageOpChain.getFirstToken();
		if(t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT)){
			if(imageOpChain.getArg().getExprList().size() != 0){
					throw new TypeCheckException("Length of ImageOpChain not 0, Token="+imageOpChain.getFirstToken()
					.getText()+" Global Position= "+imageOpChain.getFirstToken().pos+" Line Pos="+imageOpChain.
					getFirstToken().getLinePos());
				}
			imageOpChain.setTypeName(INTEGER);
			}
		else if(t.isKind(KW_SCALE)){
			if(imageOpChain.getArg().getExprList().size() != 1){
				throw new TypeCheckException("Length of ImageOpChain not 1, Token="+imageOpChain.getFirstToken().
						getText()+" Global Position= "+imageOpChain.getFirstToken().pos+" Line Pos="+imageOpChain.
						getFirstToken().getLinePos());
			}
			imageOpChain.setTypeName(IMAGE);
		}		
		else{
			throw new TypeCheckException("Incorrect ImageOpChain operator, Token="+imageOpChain.getFirstToken().
					getText()+" Global Position= "+imageOpChain.getFirstToken().pos+" Line Pos="+ imageOpChain.
					getFirstToken().getLinePos());
		}		
		imageOpChain.getArg().visit(this, arg);
		
		return imageOpChain.getTypeName();
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		// TODO Auto-generated method stub
		List<Expression> listExp = tuple.getExprList();
		boolean exp_int = true;
		for(Expression e: listExp) {
			if(!e.visit(this, arg).equals(INTEGER)) {
				exp_int = false;
			}
		}
		if(!exp_int) {
			throw new TypeCheckException("Tuple Error, Token = "+tuple.getFirstToken().getText()+" Global Position="
					+tuple.getFirstToken().pos+" Line Pos= "+tuple.getFirstToken().getLinePos());
		}
		return null;
	}


}

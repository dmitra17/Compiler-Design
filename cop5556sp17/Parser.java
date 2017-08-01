package cop5556sp17;

import cop5556sp17.Scanner.Kind;
import static cop5556sp17.Scanner.Kind.*;
import java.util.ArrayList;
import cop5556sp17.Scanner.Token;
import cop5556sp17.AST.*;

public class Parser {

	/**
	 * Exception to be thrown if a syntax error is detected in the input.
	 * You will want to provide a useful error message.
	 *
	 */
	@SuppressWarnings("serial")
	public static class SyntaxException extends Exception {
		public SyntaxException(String message) {
			super(message);
		}
	}
	
	/**
	 * Useful during development to ensure unimplemented routines are
	 * not accidentally called during development.  Delete it when 
	 * the Parser is finished.
	 *
	 */
	
	/*
	@SuppressWarnings("serial")	
	public static class UnimplementedFeatureException extends RuntimeException {
		public UnimplementedFeatureException() {
			super();
		}
	}
	*/

	Scanner scanner;
	Token t;

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * parse the input using tokens from the scanner.
	 * Check for EOF (i.e. no trailing junk) when finished
	 * 
	 * @throws SyntaxException
	 */
	Program parse() throws SyntaxException {
		
		Program p = program();
		matchEOF();
		return p;
	}

	Expression expression() throws SyntaxException {
		//TODO
		Token ft=t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = term();
		while (t.isKind(LT) || t.isKind(LE) || t.isKind(GT) || 
				t.isKind(GE) || t.isKind(EQUAL) || t.isKind(NOTEQUAL)) { 
			Token op = t;
			consume(); 
			e1 = term();
			e0 = new BinaryExpression(ft,e0,op,e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression term() throws SyntaxException {
		//TODO
		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = elem();
		while (t.isKind(PLUS) || t.isKind(MINUS) || t.isKind(OR)) { 
			Token op = t;
			consume(); 
			e1 = elem();
			e0 = new BinaryExpression(ft,e0,op,e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression elem() throws SyntaxException {
		//TODO
		Token ft = t;
		Expression e0 = null;
		Expression e1 = null;
		e0 = factor();
		while (t.isKind(TIMES) || t.isKind(DIV) || t.isKind(AND) || t.isKind(MOD)) {
			Token op = t;
			consume(); 
			e1 = factor(); 
			e0 = new BinaryExpression(ft,e0,op,e1);
		}
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Expression factor() throws SyntaxException {
		Kind kind = t.kind;
		Expression e0 = null;
		switch (kind) {
		case IDENT: {			
			e0 = new IdentExpression(t);
			consume();
		}
			break;
		case INT_LIT: {			
			e0 = new IntLitExpression(t);
			consume();
		}
			break;
		case KW_TRUE:
		case KW_FALSE: {			
			e0 = new BooleanLitExpression(t);
			consume();
		}
			break;
		case KW_SCREENWIDTH:
		case KW_SCREENHEIGHT: {			
			e0 = new ConstantExpression(t);
			consume();
		}
			break;
		case LPAREN: {
			consume();
			e0=expression();
			match(RPAREN);
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal factor "+t.getLinePos()+" "+t.getText());
		}		
		return e0;
	}

	Block block() throws SyntaxException {
		//TODO
		Token ft = t;
		Block b0 = null;
		ArrayList<Dec> list_d = new ArrayList<Dec>();
		ArrayList<Statement> list_s = new ArrayList<Statement>();
		match(LBRACE);
		while(t.isKind(KW_INTEGER)  || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME) 
					|| t.isKind(OP_SLEEP) || t.isKind(KW_WHILE) || t.isKind(KW_IF) || t.isKind(IDENT) 
					|| t.isKind(OP_BLUR)|| t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE) || t.isKind(KW_SHOW) 
					|| t.isKind(KW_HIDE) || t.isKind(KW_MOVE)|| t.isKind(KW_XLOC) 
					|| t.isKind(KW_YLOC) || t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE))
		{
				if(t.isKind(KW_INTEGER)  || t.isKind(KW_BOOLEAN) || t.isKind(KW_IMAGE) || t.isKind(KW_FRAME)) {
					list_d.add(dec());
				}
				else {
					list_s.add(statement());
				}
		}
		match(RBRACE); 
		b0 = new Block(ft,list_d,list_s);
		return b0;
	}

		//throw new UnimplementedFeatureException();


	Program program() throws SyntaxException {
		//TODO
		Program p = null;
		Token ft = t;
		Block b0 = null;
		ArrayList<ParamDec> list = new ArrayList<ParamDec>();
		if(t.isKind(IDENT)) {
			consume();
			if(t.isKind(LBRACE)) {
				b0 = block();
			}
			else if(t.isKind(KW_URL) || t.isKind(KW_FILE) || t.isKind(KW_INTEGER) || t.isKind(KW_BOOLEAN)) {
				list.add(paramDec());
				while(t.isKind(COMMA)) {
					consume();
					list.add(paramDec());
				}
				b0 = block();
			}
			else {
				throw new SyntaxException("illegal program() "+t.getLinePos()+" "+t.getText());
			}
		}	
		else {
			throw new SyntaxException("illegal program() "+t.getLinePos()+" "+t.getText());
		}
		p = new Program(ft,list,b0);
		return p;
		//throw new UnimplementedFeatureException();
	}	

	ParamDec paramDec() throws SyntaxException {
		//TODO
		Kind kind = t.kind;
		Token ft = t;
		ParamDec d = null;
		switch (kind) {
		case KW_URL: {
			consume();
		}
			break;
		case KW_FILE: {
			consume();
		}
			break;
		case KW_INTEGER: {
			consume();
		}
			break;
		case KW_BOOLEAN: {
			consume();
		}
			break;
			default:
				//you will want to provide a more useful error message
				throw new SyntaxException("illegal paramDec() "+t.getLinePos()+" "+t.getText());
			}
		if(t.isKind(IDENT)) {
			Token i = t;
			consume();
			d = new ParamDec(ft,i);
		}
		else {
			throw new SyntaxException("illegal paramDec() "+t.getLinePos()+" "+t.getText());
		}
		return d;
		//throw new UnimplementedFeatureException();
	}

	Dec dec() throws SyntaxException {
		//TODO
		Kind kind = t.kind;
		Token ft = t;
		Dec d = null;
		switch (kind) {
		case KW_INTEGER: {
			consume();
		}
			break;
		case KW_BOOLEAN: {
			consume();
		}
			break;
		case KW_IMAGE: {
			consume();
		}
			break;
		case KW_FRAME: {
			consume();
		}
			break;
			default:
				//you will want to provide a more useful error message
				throw new SyntaxException("illegal dec() "+t.getLinePos()+" "+t.getText());
			}
		if(t.isKind(IDENT)) {
			Token i = t;
			consume();
			d = new Dec(ft,i);
		}
		else {
			throw new SyntaxException("illegal dec() "+t.getLinePos()+" "+t.getText());
		}
		return d;
	}
		//throw new UnimplementedFeatureException();

	Statement statement() throws SyntaxException {
		//TODO
		Token st = t;
		Statement s0 = null;
		Expression e0 = null;
		Block b0 = null;
		if(t.isKind(OP_SLEEP)) {					
			consume();	
			e0 = expression();
			s0 = new SleepStatement(st,e0);	
			match(SEMI);			
		}		
		else if(t.isKind(KW_WHILE)) {
			consume();
			match(LPAREN);
			e0 = expression();			
			match(RPAREN);
			b0 = block();
			s0 = new WhileStatement(st,e0,b0);
		}
		else if(t.isKind(KW_IF)) {
			consume();
			match(LPAREN);
			e0 = expression();
			match(RPAREN);
			b0 = block();
			s0 = new IfStatement(st,e0,b0);
		}
		else if(t.isKind(IDENT)) {
			Token nextToken = scanner.peek();
			if(nextToken.isKind(ASSIGN)) {
				s0=assign();
				match(SEMI);
			}
			else {
				s0=chain();
				match(SEMI);
			}
		}
		else if(t.isKind(OP_BLUR)|| t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE) || t.isKind(KW_SHOW) 
				|| t.isKind(KW_HIDE) || t.isKind(KW_MOVE)|| t.isKind(KW_XLOC) || t.isKind(KW_YLOC) 
				|| t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)) {
			s0=chain();
			match(SEMI);
		}
		else {
			throw new SyntaxException("illegal statement() "+t.getLinePos()+" "+t.getText());

		}
		return s0;
		//throw new UnimplementedFeatureException();
	}
	
	AssignmentStatement assign() throws SyntaxException {
		Token st = t;
		AssignmentStatement as = null;
		IdentLValue ilv = null;
		Expression e0 = null;
		if(t.isKind(IDENT)) {
			ilv = new IdentLValue(st);
			consume();
			if(t.isKind(ASSIGN)) {
				consume();
				e0 = expression();
				as = new AssignmentStatement(st,ilv,e0);
			}
		}
		else {
			throw new SyntaxException("illegal ifStatement() "+t.getLinePos()+" "+t.getText());
		}
		return as;
	}

	Chain chain() throws SyntaxException {
		//TODO
		Token ft = t, op =null;
		Chain c0 = null;
		ChainElem c1 = null;
		c0 = chainElem();
		op = arrowOp();
		//added
		c1 = chainElem();
		c0=new BinaryChain(ft,c0,op,c1);
		while (t.isKind(ARROW) || t.isKind(BARARROW)) { 
			op = t;
			consume(); 
			c1=chainElem();
			c0=new BinaryChain(ft,c0,op,c1);
		}
		return c0;		
		//throw new UnimplementedFeatureException();
	}
	
	WhileStatement whileStatement() throws SyntaxException {
		Expression e0 = null;
		Token ft = t;
		Block b0 = null;
		if(t.isKind(KW_WHILE)) {
			consume();
			if(t.isKind(LPAREN)) {
				consume();
				e0=expression();
				match(RPAREN);
			}
			b0=block();
		}
		else {
			throw new SyntaxException("illegal whileStatement() "+t.getLinePos()+" "+t.getText());
		}
		WhileStatement ws = new WhileStatement(ft,e0,b0);
		return ws;
	}
	
	IfStatement ifStatement() throws SyntaxException {
		Expression e0 = null;
		Token ft = t;
		Block b0 = null;
		if(t.isKind(KW_IF)) {
			consume();
			if(t.isKind(LPAREN)) {
				consume();
				e0=expression();
				match(RPAREN);
			}
			b0=block();
		}
		else {
			throw new SyntaxException("illegal ifStatement() "+t.getLinePos()+" "+t.getText());
		}
		IfStatement is = new IfStatement(ft,e0,b0);
		return is;
	}

	ChainElem chainElem() throws SyntaxException {
		//TODO
		ChainElem e0 = null;
		Token ft = t;
		
		if(t.isKind(IDENT)) {
			e0 = new IdentChain(ft);
			consume();
		}
		else if(t.isKind(OP_BLUR) || t.isKind(OP_GRAY) || t.isKind(OP_CONVOLVE)) {			
			consume();
			//arg();
			e0 = new FilterOpChain(ft, arg());
		}
		else if(t.isKind(KW_SHOW) || t.isKind(KW_HIDE) || t.isKind(KW_MOVE) || t.isKind(KW_XLOC) || 
				t.isKind(KW_YLOC)) {
			consume();
			//arg();
			e0 = new FrameOpChain(ft, arg());
		}
		else if(t.isKind(OP_WIDTH) || t.isKind(OP_HEIGHT) || t.isKind(KW_SCALE)) {
			consume();
			//arg();
			e0 = new ImageOpChain(ft, arg());
		}
		else {
			throw new SyntaxException("illegal chainElem() "+t.getLinePos()+" "+t.getText());
		}		
		return e0;
		//throw new UnimplementedFeatureException();
	}

	Tuple arg() throws SyntaxException {
		//TODO
		Token ft = t;
		ArrayList<Expression> list = new ArrayList<Expression>();		
		if(t.isKind(LPAREN)) {
			consume();
			list.add(expression());
			while(t.isKind(COMMA)) {
				consume();
				list.add(expression());
			}
			match(RPAREN);
		}		
		Tuple obj = new Tuple(ft, list);
		return obj;
		//throw new UnimplementedFeatureException();
	}
	
	Token arrowOp() throws SyntaxException {
		Kind kind = t.kind;
		Token op =t;
		switch (kind) {
		case ARROW: {
			consume();
		}
			break;
		case BARARROW: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal arrowOp() "+t.getLinePos()+" "+t.getText());
		}
		return op;
	}
	
	void filterOp() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case OP_BLUR: {
			consume();
		}
			break;
		case OP_GRAY: {
			consume();
		}
			break;
		case OP_CONVOLVE: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal filterOp "+t.getLinePos()+" "+t.getText());
		}
	}
	
	void frameOp() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case KW_SHOW: {
			consume();
		}
			break;
		case KW_HIDE: {
			consume();
		}
			break;
		case KW_MOVE: {
			consume();
		}
			break;
		case KW_XLOC: {
			consume();
		}
			break;
		case KW_YLOC: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal frameOp "+t.getLinePos()+" "+t.getText());
		}
	}
	
	void imageOp() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case OP_WIDTH: {
			consume();
		}
			break;
		case OP_HEIGHT: {
			consume();
		}
			break;
		case KW_SCALE: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal imageOp "+t.getLinePos()+" "+t.getText());
		}
	}
	
	void relOp() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case LT: {
			consume();
		}
			break;
		case LE: {
			consume();
		}
			break;
		case GT: {
			consume();
		}
			break;
		case GE: {
			consume();
		}
			break;
		case EQUAL: {
			consume();
		}
			break;
		case NOTEQUAL: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal relOp "+t.getLinePos()+" "+t.getText());
		}
	}
	
	void weakOp() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case PLUS: {
			consume();
		}
			break;
		case MINUS: {
			consume();
		}
			break;
		case OR: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal weakOp "+t.getLinePos()+" "+t.getText());
		}
	}
	
	void strongOp() throws SyntaxException {
		Kind kind = t.kind;
		switch (kind) {
		case TIMES: {
			consume();
		}
			break;
		case DIV: {
			consume();
		}
			break;
		case AND: {
			consume();
		}
			break;
		case MOD: {
			consume();
		}
			break;
		default:
			//you will want to provide a more useful error message
			throw new SyntaxException("illegal strongOp "+t.getLinePos()+" "+t.getText());
		}
	}
	
	
	/**
	 * Checks whether the current token is the EOF token. If not, a
	 * SyntaxException is thrown.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.isKind(EOF)) {
			return t;
		}
		throw new SyntaxException("expected EOF");
	}

	/**
	 * Checks if the current token has the given kind. If so, the current token
	 * is consumed and returned. If not, a SyntaxException is thrown.
	 * 
	 * Precondition: kind != EOF
	 * 
	 * @param kind
	 * @return
	 * @throws SyntaxException
	 */
	private Token match(Kind kind) throws SyntaxException {
		if (t.isKind(kind)) {
			return consume();
		}
		throw new SyntaxException("saw " + t.kind + "expected " + kind);
	}

	/**
	 * Checks if the current token has one of the given kinds. If so, the
	 * current token is consumed and returned. If not, a SyntaxException is
	 * thrown.
	 * 
	 * * Precondition: for all given kinds, kind != EOF
	 * 
	 * @param kinds
	 *            list of kinds, matches any one
	 * @return
	 * @throws SyntaxException
	 */
	//private Token match(Kind... kinds) throws SyntaxException {
		// TODO. Optional but handy
		//return null; //replace this statement
	//}

	/**
	 * Gets the next token and returns the consumed token.
	 * 
	 * Precondition: t.kind != EOF
	 * 
	 * @return
	 * 
	 */
	private Token consume() throws SyntaxException {
		Token tmp = t;
		t = scanner.nextToken();
		return tmp;
	}

}

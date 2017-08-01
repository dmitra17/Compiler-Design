package cop5556sp17;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Scanner {
	/**
	 * Kind enum
	 */	
	public static enum Kind {
		IDENT(""), INT_LIT(""), KW_INTEGER("integer"), KW_BOOLEAN("boolean"), 
		KW_IMAGE("image"), KW_URL("url"), KW_FILE("file"), KW_FRAME("frame"), 
		KW_WHILE("while"), KW_IF("if"), KW_TRUE("true"), KW_FALSE("false"), 
		SEMI(";"), COMMA(","), LPAREN("("), RPAREN(")"), LBRACE("{"), 
		RBRACE("}"), ARROW("->"), BARARROW("|->"), OR("|"), AND("&"), 
		EQUAL("=="), NOTEQUAL("!="), LT("<"), GT(">"), LE("<="), GE(">="), 
		PLUS("+"), MINUS("-"), TIMES("*"), DIV("/"), MOD("%"), NOT("!"), 
		ASSIGN("<-"), OP_BLUR("blur"), OP_GRAY("gray"), OP_CONVOLVE("convolve"), 
		KW_SCREENHEIGHT("screenheight"), KW_SCREENWIDTH("screenwidth"), 
		OP_WIDTH("width"), OP_HEIGHT("height"), KW_XLOC("xloc"), KW_YLOC("yloc"), 
		KW_HIDE("hide"), KW_SHOW("show"), KW_MOVE("move"), OP_SLEEP("sleep"), 
		KW_SCALE("scale"), EOF("eof");

		Kind(String text) {
			this.text = text;
		}

		final String text;

		String getText() {
			return text;
		}
	}
	public static enum State {
		START, IN_DIGIT, IN_IDENT, AFTER_EQ, AFTER_NOT, AFTER_LESSTHAN, AFTER_GREATERTHAN, 
		AFTER_MINUS, AFTER_OR, AFTER_ORMINUS, AFTER_DIV, COMMENT_S, COMMENT_END_STAR, COMMENT_E;                                                        
	}
	
	public HashMap<String,Kind> hm = new HashMap<String,Kind>();
	
	public ArrayList<Integer> line_pos_array = new ArrayList<Integer>();
	
/**
 * Thrown by Scanner when an illegal character is encountered
 */
	@SuppressWarnings("serial")
	public static class IllegalCharException extends Exception {
		public IllegalCharException(String message) {
			super(message);
		}
	}
	
	/**
	 * Thrown by Scanner when an int literal is not a value that can be represented by an int.
	 */
	@SuppressWarnings("serial")
	public static class IllegalNumberException extends Exception {
	public IllegalNumberException(String message){
		super(message);
		}
	}	

	/**
	 * Holds the line and position in the line of a token.
	 */
	static class LinePos {
		public final int line;
		public final int posInLine;
		
		public LinePos(int line, int posInLine) {
			super();
			this.line = line;
			this.posInLine = posInLine;
		}

		@Override
		public String toString() {
			return "LinePos [line=" + line + ", posInLine=" + posInLine + "]";
		}
	}
	public class Token {
		public final Kind kind;
		public final int pos;  //position in input array
		public final int length;  
		
		
		 @Override
		  public int hashCode() {
		   final int prime = 31;
		   int result = 1;
		   result = prime * result + getOuterType().hashCode();
		   result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		   result = prime * result + length;
		   result = prime * result + pos;
		   return result;
		  }

		  @Override
		  public boolean equals(Object obj) {
		   if (this == obj) {
		    return true;
		   }
		   if (obj == null) {
		    return false;
		   }
		   if (!(obj instanceof Token)) {
		    return false;
		   }
		   Token other = (Token) obj;
		   if (!getOuterType().equals(other.getOuterType())) {
		    return false;
		   }
		   if (kind != other.kind) {
		    return false;
		   }
		   if (length != other.length) {
		    return false;
		   }
		   if (pos != other.pos) {
		    return false;
		   }
		   return true;
		  }

		 

		  private Scanner getOuterType() {
		   return Scanner.this;
		  }
		
		
		
		

		//returns the text of this Token
		public String getText() {
			//TODO IMPLEMENT THIS
			String x = chars.substring(pos, pos+length);
			return x;
		}
		
		//returns a LinePos object representing the line and column of this Token
		LinePos getLinePos(){
			//TODO IMPLEMENT THIS
			
			int newlineindex=Collections.binarySearch(line_pos_array, pos);                        
			int line_number = Math.abs(newlineindex+2);                        
			int pos_in_line = pos-line_pos_array.get(line_number)-1; 
			
			return new LinePos(line_number,pos_in_line);
		}

		Token(Kind kind, int pos, int length) {
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			//System.out.println(kind+"     "+pos+"   "+length);
		}

		/** 
		 * Precondition:  kind = Kind.INT_LIT,  the text can be represented with a Java int.
		 * Note that the validity of the input should have been checked when the Token was created.
		 * So the exception should never be thrown.
		 * 
		 * @return  int value of this token, which should represent an INT_LIT
		 * @throws NumberFormatException
		 */
		public int intVal() throws NumberFormatException{
			//TODO IMPLEMENT THIS
			int y = Integer.parseInt(chars.substring(pos, pos+length));
			return y;
		}

		public boolean isKind(Kind k) {
			// TODO Auto-generated method stub
			if(k.equals(kind))
				return true;
			else 
			return false;
		}	
	}

	Scanner(String chars) {
		this.chars = chars;
		tokens = new ArrayList<Token>();
		hm.put("integer",Kind.KW_INTEGER);
		hm.put("boolean",Kind.KW_BOOLEAN);
		hm.put("image",Kind.KW_IMAGE);
		hm.put("url",Kind.KW_URL);
		hm.put("file",Kind.KW_FILE);
		hm.put("frame",Kind.KW_FRAME);
		hm.put("while",Kind.KW_WHILE);
		hm.put("if",Kind.KW_IF);
		hm.put("true",Kind.KW_TRUE);
		hm.put("false",Kind.KW_FALSE);
		hm.put("screenheight",Kind.KW_SCREENHEIGHT);
		hm.put("screenwidth",Kind.KW_SCREENWIDTH);
		hm.put("width",Kind.OP_WIDTH);
		hm.put("height",Kind.OP_HEIGHT);
		hm.put("xloc",Kind.KW_XLOC);
		hm.put("yloc",Kind.KW_YLOC);
		hm.put("hide",Kind.KW_HIDE);
		hm.put("show",Kind.KW_SHOW);
		hm.put("move",Kind.KW_MOVE);
		hm.put("sleep",Kind.OP_SLEEP);
		hm.put("scale",Kind.KW_SCALE); 
		hm.put("blur",Kind.OP_BLUR);
		hm.put("gray",Kind.OP_GRAY);
		hm.put("convolve",Kind.OP_CONVOLVE);

	}
	/**
	 * Initializes Scanner object by traversing chars and adding tokens to tokens list.
	 * 
	 * @return this scanner
	 * @throws IllegalCharException
	 * @throws IllegalNumberException
	 */
	public Scanner scan() throws IllegalCharException, IllegalNumberException {
		int pos = 0; 
		//TODO IMPLEMENT THIS!!!!
		int length = chars.length();
	    State state = State.START;
	    int startPos = 0;
	    int ch;
	    line_pos_array.add(-1);
	    while (pos <= length) {
	        ch = pos < length ? chars.charAt(pos) : -1;
	        //System.out.println("State: "+state+" pos: "+pos);
	        switch (state) {
	            case START: {
	            	if(pos<length) {
	            		pos=skipWhiteSpace(pos);
	            	}
	                ch = pos < length ? chars.charAt(pos) : -1;
	                startPos = pos;
	                switch (ch) {
	                    case -1: {tokens.add(new Token(Kind.EOF, pos, 0)); pos++;}  break;
	                    case '+': {tokens.add(new Token(Kind.PLUS, startPos, 1));pos++;} break;
	                    case '*': {tokens.add(new Token(Kind.TIMES, startPos, 1));pos++;} break;	                   
	                    case '&': {tokens.add(new Token(Kind.AND, startPos, 1));pos++;} break;
	                    case '%': {tokens.add(new Token(Kind.MOD, startPos, 1));pos++;} break;
	                    case ';': {tokens.add(new Token(Kind.SEMI, startPos, 1));pos++;} break;
	                    case ',': {tokens.add(new Token(Kind.COMMA, startPos, 1));pos++;} break;
	                    case '(': {tokens.add(new Token(Kind.LPAREN, startPos, 1));pos++;} break;
	                    case ')': {tokens.add(new Token(Kind.RPAREN, startPos, 1));pos++;} break;
	                    case '{': {tokens.add(new Token(Kind.LBRACE, startPos, 1));pos++;} break;
	                    case '}': {tokens.add(new Token(Kind.RBRACE, startPos, 1));pos++;} break;
	                    
	                    case '=': {state = State.AFTER_EQ;pos++;}break;
	                    case '!': {state = State.AFTER_NOT;pos++;}break;
	                    case '<': {state = State.AFTER_LESSTHAN;pos++;}break;
	                    case '>': {state = State.AFTER_GREATERTHAN;pos++;}break;
	                    case '-': {state = State.AFTER_MINUS;pos++;}break;
	                    case '|': {state = State.AFTER_OR; pos++;} break;
	                    case '/': {state = State.AFTER_DIV; pos++;} break;
	                    
	                    
	                    case '0': {tokens.add(new Token(Kind.INT_LIT,startPos, 1));pos++;}break;
	                    default: {
	                        if (Character.isDigit(ch)) {state = State.IN_DIGIT;pos++;} 
	                        else if (Character.isJavaIdentifierStart(ch)) {
	                             state = State.IN_IDENT;pos++;
	                         } 
	                         else {throw new IllegalCharException(
	                                    "illegal char " +(char)ch+" at pos "+pos);
	                         }
	                      }
	                } // switch (ch)
	            }  break;
	            case IN_DIGIT: {
	            	if(Character.isDigit(ch)) {
	            		pos++;
	            	}
	            	else {
	            		tokens.add(new Token(Kind.INT_LIT, startPos, pos - startPos));
	            		try {
	            			Integer.parseInt(chars.substring(startPos, pos));
	            		} catch(NumberFormatException e) {
	            			//System.out.println("Number out of range ");
	            			throw new IllegalNumberException("Number out of range ");
	            		}
	            		state = State.START;
	            	}
	            }  break;
	            case IN_IDENT: {
	            	if (Character.isJavaIdentifierPart(ch)) {
	                    pos++;
	              } 
	            else {
	            	  	Kind hm1 = hm.get(chars.substring(startPos, pos));
	            	  	if(hm1!=null)
	            	  	{
	            	  		tokens.add(new Token(hm1, startPos, pos - startPos));
	            	  	}
	            	  	else {
	                      tokens.add(new Token(Kind.IDENT, startPos, pos - startPos));	                      
	            	  	}
	            	  	state = State.START;
	              }
	            }  break;
	            case AFTER_EQ: {
	            	if(ch=='=') 
	            	{
	            		state = State.START;
	            		tokens.add(new Token(Kind.EQUAL, startPos, 2));
	            		pos++;
	            	}
                     //state = State.IN_IDENT;pos++;
                     else 
                     {
                    	 throw new IllegalCharException(
                         "illegal char " +ch+" at pos "+pos);
                     }
	            }  break;
	            
	            
	            
	            case AFTER_DIV: {
	            	if(ch=='*') 
	            	{
	            		state = State.COMMENT_S;
	            		//System.out.println("AFterDIV_*");
	            		//tokens.add(new Token(Kind.COMMENT_S, startPos, 2));
	            		pos++;
	            	}                     
                     else 
                     {
                    	 tokens.add(new Token(Kind.DIV, startPos, 1));
                    	 state = State.START;
                     }
	            }  break;
	            
	            case COMMENT_S: {
	            	if(ch=='*') 
	            	{
	            		state = State.COMMENT_END_STAR;
	            		//System.out.println("If * of COMMENT_S");	      
	            		//tokens.add(new Token(Kind.COMMENT_END_STAR, startPos, 1));
	            		pos++;
	            	}                     
                    else if(ch==-1) {
	            		tokens.add(new Token(Kind.EOF, pos, 0)); 
	            		pos++;	            		
     	              } 
     	            else if(ch=='\n') {
     	            	line_pos_array.add(pos);
     	            	pos++;
     	            } 
     	            else {
     	            	//System.out.println("Else of Comment_S");
                     pos++;                    
     	            } 	            
	            } break;
	            case COMMENT_END_STAR: {
	            	if(ch=='/') {
	            		state = State.START;
	            		//System.out.println("Got comment end slash");
	            		//tokens.add(new Token(Kind.COMMENT_E, startPos, 2));
	            		pos++;
	            	}
	            	else if(ch==-1) {
	            		tokens.add(new Token(Kind.EOF, pos, 0)); 
	            		pos++;
	            	}	            	
	            	else if(ch=='*')
	                   {
 	                    pos++;
 	              } 
	            	else {
	            		state = State.COMMENT_S;
	            		pos++;
	            	}
	            	
	            }break;
	          	            
	            case AFTER_NOT: {
	            	if(ch=='=') 
	            	{
	            		state = State.START;
	            		tokens.add(new Token(Kind.NOTEQUAL, startPos, 2));
	            		pos++;
	            	}                  
                     else 
                     {
                    	 tokens.add(new Token(Kind.NOT, startPos, 1));
                    	 state = State.START;
                     }
	            }  break;
	            case AFTER_LESSTHAN: {
	            	if(ch=='=') 
	            	{
	            		state = State.START;
	            		tokens.add(new Token(Kind.LE, startPos, 2));
	            		pos++;
	            	}
                     else if(ch=='-')
                     {
                    	state = State.START;
 	            		tokens.add(new Token(Kind.ASSIGN, startPos, 2));
 	            		pos++;
                     }
                     else {
                    	 tokens.add(new Token(Kind.LT, startPos, 1));
                    	 state = State.START;
                     }                    	 
	            }  break;
	            case AFTER_GREATERTHAN: {
	            	if(ch=='=') 
	            	{
	            		state = State.START;
	            		tokens.add(new Token(Kind.GE, startPos, 2));
	            		pos++;
	            	}                  
                     else 
                     {
                    	 tokens.add(new Token(Kind.GT, startPos, 1));
                    	 state = State.START;
                     }
	            }  break;
	            case AFTER_MINUS: {
	            	if(ch=='>') 
	            	{
	            		state = State.START;
	            		tokens.add(new Token(Kind.ARROW, startPos, 2));
	            		pos++;
	            	}                  
                     else 
                     {
                    	 tokens.add(new Token(Kind.MINUS, startPos, 1));
                    	 state = State.START;
                     }
	            }  break;
	            case AFTER_OR: {
	            	if(ch=='-') 
	            	{
	            		state = State.AFTER_ORMINUS;
	            		pos++;  		
	            	}                  
                     else 
                     {
                    	 tokens.add(new Token(Kind.OR, startPos, 1));
                    	 state = State.START;
                     }
	            }  break;
	            case AFTER_ORMINUS: {
	            	if(ch=='>') 
	            	{
	            		state = State.START;
	            		tokens.add(new Token(Kind.BARARROW, startPos, 3));
	            		pos++;		
	            	}                  
                     else 
                     {
                    	 tokens.add(new Token(Kind.OR, startPos, 1));
                    	 tokens.add(new Token(Kind.MINUS, startPos+1, 1));
                    	 state = State.START;
                     }
	            }  break;
	            default:  assert false;
	        }// switch(state)
	    } // while

		//tokens.add(new Token(Kind.EOF,pos,0));
		return this;  
	}



	private int skipWhiteSpace(int pos) {
		// TODO Auto-generated method stub
		while(Character.isWhitespace(chars.charAt(pos))) {
			if(chars.charAt(pos) == '\n') {
				line_pos_array.add(pos);
			}
			pos++;
			if(pos==chars.length()) {
				break;
			}
		}
		return pos;
	}

	final ArrayList<Token> tokens;
	final String chars;
	int tokenNum;

	/*
	 * Return the next token in the token list and update the state so that
	 * the next call will return the Token..  
	 */
	public Token nextToken() {
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum++);
	}	
	/*
	 * Return the next token in the token list without updating the state.
	 * (So the following call to next will return the same token.)
	 */
	public Token peek(){
		if (tokenNum >= tokens.size())
			return null;
		return tokens.get(tokenNum);		
	}
	/**
	 * Returns a LinePos object containing the line and position in line of the 
	 * given token.  
	 * 
	 * Line numbers start counting at 0
	 * 
	 * @param t
	 * @return
	 */
	public LinePos getLinePos(Token t) {
		//TODO IMPLEMENT THIS
		return t.getLinePos();
	}
}
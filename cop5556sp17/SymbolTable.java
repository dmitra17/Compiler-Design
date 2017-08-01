package cop5556sp17;



import cop5556sp17.AST.Dec;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;


public class SymbolTable {
	
	
	//TODO  add fields
	class STAttribute {
		int s;
		Dec d;
		public STAttribute(int s, Dec d) {
			this.s=s;
			this.d=d;
		}		
	}
	
	HashMap<String, ArrayList<STAttribute>> entry;
	Stack<Integer> scope;
	int c_scope, n_scope;

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		//TODO:  IMPLEMENT THIS
		c_scope = n_scope;
		n_scope++;
		scope.push(c_scope);		
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		//TODO:  IMPLEMENT THIS
		scope.pop();
		c_scope = scope.peek();
	}
	
	public boolean insert(String ident, Dec dec){
		//TODO:  IMPLEMENT THIS
		if(entry.containsKey(ident)) {
			ArrayList<STAttribute> aList = entry.get(ident);
			for(int i=0;i<aList.size();i++) {
				STAttribute listEntry = aList.get(i);
				if(listEntry.s==c_scope) {
					return false;
				}
			}
			aList.add(new STAttribute(c_scope,dec));
			return true;
		}
		ArrayList<STAttribute> newAttribList = new ArrayList<STAttribute>();
		STAttribute newAttrib = new STAttribute(c_scope,dec);
		newAttribList.add(newAttrib);
		entry.put(ident, newAttribList);		
		return true;
	}
	
	public Dec lookup(String ident){
		//TODO:  IMPLEMENT THIS
		
		if(entry.containsKey(ident)){
			ArrayList<STAttribute> attribList = entry.get(ident);
			Dec decIdent=null;
			
			for(int i=0; i<attribList.size(); i++){
				STAttribute listEntry = attribList.get(i);				
				if(listEntry.s<=c_scope){
					if(scope.contains(listEntry.s)){
						decIdent=listEntry.d;
					}
				}
				else{
					break;
				}
			}
			return decIdent;
		}
		return null;
	}
		
	public SymbolTable() {
		//TODO:  IMPLEMENT THIS
		this.entry = new HashMap<String,ArrayList<STAttribute>>();
		this.scope = new Stack<Integer>();
		this.c_scope = 0;
		this.scope.push(this.c_scope);
		this.n_scope=1;
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		
		return "currentScope="+c_scope;
	}
	
	


}

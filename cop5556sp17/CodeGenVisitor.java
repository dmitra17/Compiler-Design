package cop5556sp17;

import java.util.ArrayList;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import cop5556sp17.Scanner.Kind;
import cop5556sp17.AST.ASTVisitor;
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
import cop5556sp17.AST.Tuple;
import cop5556sp17.AST.Type;
import cop5556sp17.AST.Type.TypeName;
import cop5556sp17.AST.WhileStatement;

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;
	Label cStart;
	Label cEnd;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	private int slot=1;
	private int iterate=0;
	int bac = 0 ;

	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.getName();
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object",
				new String[] { "java/lang/Runnable" });
		cw.visitSource(sourceFileName, null);

		// generate constructor code
		// get a MethodVisitor
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		// Create label at start of code
		Label constructorStart = new Label();
		mv.visitLabel(constructorStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering <init>");
		// generate code to call superclass constructor
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		// visit parameter decs to add each as field to the class
		// pass in mv so decs can add their initialization code to the
		// constructor.
		ArrayList<ParamDec> params = program.getParams();
		for (ParamDec dec : params) 
		{
			dec.visit(this, mv);
			iterate++;
		}
		mv.visitInsn(RETURN);
		// create label at end of code
		Label constructorEnd = new Label();
		mv.visitLabel(constructorEnd);
		// finish up by visiting local vars of constructor
		// the fourth and fifth arguments are the region of code where the local
		// variable is defined as represented by the labels we inserted.
		mv.visitLocalVariable("this", classDesc, null, constructorStart, constructorEnd, 0);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, constructorStart, constructorEnd, 1);
		// indicates the max stack size for the method.
		// because we used the COMPUTE_FRAMES parameter in the classwriter
		// constructor, asm
		// will do this for us. The parameters to visitMaxs don't matter, but
		// the method must
		// be called.
		mv.visitMaxs(1, 1);
		// finish up code generation for this method.
		mv.visitEnd();
		// end of constructor

		// create main method which does the following
		// 1. instantiate an instance of the class being generated, passing the
		// String[] with command line arguments
		// 2. invoke the run method.
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null,
				null);
		mv.visitCode();
		Label mainStart = new Label();
		mv.visitLabel(mainStart);
		// this is for convenience during development--you can see that the code
		// is doing something.
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering main");
		mv.visitTypeInsn(NEW, className);
		mv.visitInsn(DUP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, className, "<init>", "([Ljava/lang/String;)V", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, className, "run", "()V", false);
		mv.visitInsn(RETURN);
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);
		mv.visitLocalVariable("instance", classDesc, null, mainStart, mainEnd, 1);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		// create run method
		mv = cw.visitMethod(ACC_PUBLIC, "run", "()V", null, null);
		mv.visitCode();
		Label startRun = new Label();
		mv.visitLabel(startRun);
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering run");
		program.getB().visit(this, null);
		mv.visitInsn(RETURN);
		Label endRun = new Label();
		mv.visitLabel(endRun);
		mv.visitLocalVariable("this", classDesc, null, startRun, endRun, 0);
//TODO  visit the local variables
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, startRun, endRun, 1);
		mv.visitMaxs(1, 1);
		mv.visitEnd(); // end of run method		
		cw.visitEnd();//end of class		
		//generate classfile and return it
		return cw.toByteArray();
	}

	@Override
	public Object visitAssignmentStatement(AssignmentStatement assignStatement, Object arg) throws Exception {
		if(assignStatement.getVar().getDec() instanceof ParamDec){
			mv.visitVarInsn(ALOAD, 0);
		}
		assignStatement.getE().visit(this, arg);
		CodeGenUtils.genPrint(DEVEL, mv, "\nassignment: " + assignStatement.var.getText() + "=");
		CodeGenUtils.genPrintTOS(GRADE, mv, assignStatement.getE().getTypeName());		
		assignStatement.getVar().visit(this, arg);
		return null;
	}

	@Override
	public Object visitBinaryChain(BinaryChain binaryChain, Object arg) throws Exception {		
		binaryChain.getE0().visit(this, 1);
		binaryChain.getE1().visit(this, 2);		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitBinaryExpression(BinaryExpression binaryExpression, Object arg) throws Exception {
      //TODO  Implement this		
		CodeGenUtils.genPrint(DEVEL, mv, "\nentering binary exp");
		Label set_true = new Label();
		Label l = new Label();
		TypeName t1 = binaryExpression.getE0().getTypeName();
		TypeName t2 = binaryExpression.getE1().getTypeName();
		if (binaryExpression.getE1().getTypeName().equals(TypeName.IMAGE)
				&&binaryExpression.getE0().getTypeName().equals(TypeName.INTEGER)) {			
			binaryExpression.getE1().visit(this, arg);
			binaryExpression.getE0().visit(this, arg);
		} else {
			binaryExpression.getE0().visit(this, arg);
			binaryExpression.getE1().visit(this, arg);
		}		
		if(binaryExpression.getOp().getText().equals("+")) {
			if(t1.equals(TypeName.INTEGER) && t2.equals(TypeName.INTEGER))
				mv.visitInsn(IADD);
			else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "add", 
						PLPRuntimeImageOps.addSig, false);
		}
		else if(binaryExpression.getOp().getText().equals("-")) {			
			if(t1.equals(TypeName.INTEGER) && t2.equals(TypeName.INTEGER))
				mv.visitInsn(ISUB);
			else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.IMAGE))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "sub", 
						PLPRuntimeImageOps.subSig, false);			
		}
		else if(binaryExpression.getOp().getText().equals("==")) {
			if (binaryExpression.getE0().getTypeName().equals(TypeName.INTEGER)
					|| binaryExpression.getE0().getTypeName().equals(TypeName.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPEQ, set_true);
			} else {
				mv.visitJumpInsn(IF_ACMPEQ, set_true);
			}
			mv.visitLdcInsn(false);
		}
		else if(binaryExpression.getOp().getText().equals("!=")) {
			
			if (binaryExpression.getE0().getTypeName().equals(TypeName.INTEGER)
					|| binaryExpression.getE0().getTypeName().equals(TypeName.BOOLEAN)) {
				mv.visitJumpInsn(IF_ICMPNE, set_true);
			} else {
				mv.visitJumpInsn(IF_ACMPNE, set_true);
			}
			mv.visitLdcInsn(false);
		}
		else if(binaryExpression.getOp().getText().equals("<")) {
			mv.visitJumpInsn(IF_ICMPLT, set_true);
			mv.visitLdcInsn(false);
		}
		else if(binaryExpression.getOp().getText().equals(">")) {
			mv.visitJumpInsn(IF_ICMPGT, set_true);
			mv.visitLdcInsn(false);
		}
		else if(binaryExpression.getOp().getText().equals(">=")) {
			mv.visitJumpInsn(IF_ICMPGE, set_true);
			mv.visitLdcInsn(false);
		}
		else if(binaryExpression.getOp().getText().equals("<=")) {
			mv.visitJumpInsn(IF_ICMPLE, set_true);
			mv.visitLdcInsn(false);
		}
		else if(binaryExpression.getOp().getText().equals("&")) {
			mv.visitInsn(IAND);
		}
		else if(binaryExpression.getOp().getText().equals("|")) {
			mv.visitInsn(IOR);
		}
		else if(binaryExpression.getOp().getText().equals("*")) {
			if (binaryExpression.getE0().getTypeName().equals(TypeName.IMAGE)||
					binaryExpression.getE1().getTypeName().equals(TypeName.IMAGE)) {
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "mul",
						"(Ljava/awt/image/BufferedImage;I)Ljava/awt/image/BufferedImage;",
						false);
			} else {
				mv.visitInsn(IMUL);
			}
		}
		else if(binaryExpression.getOp().getText().equals("/")) {
			if(t1.equals(TypeName.INTEGER) && t2.equals(TypeName.INTEGER))
				mv.visitInsn(IDIV);
			else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.INTEGER))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "div", PLPRuntimeImageOps.divSig, false);
		}
		else if(binaryExpression.getOp().getText().equals("%")) {
			if(t1.equals(TypeName.INTEGER) && t2.equals(TypeName.INTEGER))
				mv.visitInsn(IREM);
			else if(t1.equals(TypeName.IMAGE) && t2.equals(TypeName.INTEGER))
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "mod", PLPRuntimeImageOps.modSig, false);			
		}		
		mv.visitJumpInsn(GOTO, l);
		mv.visitLabel(set_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(l);				
		return null;
	}

	@Override
	public Object visitBlock(Block block, Object arg) throws Exception {
		//TODO  Implement this
		Label l0 = new Label();
		for(Dec dec: block.getDecs()){
			switch(dec.getFirstToken().kind) {
				case KW_FRAME:
				case KW_IMAGE:
					mv.visitInsn(ACONST_NULL);
					mv.visitVarInsn(ASTORE, slot);
					break;
				default:			
					break;
			}
			dec.set_current_slot(slot);
			slot++;
		}
		cStart = l0;
		mv.visitLabel(l0);
		for(Statement stmt:block.getStatements()){
			stmt.visit(this, arg);
			if(stmt instanceof BinaryChain){
				mv.visitInsn(POP);
			}
		}
		Label l1 = new Label();
		cEnd = l1;
		mv.visitLabel(l1);
		for(Dec dec: block.getDecs()){
			dec.visit(this, mv);
		}
		return null;
	}

	@Override
	public Object visitBooleanLitExpression(BooleanLitExpression booleanLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(booleanLitExpression.getValue());
		return null;
	}

	@Override
	public Object visitConstantExpression(ConstantExpression constantExpression, Object arg) {		
		if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENHEIGHT)){
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFrame", "getScreenHeight", "()I", false);
		}
		else if(constantExpression.getFirstToken().isKind(Kind.KW_SCREENWIDTH)){
			mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeFrame", "getScreenWidth", "()I", false);
		}		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitDec(Dec declaration, Object arg) throws Exception {
		//TODO Implement this		
		mv.visitLocalVariable(declaration.getIdent().getText(),
		Type.getTypeName(declaration.getType()).getJVMTypeDesc(),
			null, cStart, cEnd, declaration. get_current_slot());
		return null;
	}

	@Override
	public Object visitFilterOpChain(FilterOpChain filterOpChain, Object arg) throws Exception {
		if(filterOpChain.getFirstToken().getText().equals("blur")) {
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "blurOp", PLPRuntimeFilterOps.opSig, false);			
		}
		else if(filterOpChain.getFirstToken().getText().equals("convolve")) {
			mv.visitInsn(ACONST_NULL);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "convolveOp", PLPRuntimeFilterOps.opSig,false);
		}
		else if(filterOpChain.getFirstToken().getText().equals("gray")) {
			if(bac==1){
				mv.visitInsn(DUP);
			}
			else{
				mv.visitInsn(ACONST_NULL);
			}			
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFilterOps.JVMName, "grayOp", PLPRuntimeFilterOps.opSig, false);
		}
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitFrameOpChain(FrameOpChain frameOpChain, Object arg) throws Exception {
		frameOpChain.getArg().visit(this, arg);
		if(frameOpChain.getFirstToken().getText().equals("xloc")) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getXVal", PLPRuntimeFrame.getXValDesc,false);
		}
		else if(frameOpChain.getFirstToken().getText().equals("yloc")) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "getYVal", PLPRuntimeFrame.getXValDesc,false);
		}
		else if(frameOpChain.getFirstToken().getText().equals("show")) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "showImage", PLPRuntimeFrame.showImageDesc,false);
		}
		else if(frameOpChain.getFirstToken().getText().equals("hide")) {
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "hideImage", PLPRuntimeFrame.hideImageDesc,false);
		}
		else if(frameOpChain.getFirstToken().getText().equals("move"))
		{
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeFrame.JVMClassName, "moveFrame", PLPRuntimeFrame.moveFrameDesc,false);
		}		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentChain(IdentChain identChain, Object arg) throws Exception {
		Integer partName = (Integer) arg;
		TypeName type = identChain.getTypeName();		
		if(partName==1){		
			if(identChain.getDec() instanceof ParamDec){				
				mv.visitVarInsn(ALOAD, 0);
				mv.visitFieldInsn(GETFIELD, className,
				identChain.getFirstToken().getText(),
				identChain.getTypeName().getJVMTypeDesc());
				switch(type) {
				case URL:
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className,
							"readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
					break;
				case FILE:
					mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className,
							"readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
					break;
				default:
					break;
				}
			} else {				
				if (identChain.getTypeName().equals(TypeName.INTEGER)
				 || identChain.getTypeName().equals(TypeName.BOOLEAN)) {
					mv.visitVarInsn(ILOAD, identChain.getDec().get_current_slot());
				} else {
					switch(type) {
					case URL:
						mv.visitVarInsn(ALOAD, identChain.getDec().get_current_slot());
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className,
								"readFromURL", PLPRuntimeImageIO.readFromURLSig, false);
						break;
					case FILE:
						mv.visitVarInsn(ALOAD, identChain.getDec().get_current_slot());
						mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className,
								"readFromFile", PLPRuntimeImageIO.readFromFileDesc, false);
						break;
					case FRAME:
					case IMAGE:
						mv.visitVarInsn(ALOAD, identChain.getDec().get_current_slot());
						break;
					default:
							break;
					}
				}
			}
		}
		else{
			 if(identChain.getTypeName().equals(TypeName.FILE)){				
				if(identChain.getDec() instanceof ParamDec){
					mv.visitVarInsn(ALOAD, 0);
					mv.visitFieldInsn(GETFIELD, className,
							identChain.getDec().getIdent().getText(), type.getJVMTypeDesc());
				}
				else {					
					mv.visitVarInsn(ALOAD, identChain.getDec().get_current_slot());
				}

				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageIO", "write",
						"(Ljava/awt/image/BufferedImage;Ljava/io/File;)Ljava/awt/image/BufferedImage;", 
						false);
			}
			else if(identChain.getTypeName().equals(TypeName.FRAME)){				
				mv.visitVarInsn(ALOAD, identChain.getDec().get_current_slot());
				mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeFrame.JVMClassName,
						"createOrSetFrame", PLPRuntimeFrame.createOrSetFrameSig, false);
				mv.visitInsn(DUP);
				mv.visitVarInsn(ASTORE, identChain.getDec().get_current_slot());
			}
			 if(!identChain.getTypeName().equals(TypeName.FRAME)){
			mv.visitInsn(DUP);
		}
			if (identChain.getTypeName().equals(TypeName.IMAGE)
			 || identChain.getTypeName().equals(TypeName.INTEGER)
					|| identChain.getTypeName().equals(TypeName.BOOLEAN)) {
				if (identChain.getDec() instanceof ParamDec) {
					
					mv.visitVarInsn(ALOAD, 0);
					mv.visitInsn(SWAP);
					mv.visitFieldInsn(PUTFIELD, className, identChain.getFirstToken().getText(),
							identChain.getTypeName().getJVMTypeDesc());
				} else {
					if (identChain.getTypeName().equals(TypeName.IMAGE)) {
						mv.visitVarInsn(ASTORE, identChain.getDec().get_current_slot());
					} 
					else if (identChain.getTypeName().equals(TypeName.INTEGER)
							|| identChain.getTypeName().equals(TypeName.BOOLEAN)) {
						mv.visitVarInsn(ISTORE, identChain.getDec().get_current_slot());
					}
				}
			}
		}		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIdentExpression(IdentExpression identExpression, Object arg) throws Exception {
		//TODO Implement this
		if(identExpression.getDec() instanceof ParamDec){			
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, className,
			identExpression.getFirstToken().getText(),
			 identExpression.getTypeName().getJVMTypeDesc());
		} else {			
			if (identExpression.getTypeName().equals(TypeName.INTEGER)
			|| identExpression.getTypeName().equals(TypeName.BOOLEAN)) {
				mv.visitVarInsn(ILOAD, identExpression.getDec().get_current_slot());
			}
			else if(identExpression.getTypeName().equals(TypeName.FILE)
			||identExpression.getTypeName().equals(TypeName.URL)
			||identExpression.getTypeName().equals(TypeName.IMAGE)
			||identExpression.getTypeName().equals(TypeName.FRAME)){
				mv.visitVarInsn(ALOAD, identExpression.getDec().get_current_slot());
			}
		}
		return null;
	}

	@Override
	public Object visitIdentLValue(IdentLValue identX, Object arg) throws Exception {
		//TODO Implement this
		if(identX.getDec() instanceof ParamDec){			
			mv.visitFieldInsn(PUTFIELD, className, identX.getFirstToken().getText(),
			 identX.getDec().getTypeName().getJVMTypeDesc());
		} else {
			if(identX.getDec().getTypeName().equals(TypeName.IMAGE)){
				mv.visitMethodInsn(INVOKESTATIC, "cop5556sp17/PLPRuntimeImageOps", "copyImage", 
						"(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;", false);
				mv.visitVarInsn(ASTORE, identX.getDec().get_current_slot());
			}
			else if (identX.getDec().getTypeName().equals(TypeName.INTEGER)
					|| identX.getDec().getTypeName().equals(TypeName.BOOLEAN)) {				
				mv.visitVarInsn(ISTORE, identX.getDec().get_current_slot());
			}
		}
		return null;
	}

	@Override
	public Object visitIfStatement(IfStatement ifStatement, Object arg) throws Exception {
		//TODO Implement this
		ifStatement.getE().visit(this, arg);
		Label l1 = new Label();
		mv.visitJumpInsn(IFEQ, l1);
		ifStatement.getB().visit(this, arg);
		mv.visitLabel(l1);
		return null;
	}

	@Override
	public Object visitImageOpChain(ImageOpChain imageOpChain, Object arg) throws Exception {
		imageOpChain.getArg().visit(this, arg);
		if (imageOpChain.getFirstToken().kind.equals(Kind.OP_WIDTH)) 
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getWidth", 
					PLPRuntimeImageOps.getWidthSig,false);
		else if (imageOpChain.getFirstToken().kind.equals(Kind.OP_HEIGHT)) 
			mv.visitMethodInsn(INVOKEVIRTUAL, PLPRuntimeImageIO.BufferedImageClassName, "getHeight", 
					PLPRuntimeImageOps.getHeightSig,false);
		else if (imageOpChain.getFirstToken().kind.equals(Kind.KW_SCALE)) {			
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageOps.JVMName, "scale", 
					PLPRuntimeImageOps.scaleSig,false);
		}		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitIntLitExpression(IntLitExpression intLitExpression, Object arg) throws Exception {
		//TODO Implement this
		mv.visitLdcInsn(new Integer(intLitExpression.value));
		return null;
	}

	@Override
	public Object visitParamDec(ParamDec paramDec, Object arg) throws Exception {
		//TODO Implement this
		//For assignment 5, only needs to handle integers and booleans
		cw.visitField(ACC_PUBLIC, paramDec.getIdent().getText(),
				Type.getTypeName(paramDec.getType()).getJVMTypeDesc(), null, null);
		TypeName type  = Type.getTypeName(paramDec.getType());
		switch(type) {
		case INTEGER: 
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(iterate);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", 
					false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "I");
			break;
		case BOOLEAN:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(iterate);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z",
					false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), "Z");
			break;
		case FILE:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/io/File");
			mv.visitInsn(DUP);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(iterate);
			mv.visitInsn(AALOAD);
			mv.visitMethodInsn(INVOKESPECIAL, "java/io/File", "<init>", "(Ljava/lang/String;)V", false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), type.getJVMTypeDesc());
			break;
		case URL:
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitLdcInsn(iterate);
			mv.visitMethodInsn(INVOKESTATIC, PLPRuntimeImageIO.className,
					"getURL", PLPRuntimeImageIO.getURLSig, false);
			mv.visitFieldInsn(PUTFIELD, className, paramDec.getIdent().getText(), type.getJVMTypeDesc());
			break;
		default: 
			break;
		}
		return null;
	}

	@Override
	public Object visitSleepStatement(SleepStatement sleepStatement, Object arg) throws Exception {		
		sleepStatement.getE().visit(this, arg);
		mv.visitInsn(I2L);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "sleep", "(J)V", false);		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitTuple(Tuple tuple, Object arg) throws Exception {
		for(Expression e: tuple.getExprList()){
			e.visit(this, arg);
		}		
		//assert false : "not yet implemented";
		return null;
	}

	@Override
	public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws Exception {
		//TODO Implement this
		Label l1 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		Label l2 = new Label();
		mv.visitLabel(l2);
		whileStatement.getB().visit(this, arg);
		mv.visitLabel(l1);
		whileStatement.getE().visit(this, arg);
		mv.visitJumpInsn(IFNE, l2);
		return null;
	}

}

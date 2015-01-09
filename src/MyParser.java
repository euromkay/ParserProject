import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;

import java_cup.runtime.Symbol;

class MyParser extends parser {
	private Lexer m_lexer;
	private Writer writer;
	private ErrorPrinter m_errors;
	private int m_nNumErrors;
	private String m_strLastLexeme;
	private boolean m_bSyntaxError = true;
	private int m_nSavedLineNum;
	private AddressManager am = new AddressManager();
	
	private SymbolTable symTab;

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public MyParser(Lexer lexer, ErrorPrinter errors) {
		m_lexer = lexer;
		symTab = new SymbolTable();
		m_errors = errors;
		m_nNumErrors = 0;
		writer = new Writer(symTab, am);
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public boolean Ok() {
		return m_nNumErrors == 0;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public Symbol scan() {
		Token t = m_lexer.GetToken();

		// We'll save the last token read for error messages.
		// Sometimes, the token is lost reading for the next
		// token which can be null.
		m_strLastLexeme = t.GetLexeme();

		switch (t.GetCode()) {
		case sym.T_ID:
		case sym.T_ID_U:
		case sym.T_STR_LITERAL:
		case sym.T_FLOAT_LITERAL:
		case sym.T_INT_LITERAL:
			return new Symbol(t.GetCode(), t.GetLexeme());
		default:
			return new Symbol(t.GetCode());
		}
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public void syntax_error(Symbol s) {
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public void report_fatal_error(Symbol s) {
		m_nNumErrors++;
		if (m_bSyntaxError) {
			m_nNumErrors++;

			// It is possible that the error was detected
			// at the end of a line - in which case, s will
			// be null. Instead, we saved the last token
			// read in to give a more meaningful error
			// message.
			m_errors.print(Formatter.toString(ErrorMsg.syntax_error,
					m_strLastLexeme));
		}
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public void unrecovered_syntax_error(Symbol s) {
		report_fatal_error(s);
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public void DisableSyntaxError() {
		m_bSyntaxError = false;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public void EnableSyntaxError() {
		m_bSyntaxError = true;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public String GetFile() {
		return m_lexer.getEPFilename();
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public int GetLineNum() {
		return m_lexer.getLineNumber();
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public void SaveLineNum() {
		m_nSavedLineNum = m_lexer.getLineNumber();
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public int GetSavedLineNum() {
		return m_nSavedLineNum;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoProgramStart() {
		// Opens the global scope.
		symTab.openScope();
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoProgramEnd() {
		symTab.closeScope();
		writer.finish();
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoVarDecl(boolean statik, Type t, String name, Vector<STO> arraySTOs, STO init) {
		
		STO error = hasError(arraySTOs);
		if(error != null)
			return error;
		
		t = Type.mergeType(t, arraySTOs);
		
		if (symTab.accessLocal(name) != null) 
			return generateError(ErrorMsg.redeclared_id, name);
		if(init != null){
			if(init.isError())
				return init;
			if(!init.getType().isAssignable(t)){
				return generateError(ErrorMsg.error8_Assign, init.getType().getName(), t.getName());
			}
		}
		
		
		return varHelper(statik, t, name);
	}
	
	STO DoVarDecl(boolean statik, Type t, String name, Vector<STO> arraySTOs, Vector<STO> ctrs) {
		STO error = hasError(arraySTOs);
		if(error != null)
			return error;
		
		t = Type.mergeType(t, arraySTOs);
		if (symTab.accessLocal(name) != null) 
			return generateError(ErrorMsg.redeclared_id, name);
		
		//TODO constructor call checks
			
		return varHelper(statik, t, name);
	}
	
	private STO varHelper(boolean statik, Type t, String name){
		VarSTO v = new VarSTO(name, t);
		if(statik)
			v.setStatic();
		if(t.isArray())
			v.setIsModifiable(false);
		symTab.insert(v);
			
		return v;
	}
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoIterationVarDecl(VarSTO iter, STO arr) {
		String error;
		if (symTab.accessLocal(iter.getName()) != null) {
			m_nNumErrors++;
			error = Formatter.toString(ErrorMsg.redeclared_id, iter.getName());
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		if(!(arr.getType() instanceof ArrayType)){
			m_nNumErrors++;
			error = ErrorMsg.error12a_Foreach;
			m_errors.print(error);
			return new ErrorSTO(error);
		}			
		ArrayType exprType = (ArrayType) arr.getType();
		Type iterType = iter.getType();
		if(iter.isRef()){
			if(!iterType.isEquivalent(exprType.getSubtype())){
				m_nNumErrors++;
				error = Formatter.toString(ErrorMsg.error12r_Foreach, exprType.getSubtype().getName(), iter.getName(), iterType.getName());
				m_errors.print(error);
				return new ErrorSTO(error);
			}
		}
		else{
			if(!iterType.isAssignable(exprType.getSubtype())){
				m_nNumErrors++;
				error = Formatter.toString(ErrorMsg.error12v_Foreach, exprType.getSubtype().getName(), iter.getName(), iterType.getName());
				m_errors.print(error);
				return new ErrorSTO(error);
			}
		}
		
		symTab.insert(iter);
		
		return new ExprSTO("foreach(" + iter.getType().getName() + " " + iter.getName() + " : " + arr.getName() + ")");
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoExternDecl(Type input_t, String name, Vector<STO> arraySTOs) {
		if(hasError(arraySTOs) != null)
			return;
		
		Type t = Type.mergeType(input_t, arraySTOs);
		VarSTO sto = new VarSTO(name, t);
		symTab.insert(sto);
		sto.setAddress(new Address(name));
		
		
		String arrayExpr = "";
		for(STO s: arraySTOs){
			arrayExpr += s.getName();
		}
		writer.comment("extern " + input_t.toString() + name + arrayExpr);
	}

	private STO hasError(Vector<STO> list){
		for(STO s: list){
			if(s.isError())
				return s;
		}
		return null;
	}
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoBinaryExpr(STO a, Operator o, STO b) {
		if(a instanceof ErrorSTO)
			return a;
		if(b instanceof ErrorSTO)
			return b;
		STO result = o.checkOperands(a, b);
		printErrorSTO(result);

		if(result.isError())
			return result;
		
		if(!(o.getName().equals(Operator.AND) || o.getName().equals(Operator.OR)))
			WriteBinaryExpr(a, o, b, result);
		
		return result;
	}
	
	private void printErrorSTO(STO result){
		if (result instanceof ErrorSTO) {
			m_nNumErrors++;
			m_errors.print(result.getName()); // maybe?
		}
	}
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	public STO DoNotOp(STO a) {
		if(a instanceof ErrorSTO)
			return a;
		
		Type aType = a.getType();
		
		if(aType instanceof BoolType){
			STO s = BoolType.getSTO(a);
			return s;
		}

		return generateError(ErrorMsg.error1u_Expr, aType.getName(), "!", BoolType.NAME);
	}
	
	
	/*public STO DoIncdDecOp(STO a, Operator o){
		STO result = null;
		Type aType = a.getType();
		if(!(aType instanceof NumericType))
			result = new ErrorSTO(Formatter.toString(ErrorMsg.error2_Type, aType.getName(), o.getName()));
		
		else if(!a.isModLValue()){
			result = new ErrorSTO(Formatter.toString(ErrorMsg.error2_Lval, aType.getName(), o.getName()));
		}
		else{
			result = new ExprSTO(UNKNOWN_S, new RValType(UNKNOWN_S, UNKNOWN_I));
		}
		printErrorSTO(result);
		
		return result;
	}*/

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoConstDecl(boolean statik, Type t, String name, STO init) {
		if(init.isError())
			return init;

		if (symTab.accessLocal(name) != null) 
			return generateError(ErrorMsg.redeclared_id, name);

		if(!t.isAssignable(init.getType()))
			generateError(ErrorMsg.error8_Assign, init.getType().getName(), t.getName());
		
		if(!init.isConst())
			generateError(ErrorMsg.error8_CompileTime, name);
		
		ConstSTO c = new ConstSTO(name, t, ((ConstSTO) init).getValue());
		c.setIsAddressable(true);
		c.setSTOInit(init);
		if(statik)
			c.setStatic();
		
		symTab.insert(c);
		
		return c;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoTypedefDecl(Vector<String> lstIDs, Type type) {
		for (int i = 0; i < lstIDs.size(); i++) {
			String id = lstIDs.elementAt(i);

			if (symTab.accessLocal(id) != null) {
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
			}

			StructdefSTO sto = new StructdefSTO(id, type.newType());
			symTab.insert(sto);
		}
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoStructdefDecl1(String id) {
		if (symTab.accessLocal(id) != null) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
		}
	
		StructdefSTO sto = new StructdefSTO(id, new StructType(id)); 
		symTab.setStructSTO(sto);
		symTab.insert(sto);
	}
	
	public void DoStructVarCheck(Vector<STO> vars){
		StructdefSTO struct = symTab.getStruct();
		String name = struct.getName();
		
		ArrayList<String> list = new ArrayList<String>();
		Integer structSize = 0;
		for(STO s: vars){
			String id = s.getName();
			
			String t = s.getName();
			s.setName("." + name + "_" + t);
			
			if(list.contains(id))
				generateError(ErrorMsg.error13a_Struct, id);
			else{
				list.add(id);
				s.setOffset(structSize.toString());
				structSize =+ s.getType().getSize();
			}
		}
		
		StructType sType = struct.getStructType();
		sType.setStructVars(vars, structSize);	
	}
	
	void DoConstructorCheck(Vector<STO> ctorList){
		StructdefSTO struct = symTab.getStruct();
		StructType structType = symTab.getStruct().getStructType();
		
		String structName = struct.getName();
		
		ArrayList<String> funcs = new ArrayList<String>();
		
		boolean hasDestruct = false;
		
		for(STO f: ctorList){
			if(f.isError())
				return;
			
			String name = f.getName();//full mangled function name
			if(name.startsWith("~")){  //destructor
				String destructorName = name.substring(1);
				if(!structName.equals(destructorName)){
					generateError(ErrorMsg.error13b_Dtor, destructorName, structName);
					continue;
				}
				if(hasDestruct){
					generateError(ErrorMsg.error9_Decl);
					continue;
				}
				hasDestruct = true;
			}
			else{
				String baseName = ((FuncSTO) f).getBaseName();
				if(!structName.equals(baseName)){
					generateError(ErrorMsg.error13b_Ctor, baseName, structName);
					continue;
				}
				if(funcs.contains(name)){
					generateError(ErrorMsg.error9_Decl);
					continue;
				}
				funcs.add(name);
			}
			
			structType.addFunc(f);
		}
	}
	
	
	void DoStructFuncCheck(Vector<STO> funcs) {
		StructdefSTO struct = symTab.getStruct();
		
		ArrayList<String> varList = new ArrayList<String>();
		for(STO s: struct.getStructType().getVars()){
			varList.add(s.getName());
		}
		
		ArrayList<String> funcList = new ArrayList<String>();
		
		for(STO func_s: funcs){
			if(func_s.isError())
				return;
			FuncSTO func = (FuncSTO) func_s;
			if(varList.contains(func.getBaseName())){
				generateError(ErrorMsg.error13a_Struct, func.getBaseName());
			}
			else if(funcList.contains(func.getName())){
				generateError(ErrorMsg.error9_Decl);
			}else{
				varList.add(func.getName());
			}
		}
		
		symTab.setStructSTO(null);
	}
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO ValidArray(STO s) {
		if(!(s instanceof ConstSTO)){
			m_nNumErrors++;
			m_errors.print(ErrorMsg.error10c_Array);
			return new ErrorSTO(ErrorMsg.error10c_Array);
		}
		if(!(s.getType().isInt())){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error10i_Array, s.getType().getName()));
			return new ErrorSTO(ErrorMsg.error10i_Array);
		}	
		if(((ConstSTO)s).getValue().compareTo(BigDecimal.ONE) < 0){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error10z_Array, ((ConstSTO)s).getIntValue()));
			return new ErrorSTO(ErrorMsg.error10z_Array);
		}
		
		return s;
	}

	FuncSTO DoFuncDecl_1(String id) {
		return DoFuncDecl_1(symTab.getStruct().getType(), false, id);
	}
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	FuncSTO DoFuncDecl_1(Type t, boolean ref, String id) {
		if (symTab.accessLocal(id) != null) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
		}
		
		FuncSTO sto = new FuncSTO(id, t);
		
		symTab.insert(sto);

		symTab.openScope();
		symTab.setFunc(sto);
		
		return sto;
	}
	
	void DoFormalParams(Vector<VarSTO> params) {
		FuncSTO func = symTab.getFunc();
		if (func == null) {
			m_nNumErrors++;
			m_errors.print("internal: DoFormalParams says no proc!");
		}
		for(VarSTO v: params){
			symTab.insert(v);
		}
		func.setParameters(params);
	}
	// ----------------------------------------------------------------
	// PHASE1.6B //Makesure statements in skeleton code have nonvoid or non null EXprSTO 
	// v is the statement list
	// ----------------------------------------------------------------
	STO DoFuncDecl_2(Vector<String> statements){
		return DoFuncDecl_2(false, new VoidType(), statements);
	}
	STO DoFuncDecl_2(boolean extern, Type funType, Vector<String> statements) {
		
		
		int rets = 0;//return statements
		//retExpr is each statement that might be a return (type);
		for(String retExpr: statements){
			if(retExpr.startsWith("return")){
				rets ++;
			}
		}
		if(rets == 0 && !(funType instanceof VoidType)){
			if(!extern)
				return generateError(ErrorMsg.error6a_Return_expr);
		}
		STO s = null;
		if(!(s instanceof ErrorSTO))
			s = symTab.getFunc();
		symTab.closeScope();
		symTab.setFunc(null);
		
		return s;
	}

	// ----------------------------------------------------------------
	// PHASE1.6A
	// ----------------------------------------------------------------
	String DoReturn(STO sto){
		String error;
		STO s = sto;
		if(sto.isError())
			return "";
		
		FunctionPointerType fs = (FunctionPointerType) symTab.getFunc().getType();
		Type neededType = fs.getReturnType();
		Type givenType = s.getType();
		if(fs.isRef()){
			if(!fs.getReturnType().isEquivalent(s.getType())){
				m_nNumErrors++;
				error = Formatter.toString(ErrorMsg.error6b_Return_equiv, givenType.getName(), neededType.getName());
				m_errors.print(error);
				s = new ErrorSTO(error);
			}
			else if(!sto.isModLValue() && !(sto instanceof VoidSTO)){
				m_nNumErrors++;
				error = ErrorMsg.error6b_Return_modlval;
				m_errors.print(error);
				s = new ErrorSTO(error);
			}
		}
		else{
			if(!fs.getReturnType().isAssignable(s.getType())){
				m_nNumErrors++;
				error = Formatter.toString(ErrorMsg.error6a_Return_type, givenType.getName(), neededType.getName());
				m_errors.print(error);
				s = new ErrorSTO(error);
			}
		}
		
		return "return " + sto.getName();
	}
	
	// ----------------------------------------------------------------
	// PHASE1.6B
	// ----------------------------------------------------------------
	Type DetermineReturnType(Type typ, Vector<String> modList){
			return typ;
		}
		
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoBlockOpen() {
		// Open a scope.
		symTab.openScope();
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoBlockClose() {
		symTab.closeScope();
	}
	
	// ----------------------------------------------------------------
	//	CHECK 19
	// ----------------------------------------------------------------
	STO DoSizeOf(STO sto){
		if(sto instanceof ErrorSTO)
			return sto;
		if(!sto.getIsAddressable()){
			m_nNumErrors++;
			m_errors.print(ErrorMsg.error19_Sizeof);
			return new ErrorSTO(ErrorMsg.error19_Sizeof);
		}
		
		return new ConstSTO("size of " + sto.getName(), new IntType(), sto.getType().getSize().toString());
	}
	
	STO DoSizeOf(Type t){
		return new ConstSTO("size of " + t.getName(), new IntType(), t.getSize().toString());
	}
	
	// ----------------------------------------------------------------
	//	CHECK 20
	// ----------------------------------------------------------------
	STO DoCast(Type t, STO s){
		
		if(s instanceof ErrorSTO)
			return s;
		
		if(!(t instanceof NumericType) && !(t instanceof BoolType) && !(t instanceof PointerType)){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error20_Cast,s.getType().getName(), t.getName()));
			return new ErrorSTO(ErrorMsg.error20_Cast);
		}
		
		if(s instanceof ConstSTO){
			ConstSTO csto = ((ConstSTO) s);
			if(t instanceof BoolType){
				if(csto.getValue() != BoolType.FALSE)
					return new ConstSTO(s.getName(), t, BoolType.TRUE);
			}
			else if(!(t instanceof FloatType)){
				String val = csto.getValue().toString();
				int i = val.indexOf(".");
				return new ConstSTO(s.getName(), t, val.substring(0, i));
			}
			return new ConstSTO(s.getName(), t, ((ConstSTO) s).getValue());
		}
		
		return new ExprSTO(s.getName(), t);
	}
	// ----------------------------------------------------------------
	//	
	// ----------------------------------------------------------------
	STO DoAssignExpr(STO stoLeft, STO stoRight) {
		
		if(stoLeft instanceof ErrorSTO || stoRight instanceof ErrorSTO)
			return new ErrorSTO(stoLeft.getName());

		
		//Phase 1 Check #3a where stoDes is NOT a modifiable L-Val IS THIS IT?>
		if (!stoLeft.isModLValue() || stoLeft.getType() instanceof ArrayType) {
			return generateError(ErrorMsg.error3a_Assign);
		}
		Type lType = stoLeft.getType();
		Type rType = stoRight.getType();
		if(!(rType.isAssignable(lType)))
			return generateError(ErrorMsg.error3b_Assign, rType.getName(), lType.getName());
			
		
		return stoLeft;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoFuncCall(STO sto, Vector<STO> paramList) {
		if(sto.isError())
			return sto;
		
		int prev = m_nNumErrors;
		
		if (!(sto.getType() instanceof FunctionPointerType)) 
			return generateError(ErrorMsg.not_function, sto.getName());
		

		FunctionPointerType fsto = (FunctionPointerType) sto.getType();
		
		if(paramList.size() != fsto.getNumParams()){	//Check 5, number params expected and passed in don't match
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error5n_Call, paramList.size(), fsto.getNumParams()));
		}
		
		for(int i = 0; i < paramList.size() && i < fsto.getNumParams(); i++){
			VarSTO parameter = fsto.get(i);
			boolean isRef = parameter.isRef();
			STO argument = paramList.get(i);
			
			if(isRef){ //Checking parameter declared as pass-by-reference(&) and corresponding arg types
				if(!parameter.getType().isEquivalent(argument.getType())){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error5r_Call, argument.getType().getName(), parameter.getName(), parameter.getType().getName()));
				}
				else if(!(argument.isModLValue()) ){//&& !(argument.getType() instanceof ArrayType)){ 456
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error5c_Call, argument.getName(), argument.getType().getName()));
				}
			}
			else{	//Checking parameter types against those being passed in
				if(!parameter.getType().isAssignable(argument.getType())){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error5a_Call, parameter.getType().getName(), argument.getName(), argument.getType().getName()));
				}
			}
				
		}
		
		if(sto.getIsModifiable() || fsto.getReturnType() instanceof PointerType){
			return new VarSTO(fsto.getName(), fsto.getReturnType());
		}
		
		if(prev != m_nNumErrors)
			return new ErrorSTO("");
		
		return new ExprSTO(fsto.getName(), fsto.getReturnType());
	}
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	
	Type DoFuncPtr(Type rt, Vector<VarSTO> lst, boolean ref){
		FunctionPointerType ft = new FunctionPointerType(rt);
		ft.setRef(ref);
		ft.setParameters(lst);
		
		
		return ft;
	}
	
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoDesignator2_Dot(STO sto, String memberID) {
		if(sto instanceof ErrorSTO)
			return sto;
		
		//Good place to do the struct checks
		String error = null;
		
		if (!(sto.getType() instanceof StructType)){
			error = (Formatter.toString(ErrorMsg.error14t_StructExp, sto.getType().getName()));
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		else{	
			//System.out.println(sto.getName());
			StructType t = (StructType) sto.getType();
			
			if(sto.getName().equals("this")){
				STO thisSTO = symTab.isInStructScope(memberID);
				if(thisSTO == null){
					error = (Formatter.toString(ErrorMsg.error14c_StructExpThis, memberID));
					m_nNumErrors++;
					m_errors.print(error);
					return new ErrorSTO(error);
				}
				else 
					return thisSTO;
			}else{
				for(STO possible: t.getFields()){
					if(possible instanceof FuncSTO){
						if(possible.getName().equals("." + t.getName() + "_" + memberID))
							return possible;
					}
					if(possible.getName().equals(memberID))
						return possible;
				
				}
			}
		}	
		
		
		error = (Formatter.toString(ErrorMsg.error14f_StructExp, memberID, sto.getType().getName()));
		m_nNumErrors++;
		m_errors.print(error);
		return new ErrorSTO(error);
		
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoDesignator2_Array(STO a, STO e) {
		if(e instanceof ErrorSTO || a instanceof ErrorSTO)
			return e;
		
		if(!(e.getType().isInt())){
			String error = (Formatter.toString(ErrorMsg.error11i_ArrExp, e.getType().getName()));
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		if(!a.getType().isArray() && !a.getType().isPointer()){
			String error = (Formatter.toString(ErrorMsg.error11t_ArrExp, a.getType().getName()));
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		
		if(e instanceof ConstSTO && a.getType() instanceof ArrayType){
			ConstSTO es = (ConstSTO) e;
			int aLength = Integer.parseInt(((ArrayType) a.getType()).getLength());
			if(es.getIntValue() > aLength || aLength < 0){
				m_nNumErrors++;
				String error = Formatter.toString(ErrorMsg.error11b_ArrExp, es.getIntValue(), aLength);
				m_errors.print(error);
					return new ErrorSTO(error);
			}
				
		}
		
		ArrointType tt = (ArrointType) a.getType();
		return new VarSTO("", tt.getSubtype());
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoDesignator3_ID(String strID) {
		STO sto;

		if ((sto = symTab.access(strID)) == null) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.undeclared_id, strID));
			sto = new ErrorSTO(strID);
		}

		return sto;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoDesignator4_ID(String strID) {
		STO sto;
		
		if ((sto = symTab.accessGlobal(strID)) == null)
			return generateError(ErrorMsg.error0g_Scope, strID);
		

		return sto;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	Type DoStructType_ID(String strID) {
		STO sto;

		if ((sto = symTab.access(strID)) == null)
			return generateError(ErrorMsg.undeclared_id, strID).getType();
		

		if (!sto.isStructdef()) 
			return generateError(ErrorMsg.not_type, sto.getName()).getType();
	
		return sto.getType();
	}
	
	

	public void DoExprBoolCheck(STO a) {
		if(a instanceof ErrorSTO)
			return;
		Type aType = a.getType();
		if(!aType.isBool()){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error4_Test, aType.getName()));
		}
				
		
	}
	
	// ----------------------------------------------------------------
	// PHASE 1.7
	// EXIT EXPR TYPE OF INT 
	// ----------------------------------------------------------------
	public String DoExprIntCheck(STO a) {
		if(a instanceof ErrorSTO)
			return a.getName();
		Type aType = a.getType();
		if (!aType.isInt()){
			return generateError(ErrorMsg.error7_Exit, aType.getName()).getName();
		}
		else		
			return "exit(" + a.getName() + ")";
	}

	// ----------------------------------------------------------------
	// PHASE 1.5.2
	// DETECT AN ILLEGAL FUNCTION CALL: 
	// A parameter is declared as pass-by-reference (using the &) and the
	// corresponding argument's type is not equivalent to the parameter type
	// ----------------------------------------------------------------
	STO DoRefCheck(Type _1, Boolean _2, String _3){
		VarSTO result = new VarSTO(_3, _1);
		result.setRef(_2);
		return result;
	}
	STO DoDeref(STO des){
		if(des instanceof ErrorSTO)
			return des;
		String error = null;
		
		if(!(des.getType() instanceof PointerType && !(des.getType() instanceof NullPointerType))){
			error = Formatter.toString(ErrorMsg.error15_Receiver, des.getType().getName());
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		else{
			PointerType pt = (PointerType) des.getType();
			return new VarSTO(des.getName(), pt.getSubtype());
		}
		
	}
	
	public STO DoAddressOf(STO s) {
		if(s instanceof ErrorSTO)
			return s;
		String error = null;
		if(!s.getIsAddressable()){
			m_nNumErrors++;
			error = Formatter.toString(ErrorMsg.error18_AddressOf, s.getType().getName());
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		return new ExprSTO(s.getName(), new PointerType(s.getType()));
	}
	
	STO DoArrowDeref(STO sto, String id){
		if(sto instanceof ErrorSTO)
			return sto;
		
		
		String error = null;
		if(!(sto.getType() instanceof PointerType)){
			error = Formatter.toString(ErrorMsg.error15_ReceiverArrow, sto.getType().getName());
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		PointerType pt = (PointerType) sto.getType();
		
		if(!(pt.getSubtype() instanceof StructType)){
			error = Formatter.toString(ErrorMsg.error15_ReceiverArrow, sto.getType().getName());
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		
		StructType struct = (StructType) pt.getSubtype();
		
		for(STO e : struct.getFields()){
			if(e.getName().equals(id))
				return e;		
		}
					
		error = Formatter.toString(ErrorMsg.error14f_StructExp, id, sto.getType().getName());
		m_nNumErrors++;
		m_errors.print(error);
		return new ErrorSTO(error);
	}

	public STO checkConstExpr(String id, STO _4) {
		if(_4.isError())
			return _4;
		
		else if(!_4.isConst()){
			m_nNumErrors++;
			String error = Formatter.toString(ErrorMsg.error8_CompileTime, id);
			m_errors.print(error);
			return new ErrorSTO(error);
		}else
			return _4;
	}
	
	public Type TransformType(int arraySize, int pointerSize, Type t){

   	 	for(int i = 0; i < pointerSize; i++){
   	 		t = new PointerType(t);
   	 	}

   	 	if(arraySize > 0)
   	 		t = new ArrayType(t, arraySize);
   	 	
   	 	return t;
	}
	
	public static final boolean BREAK = true, CONTINUE = false;
	
	
	
	

	public Vector<STO> ChangeTypes(Type _1, InfoBlock _2) {
		Vector<STO> list = new Vector<STO>();
		for(InfoBlock.Triplet t: _2.getList()){
			VarSTO s = new VarSTO(t.name, TransformType(t.arraySize, t.pointer, _1));
			list.add(s);
			this.symTab.insert(s);
		}
		return list;
	}
	
	public void DoAllocCheck(String s, STO x){
		if(x instanceof ErrorSTO)
			return;
		boolean isDelete = s.equals("delete");
		if(!x.isModLValue()){
			m_nNumErrors++;
			if(!isDelete)
				m_errors.print(ErrorMsg.error16_New_var);
			else
				m_errors.print(ErrorMsg.error16_Delete_var);
		}
		if(!(x.getType() instanceof PointerType)){
			m_nNumErrors++;
			if(!isDelete)
				m_errors.print(Formatter.toString(ErrorMsg.error16_New, x.getType().getName()));
			else
				m_errors.print(Formatter.toString(ErrorMsg.error16_Delete, x.getType().getName()));
		}
	}

	public Vector<VarSTO> DoMultiParamCheck(Vector<VarSTO> _1) {
		ArrayList<String> list = new ArrayList<String>();
		for(VarSTO v: _1){
			String name = v.getName();
			if(list.contains(name)){
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, name));
			}
			else
				list.add(name);
		}
		return _1;
	}

	public Type SetQualIdent(STO s) {
		Type t =  s.getType();
		t.setName(s.getName());
		return t;
	}

	private ErrorSTO generateError(String message, String ... parameters){
		m_nNumErrors++;
		String error = null;
		int size = parameters.length;
		if(size == 0){
			error = message;
		}else if(size == 1){
			error = Formatter.toString(message, parameters[0]);
		}else if(size == 2){
			error = Formatter.toString(message, parameters[0], parameters[1]);
		}else if(size == 3){
			error = Formatter.toString(message, parameters[0], parameters[1], parameters[2]);
		}
		m_errors.print(error);
		return new ErrorSTO(error);
	}
	
	/************************ START ASSEMBLY WRITING *************************/

	public String WriteVarDeclComment(STO left, STO right){
		String label;
		String left_t = left.getType() + " " + left.getName();
		if(left.isConst())
			left_t = "const " + left_t;
		
		String right_t = "";
		if(right != null)
			right_t = " = " + right.getName();
		
		label = left_t + right_t;
		writer.comment(label);
		
		return label;
	}
	public String WriteVarDeclComment(STO left, Vector<STO> cParams){
		String label;
		if(cParams == null){
			label = left.getType() + " " + left.getName();
			writer.comment(label);
		}
		else{ 
			label = left.getType() + " " + left.getName() + " : (";
			for(STO s: cParams){
				label += s.getName();
				if(cParams.indexOf(s) + 1 != cParams.size())
					label += ", ";
				else
					label += ")";
			}
			writer.comment(label);
		}
		return label;
	}
	
	public void WriteVarDecl(STO left){
		if(left.isError())
			return;
		
		if(writer.isGlobal()){
			
			writer.write(Template.DATA);
			writer.write(Template.ALIGN, "4");
		
			//Initialize Default Null Value
			VarSTO left_s = (VarSTO) left;
			left_s.setAddress(new Address(left_s.getName()));
				
				
			String skip;
			if(isFloat(left_s))
				skip = Template.FLOAT_VAR_DECL;
			else if (left_s.getType().isArray())
				skip = Template.SKIP;
			else
				skip = Template.INT_VAR_DECL;

			writer.writeLater = true;
			String val = "0";
			//if theres some sort of initializatoin
			
			Type lType = left.getType();
			if(lType.isArray()){
				val = lType.getSize().toString();
					
				Address lAdd = am.getAddress();
				Address _4 = am.getAddress(), _0 = am.getAddress(); 
						
				writer.set("4", _4);
				writer.set("0", _0);
				//clears our global arrays, setting contents to 0
				for(int i = 0; i < lType.getSize(); i+=4){
					writer.store(lAdd, _0);
					writer.addOp(lAdd, _4, lAdd);
				}
						
				lAdd.release();
				_4.release();
				_0.release();
						
			}

			writer.writeLater = false;
			writer.write(skip, left.getName(), val);

		//local
		}else{
			if(left.isStatic()){
				String name = "." + symTab.getFunc().getName() + "_" + left.getName();
				String nameBool = name + "_bool";
				final String alreadyInitialized = name + "_finish";

				left.setAddress(new Address(name));
					
				writer.newLine();
				Address bool_a = am.getAddress();
				
				writer.set(nameBool, bool_a);
				writer.load(bool_a, bool_a);
				writer.cmp(bool_a, Address.G0);
				
					
				writer.bne(alreadyInitialized);
				writer.newLine();
					
				writer.changeSection(Writer.DATA);
				writer.write(Template.ALIGN, "4");
				String type;
				if(left.getType() instanceof FloatType)
					type = Template.FLOAT_VAR_DECL;
				else
					type = Template.INT_VAR_DECL;
					
					
				writer.write(type, name, "0");
				writer.write(type, nameBool, "0");
					
				writer.changeSection(Writer.TEXT);
					
				Address _1 = am.getAddress();
					
				writer.set(nameBool, bool_a);
				writer.set("1", _1);
				writer.store(_1, bool_a);
				writer.label(alreadyInitialized);
				writer.newLine();

				_1.release();
				bool_a.release();
					
			}
			else{
				writer.addSTO(left);
			}
		}

		writer.newLine();
		
		
	}
	
	public void WriteVarInit(STO left, STO right){
		if(right == null || right.isError())
			return;
		
		if(isIntToFloat(left, right))
			fitos(right, left);
		else
			store(left, right);
		
		String right_t = "";
		if(right != null)
			right_t = " = " + right.getName();
		
		writer.comment(left.getName() + right_t);
	}
	
	public void WriteVarInit(STO left, Vector<STO> cParams){
		//TODO
	}
	
	private void store(STO to, STO from){
		to.store(from, writer);
	}
	
	private void store(STO to, Address from){
		to.store(from, writer);
	}
	
	private boolean isFloat(STO s){
		return s.getType() instanceof FloatType;
	}
	
	private void writeAddress(STO sto, Address a1){
		sto.writeAddress(a1, writer);
	}
	
	private void writeVal(STO sto, Address a1) {
		sto.writeVal(a1, writer);
	}

	private boolean isIntToFloat(STO f, STO i){
		return f.getType() instanceof FloatType && i.getType() instanceof IntType;
	}

	private void fitos(STO int_, STO float_){
		Address fReg = new Address("%f0");
		Address tempAdd = am.getAddress();
		writeVal(int_, fReg);
		writer.fitos(fReg, fReg);
		writeAddress(float_, tempAdd);
		writer.store(fReg, tempAdd);
		
		tempAdd.release();
	}
	
	public Integer getCodeBlockVars(Vector<VarSTO> paramList){
		int size = 0;
		for(VarSTO s : paramList){
			size += s.getType().getSize();
		}
		return size;
	}
	
	public void WriteFuncDecl(String fname, Vector<VarSTO> paramList){
		int offset = 0;
		if(symTab.m_nLevel == 3){
			String structName = symTab.m_stkScopes.get(1).getLastStructName();
			fname = "."+ structName + "_" + fname;
			offset++;
		}
		//Write Function Comment
		writer.write(Template.LABEL_COMMENT, fname);
		
		//Write Function Header
		writer.changeSection(Writer.TEXT);
		writer.write(Template.GLOBAL, fname);
		writer.newLine();
		writer.label(fname);
		
		writer.set("SAVE." + fname, new Address("%g1")); 
		writer.write(Template.SAVE, Template.SP, Template.G1, Template.SP);
		writer.newLine();
		
		if(fname.equals("main"))
			initializeGlobals();
		
		String s = "%i";
		Address length_a = am.getAddress();
		
		for(int i = 0; i < paramList.size(); i++){
			VarSTO sto = (VarSTO) paramList.get(i);
			Address param_a = new Address(s + (i + offset));
			if(sto.isRef() || sto.getType() instanceof ArrayType || sto.getType() instanceof StructType){
				if(sto.getType() instanceof ArrayType){
					ArrayType aType = (ArrayType) sto.getType();
					sto.setRef(true);
					
					VarSTO v = new VarSTO("", new IntType());
					writer.addSTO(v);
					
					writer.set(aType.getLength(), length_a);
					v.store(length_a, writer);
					sto.setInit(v);
				}
				if(sto.getType() instanceof StructType)
					sto.setRef(true);
				sto.setAddress(param_a);
			}else{
				writer.addSTO(sto);
				sto.store(param_a, writer);
			}
		}
		
		length_a.release();
		
		writer.newLine();
	}
	
	private void initializeGlobals(){
		Address globalInit_a = am.getAddress();
		
		writer.set(Writer.GLOBAL_INIT, globalInit_a);
		writer.load(globalInit_a, globalInit_a);
		writer.cmp(globalInit_a, Address.G0);
		final String alreadyInitialized = ".globalFinish";
		writer.bne(alreadyInitialized);
		writer.writeGlobalInits();
		writer.newLine();
		
		Address _1_a = am.getAddress();
		writer.set(Writer.GLOBAL_INIT, globalInit_a);
		writer.set("1", _1_a);
		writer.store(_1_a, globalInit_a);
		writer.label(alreadyInitialized);
		writer.newLine();
		
		globalInit_a.release();
		_1_a.release();
	}

	
	public void WriteID(STO s_, String s){
		if(s_ instanceof FuncSTO)
			s_.setAddress(new Address(s));
		return;/*
		if(s_ instanceof FuncSTO)
			return;
		AddrSTO s = (AddrSTO) s_;
		s.setAddressOfInitialization(s.getName());
		//writer.ld(sto.getAddress(), dest);
		//writer.store(dest, TEMP_FP);*/
	}
	
	
	
	public void WriteFuncDeclFinish(String fname) {
		//writer.functionFinish();
		writer.write(Template.RET);
		writer.write(Template.RESTORE);	
		writer.newLine();
		if(symTab.m_nLevel == 2){
			String structName = symTab.m_stkScopes.get(1).getLastStructName();
			fname = "."+ structName + "_" + fname;
		}

		writer.save(fname, writer.getStackSize());
		writer.localVarSpace = 0;
	}

	public void WriteAssignExpr(STO left, STO right_s) {
		if(left.isError())
			return;
		VarSTO left_s = (VarSTO) left;
		Address left_a = am.getAddress();
		writeAddress(left, left_a);
		Address right_a;
		
		
		if(isIntToFloat(left_s, right_s)){
			right_a = new Address("%f0");
			fitos(right_s, left_s);
		}else{
			right_a = am.getAddress();
			if(right_s.getType() instanceof ArrayType)// || right_s instanceof FuncSTO)
				writeAddress(right_s, right_a);
			//else if(right_s instanceof FuncSTO){
				//writer.set(right_s.getName(), right_a);
			else
				writeVal(right_s, right_a);
		}
		writer.store(right_a, left_a);

		if(right_s instanceof FuncSTO){
			left_s.setInit(right_s);
			left_s.setInit2(((FuncSTO)right_s).getInit());
		}
		
		right_a.release();
		left_a.release();
		writer.comment(left_s.getName() + " = " + right_s.getName());
	}

	
	public void WriteIntLiteral(STO s){
		Address a = am.getAddress();
		writer.set(s.getName(), a);
		writer.addSTO(s);
		s.store(a, writer);
		
		a.release();
	}
	
	public int literalCount;
	
	public void WriteFloatLiteral(STO s) {
			
		writer.addSTO(s);
		int section = writer.getSection();
		writer.changeSection(Writer.RODATA);
		String label = "_float_" + s.getName() + literalCount++;
		writer.write(Template.FLOAT_VAR_DECL, label, s.getName());
		writer.newLine();
	
		Address a = am.getAddress();
		writer.changeSection(section);
		writer.set(label, a);
		writer.load(a, a);
		s.store(a, writer);
		
		a.release();
	}

	public static final String TRUE_S = "1", FALSE_S = "0";
	
	public void WriteBoolLiteral(STO s, String value) {
		
		//assured its local now
		
		Address a = am.getAddress();
		writer.addSTO(s);
		writer.set(value, a);
		s.store(a, writer);
		
		a.release();
	}
	
	public void WriteStringLiteral(String s) {
		writer.changeSection(Writer.RODATA);
		String label = "_string" + literalCount++;
		writer.write(Template.ASCIZ, label, "\""+s+"\"");
		writer.newLine();
		
		writer.changeSection(Writer.TEXT);
		
	}
	

	
	public boolean isInGlobalScope() {
		return writer.isGlobal();
	}
	
	public static final String BRANCH = ".branch";
	public void COut(STO s){
		if(s.isError())
			return;
		
		Type t = s.getType();
		if(t instanceof FloatType){
			s.writeVal(Address.F0, writer);
			writer.call("printFloat");
		}else{
			if(t instanceof BoolType){
				writer.set(Template.STRFORMAT, Address.O0);
				Address a = am.getAddress();
				s.writeVal(a, writer);
				writer.cmp(a, Address.G0);
				a.release();
				writer.bne(BRANCH + literalCount);
				
				//print false
				writer.set(Template.BOOLF, Address.O1);
				writer.ba(BRANCH+literalCount+".DONE");
				writer.label(BRANCH + literalCount);
				writer.set(Template.BOOLT, Address.O1);
				
				writer.label(BRANCH+literalCount++ + ".DONE");	
			}else if(t instanceof StringType){
				writer.set(Template.STRFORMAT, Address.O0);
				writer.set("_string"+(literalCount-1), Address.O1);
			}else {
				writer.set(Template.INTFORMAT, Address.O0);
				s.writeVal(Address.O1, writer);
			}
			writer.call("printf");
		}
	}

	public void WriteENDL() {
		writer.set(Template.STRFORMAT, Address.O0);
		writer.set("_endl", Address.O1);
		writer.call("printf");		
	}

	public void WriteBinaryExpr(STO a, Operator o, STO b, STO result){
		writer.comment(a.getName() + " " + o.getName() + " " + b.getName());
		
		writer.addSTO(result); // takes care of addressing it
		
		Address a1, a2, res_a;
		Boolean f_flag = false;
	
		Type aType = a.getType(), bType = b.getType();
		
		if(aType instanceof FloatType || bType instanceof FloatType ){
			a1  = Address.F0;
			a2  = Address.F1;
			res_a = Address.F0;
			f_flag = true;

			a.writeVal(a1, writer);
			b.writeVal(a2, writer);
			
			if(aType instanceof IntType){
				writer.fitos(a1, a1);
			}
			if(bType instanceof IntType){
				writer.fitos(a2, a2);
			}		
		}
		else{
			a1 = Address.O0;
			a2 = Address.O1;
			res_a = Address.O0;
			
			a.writeVal(a1, writer);
			if(a != b)
				b.writeVal(a2, writer);
		}
		o.writeSparc(a, b, result, a1, a2, res_a, f_flag, this);
		
			
		writer.newLine();
		
		
	}
	public static final boolean INC = true;
	public static final boolean DEC = false;
	public static void incDecHelper(STO s, Address a, boolean inc, boolean fFlag, Writer writer){
		
		if(s.getType() instanceof FloatType || s.getType() instanceof IntType){
			if(inc)
				writer.inc(a, fFlag);
			else
				writer.dec(a, fFlag);
		}
		else{
			PointerType t = (PointerType) s.getType();
			Address size_a = writer.getAddressManager().getAddress();
			Address sto_a = writer.getAddressManager().getAddress();
			
			
			writer.set(t.getSubtype().getSize().toString(), size_a);
			s.writeVal(sto_a, writer);
			if(inc)
				writer.addOp(sto_a, size_a, a);
			else
				writer.minusOp(sto_a, size_a, a);
			
			size_a.release();
			sto_a.release();
		}
		
	}
	
	public static final boolean OR_FLAG = true, AND_FLAG = false;
	
	public void WriteEqStmt_1(STO s, boolean flag){
		Address a = am.getAddress();
		writeAddress(s, a);
		writer.cmp(a, Address.G0);
		String str = ".eq" + literalCount++;
		labelList.add(str);
		if(flag == OR_FLAG)
			writer.bne(str);
		else//flag == AND_FLAG
			writer.be(str);
		
		a.release();
	}
	public static final String DONE = ".DONE";
	public void WriteEqStmt_2(STO s, STO result, boolean flag){
		Address a = am.getAddress();
		writer.addSTO(result);
		writer.ld(s.getAddress(), a);
		writer.cmp(a, Address.G0);
		String str = labelList.pop(), result1, result2;
		if(flag == OR_FLAG){
			writer.bne(str);
			result1 = "0";
			result2 = "1";
		}else{
			writer.be(str);
			result1 = "1";
			result2 = "0";
		}
		writer.set(result1, a);
		writer.ba(str+DONE);
		writer.label(str);
		writer.set(result2, a);
		writer.label(str+DONE);
		result.store(a, writer);
		
		a.release();
	}
	
	
	public void WriteSizeOf(STO s){
		Address a = am.getAddress();
		writer.set(((ConstSTO) s).getIntValue().toString(), a);
		writer.addSTO(s);
		s.store(a, writer);
		
		a.release();
	}
	
	public void WriteReturn(STO s){
		FuncSTO currentFunc = symTab.getFunc();
		FunctionPointerType fT = (FunctionPointerType) currentFunc.getType();
		
		if(s != null){
			if(fT.isRef()){
				writeAddress(s, Address.I0);
			}
			else{
				if(isIntToFloat(currentFunc, s)){
					writeVal(s, Address.F0);
					writer.fitos(Address.F0, Address.F0);
				}else
					writeVal(s, Address.I0);
			}
		}else
			writer.set(Address.G0, Address.I0);
		
		writer.returnstmt();		
	}
	
	public void WriteFuncCall(FuncSTO func, Vector<STO> args, STO fsto){
		if(fsto.isError())
			return;
		
		
		writer.addSTO(fsto);
		Vector<VarSTO> funcParams = ((FunctionPointerType) func.getType()).getParams();
		String o = "%o";
		
		int structOffset;
		if(func.isMemberFunction()){
			structOffset = 1;
		}else{
			structOffset = 0;
		}
		
		
		for(int i = 0; i < args.size(); i++){
			Address a = new Address(o+(i + structOffset));
			STO e = args.get(i);
			if(funcParams.get(i).isRef() || e.getType() instanceof ArrayType || e.getType() instanceof StructType){
				writeAddress(e, a);
			}
			else{
				if(isIntToFloat(e, funcParams.get(i))){
					VarSTO temp = new VarSTO("", new FloatType());
					writer.addSTO(temp);
					
					Address temp_a = am.getAddress();
					
					writeAddress(temp, temp_a);
					writeVal(e, Address.F0);
					writer.fitos(Address.F0, Address.F0);
					writer.store(Address.F0, temp_a);
					writer.ld(temp_a, a);
					
					temp_a.release();
				}else{
					writeVal(e, a);
				}
			}
			i++;
		}
		
		writer.call(func.getName());
		if(!(func.getReturnType() instanceof VoidType))
			fsto.store(Address.O0, writer);
	}
	/*
	public void WriteFuncCall(VarSTO v, Vector<STO> params, STO fsto){
		FuncSTO func = (FuncSTO) v.getInit();
		STO structSTO = v.getInit2();
		writer.addSTO(fsto);
		Vector<VarSTO> funcParams = ((FunctionPointerType) func.getType()).getParams();
		String o = "%o";
		
		int structOffset;
		if(func.isMemberFunction()){
			structOffset = 1;
			writeAddress(structSTO, Address.O0);
		}else{
			structOffset = 0;
		}
		
		
		for(int i = 0; i < params.size(); i++){
			STO e = params.get(i);
			if(funcParams.get(i).isRef() || e.getType() instanceof ArrayType || e.getType() instanceof StructType){
				e.writeAddress(o+(i + structOffset), writer);
			}
			else
				e.writeVal(o+(i + structOffset), writer);
			i++;
		}
		
		writer.call(func.getName());
		if(!(func.getReturnType() instanceof VoidType))
			writer.store(Template.O0, fsto.getAddress());
	}*/
	
	public void WriteNotOp(STO s, STO result){
		writer.addSTO(result);
		
		Address a = am.getAddress(), _1_a = am.getAddress();
		
		writeVal(s, a);
		writer.set("1", _1_a);
		writer.xorOp(a, _1_a, a);
		result.store(a, writer);
		
		_1_a.release();
		a.release();
	}
	
	Stack<String> labelList = new Stack<String>();
	public void WriteIfStatement(STO s){
		Address a = am.getAddress();
		s.writeVal(a, writer);
		writer.cmp(a, Address.G0);
		String label = ".iflabel" + literalCount++;
		labelList.push(label);
		writer.be(label);
		
		a.release();
	}
	
	public void WriteIfStatementEnd(){
		String s = labelList.pop();
		writer.ba(labelList.push(s+".DONE"));
		writer.label(s);
	}
	
	public void WriteEndStatementEnd(){
		String s = labelList.pop();
		writer.label(s);
	}
	
	public void WriteSpace(String s){
		writer.comment(s);
		writer.newLine();
	}
	
	public void WriteExit(STO s){
		writeVal(s, Address.O0);
		writer.call("exit");
		
	}
	
	public void DoCIn(STO input){
		Address a;
		if(input.getType() instanceof IntType){
			writer.call("inputInt");
			a = Address.O0;
		}else{
			writer.call("inputFloat");
			a = Address.F0;
		}
		input.store(a, writer);
		
	}
	Stack<String> whileForList = new Stack<String>();
	public STO WriteForWhileBegin(String s){
		Address a = am.getAddress();
		String label = "." + s + literalCount++;
		whileForList.add(label);
		
		ExprSTO c = new ExprSTO(whileForList.peek()+".counter", new IntType());
		symTab.insert(c);
		writer.addSTO(c);
		writer.set("0", a);
		writer.store(a, c.getAddress());
		
		writer.label(label);

		a.release();
		return c;
	}
	
	public void WriteForMiddle(STO s_index, STO s_array) {
		VarSTO index = (VarSTO) s_index, array = (VarSTO) s_array;
		STO counter = symTab.access(whileForList.peek()+".counter");
		ArrayType aT = ((ArrayType) array.getType());
		String length = aT.getLength().toString();
		Address length_a = am.getAddress();
		Address counter_a = am.getAddress();
		
		writer.set(length, length_a);
		counter.writeVal(counter_a, writer);
		writer.cmp(counter_a, length_a);
		writer.be(whileForList.peek() + DONE);
		
		length_a.release();
		counter_a.release();
		
		index.setInit(array);
		array.setInit2(index);
		//index.setAddress("ARRAY");
		if(index.isRef()){
			Address a = am.getAddress();
			writeVal(index, a);
			writer.addSTO(index);
			index.store(a, writer);
			a.release();
		}
	}
	
	public void WriteWhileMiddle(STO s){
		Address a = am.getAddress();
		writer.ld(s.getAddress(), a);
		writer.cmp(Address.G0, a);
		writer.be(whileForList.peek() + DONE);
		
		a.release();
	}
	
	public void WriteWhileEnd(){
		String str = whileForList.pop();
		writer.ba(str);
		writer.label(str + DONE);
		writer.newLine();
		literalCount++;
	}
	
	private static final String CHECK = "CHECK";
	
	public void WriteForEnd(){
		Address a = am.getAddress();
		writer.label(whileForList.peek() + CHECK);
		STO counter = symTab.access(whileForList.peek()+".counter");
		counter.writeVal(a, writer);
		writer.inc(a, false);
		writer.store(a, counter.getAddress());
		WriteWhileEnd();
		
		a.release();
	}

	public void WriteContBr(boolean b) {
		if(whileForList.size() == 0){
			if(b == BREAK){
				generateError(ErrorMsg.error12_Break);
			}else
				generateError(ErrorMsg.error12_Continue);
		}
		String s = whileForList.peek();
		if(!b)
			s += DONE;
		else
			s += CHECK;
		writer.ba(s);
		writer.newLine();
	}
	
	public void WriteAmpersand(STO given, STO result){
		if(result.isError())
			return;
		
		Address a = am.getAddress();
		writer.addSTO(result);
		
		given.writeAddress(a, writer);
		result.store(a, writer);
		
		a.release();
	}
	public void WriteDeRef(STO s, STO result){
		writer.addSTO(result);
		
		Address a = am.getAddress();
		s.writeVal(a, writer); //address in reg
		writer.load(a, a);  //value in reg now
		
		result.store(a, writer);
		
		a.release();
	}

	public void WriteArrayIndex(STO array, STO number, STO res) {
		if(res.isError())
			return;
		VarSTO result = (VarSTO) res;
		result.setInit(array);
		result.setInit2(number);
		//result.setAddress("ARRAY");
		
		Address index_a = am.getAddress();
		Address total_a = am.getAddress();
		
		String good = "arraygood" + literalCount;
		String bad = "arraybad" + literalCount++;
		number.writeVal(index_a, writer);
		writer.cmp(index_a, Address.G0);
		writer.bl(bad);
		writer.set(((ArrayType)array.getType()).getLength().toString(), total_a);
		writer.cmp(index_a, total_a);
		writer.bl(good);
		writer.label(bad);
		writer.set(Template.ARRAY_ERROR_FORMAT, Address.O0);
		writeVal(number, Address.O0);
		writer.set(((ArrayType)array.getType()).getLength().toString(), Address.O2);
		writer.call("printf");
		writer.set("1", Address.O0);
		writer.call("exit");
		writer.label(good);
	
		
		total_a.release();
		index_a.release();
	}

	public void WriteArrowDeref(STO structSTO, String _3, STO result) {
		StructType t = (StructType) ((PointerType) structSTO.getType()).getSubtype();
		Integer size = 0;
		for(STO member: t.getFields()){
			if(member.getName().equals(_3))
				break;
			size += member.getType().getSize();
		}
		result.setOffset(size.toString());
		
		Address a = am.getAddress(), offset_a = am.getAddress();
		
		writeVal(structSTO, a);
		writer.set(result.getOffset(), offset_a);
		writer.addOp(a, offset_a, a);
		writer.ld(a, a);
		
		writer.addSTO(result);
		store(result, a);
		
		a.release();
		offset_a.release();
		
		if(result instanceof VarSTO){
			((VarSTO) result).setInit(structSTO);
			structSTO.stringField = new String(_3);
			//result.setAddress("STRUCT");
		}else{
			structSTO.writeAddress(Address.O0, writer);
		}
		if(result instanceof FuncSTO){
			((FuncSTO) result).setInit(structSTO);
		}
	}
	
	public STO doThis(){
		return symTab.getStruct();
	}
	
	public void WriteDot(STO structSTO, String _3, STO result) {
		StructType t = (StructType) structSTO.getType();
		Integer size = 0;
		for(STO member: t.getFields()){
			if(member.getName().equals(_3))
				break;
			size += member.getType().getSize();
		}
		result.setOffset(size.toString());
		if(result instanceof VarSTO){
			((VarSTO)result).setInit(structSTO);
			structSTO.stringField = new String(_3);
			//result.setAddress("STRUCT");
		}
		else{
			structSTO.writeAddress(Address.O0, writer);
		}
		if(result instanceof FuncSTO){
			((FuncSTO) result).setInit(structSTO);
		}
	}

	public void WriteNewStmt(STO s, Vector<STO> params) {
		Address a = am.getAddress();
		writer.set("1", Address.O0);
		writer.set(s.getType().getSize().toString(), Address.O1);
		writer.call("calloc");
		store(s, Address.O0);
		
		
		
		String funcToCall = FuncSTO.getName(s.getType().getName(), params);
		for(int i = 0; i < params.size(); i++){
			STO p = params.get(i);
			//p.write
		}
		
		a.release();
	}

	public void WriteDeleteStmt(STO s) {
		String good = ".deleteAttempt" + literalCount++ + "good";
		Address a = am.getAddress();
		
		
		s.writeVal(a, writer);
		writer.cmp(Address.G0, a);
		writer.bne(good);
		writer.set(Template.ARRAY_DEL_FORMAT, Address.O0);
		writer.call("printf");
		writer.set("1", Address.O0);
		writer.call("exit");
		writer.label(good);
		writer.call("free");
		store(s, Address.G0);

		a.release();
	}

	
	//cast from pointer to int to pointer, should be okay
	public void WriteCast(STO original, STO casted){
		writer.addSTO(casted);
		Type oT = original.getType(), cT = casted.getType();
		if(cT.getClass() == oT.getClass() || casted.getType() instanceof PointerType){
			store(casted, original);
		}
		if(cT instanceof BoolType){
			Address a = am.getAddress();
			
			writeVal(original, a);
			writer.cmp(a, Address.G0);
			String label = ".cast" + literalCount++;
			writer.bne(label);
			writer.set("0", a);
			writer.ba(label + DONE);
			writer.label(label);
			writer.set("1", a);
			writer.label(label + DONE);
			store(casted, a);
			
			a.release();
		}else if(cT instanceof IntType){
			if(oT instanceof BoolType){
				store(casted, original);
			}
			else if(oT instanceof FloatType){
				writeVal(original, Address.F0);
				writer.fstoi(Template.F0, Template.F0);
				casted.store(Address.F0, writer);
			}
		}else if(cT instanceof FloatType){
			if(oT instanceof IntType || oT instanceof BoolType){
				fitos(original, casted);
			}
		}
	}
	
	public void WriteNull(STO s){
		writer.addSTO(s);
		s.store(Address.G0, writer);
		
	}

	public Writer getWriter() {
		return writer;
	}

	public STO DoParens(STO _2) {
		if(_2.isError())
			return _2;
		STO s = _2.newSTO();
		s.setName("(" + _2.getName() + ")");
		return s;
	}
	
	
}
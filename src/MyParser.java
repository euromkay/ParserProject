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
		error = checkArray(arraySTOs);
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
	
	private STO checkArray(Vector<STO> arrayList){
		for(STO s: arrayList){
			s = DoArrayCheck(s);
			if(s.isError())
				return s;
		}
		return null;
	}
	
	STO DoParamDecl(Type t, boolean ref, String name, Vector<STO> arrayList){
		STO error = hasError(arrayList);
		if(error != null)
			return error;
		error = checkArray(arrayList);
		if(error != null)
			return error;
		
		return DoRefCheck(Type.mergeType(t, arrayList), ref, name);
	}
	
	STO DoFieldVarDecl(String name, Type t, Vector<STO> arrayList){
		STO error = hasError(arrayList);
		if(error != null)
			return error;
		error = checkArray(arrayList);
		if(error != null)
			return error;
		
		return new VarSTO(name, Type.mergeType(t, arrayList));
	}
	
	STO DoVarDecl(boolean statik, Type t, String name, Vector<STO> arraySTOs, Vector<VarSTO> ctrsArgs) {
		STO error = hasError(arraySTOs);
		if(error != null)
			return error;
		if(t instanceof ErrorType)
			return new ErrorSTO(t);
		StructType type = (StructType) t;
		
		t = Type.mergeType(t, arraySTOs);
		if (symTab.accessLocal(name) != null) 
			return generateError(ErrorMsg.redeclared_id, name);
		
		
		ArrayList<FuncSTO> ctors = new ArrayList<FuncSTO>();
		for(STO func: type.getFuncs()){
			if(isCtor(type.getName(), (FuncSTO) func))
				ctors.add((FuncSTO) func);
		}
		
		if(ctors.size() == 1){
			FuncSTO f = ctors.get(0);
			if(ctrsArgs.size() != f.getFunctionType().getNumParams())	//Check 5, number params expected and passed in don't match
				return generateError(ErrorMsg.error5n_Call, ctrsArgs.size() +"", f.getFunctionType().getNumParams() + "");
			error = funcCallChecker(ctrsArgs, f, false);
			if(error != null)
				return error;
			
		}else{
			FuncSTO f = null ;
			String neededCtor = FuncSTO.getName(type.getName(), ctrsArgs);
			for(FuncSTO possCtr: ctors){
				if(possCtr.getName().equals(neededCtor)){
					f = possCtr;
					break;
				}
			}
			
			if(f == null)
				return generateError(ErrorMsg.error9_Illegal, ctors.get(0).getBaseName());
			
			error = funcCallChecker(ctrsArgs, f, true);
			if(error != null)
				return error;
		}
		
		
		return varHelper(statik, t, name);
	}
	
	private boolean isCtor(String structName,FuncSTO s){
		String cTor = s.getBaseName();
		
		return cTor.equals(structName);
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
		if (symTab.accessLocal(iter.getName()) != null)
			return generateError(ErrorMsg.redeclared_id, iter.getName());
		
		if(!(arr.getType() instanceof ArrayType))
			return generateError(ErrorMsg.error12a_Foreach);
		
		Type aType = ((ArrayType) arr.getType()).getSubtype();
		Type iterType = iter.getType();
		if(iter.isRef()){
			if(!aType.isEquivalent(iterType))
				return generateError(ErrorMsg.error12r_Foreach, iterType.getName(), iter.getName(), aType.getName());
		}
		else{
			if(!aType.isAssignable(iterType))
				return generateError(ErrorMsg.error12v_Foreach, iterType.getName(), iter.getName(), aType.getName());
			
		}
		
		symTab.insert(iter);
		
		return arr;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	void DoExternDecl(Type input_t, String name, Vector<STO> arraySTOs) {
		if(hasError(arraySTOs) != null)
			return;
		
		STO error = checkArray(arraySTOs);
		if(error != null)
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

		if(result.isError()){
			printErrorSTO(result);
			return result;
		}
		
		if(!(o.getName().equals(Operator.AND) || o.getName().equals(Operator.OR)))
			WriteBinaryExpr(a, o, b, result);
		
		return result;
	}
	
	private void printErrorSTO(STO result){
		m_nNumErrors++;
		m_errors.print(result.getName()); // maybe?
		
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

		if(!init.getType().isAssignable(t))
			return generateError(ErrorMsg.error8_Assign, init.getType().getName(), t.getName());
		
		if(!init.isConst())
			return generateError(ErrorMsg.error8_CompileTime, name);
		
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
		
		ArrayList<String> list = new ArrayList<String>();
		Integer structSize = 0;
		for(STO s: vars){
			String id = s.getName();
			
			
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
		
		if(ctorList.isEmpty()){
			FuncSTO f = new FuncSTO(structName, new VoidType());
			structType.addFunc(f);
			return;
		}
		
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
	
	
	void DoStructFuncCheck(Vector<STO> funcs, Vector<STO> ctrs) {
		StructdefSTO struct = symTab.getStruct();
		
		ArrayList<String> varNames = new ArrayList<String>();
		for(STO s: struct.getStructType().getVars()){
			varNames.add(s.getName());
		}
		
		ArrayList<String> funcList = new ArrayList<String>();
		
		for(STO func_s: funcs){
			if(func_s.isError())
				return;
			FuncSTO func = (FuncSTO) func_s;
			if(varNames.contains(func.getBaseName())){
				generateError(ErrorMsg.error13a_Struct, func.getBaseName());
			}
			else if(funcList.contains(func.getName())){
				generateError(ErrorMsg.error9_Decl, func.getBaseName());
			}else{
				funcList.add(func.getName());
			}
		}

		String prefix = "." + struct.getName() + "_";
		for(STO s: funcs){
			s.setName(prefix + s.getName());
			struct.getStructType().addFunc(s);
			symTab.insert(s);
		}
		for(STO s: ctrs){
			struct.getStructType().addCtor(s);
			symTab.insert(s);
		}
		
		for(STO s: struct.getStructType().getVars())
			s.setName(prefix + s.getName());
		
		
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

	STO DoFuncDecl_1(String id) {
		return DoFuncDecl_1(symTab.getStruct().getType(), false, id);
	}
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoFuncDecl_1(Type t, boolean ref, String id) {
		STO error = null;
		if (symTab.accessLocal(id) != null) {
			if(!symTab.accessLocal(id).isFunc())
				error = generateError(ErrorMsg.redeclared_id, id);
		}
		
		FuncSTO sto = new FuncSTO(id, t);
		symTab.setFunc(sto);
		symTab.openScope();
		
		if(error != null)
			return error;
		
		sto.setRef(ref);
		

		
		return sto;
	}
	
	void DoFormalParams(Vector<VarSTO> params) {
		FuncSTO func = symTab.getFunc();
		if (func == null) {
			m_nNumErrors++;
			m_errors.print("internal: DoFormalParams says no proc!");
		}
		
		if(symTab.accessFunc(func.getBaseName(), params) != null  && !symTab.hasStruct()){
			generateError(ErrorMsg.error9_Decl, func.getBaseName());
			return;
		}
		symTab.insert(func);
		
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
		for(String retExpr: statements)
			if(retExpr.startsWith("return"))
				rets ++;
		
		if(rets == 0 && !(funType instanceof VoidType))
			if(!extern)
				return generateError(ErrorMsg.error6c_Return_missing);
		FuncSTO current = symTab.getFunc();
		

		symTab.closeScope();
		
		symTab.insert(current);
		
		symTab.setFunc(null);
		
		return current;
	}

	// ----------------------------------------------------------------
	// PHASE1.6A
	// ----------------------------------------------------------------
	String DoReturn(STO sto){
		final String RETURN = "return ";
		if(sto != null && sto.isError())
			return "";
		
		
		FunctionPointerType fType = (FunctionPointerType) symTab.getFunc().getType();
		Type rType = fType.getReturnType();
		
		if(sto == null){
			if(!(rType instanceof VoidType)){
				generateError(ErrorMsg.error6a_Return_expr);
				return RETURN;
			}
			else
				return RETURN;
		}
		
		Type givenType = sto.getType();
		if(fType.isRef()){
			if(!givenType.isEquivalent(rType)){
				generateError(ErrorMsg.error6b_Return_equiv, givenType.getName(), rType.getName());
				return RETURN;
			}
			else if(!sto.isModLValue() && !(sto instanceof VoidSTO)){
				generateError(ErrorMsg.error6b_Return_modlval);
				return RETURN;
			}
		}
		else{
			if(!givenType.isAssignable(rType)){
				generateError(ErrorMsg.error6a_Return_type, givenType.getName(), rType.getName());
				return RETURN;
			}
		}
		
		return RETURN + sto.getName();
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
		if(sto.isError())
			return sto;
		
		if(!sto.getIsAddressable()){
			m_nNumErrors++;
			m_errors.print(ErrorMsg.error19_Sizeof);
			return new ErrorSTO(ErrorMsg.error19_Sizeof);
		}
		
		
		return new ConstSTO("size of " + sto.getName(), new IntType(), sto.getType().getSize().toString());
	}
	
	STO DoSizeOf(Type t, Vector<STO> arrayList){
		t = Type.mergeType(t, arrayList);
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
			else if(!t.isFloat()){
				String val = csto.getValue().toString();
				int i = val.length();
				if(csto.getType().isFloat())
					i = val.indexOf(".");
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

	public STO DoArrayCheck(STO s){
		if(s.isError())
			return s;
		if(!s.getType().isEquivalent(new IntType()))
			return generateError(ErrorMsg.error10i_Array, s.getType().getName());
		if(!s.isConst())
			return generateError(ErrorMsg.error10c_Array);
		BigDecimal value = ((ConstSTO) s).getValue();
		if(value.compareTo(BigDecimal.ZERO) <= 0)
			return generateError(ErrorMsg.error10z_Array, value.toString());
		
		return s;
	}
	
	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoFuncCall(STO sto, Vector<VarSTO> argList) {
		if(sto.isError())
			return sto;
		
		if (!(sto.getType() instanceof FunctionPointerType)) 
			return generateError(ErrorMsg.not_function, sto.getName());
		
		
		FuncSTO fSTO = (FuncSTO) sto;
		String baseName = fSTO.getBaseName();
		ArrayList<FuncSTO> funcs = fSTO.getBrothers();
		boolean overloaded = funcs.size() > 1;
		
		if(overloaded){
			sto = pickFunction(fSTO, argList);
			if(sto == null)
				return generateError(ErrorMsg.error9_Illegal, baseName);
		}
		STO error;
		
		

		FunctionPointerType fstoT = (FunctionPointerType) sto.getType();
		
		if(argList.size() != fstoT.getNumParams())	//Check 5, number params expected and passed in don't match
			error = generateError(ErrorMsg.error5n_Call, argList.size() +"", fstoT.getNumParams() + "");
		else
			error = funcCallChecker(argList, (FuncSTO) sto, overloaded);

		if(error != null)
			return error;
		
		
		if(fstoT.isRef())
			return new VarSTO(sto.getName(), fstoT.getReturnType());
		
		
		return new ExprSTO(sto.getName(), fstoT.getReturnType());
	}
	
	private STO pickFunction(FuncSTO s, Vector<VarSTO> argList){
		for(FuncSTO curr: s.getBrothers()){
			FunctionPointerType t = curr.getFunctionType();
			if(t.getNumParams() != argList.size())
				continue;
			for(int i = 0; i < argList.size() && i < argList.size(); i++){
				VarSTO parameter = t.get(i);
				boolean isRef = parameter.isRef();
				STO argument = argList.get(i);
				
				if(isRef){ //Checking parameter declared as pass-by-reference(&) and corresponding arg types
					if(!argument.getType().isEquivalent(parameter.getType()))
						continue;
					else if(!(argument.isModLValue()) )
						continue;
				}
				else{	//Checking parameter types against those being passed in
					if(!argument.getType().isAssignable(parameter.getType()))
						continue;
				}
			}
			return curr;
		}
		
		return null;
	}
	
	private STO funcCallChecker(Vector<VarSTO> argList, FuncSTO f, boolean overloaded){
		FunctionPointerType fstoT = f.getFunctionType();
		STO error = null;
		for(int i = 0; i < argList.size() && i < fstoT.getNumParams(); i++){
			VarSTO parameter = fstoT.get(i);
			boolean isRef = parameter.isRef();
			STO argument = argList.get(i);
			
			if(isRef){ //Checking parameter declared as pass-by-reference(&) and corresponding arg types
				if(!argument.getType().isEquivalent(parameter.getType())){
					if(overloaded)
						error = throwError9(f.getBaseName());
					else //TODO
						error = generateError(ErrorMsg.error5r_Call, argument.getType().getName(), parameter.getName(), parameter.getType().getName());
				}
				else if(!(argument.isModLValue()) ){//&& !(argument.getType() instanceof ArrayType)){
					if(overloaded)
						error = throwError9(f.getBaseName());
					else
						error = generateError(ErrorMsg.error5c_Call, parameter.getName(), parameter.getType().getName());
				}
			}
			else{	//Checking parameter types against those being passed in
				if(!argument.getType().isAssignable(parameter.getType())){
					if(overloaded)
						error = throwError9(f.getBaseName());
					else
						error = generateError(ErrorMsg.error5a_Call, argument.getType().getName(), parameter.getName(), parameter.getType().getName());
				}
			}
		}
		
		return error;
	}
	
	private STO throwError9(String name){
		return generateError(ErrorMsg.error9_Illegal, name);
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
		
		if (!(sto.getType() instanceof StructType))
			return generateError(ErrorMsg.error14t_StructExp, sto.getType().getName());
		
		StructType t = (StructType) sto.getType();
		String fullName;
		if(symTab.getStruct() == null)
			fullName = "." + t.getName() + "_" + memberID;
		else
			fullName = memberID;
			
		
		for(STO possible: t.getMembers()){
			if(possible instanceof FuncSTO){
				if(((FuncSTO) possible).getBaseName().equals(memberID))
					return possible;
			}
			else if(possible.getName().equals(fullName))
				return possible;
				
		}
		if(symTab.hasFunc()){
			FuncSTO f = symTab.getFunc();
			if(f.getBaseName().equals(memberID))
				return f;
		}

		Scope p = symTab.closeScope();
		FuncSTO s = symTab.accessLocalFunc(memberID);
		symTab.openScope(p);
		if(s != null)
			return s;
			
		return generateError(ErrorMsg.error14f_StructExp, memberID, sto.getType().getName());
		
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoDesignator2_Array(STO a, STO e) {
		if(e.isError() || a.isError())
			return e;

		if(!(a.getType() instanceof ArrointType) || a.getType() instanceof NullPointerType)
			return generateError(ErrorMsg.error11t_ArrExp, a.getType().getName());
		
		if(!(e.getType().isInt()))
			return generateError(ErrorMsg.error11i_ArrExp, e.getType().getName());
		
		if(e.isConst() && a.getType() instanceof ArrayType){
			ConstSTO es = (ConstSTO) e;
			int aLength = Integer.parseInt(((ArrayType) a.getType()).getLength());
			if(es.getIntValue() > aLength || es.getIntValue() < 0){
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
	
	public STO DoArrowDeref(STO sto, String memberID){
		if(sto instanceof ErrorSTO)
			return sto;
		
		
		if(!(sto.getType() instanceof PointerType))
			return generateError(ErrorMsg.error15_ReceiverArrow, sto.getType().getName());
		
		PointerType pt = (PointerType) sto.getType();
		
		if(!(pt.getSubtype() instanceof StructType))
			return generateError(ErrorMsg.error15_ReceiverArrow, sto.getType().getName());
		
		
		StructType t = (StructType) pt.getSubtype();
		
		String fullName;
		if(symTab.getStruct() == null)
			fullName = "." + t.getName() + "_" + memberID;
		else
			fullName = memberID;
			
		
		for(STO possible: t.getMembers()){
			if(possible instanceof FuncSTO){
				if(((FuncSTO) possible).getBaseName().equals(memberID))
					return possible;
			}
			else if(possible.getName().equals(fullName))
				return possible;
				
		}
		if(symTab.hasFunc()){
			FuncSTO f = symTab.getFunc();
			if(f.getBaseName().equals(memberID))
				return f;
		}

		Scope p = symTab.closeScope();
		FuncSTO s = symTab.accessLocalFunc(memberID);
		symTab.openScope(p);
		if(s != null)
			return s;
			
		return generateError(ErrorMsg.error14f_StructExp, memberID, sto.getType().getName());
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
	
	public STO DoAllocCheck(String s, STO x, Vector<STO> args){
		if(x.isError())
			return x;
		boolean isDelete = s.equals("delete");
		if(!x.getIsAddressable() || !x.getIsModifiable()){
			if(!isDelete)
				return generateError(ErrorMsg.error16_New_var);
			else
				return generateError(ErrorMsg.error16_Delete_var);
		}
		if(!(x.getType() instanceof PointerType)){
			if(!isDelete)
				return generateError(ErrorMsg.error16_New, x.getType().getName().toString());
			else
				return generateError(ErrorMsg.error16_Delete, x.getType().getName());
		}
		
		//deletes don't have args, can't even parse
		if(isDelete)
			return null;

		Type subType = ((PointerType) x.getType()).getSubtype();
		if(args != null){
			if(!(subType instanceof StructType))
				return generateError(ErrorMsg.error16b_NonStructCtorCall, subType.getName().toString());
		}
		else
			args = new Vector<STO>();
		StructType struct_t = (StructType) subType;
		
		//only one constructor size
		if(struct_t.getCtors().size() < 2){
			FunctionPointerType ctr = ((FuncSTO) struct_t.getCtors().get(0)).getFunctionType();
			if(ctr.getNumParams() != args.size())
				return generateError(ErrorMsg.error5n_Call, args.size()+"", ctr.getNumParams() + "");
			for(int i = 0; i < args.size() && i < args.size(); i++){
				VarSTO parameter = ctr.get(i);
				boolean isRef = parameter.isRef();
				STO argument = args.get(i);
				
				if(isRef){ //Checking parameter declared as pass-by-reference(&) and corresponding arg types
					if(!argument.getType().isEquivalent(parameter.getType()))
						return generateError(ErrorMsg.error5r_Call, argument.getType().getName(), parameter.getName(), parameter.getType().getName());
					else if(!(argument.isModLValue()) )
						return generateError(ErrorMsg.error5c_Call, parameter.getName(), parameter.getType().getName());
				}
				else{	//Checking parameter types against those being passed in
					if(!argument.getType().isAssignable(parameter.getType()))
						return generateError(ErrorMsg.error5a_Call, argument.getType().getName(), parameter.getName(), parameter.getType().getName());
				}
			}
			return struct_t.getCtors().get(0);
		}
		
		for(STO poss_ctr: struct_t.getCtors()){
			FunctionPointerType ctr = ((FuncSTO) poss_ctr).getFunctionType();
			if(ctr.getNumParams() != args.size())
				continue;
			for(int i = 0; i < args.size() && i < args.size(); i++){
				VarSTO parameter = ctr.get(i);
				boolean isRef = parameter.isRef();
				STO argument = args.get(i);
				
				if(isRef){ //Checking parameter declared as pass-by-reference(&) and corresponding arg types
					if(!argument.getType().isEquivalent(parameter.getType()))
						continue;
					else if(!(argument.isModLValue()) )
						continue;
				}
				else{	//Checking parameter types against those being passed in
					if(!argument.getType().isAssignable(parameter.getType()))
						continue;
				}
			}
			return poss_ctr;
		}
		
		return throwError9(struct_t.getName());
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
		if(left.isError() || (right != null && right.isError()))
			return "";
			
		String label;
		String left_t = left.getType().getName() + " " + left.getName();
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
			label = left.getType().getName() + " " + left.getName();
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
			
			writer.changeSection(Writer.DATA);
			writer.align("4");
		
			//Initialize Default Null Value
			left.setAddress(new Address(left.getName()));
				
				
			String skip;
			if(isFloat(left))
				skip = Template.FLOAT_VAR_DECL;
			else if (left.getType().isArray())
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
				
				writeAddress(left, lAdd);
				writer.set("4", _4);
				writer.set("0", _0);
				//clears our global arrays, setting contents to 0
				for(int i = 0; i < lType.getSize(); i+=4){
					writer.store(_0, lAdd);
					writer.addOp(lAdd, _4, lAdd);
				}
						
				lAdd.release();
				_4.release();
				_0.release();
						
			}

			writer.writeLater = false;
			writer.skip(skip,  left.getName(),  val);

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
				writer.align("4");
				
				String type;
				if(left.getType() instanceof FloatType)
					type = Template.FLOAT_VAR_DECL;
				else
					type = Template.INT_VAR_DECL;
					
				writer.skip(type, name, "0");
				writer.skip(type, nameBool, "0");
					
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

		
	}
	
	public void WriteVarInit(STO left, STO right){
		if(right == null || right.isError()){
			writer.newLine();
			return;
		}
		
		if(isIntToFloat(left, right))
			fitos(right, left);
		else
			store(left, right);

		writer.newLine();
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
		Address f_add = am.getAddress();
		writeVal(int_, fReg);
		writer.fitos(fReg, fReg);
		writeAddress(float_, f_add);
		writer.store(fReg, f_add);
		
		f_add.release();
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
		
		if(symTab.getStruct() != null){
			fname = "."+ symTab.getStruct().getName() + "_" + fname;
			offset++;
		}
		//Write Function Comment
		writer.funcComment(fname);
		
		//Write Function Header
		writer.changeSection(Writer.TEXT);
		writer.global(fname);
		writer.label(fname);
		
		writer.set("SAVE." + fname, new Address("%g1")); 
		writer.save();
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
		writer.newLine();
		writer.writeGlobalInits();
		
		Address _1_a = am.getAddress();
		writer.set(Writer.GLOBAL_INIT, globalInit_a);
		writer.set("1", _1_a);
		writer.store(_1_a, globalInit_a);
		writer.label(alreadyInitialized);
		
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
	
	
	
	public void WriteFuncDeclFinish() {
		String fname = symTab.getFunc().getName();
		writer.returnstmt();
		writer.newLine();
		if(symTab.getStruct() != null){
			fname = "."+ symTab.getStruct().getName() + "_" + fname;
		}

		writer.save(fname, writer.getStackSize());
		writer.localVarSpace = 0;
	}

	public void WriteAssignExpr(STO left, STO right_s) {
		writer.comment(left.getName() + " = " + right_s.getName());
		
		if(left.isError() || right_s.isError())
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
			writer.store(right_a, left_a);
		}

		if(right_s instanceof FuncSTO){
			left_s.setInit(right_s);
			left_s.setInit2(((FuncSTO)right_s).getInit());
		}
		
		writer.newLine();
		
		right_a.release();
		left_a.release();
	}

	
	public void WriteIntLiteral(STO s){
		writer.comment(s.getName());
		
		Address a = am.getAddress();
		writer.set(s.getName(), a);
		writer.addSTO(s);
		s.store(a, writer);
		
		a.release();
		
		writer.newLine();
	}
	
	public int literalCount;
	
	public void WriteFloatLiteral(STO s) {
		writer.comment(s.getName());
			
		writer.addSTO(s);
		int section = writer.getSection();
		writer.changeSection(Writer.RODATA);
		String label = "_float_" + s.getName() + literalCount++;
		writer.skip(Template.FLOAT_VAR_DECL, label, s.getName());
		writer.newLine();
	
		Address a = am.getAddress();
		writer.changeSection(section);
		writer.set(label, a);
		writer.load(a, a);
		s.store(a, writer);
		
		a.release();
		
		writer.newLine();
	}

	public static final String TRUE_S = "1", FALSE_S = "0";
	
	public void WriteBoolLiteral(STO s, String value) {
		
		//assured its local now
		
		Address a = am.getAddress();
		writer.addSTO(s);
		writer.set(value, a);
		s.store(a, writer);
		
		a.release();
		writer.comment(s.getName());
	}
	
	public void WriteStringLiteral(String s) {
		writer.changeSection(Writer.RODATA);
		String label = "_string" + literalCount++;
		writer.skip(Template.ASCIZ, label, "\""+s+"\"");
		writer.newLine();
		
		writer.changeSection(Writer.TEXT);
		writer.comment(s);
		
	}
	

	
	public boolean isInGlobalScope() {
		return writer.isGlobal();
	}
	
	public static final String BRANCH = ".branch";
	public void COut(STO s){
		if(s.isError())
			return;
		
		writer.comment("cout << " + s.getName());
		
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
		writer.newLine();
	}

	public void WriteENDL() {
		writer.comment("cout << endl");
		
		writer.set(Template.STRFORMAT, Address.O0);
		writer.set("_endl", Address.O1);
		writer.call("printf");		
		
		writer.newLine();
	}

	public void WriteBinaryExpr(STO a, Operator o, STO b, STO result){
		
		writer.comment(result.getName());
		
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
		
		if(s.getType().isAssignable(new FloatType())){
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
		if(s.isError())
			return;
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
		if(result.isError())
			return;
		
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
		if(s != null && s.isError())
			return;
		
		
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
	
	public void WriteFuncCall(STO s, Vector<VarSTO> args, STO result){
		if(result.isError())
			return;
		
		writer.addSTO(result);
	}
	
	private void WriteFuncCall(FuncSTO func, Vector<VarSTO> args, STO fsto){
		func = symTab.accessFunc(func.getBaseName(), args);
		
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
				e.writeAddress(new Address(o+(i + structOffset)), writer);
			}
			else
				e.writeVal(new Address(o+(i + structOffset)), writer);
			i++;
		}
		
		writer.call(func.getName());
		if(!(func.getReturnType() instanceof VoidType))
			writer.store(Address.O0, fsto.getAddress());
	}
	
	public void WriteNotOp(STO s, STO result){
		if(result.isError())
			return;
		
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
		String label = ".iflabel" + literalCount++;
		labelList.push(label);
		
		if(s.isError())
			return;
		
		Address a = am.getAddress();
		s.writeVal(a, writer);
		writer.cmp(a, Address.G0);
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
		writer.comment("exit " + s.getName());
		writeVal(s, Address.O0);
		writer.call("exit");
		
		writer.newLine();
		
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
	public void WriteForWhileBegin(String s){
		writer.comment(s + " ()");
		Address _0_a = am.getAddress();
		String label = "." + s + literalCount++;
		whileForList.add(label);
		
		ExprSTO counter = new ExprSTO(whileForList.peek()+".counter", new IntType());
		symTab.insert(counter);
		writer.addSTO(counter);
		writer.set("0", _0_a);
		
		store(counter, _0_a);
		
		writer.newLine();
		writer.label(label);

		_0_a.release();
	}
	
	public String WriteForMiddle(STO s_index, STO s_array) {
		if(s_index.isError() || s_array.isError())
			return "";
		writer.addSTO(s_index);
		
		
		VarSTO index = (VarSTO) s_index, array = (VarSTO) s_array;
		STO counter = symTab.access(whileForList.peek()+".counter");

		ArrayType aT = (ArrayType) array.getType();
		String maxlength = aT.getLength().toString();
		
		Address length_a = am.getAddress();
		Address counter_a = Address.O1;
		
		writer.set(maxlength, length_a);
		writeVal(counter, counter_a);
		writer.cmp(counter_a, length_a);
		writer.be(whileForList.peek() + DONE);
		
		length_a.release();
		
		Address sTypeLen_a = Address.O0; 
		String subTypeLength = aT.getSubtype().getSize().toString();
		writer.set(subTypeLength, sTypeLen_a);
		writer.call(".mul");
		
		Address array_a = am.getAddress();
		writeAddress(array, array_a);//this should be writing the address of the first element
		writer.addOp(array_a, Address.O0, array_a);
		
		if(!index.isRef())
			writer.ld(array_a, array_a);
		//array_a has the correct value you want to put in a
		
		boolean isRef = index.isRef();
		index.setRef(false);
		store(index, array_a);
		index.setRef(isRef);
		
		writer.newLine();
		array_a.release();
		
		return "foreach(" + s_index.getType().getName() + " " + s_index.getName() + " : " + s_array.getName() + ")";
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
	
	private static final String INCR = "_INCR";
	
	public void WriteForEnd(){
		Address counter_a = am.getAddress();
		writer.label(whileForList.peek() + INCR);
		STO counter = symTab.access(whileForList.peek()+".counter");
		counter.writeVal(counter_a, writer);
		writer.inc(counter_a, false);
		counter.store(counter_a, writer);
		
		counter_a.release();
		
		WriteWhileEnd();
		
	}

	public void WriteContBr(boolean b) {
		if(whileForList.size() == 0){
			if(b == BREAK){
				generateError(ErrorMsg.error12_Break);
			}else
				generateError(ErrorMsg.error12_Continue);
		}
		if(whileForList.isEmpty())
			return;
		
		String s = whileForList.peek();
		if(!b)
			s += DONE;
		else
			s += INCR;
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
		if(result.isError())
			return;
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
		writer.addSTO(res);  //TODO
		//result.setAddress("ARRAY");
		
		if(array.getType().isPointer())
			return;
		
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
		for(STO member: t.getFuncs()){
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
		if(result.isError())
			return;
		
		writer.addSTO(result);
		
		StructType t = (StructType) structSTO.getType();
		Integer size = 0;
		for(STO member: t.getFuncs()){
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
			if(!structSTO.isStructdef())
				structSTO.writeAddress(Address.O0, writer);
		}
		if(result instanceof FuncSTO){
			((FuncSTO) result).setInit(structSTO);
		}
	}

	public String WriteNewStmt(STO s, Vector<VarSTO> params, STO chosenFunc) {
		Address a = am.getAddress();
		writer.set("1", Address.O0);
		writer.set(s.getType().getSize().toString(), Address.O1);
		writer.call("calloc");
		store(s, Address.O0);
		
		
		if(params == null)
			params = new Vector<VarSTO>();
		String funcToCall = FuncSTO.getName(s.getType().getName(), params);
		for(int i = 0; i < params.size(); i++){
			STO p = params.get(i);
			//p.write
		}
		
		a.release();
		
		return "new " + s.getName();
	}

	public String WriteDeleteStmt(STO s) {
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
		
		return "delete " + s.getName();
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

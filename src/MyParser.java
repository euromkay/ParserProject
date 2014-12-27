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
	ArrayList<STO> DoVarDecl(InfoBlock lstIDs, Type t, boolean statik) {
		ArrayList<STO> stos = new ArrayList<STO>();
		Type tOriginal = t;
		ArrayList<InfoBlock.Triplet> list = lstIDs.getList();
		for (int i = 0; i < list.size(); i++) {
			InfoBlock.Triplet trip = list.get(i);
			t = TransformType(trip.arraySize, trip.pointer, t);
			
			STO right = trip.init; //this is the initializing type of the sto
			String id = trip.name;
			
			if (symTab.accessLocal(id) != null) {
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
			}
			
			if(right != null)
				if(!t.isAssignable(right.getType())){
					generateError(ErrorMsg.error8_Assign, right.getType().getName(), t.getName());
				}
			
			VarSTO left = new VarSTO(id, t); // Set the type of the STO
			if(statik)
				left.setStatic();
			if(right != null)
				left.setInit(right);
			if(t instanceof ArrayType)
				left.setIsModifiable(false);
			symTab.insert(left);
			
			stos.add(left);
			t = tOriginal;
		}
		
		return stos;
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
			if(!iterType.isEquivalent(exprType.getType())){
				m_nNumErrors++;
				error = Formatter.toString(ErrorMsg.error12r_Foreach, exprType.getType().getName(), iter.getName(), iterType.getName());
				m_errors.print(error);
				return new ErrorSTO(error);
			}
		}
		else{
			if(!iterType.isAssignable(exprType.getType())){
				m_nNumErrors++;
				error = Formatter.toString(ErrorMsg.error12v_Foreach, exprType.getType().getName(), iter.getName(), iterType.getName());
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
	void DoExternDecl(Type t, InfoBlock lstIDs) {
		for (int i = 0; i < lstIDs.getList().size(); i++) {
			String id = lstIDs.getList().get(i).name;

			if (symTab.accessLocal(id) != null) {
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
			}
			//Maybe t should be VoidType
			VarSTO sto = new VarSTO(id, t);
			symTab.insert(sto);
			sto.setAddress(new Address(id));
		}
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
			WriteDoNotOp(a, s);
			return s;
		}

		m_nNumErrors++;
		m_errors.print(Formatter.toString(ErrorMsg.error1u_Expr, aType.getName(), "!", BoolType.NAME));
		return new ErrorSTO(ErrorMsg.error1u_Expr);
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
	ArrayList<STO> DoConstDecl(Type t, InfoBlock lstIDs, boolean statik) {
		ArrayList<STO> params = new ArrayList<STO>();
		for (int i = 0; i < lstIDs.getList().size(); i++) {
			STO s = lstIDs.getList().get(i).init;
			if(s instanceof ErrorSTO)
				continue;
			String id = lstIDs.getList().get(i).name;
			
			
			if (symTab.accessLocal(id) != null) {
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
			}

			if(!t.isAssignable(s.getType())){
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.error8_Assign, s.getType().getName(), t.getName() ));
			}
			
			
			//Checking for ConstExpr
			if(s instanceof ConstSTO){
				VarSTO v = new VarSTO(id, t);
				v.setIsAddressable(true);
				v.setInit(s);
				symTab.insert(v);
				if(statik)
					v.setStatic();
				params.add(v);
			}
			else{
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.error8_CompileTime, id));
			}
		}
		
		return params;
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

			TypedefSTO sto = new TypedefSTO(id, type.newType());
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
	
		TypedefSTO sto = new TypedefSTO(id, new StructType(id)); 
		symTab.setStructSTO(sto);
		symTab.insert(sto);
	}
	
	void DoStructdefDecl2(String id, Vector<STO> fields, int size) {
		if (symTab.accessLocal(id) != null) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.redeclared_id, id));
		}
		Integer offset = 0;
		for(STO field: fields){
			field.setOffset(offset.toString());
			field.setAddress("STRUCT");
			//s.stringField = s.getName();
			offset += field.getType().getSize();
		}
		
		TypedefSTO sto = (TypedefSTO) symTab.access(id);
		((StructType) sto.getType()).setFields(fields, offset);
		symTab.setFunc(null);
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
		if(!(s.getType().isIntAssignable())){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error10i_Array, s.getType().getName()));
			return new ErrorSTO(ErrorMsg.error10i_Array);
		}	
		if(((ConstSTO)s).getValue() < 1){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error10z_Array, ((ConstSTO)s).getIntValue()));
			return new ErrorSTO(ErrorMsg.error10z_Array);
		}
		
		return s;
	}


	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	FuncSTO DoFuncDecl_1(String id, Type t) {
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

	// ----------------------------------------------------------------
	// PHASE1.6B //Makesure statements in skeleton code have nonvoid or non null EXprSTO 
	// v is the statement list
	// ----------------------------------------------------------------
	STO DoFuncDecl_2(Type funType, Vector<String> statements) {
		STO s = null;
		
		int rets = 0;//return statements
		//retExpr is each statement that might be a return (type);
		for(String retExpr: statements){
			if(retExpr.startsWith("return")){
				rets ++;
			}
			
		}
		if(rets == 0 && !(funType instanceof VoidType)){
			m_nNumErrors++;
			m_errors.print(ErrorMsg.error6a_Return_expr);
			s = new ErrorSTO(ErrorMsg.error6a_Return_expr);
		}
		if(!(s instanceof ErrorSTO))
			s = symTab.getFunc();
		symTab.closeScope();
		symTab.setFunc(null);
		
		return s;
	}

	// ----------------------------------------------------------------
	// PHASE1.6A
	// ----------------------------------------------------------------
	STO DoReturn(STO sto){
		String error;
		STO s = sto;
		if(sto.isError())
			return sto;
		
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
		
		return new ExprSTO("return " + sto.getName());
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
		
		return new ConstSTO("size of " + sto.getName(), new IntType(), Double.valueOf(sto.getType().getSize()));
	}
	
	STO DoSizeOf(Type t){
		return new ConstSTO("size of " + t.getName(), new IntType(), Double.valueOf(t.getSize()));
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
				Double d = Double.parseDouble(val.substring(0, i));
				return new ConstSTO(s.getName(), t, d);
			}
			return new ConstSTO(s.getName(), t, ((ConstSTO) s).getValue());
		}
		
		return new ExprSTO(s.getName(), t);
	}
	// ----------------------------------------------------------------
	//	
	// ----------------------------------------------------------------
	STO DoAssignExpr(STO stoLeft, STO stoRight) {
		
		//why are we losing stoRight to a null
		if(stoLeft instanceof ErrorSTO || stoRight instanceof ErrorSTO)
			return new ErrorSTO(stoLeft.getName());

		
		//Phase 1 Check #3a where stoDes is NOT a modifiable L-Val IS THIS IT?>
		if (!stoLeft.isModLValue() || stoLeft.getType() instanceof ArrayType) {
			m_nNumErrors++;
			m_errors.print(ErrorMsg.error3a_Assign);
			return new ErrorSTO(stoLeft.getName()); //Is the arg in the parameter correct?
		}
		Type a = stoLeft.getType();
		Type b = stoRight.getType();
		if(!(a.isAssignable(b))){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, b.getName(), a.getName()));
			return new ErrorSTO(stoLeft.getName());
		}
		/*if(a instanceof BoolType && !(b instanceof BoolType)){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, a.getName(), b.getName()));
			return new ErrorSTO(stoLeft.getName());
		}
		else if(a instanceof IntType && !(b instanceof IntType)){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, b.getName(), a.getName()));
			return new ErrorSTO(stoLeft.getName());
		}
		else if(a instanceof FloatType && !(b instanceof NumericType)){
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error3b_Assign, b.getName(), a.getName()));
			return new ErrorSTO(stoLeft.getName());
		}
		else if(ainstanceof )*/
		
		return stoLeft;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoFuncCall(STO sto, Vector<STO> paramList) {
		if(sto.isError())
			return sto;
		
		int prev = m_nNumErrors;
		
		if (!(sto.getType() instanceof FunctionPointerType)) {
		 	m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.not_function,
					sto.getName()));
			return new ErrorSTO(sto.getName());
		}

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
					error = (Formatter.toString(ErrorMsg.error14b_StructExpThis, memberID));
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
		
		if(!(e.getType().isIntAssignable())){
			String error = (Formatter.toString(ErrorMsg.error11i_ArrExp, e.getType().getName()));
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		if(!a.getType().isArrayType() && !a.getType().isPointerType()){
			String error = (Formatter.toString(ErrorMsg.error11t_ArrExp, a.getType().getName()));
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		
		if(e instanceof ConstSTO && a.getType() instanceof ArrayType){
			ConstSTO es = (ConstSTO) e;
			int aLength = ((ArrayType) a.getType()).getLength();
			if(es.getIntValue() > aLength || aLength < 0){
				m_nNumErrors++;
				String error = Formatter.toString(ErrorMsg.error11b_ArrExp, es.getIntValue(), aLength);
				m_errors.print(error);
					return new ErrorSTO(error);
			}
				
		}
		
		ArrointType tt = (ArrointType) a.getType();
		return new VarSTO("", tt.getType());
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
		
		if ((sto = symTab.accessGlobal(strID)) == null) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.error0g_Scope, strID));
			sto = new ErrorSTO(strID);
		}

		return sto;
	}

	// ----------------------------------------------------------------
	//
	// ----------------------------------------------------------------
	STO DoQualIdent(String strID) {
		STO sto;

		if ((sto = symTab.access(strID)) == null) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.undeclared_id, strID));
			return new ErrorSTO(strID);
		}

		if (!sto.isTypedef()) {
			m_nNumErrors++;
			m_errors.print(Formatter.toString(ErrorMsg.not_type, sto.getName()));
			return new ErrorSTO(sto.getName());
		}
		
		return sto;
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
		if (!aType.isIntAssignable()){
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
			return new VarSTO(des.getName(), pt.getType());
		}
		
	}
	
	public STO DoAddressOf(STO s) {
		if(s instanceof ErrorSTO)
			return s;
		String error = null;
		if(!s.getIsAddressable()){
			m_nNumErrors++;
			error = Formatter.toString(ErrorMsg.error21_AddressOf, s.getType().getName());
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
		
		if(!(pt.getType() instanceof StructType)){
			error = Formatter.toString(ErrorMsg.error15_ReceiverArrow, sto.getType().getName());
			m_nNumErrors++;
			m_errors.print(error);
			return new ErrorSTO(error);
		}
		
		StructType struct = (StructType) pt.getType();
		
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
	
	
	
	public int DoStructFieldCheck(Vector<STO> v, String name){
		
		ArrayList<String> list = new ArrayList<String>();
		int structSize = 0;
		for(STO s: v){
			String id = s.getName();
			if(!(s instanceof FuncSTO))
				structSize += s.getType().getSize();
			else{
				String t = s.getName();
				s.setName("." + name + "_" + t);
			}
			if(!(s.getType() instanceof PointerType)){
				if(s.getType().getName().equals(name)){
					m_nNumErrors++;
					m_errors.print(Formatter.toString(ErrorMsg.error13b_Struct, id));
				}
			}
			if(list.contains(id)){
				m_nNumErrors++;
				m_errors.print(Formatter.toString(ErrorMsg.error13a_Struct, id));
			}
			else{
				list.add(id);
			}
		}
		return structSize;		
	}

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

	public String WriteVarDecl(ArrayList<STO> stos){
		String params = "";
		
		for(STO e : stos){
			if(e instanceof ErrorSTO)
				return e.getName();
			params += e.getType().getName() + " " + e.getName() +",";
		}
		params = params.substring(0, params.length()-1);

		
		if(writer.isGlobal()){
			
			writer.write(Template.DATA);
		
			//Initialize Default Null Value
			for(STO ef : stos){
				VarSTO lSTO = (VarSTO) ef;
				
				ef.setAddress(new Address(ef.getName()));
				
				writer.write(Template.ALIGN, "4");
				
				String skip;
				if(isFloat(lSTO))
					skip = Template.FLOAT_VAR_DECL;
				else if (lSTO.getType() instanceof CompositeType)
					skip = Template.SKIP;
				else
					skip = Template.INT_VAR_DECL;

				writer.writeLater = true;
				String val = "0";
				//if theres some sort of initializatoin
				if(lSTO.getInit() != null){
					STO rSTO = lSTO.getInit();
					if(isIntToFloat(lSTO, lSTO.getInit())){
						fitos(lSTO, rSTO);
					}else{
						store(lSTO, rSTO);
					}
				//clear out anything with size greater than 4, so structs and arrays
				}else{
					Type lType = lSTO.getType();
					if(lType instanceof CompositeType){
						val = lSTO.getType().getSize().toString();
						Address lAdd = am.getAddress();
						Address _4 = am.getAddress(), _0 = am.getAddress(); 
						
						writer.set("4", _4);
						writer.set("0", _0);
						for(int i = 0; i < lSTO.getType().getSize(); i+=4){
							writer.store(lAdd, _0);
							writer.addOp(lAdd, _4, lAdd);
						}
						
						lAdd.release();
						_4.release();
						_0.release();
						
					}
					
					//if(e.getType() instanceof )
				}

				writer.writeLater = false;
				writer.write(skip, lSTO.getName(), val);
			}
		
			writer.newLine();
		}else{

			for(STO s : stos){
				VarSTO lVar = (VarSTO) s;
				if(lVar.isStatic()){
					String name = "." + symTab.getFunc().getName() + "_" + lVar.getName();
					lVar.setAddress(name);
					String nameBool = name + "_bool";
					
					writer.newLine();
					writer.set(nameBool, Template.L0);
					writer.load(Template.L0, Template.L0);
					writer.cmp(Template.L0, Template.G0);
					final String alreadyInitialized = name + "_finish";
					writer.bne(alreadyInitialized);
					writer.newLine();
					
					writer.changeSection(Writer.DATA);
					writer.write(Template.ALIGN, "4");
					String type;
					if(lVar.getType() instanceof FloatType)
						type = Template.FLOAT_VAR_DECL;
					else
						type = Template.INT_VAR_DECL;
					
					
					writer.write(type, name, "0");
					writer.write(type, nameBool, "0");
					
					writer.changeSection(Writer.TEXT);
					lVar.writeAddress(Template.L0, writer);
					if(lVar.getInit() != null){
						STO rVar = lVar.getInit();
						if(isIntToFloat(lVar, rVar)){
							fitos(rVar, Template.L0);
						}else{
							rVar.writeVal(Template.L1, writer);
							writer.store(Template.L1, Template.L0);
						}
					}
					
					writer.set(nameBool, Template.L0);
					writer.set("1", Template.L6);
					writer.store(Template.L6, Template.L0);
					writer.label(alreadyInitialized);
					writer.newLine();
				}else{
					if(lVar.getInit() != null){ //initalized
						lVar.writeAddress(Template.L1, writer);
						STO rVar = lVar.getInit();
						if(isIntToFloat(lVar, rVar)){
							fitos(rVar, lVar);
						}else{
							store(rVar, lVar);
						}
						writer.newLine();
					}	
				}
			}
			
			//set it into local vars, then put it on the frame pointer
		}
		
		return params;
	}
	
	private void store(STO to, STO from){
		to.store(from, writer);
	}
	
	private boolean isFloat(STO s){
		return s.getType() instanceof FloatType;
	}
	
	private void writeAddress(STO sto, String address){
		sto.writeAddress(address, writer);
	}
	
	private void writeVal(STO sto, String address) {
		sto.writeVal(address, writer);
	}

	private boolean isIntToFloat(STO f, STO i){
		return f.getType() instanceof FloatType && i.getType() instanceof IntType;
	}

	private void fitos(STO int_, STO float_){
		int_.writeVal(Template.F0, writer);
		writer.fitos(Template.F0, Template.F0);
		writeAddress(float_, Template.L1);
		writer.store(Template.F0, Template.L1);
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
		
		writer.set("SAVE." + fname, Template.G1); 
		writer.write(Template.SAVE, Template.SP, Template.G1, Template.SP);
		writer.newLine();
		
		if(fname.equals("main"))
			initializeGlobals();
		
		String s = "%i";
		for(int i = 0; i < paramList.size(); i++){
			VarSTO sto = (VarSTO) paramList.get(i);
			if(sto.isRef() || sto.getType() instanceof ArrayType || sto.getType() instanceof StructType){
				if(sto.getType() instanceof ArrayType){
					sto.setRef(true);
					VarSTO v = new VarSTO("", new IntType());
					writer.addSTO(v);
					writer.set(((ArrayType) sto.getType()).getLength().toString(), Template.L0);
					writer.store(Template.L0, v.getAddress());
					sto.setInit(v);
				}
				if(sto.getType() instanceof StructType)
					sto.setRef(true);
				sto.setAddress(s + (i + offset));
			}else{
				writer.addSTO(sto);
				writer.store(s+(i+offset), sto.getAddress());
			}
		}
		
		writer.newLine();
	}
	
	private void initializeGlobals(){
		writer.set(Writer.GLOBAL_INIT, Template.L0);
		writer.load(Template.L0, Template.L0);
		writer.cmp(Template.L0, Template.G0);
		final String alreadyInitialized = ".globalFinish";
		writer.bne(alreadyInitialized);
		writer.writeGlobalInits();
		writer.newLine();
		writer.set(Writer.GLOBAL_INIT, Template.L0);
		writer.set("1", Template.L1);
		writer.store(Template.L1, Template.L0);
		writer.label(alreadyInitialized);
		writer.newLine();
	}

	
	public void WriteID(STO s_, String s){
		if(s_ instanceof FuncSTO)
			s_.setAddress(s);
		return;/*
		if(s_ instanceof FuncSTO)
			return;
		AddrSTO s = (AddrSTO) s_;
		s.setAddressOfInitialization(s.getName());
		//writer.ld(sto.getAddress(), dest);
		//writer.store(dest, TEMP_FP);*/
	}
	
	public void WriteID_GLOBAL(String s){
		STO sto = symTab.accessGlobal(s);
		String dest;
		if(sto.getType() instanceof FloatType)
			dest = Template.F0;
		else
			dest = Template.L7;
		writer.set(s, Template.L0);
		writer.ld(Template.L0, dest);
		//writer.store(dest, TEMP_FP);
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

	public void WriteAssignExpr(STO s_left, STO right) {
		if(s_left.isError())
			return;
		VarSTO left = (VarSTO) s_left;
		String lAdd =Template.L0;
		left.writeAddress(lAdd, writer);
		String rAdd;
		
		
		if(left.getType() instanceof FloatType && right.getType() instanceof IntType){
			rAdd = Template.F1;
			writer.ld(right.getAddress(), Template.F0);
			writer.fitos(Template.F0, rAdd);
		}else{
			rAdd = Template.L5;
			if(right.getType() instanceof ArrayType)
				right.writeAddress(rAdd, writer);
			else if(right instanceof FuncSTO){
				writer.set(right.getName(), rAdd);
			}else
				right.writeVal(rAdd, writer);
		}
		writer.store(rAdd, lAdd);
		if(right instanceof FuncSTO){
			left.setInit(right);
			left.setInit2(((FuncSTO)right).getInit());
		}
		
		writer.comment(left.getName() + " = " + right.getName());
	}

	
	public void WriteIntLiteral(STO _1){
		
		if(writer.isGlobal() || writer.getSection() != Writer.TEXT)
			writer.writeLater = true;
		writer.set(_1.getName(), Template.L7);
		writer.addSTO(_1);
		writer.store(Template.L7, _1.getAddress());
		
		writer.writeLater = false;
	}
	
	int literalCount;
	
	public void WriteFloatLiteral(STO _1) {
		if(writer.isGlobal() || writer.getSection() != Writer.TEXT)
			writer.writeLater = true;
			
		writer.addSTO(_1);
		int section = writer.getSection();
		writer.changeSection(Writer.RODATA);
		String label = "_float" + literalCount++;
		writer.write(Template.FLOAT_VAR_DECL, label, _1.getName());
		writer.newLine();
	
		writer.changeSection(section);
		writer.set(label, Template.L7);
		writer.load(Template.L7, Template.L7);
		writer.store(Template.L7, _1.getAddress());
		
		writer.writeLater = false;
		
	}

	public void WriteBoolLiteral(STO _1, boolean b) {
		
		//assured its local now
		String s;
		if(b)
			s = "1";
		else
			s = "0";
	
		if(writer.isGlobal())
			writer.writeLater = true;
		writer.addSTO(_1);
		writer.set(s, Template.L7);
		writer.store(Template.L7, _1.getAddress());
		writer.writeLater = false;
	}
	
	public String WriteStringLiteral(String _1) {
		int section = writer.getSection();
		writer.changeSection(Writer.RODATA);
		String s = "_string" + literalCount++;
		writer.write(Template.ASCIZ, s, "\""+_1+"\"");
		writer.newLine();
		
		writer.changeSection(section);
		
		return s;
	}
	

	
	public boolean isInGlobalScope() {
		return writer.isGlobal();
	}
	
	private static final String BRANCH = ".branch";
	public void COut(STO s){
		
		Type t = s.getType();
		if(t instanceof StringType){
			writer.set(Template.STRFORMAT, Template.O0);
			writer.set("_string"+(literalCount-1) ,Template.O1);
		}
		else if(t instanceof IntType){
			writer.set(Template.INTFORMAT, Template.O0);
			s.writeVal(Template.O1, writer);
		} else if(t instanceof PointerType){
			writer.set(Template.INTFORMAT, Template.O0);
			s.writeVal(Template.O1, writer);
		} else if(t instanceof BoolType){
			writer.set(Template.STRFORMAT, Template.O0);
			s.writeVal(Template.L0, writer);
			writer.cmp(Template.L0, Template.G0);
			writer.bne(BRANCH + literalCount);
			
			//print false
			writer.set(Template.BOOLF, Template.O1);
			writer.ba(BRANCH+literalCount+".DONE");
			writer.label(BRANCH + literalCount);
			writer.set(Template.BOOLT, Template.O1);
			
			writer.label(BRANCH+literalCount++ + ".DONE");	
		}
		else{
			s.writeVal(Template.F0, writer);
			writer.call("printFloat");
			return;
		}
		writer.call("printf");
		//nops included
	}

	public void WriteENDL() {
		writer.set(Template.STRFORMAT, Template.O0);
		writer.set("_endl", Template.O1);
		writer.call("printf");		
	}

	public void WriteBinaryExpr(STO a, Operator o, STO b, STO result){
		writer.addSTO(result); // takes care of addressing it
		String a1, a2;
		String res;
		Boolean f_flag = false, bNeed = true, aNeed = true, lNeed = true;
	
		Type aType = a.getType(), bType = b.getType();
		
		if(aType instanceof FloatType || bType instanceof FloatType ){
			a1 = Template.F0;
			a2 = Template.F1;
			res = Template.F3;
			f_flag = true;
			
			if(aType instanceof IntType){
				a.writeVal(Template.F0, writer);
				writer.fitos(Template.F0, Template.F0);
				aNeed = false;
			}
			if(bType instanceof IntType){
				b.writeVal(Template.F1, writer);
				writer.fitos(Template.F1, Template.F1);
				bNeed = false;
			}		
		}
		else{
			a1 = Template.O0;
			a2 = Template.O1;
			res = Template.O0;
		}
		if(aNeed)
			a.writeVal(a1, writer);
		if(bNeed && a != b)
			b.writeVal(a2, writer);
		
		if(o.getName().equals(Operator.PLUS)){
			writer.addOp(a1, a2, res, f_flag);
		}
		else if(o.getName().equals(Operator.MINUS)){
			writer.minusOp(a1, a2, res, f_flag);
		}
		else if(o.getName().equals(Operator.STAR)){
			writer.multiOp(a1, a2, res, f_flag);
		}
		else if(o.getName().equals(Operator.SLASH)){
			writer.divOp(a1, a2, res, f_flag);
		}
		else if(o.getName().equals(Operator.MOD)){
			writer.modOp(a1, a2, res, f_flag);
		}
		else if(o.getName().equals(Operator.U_MINUS)){
			writer.negOp(a1, res, f_flag);		
		}
		else if(o.getName().equals(Operator.U_PLUS)){
			if(a.getType() instanceof FloatType)
				res = a1;
		}
		else if(o.getName().equals(Operator.BAR)){
			writer.orOp(a1, a2, res);
		}
		else if(o.getName().equals(Operator.AMPERSAND)){
			writer.andOp(a1, a2, res);
		}
		else if(o.getName().equals(Operator.CARET)){
			writer.xorOp(a1, a2, res);
		}else if(o.getName().equals(Operator.MINUS_POST)){
			lNeed = false;
			writer.store(a1, result.getAddress());
			incDecHelper(a, a1, DEC, f_flag);
			a.writeAddress(Template.L0, writer);
			writer.store(a1, Template.L0);
		}else if(o.getName().equals(Operator.PLUS_POST)){
			lNeed = false;
			writer.store(a1, result.getAddress());
			incDecHelper(a, a1, INC, f_flag);
			a.writeAddress(Template.L0, writer);
			writer.store(a1, Template.L0);
		}else if(o.getName().equals(Operator.MINUS_PRE)){
			lNeed = false;
			incDecHelper(a, a1, DEC, f_flag);
			writer.store(a1, result.getAddress());
			a.writeAddress(Template.L0, writer);
			writer.store(a1, Template.L0);
		}else if(o.getName().equals(Operator.PLUS_PRE)){
			lNeed = false;	
			incDecHelper(a, a1, INC, f_flag);
			a.writeAddress(Template.L0, writer);
			writer.store(a1, Template.L0);
			writer.store(a1, result.getAddress());	
		}else{
			res = Template.L3;
			writer.cmp(a1, a2, f_flag);
			
			writer.write(o.getTemplate(f_flag), BRANCH + literalCount);
			writer.write(Template.NOP);
			writer.newLine();
			
			writer.set("0", res);
			writer.ba(BRANCH + literalCount + ".DONE");
			writer.newLine();
			writer.label(BRANCH + literalCount);
			writer.set("1", res);
			writer.label(BRANCH + literalCount + ".DONE");
			literalCount++;	
		}
		
		if(lNeed)
			writer.store(res, result.getAddress());
		
	}
	private static final boolean INC = true, DEC = false;
	private void incDecHelper(STO s, String addr, boolean inc, boolean fFlag){
		
		if(s.getType() instanceof FloatType || s.getType() instanceof IntType){
			if(inc)
				writer.inc(addr, fFlag);
			else
				writer.dec(addr, fFlag);
		}
		else{
			PointerType t = (PointerType) s.getType();
			writer.set(t.getType().getSize().toString(), Template.L2);
			s.writeVal(Template.L3, writer);
			if(inc)
				writer.addOp(Template.L2, Template.L3, addr, false);
			else
				writer.minusOp(Template.L3, Template.L2, addr, false);
			
		}
		
	}
	
	public static final boolean OR_FLAG = true, AND_FLAG = false;
	
	public void WriteEqStmt_1(STO s, boolean flag){
		s.writeAddress(Template.L0, writer);
		writer.cmp(Template.L0, Template.G0);
		String str = ".eq" + literalCount++;
		labelList.add(str);
		if(flag == OR_FLAG)
			writer.bne(str);
		else//flag == AND_FLAG
			writer.be(str);
	}
	public static final String DONE = ".DONE";
	public void WriteEqStmt_2(STO s, STO result, boolean flag){
		writer.addSTO(result);
		writer.ld(s.getAddress(), Template.L0);
		writer.cmp(Template.L0, Template.G0);
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
		writer.set(result1, Template.L1);
		writer.ba(str+DONE);
		writer.label(str);
		writer.set(result2, Template.L1);
		writer.label(str+DONE);
		writer.store(Template.L1, result.getAddress());
	}
	
	
	public void WriteSizeOf(STO s){
		if(writer.isGlobal())
			return;
		writer.set(((ConstSTO) s).getIntValue().toString(), Template.L7);
		writer.addSTO(s);
		writer.store(Template.L7, s.getAddress());		
	}
	
	public void WriteReturn(STO s){
		FuncSTO currentFunc = symTab.getFunc();
		FunctionPointerType fT = (FunctionPointerType) currentFunc.getType();
		
		if(s != null){
			if(fT.isRef()){
				s.writeAddress(Template.I0, writer);
			}
			else{
				if(isIntToFloat(currentFunc, s)){
					s.writeVal(Template.F0, writer);
					writer.fitos(Template.F0, Template.F0);
				}else
					s.writeVal(Template.I0, writer);
			}
		}
		else
			writer.set(Template.G0, Template.I0);
		
		writer.returnstmt();		
	}
	
	public void WriteFuncCall(FuncSTO func, Vector<STO> args, STO fsto){
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
			STO e = args.get(i);
			if(funcParams.get(i).isRef() || e.getType() instanceof ArrayType || e.getType() instanceof StructType){
				e.writeAddress(o+(i + structOffset), writer);
			}
			else{
				if(args.get(i).getType() instanceof IntType && funcParams.get(i).getType() instanceof FloatType){
					VarSTO temp = new VarSTO("", new IntType());
					writer.addSTO(temp);
					temp.writeAddress(Template.L0, writer);
					e.writeVal(Template.F0, writer);
					writer.fitos(Template.F0, Template.F0);
					writer.store(Template.F0, Template.L0);
					writer.ld(Template.L0, o+(i+structOffset));
					
				}else
					e.writeVal(o+(i + structOffset), writer);
			}
			i++;
		}
		
		writer.call(func.getName());
		if(!(func.getReturnType() instanceof VoidType))
			writer.store(Template.O0, fsto.getAddress());
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
			structSTO.writeAddress(Template.O0, writer);
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
	}
	
	public void WriteDoNotOp(STO a, STO s){
		writer.addSTO(s);
		
		writer.load(a.getAddress(), Template.L5);
		writer.set("1", Template.L6);
		writer.xorOp(Template.L5, Template.L6, Template.L5);
		writer.store(Template.L5, s.getAddress());
	}
	
	Stack<String> labelList = new Stack<String>();
	public void WriteIfStatement(STO s){
		s.writeVal(Template.L0, writer);
		writer.cmp(Template.L0, Template.G0);
		String label = ".iflabel" + literalCount++;
		labelList.push(label);
		writer.be(label);
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
		s.writeVal(Template.O0, writer);
		writer.call("exit");
		
	}
	
	public void DoCIn(STO input){
		String adr;
		if(input.getType() instanceof IntType){
			writer.call("inputInt");
			adr = Template.O0;
		}else{
			writer.call("inputFloat");
			adr = Template.F0;
		}
		input.writeAddress(Template.L7, writer);
		writer.store(adr, input.getAddress());
		
	}
	Stack<String> whileForList = new Stack<String>();
	public STO WriteForWhileBegin(String s){
		String label = "." + s + literalCount++;
		whileForList.add(label);
		
		ConstSTO c = new ConstSTO(whileForList.peek()+".counter", new IntType(), 0.0);
		symTab.insert(c);
		writer.addSTO(c);
		writer.set("0", Template.L0);
		writer.store(Template.L0, c.getAddress());
		
		writer.label(label);

		
		return c;
	}
	
	public void WriteForMiddle(STO s_index, STO s_array) {
		VarSTO index = (VarSTO) s_index, array = (VarSTO) s_array;
		STO counter = symTab.access(whileForList.peek()+".counter");
		ArrayType aT = ((ArrayType) array.getType());
		String length = aT.getLength().toString();
		writer.set(length, Template.L0);
		counter.writeVal(Template.L1, writer);
		writer.cmp(Template.L1, Template.L0);
		writer.be(whileForList.peek() + DONE);
		
		
		index.setInit(array);
		array.setInit2(index); 
		index.setAddress("ARRAY");
		if(index.isRef()){
			index.writeVal(Template.L0, writer);
			writer.addSTO(index);
			writer.store(Template.L0, index.getAddress());
		}
	}
	
	public void WriteWhileMiddle(STO s){
		writer.ld(s.getAddress(), Template.L0);
		writer.cmp(Template.G0, Template.L0);
		writer.be(whileForList.peek() + DONE);
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
		writer.label(whileForList.peek() + CHECK);
		STO counter = symTab.access(whileForList.peek()+".counter");
		counter.writeVal(Template.L0, writer);
		writer.inc(Template.L0, false);
		writer.store(Template.L0, counter.getAddress());
		WriteWhileEnd();
		
	}

	public void WriteContBr(boolean b) {
		final boolean brake = true, continyu = false;
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
		writer.addSTO(result);
		
		given.writeAddress(Template.L0, writer);
		result.store(Template.L0, writer);
	}
	public void WriteDeRef(STO s, STO result){
		s.writeVal(Template.L0, writer);
		writer.load(Template.L0, Template.L0);
		writer.addSTO(result);
		result.writeAddress(Template.L1, writer);
		writer.store(Template.L0, Template.L1);
		
	}

	public void WriteArrayIndex(STO array, STO number, STO res) {
		if(res.isError())
			return;
		VarSTO result = (VarSTO) res;
		result.setInit(array);
		result.setInit2(number);
		result.setAddress("ARRAY");
		
		String good = "arraygood" + literalCount;
		String bad = "arraybad" + literalCount++;
		number.writeVal(Template.L0, writer);
		writer.cmp(Template.L0, Template.G0);
		writer.bl(bad);
		writer.set(((ArrayType)array.getType()).getLength().toString(), Template.L1);
		writer.cmp(Template.L0, Template.L1);
		writer.bl(good);
		writer.label(bad);
		writer.set(Template.ARRAY_ERROR_FORMAT, Template.O0);
		number.writeVal(Template.O1, writer);
		writer.set(((ArrayType)array.getType()).getLength().toString(), Template.O2);
		writer.call("printf");
		writer.set("1", Template.O0);
		writer.call("exit");
		writer.label(good);
	}

	public void WriteArrowDeref(STO structSTO, String _3, STO result) {
		StructType t = (StructType) ((PointerType) structSTO.getType()).getType();
		Integer size = 0;
		for(STO member: t.getFields()){
			if(member.getName().equals(_3))
				break;
			size += member.getType().getSize();
		}
		result.setOffset(size.toString());
		
		structSTO.writeVal(Template.L0, writer);
		writer.set(result.getOffset(), Template.L1);
		writer.addOp(Template.L0, Template.L1, Template.L0, false);
		writer.ld(Template.L0, Template.L0);
		
		writer.addSTO(result);
		result.writeAddress(Template.L1, writer);
		writer.store(Template.L0, Template.L1);
		
		if(result instanceof VarSTO){
			((VarSTO) result).setInit(structSTO);
			structSTO.stringField = new String(_3);
			result.setAddress("STRUCT");
		}else{
			structSTO.writeAddress(Template.O0, writer);
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
			result.setAddress("STRUCT");
		}
		else{
			structSTO.writeAddress(Template.O0, writer);
		}
		if(result instanceof FuncSTO){
			((FuncSTO) result).setInit(structSTO);
		}
	}

	public void WriteNewStmt(STO _1) {
		writer.set("1", Template.O0);
		writer.set(_1.getType().getSize().toString(), Template.O1);
		writer.call("calloc");
		_1.writeAddress(Template.L0, writer);
		writer.store(Template.O0, Template.L0);
		
	}

	public void WriteDeleteStmt(STO _1) {
		String good = ".deleteAttempt" + literalCount++ + "good";
		
		_1.writeVal(Template.L0, writer);
		writer.cmp(Template.G0, Template.L0);
		writer.bne(good);
		writer.set(Template.ARRAY_DEL_FORMAT, Template.O0);
		writer.call("printf");
		writer.set("1", Template.O0);
		writer.call("exit");
		writer.label(good);
		writer.call("free");
		_1.writeAddress(Template.L0, writer);
		writer.store(Template.G0, Template.L0);

	}

	public void WriteCast(STO original, STO casted){
		writer.addSTO(casted);
		Type oT = original.getType(), cT = casted.getType();
		if(cT.getClass() == oT.getClass() || casted.getType() instanceof PointerType){
			original.writeVal(Template.L0, writer);
			casted.writeAddress(Template.L1, writer);
			writer.store(Template.L0, Template.L1);
		}
		if(cT instanceof BoolType){
			original.writeVal(Template.L0, writer);
			writer.cmp(Template.L0, Template.G0);
			String label = ".cast" + literalCount++;
			writer.bne(label);
			writer.set("0", Template.L1);
			writer.ba(label + DONE);
			writer.label(label);
			writer.set("1", Template.L1);
			writer.label(label + DONE);
			casted.writeAddress(Template.L0, writer);
			writer.store(Template.L1, Template.L0);
		}else if(cT instanceof IntType){
			if(oT instanceof BoolType){
				original.writeVal(Template.L0, writer);
				casted.writeAddress(Template.L1, writer);
				writer.store(Template.L0, Template.L1);
			}
			else if(oT instanceof FloatType){
				original.writeVal(Template.F0, writer);
				writer.fstoi(Template.F0, Template.F0);
				casted.writeAddress(Template.L0, writer);
				writer.store(Template.F0, Template.L0);
			}
		}else if(cT instanceof FloatType){
			if(oT instanceof IntType || oT instanceof BoolType){
				original.writeVal(Template.F0, writer);
				writer.fitos(Template.F0, Template.F0);
				casted.writeAddress(Template.L0, writer);
				writer.store(Template.F0, Template.L0);
			}
		}
	}
	
	public void WriteNull(STO s){
		writer.addSTO(s);
		s.writeAddress(Template.L1, writer);
		writer.store(Template.G0, Template.L1);
		
	}
	
	
}

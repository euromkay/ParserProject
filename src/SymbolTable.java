//---------------------------------------------------------------------
//
//---------------------------------------------------------------------
import java.util.*;

class SymbolTable
{
	private Stack<Scope> m_stkScopes;
	public int m_nLevel;
	private Scope m_scopeGlobal;
	private FuncSTO m_func = null;
	private StructdefSTO currentStruct = null;
	
    
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public SymbolTable()
	{
		m_nLevel = 0;
		m_stkScopes = new Stack<Scope>();
		m_scopeGlobal = null;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void insert(STO sto)
	{
		Scope scope = m_stkScopes.peek();
		scope.InsertLocal(sto);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO accessGlobal(String strName)
	{
		return m_scopeGlobal.access(strName);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO accessLocal(String strName)
	{
		Scope scope = m_stkScopes.peek();
		return scope.accessLocal(strName);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO access(String strName)
	{
		Stack<Scope> stk = new Stack<Scope>();
		Scope scope;
		STO stoReturn = null;	

		//reverses the order of the scopes so
			//for the scope example, we get the local one first, as opposed to the global ones.
		for (Enumeration<Scope> e = m_stkScopes.elements(); e.hasMoreElements();)
		{
			scope = e.nextElement();
			stk.add(scope);
		}

		while(!stk.isEmpty()){
			scope = stk.pop();
			if ((stoReturn = scope.access(strName)) != null)
				return stoReturn;
		}
		
		return null;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void openScope()
	{
		Scope scope = new Scope();

		// The first scope created will be the global scope.
		if (m_scopeGlobal == null)
			m_scopeGlobal = scope;

		m_stkScopes.push(scope);
		m_nLevel++;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void closeScope()
	{
		m_stkScopes.pop();
		m_nLevel--;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public int getLevel()
	{
		return m_nLevel;
	}


	//----------------------------------------------------------------
	//	This is the function currently being parsed.
	//----------------------------------------------------------------
	public FuncSTO getFunc() { return m_func; }
	public void setFunc(FuncSTO sto) { m_func = sto; }
	
	public StructdefSTO getStruct() { return currentStruct; }
	public void setStructSTO(StructdefSTO sto) { currentStruct = sto; }
	
	public STO isInStructScope(String id){
		Scope structScope = m_stkScopes.get(1);
		return structScope.access(id);
	}
	private ArrayList<String> overLoadedNames = new ArrayList<String>();
	public void add(String id) {
		overLoadedNames.add(id);
		
	}

	public boolean has(String id) {
		return overLoadedNames.contains(id);
	}
	
}

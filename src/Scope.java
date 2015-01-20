//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

class Scope
{
	Vector<STO> m_lstLocals;
	HashMap<String, ArrayList<FuncSTO>> funcs = new HashMap<String, ArrayList<FuncSTO>>();

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Scope()
	{
		m_lstLocals = new Vector<STO>();
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO access(String strName)
	{
		return accessLocal(strName);
	}
	
	public ArrayList<FuncSTO> accessFuncs(String baseName) {
		return accessLocalFuncs(baseName);
	}

	public ArrayList<FuncSTO> accessLocalFuncs(String baseName) {
		return funcs.get(baseName);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO accessLocal(String strName)
	{
		STO sto = null;

		for (int i = 0; i < m_lstLocals.size(); i++)
		{
			sto = m_lstLocals.elementAt(i);

			if (sto.getName().equals(strName))
				return sto;
		}
		if(funcs.get(strName) != null)
			return funcs.get(strName).get(0);
			

		return null;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void InsertLocal(STO sto)
	{
		if(sto.isFunc()){
			String baseName = ((FuncSTO) sto).getBaseName();
			if(funcs.containsKey(baseName))
				funcs.get(baseName).add((FuncSTO) sto);
			else{
				ArrayList<FuncSTO> overloads = new ArrayList<FuncSTO>();
				overloads.add((FuncSTO) sto);
				funcs.put(baseName, overloads);
			}
				
		}
		m_lstLocals.addElement(sto);
		
	}

	
	
	


	
}

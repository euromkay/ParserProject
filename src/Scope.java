//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

import java.util.Vector;

class Scope
{
	Vector<STO> m_lstLocals;

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

		return null;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public void InsertLocal(STO sto)
	{
		m_lstLocals.addElement(sto);
	}
	
	public Vector<STO> getVector(){
		return m_lstLocals;
	}

	public String getLastStructName() {
		String name = null;
		for(STO s : m_lstLocals){
			if(s.getType() instanceof StructType){
				name = s.getName();
			}
		}
		return name;
	}
}

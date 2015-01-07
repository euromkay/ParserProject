import java.util.Vector;

//---------------------------------------------------------------------
// For typedefs like: typedef int myInteger1, myInteger2;
// Also can hold the structdefs
//---------------------------------------------------------------------

class TypedefSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public TypedefSTO(String alias)
	{
		super(alias);
	}

	public TypedefSTO(String alias, Type actualType)
	{
		super(new String(alias), actualType.newType());
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isTypedef()
	{
		return true;
	}

	public STO newSTO() {
		STO s = new TypedefSTO(getName(), getType());
		return super.newSTO(s);
	}

	
}

//---------------------------------------------------------------------
//For structdefs
//---------------------------------------------------------------------

class StructdefSTO extends STO
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public StructdefSTO(String strName)
	{
		super(strName);
	}

	public StructdefSTO(String strName, Type typ)
	{
		super(strName, typ);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isStructdef()
	{
		return true;
	}
}


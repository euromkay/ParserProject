import java.util.Vector;

//---------------------------------------------------------------------
// For typedefs like: typedef int myInteger1, myInteger2;
// Also can hold the structdefs
//---------------------------------------------------------------------



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
	
	public STO newSTO() {
		STO s = new StructdefSTO(getName(), getType());
		return super.newSTO(s);
	}
	
	public StructType getStructType(){
		return (StructType) getType();
	}
}


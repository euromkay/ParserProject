//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class ExprSTO extends STO
{
	public ExprSTO(){
		super(""); 
	}
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public ExprSTO(String strName)
	{
		super(new String(strName));
                // You may want to change the isModifiable and isAddressable
                // fields as necessary
	}

	public ExprSTO(String strName, Type typ)
	{
		super(new String(strName),typ);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isExpr()
	{
		return true;
	}
	
	
	
}

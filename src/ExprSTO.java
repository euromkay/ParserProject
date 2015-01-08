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
		super(new String(strName), (typ == null) ? null : typ.newType());
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isExpr()
	{
		return true;
	}
	
	public STO newSTO() {
		return super.newSTO(new ExprSTO(getName(), getType()));
	}
	
	
}

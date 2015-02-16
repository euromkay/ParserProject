import java.util.Vector;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class VarSTO extends STO
{
	private boolean ref = false;
	
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	/*public VarSTO(String strName)
	{
		super(strName);
	}*/
	public VarSTO(String strName, Type typ)
	{
		super(new String(strName), typ);
		setIsAddressable(true);
		setIsModifiable(true);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
	}
	
	
	
	public VarSTO(String strName, Type typ, STO init, STO init2){
		super(strName, typ);
		setIsAddressable(true);
		setIsModifiable(true);
		// You may want to change the isModifiable and isAddressable 
		// fields as necessary
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isVar() 
	{
		return true;
	}

	public void setRef(Boolean ref) {
		this.ref = ref;
	}

	public boolean isRef() {
		return ref;
	}
	
	
	
	



}

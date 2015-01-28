import java.util.Vector;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class VarSTO extends STO
{
	private boolean ref = false;
	private STO init, init2;
	
	
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
		this.init = init;
		if(init2 != null)
			this.init2 = init2;
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
	
	
	
	public STO getInit(){
		return init;
	}

	public STO getInit2() {
		return init2;
	}

	public void setInit2(STO index) {
		init2 = index;
		
	}
	
	public void setInit(STO s){
		init = s;
	}

	



}

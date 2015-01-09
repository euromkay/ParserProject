import java.util.Vector;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class FuncSTO extends STO
{
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	
	
	void setRef(boolean ref){

		setIsAddressable(ref);
		setIsModifiable(ref);
		FunctionPointerType ft = (FunctionPointerType) getType();
		ft.setRef(ref);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isFunc() 
	{ 
		return true;
                // You may want to change the isModifiable and isAddressable                      
                // fields as necessary
	}

	//----------------------------------------------------------------
	// This is the return type of the function. This is different from 
	// the function's type (for function pointers).
	//----------------------------------------------------------------


	public Type getReturnType ()
	{
		return ((FunctionPointerType) getType()).getReturnType();
	}
	

	public void setParameters(Vector<VarSTO> params) {
		((FunctionPointerType) getType()).setParameters(params);
	}

	Type returnType;
	
	
	public STO newSTO(){
		FuncSTO s = new FuncSTO(getName(), returnType);
		s.isFuncMember = isFuncMember;
		if(init != null)
			s.init = init;
		return super.newSTO(s);
	}
	
	public FuncSTO(String strName, Type returnType)
	{
		super (new String(strName), new FunctionPointerType(returnType.newType()));
		this.returnType = returnType.newType();
		baseName = strName;
	}
	
	private String baseName;
	public String getBaseName() {
		return baseName;
	}

	boolean isFuncMember = false;
	private STO init;
	public STO getInit(){
		return init;
	}
	
	public void setInit(STO s){
		init = s;
	}
	
	
	public boolean isMemberFunction() {
		return isFuncMember;
	}
	public void setIsMemberFunction(){
		isFuncMember = true;
	}

	
	
}
	

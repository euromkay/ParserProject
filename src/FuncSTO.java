import java.util.Vector;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class FuncSTO extends STO
{
	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	
	public FunctionPointerType getFunctionType(){
		return (FunctionPointerType) getType();
	}
	
	void setRef(boolean ref){

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
		setName(FuncSTO.getName(getBaseName(), params));
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

	public static String getName(String name, Vector<VarSTO> params) {
		for(STO p: params){
			name += "_" + p.getType().getName();
		}
		
		return name;
	}

	public void fix() {
		setName(getName(getName(), getFunctionType().getParams()));
	}

	
	
}
	

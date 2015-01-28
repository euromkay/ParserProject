import java.util.ArrayList;
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
		return this;
	}
	
	private String lookupName;
	
	public FuncSTO(String strName, String lookupName, Type returnType)
	{
		super (new String(strName), new FunctionPointerType(returnType));
		this.returnType = returnType;
		baseName = strName;
		this.lookupName = lookupName;
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

	private ArrayList<FuncSTO> brothers;
	public void setBrothers(ArrayList<FuncSTO> overloads) {
		brothers = overloads;
	}
	
	public ArrayList<FuncSTO> getBrothers(){
		return brothers;
	}

	public String getLookupName() {
		return lookupName;
	}
	
	
}
	

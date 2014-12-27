import java.util.Vector;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

abstract class STO
{
	private String name;
	private Type type;
	private boolean isAddressable;
	private boolean isModifiable;
	private boolean statik;

	private String address = DEFAULT;
	private String offset = null;
	boolean ret = false;
	
	public STO newSTO(STO s){
		s.name = new String(name);
		s.type = type.newType();
		s.isAddressable = isAddressable;
		s.isModifiable = isModifiable;
		s.address = new String(address);
		if(offset != null)
			s.offset = offset;
		s.ret = ret;
		return s;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO(String strName){
		this(strName, null);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public STO(String strName, Type type){
		name = strName; 
		this.type = type;
		setIsAddressable(false);
		setIsModifiable(false);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String getName(){
		return name;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void setName(String str){
		name = str;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Type getType(){
		return type;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	

	//----------------------------------------------------------------
	// Addressable refers to if the object has an address. Variables
	// and declared constants have an address, whereas results from 
	// expression like (x + y) and literal constants like 77 do not 
	// have an address.
	//----------------------------------------------------------------
	public boolean getIsAddressable(){
		return isAddressable;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	protected void setIsAddressable(boolean addressable){
		isAddressable = addressable;
	}

	//----------------------------------------------------------------
	// You shouldn't need to use these two routines directly
	//----------------------------------------------------------------
	public boolean getIsModifiable(){
		return isModifiable;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	protected void setIsModifiable(boolean modifiable){
		isModifiable = modifiable;
	}

	//----------------------------------------------------------------
	// A modifiable L-value is an object that is both addressable and
	// modifiable. Objects like constants are not modifiable, so they 
	// are not modifiable L-values.
	//----------------------------------------------------------------
	public boolean isModLValue(){
		if(type instanceof ArrayType)
			return getIsAddressable();
		return getIsModifiable() && getIsAddressable();
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isStatic(){
		return statik;
	}
	
	public void setStatic(){
		statik = true;
	}
	

	//----------------------------------------------------------------
	//	It will be helpful to ask a STO what specific STO it is.
	//	The Java operator instanceof will do this, but these methods 
	//	will allow more flexibility (ErrorSTO is an example of the
	//	flexibility needed).
	//----------------------------------------------------------------
	public boolean isVar()     { return false; }
	public boolean isConst()   { return false; }
	public boolean isExpr()    { return false; }
	public boolean isFunc()    { return false; }
	public boolean isTypedef() { return false; }
	public boolean isError()   { return false; }
	
	
	
	public abstract STO newSTO();


	public static final String DEFAULT = "*(&#$*(";
	

	public void setAddress(String string){
		address = new String(string);
	}
	
	public String getAddress() {
		if(address == null)
			return null;
		return new String(address);
	}

	public void setOffset(String i){
		offset = i;
	}
	
	public String getOffset(){
		return offset.toString();
	}
	

	public String writeAddress(String string, Writer writer) {
		if(getAddress().equals("STRUCT")){
			VarSTO struct = (VarSTO) ((VarSTO) this).getInit();
			struct.writeAddress(string, writer);
			writer.set(offset.toString(), Template.L7);
			writer.addOp(string, Template.L7, string, false);
		}else if(getAddress().equals("ARRAY")){
			VarSTO array = (VarSTO) ((VarSTO) this).getInit();
			VarSTO number = (VarSTO) ((VarSTO) this).getInit2();
			array.writeAddress(string, writer);
			number.writeVal(Template.L7, writer);
			for(int i = 0; i < ((ArrayType) array.getType()).t.getSize(); i++)
				writer.addOp(string, Template.L7, string, false);
		}
		else if(getAddress().equals(DEFAULT)){
			writeStructField(writer, string);
		}else if(!getAddress().startsWith("%")){
			writer.set(getAddress(), string);
		}else if(isParameter(writer.symTab.getFunc()) && this instanceof VarSTO && (((VarSTO) this).isRef())){
			writer.set(address, string);
		}else{
			writer.minusOp(Template.FP, offset.toString(), string, false);
		}
		
		return string;
	}
	public String stringField = "";
	private void writeStructField(Writer w, String result){
		Vector<STO> v = w.symTab.m_stkScopes.get(1).getVector();
		STO s = null;
		
		Integer size = 0;
		for(int i = 1; i < v.size(); i++ ){
			s = v.get(i);
			if(s.getName().equals(getName())){
				w.set(Template.I0, Template.L7);
				w.set(size.toString(), Template.L6);
				w.addOp(Template.L6, Template.L7, result, false);
				return;
			}
			else
				size += s.getType().getSize();
		}
		
		throw new ParserException(getName());
	}
	
	
	private boolean isParameter(FuncSTO s){
		FunctionPointerType fT = (FunctionPointerType) s.getType();
		for(VarSTO vs: fT.getParams()){
			if(vs.getName().equals(getName()))
				return true;
		}
		return false;
	}
	

	public void writeVal(String res, Writer writer) {
		String a = address;
		if(!getAddress().startsWith("%")){
			a = Template.L6;
			writeAddress(a, writer);
		}
		writer.ld(a, res);
		
	}

	public void store(String l0, Writer writer) {
		
		
	}
	
	public void writeComment(){
		
	}

	
}

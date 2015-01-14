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

	private Address address;
	private String offset = null;
	boolean ret = false;
	
	public STO newSTO(STO s){
		s.name = new String(name);
		s.type = type.newType();
		s.isAddressable = isAddressable;
		s.isModifiable = isModifiable;
		s.address = address;
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
	public boolean isStructdef() { return false; }
	public boolean isError()   { return false; }
	
	
	
	public abstract STO newSTO();


	

	public void setAddress(Address a){
		address = a;
	}
	public void setAddress(String s){
		setAddress(new Address(s));
	}
	
	public Address getAddress() {
		if(address == null)
			return null;
		return address;
	}

	public void setOffset(String i){
		offset = i;
	}
	
	public String getOffset(){
		return offset.toString();
	}
	

	public Address writeAddress(Address a1, Writer writer) {
		Address tempAdd = writer.getAddressManager().getAddress();
		/*if(getAddress().equals("STRUCT")){
			VarSTO struct = (VarSTO) ((VarSTO) this).getInit();
			struct.writeAddress(a1, writer);
			writer.set(offset.toString(), tempAdd);
			writer.addOp(a1, tempAdd, a1);
		}else if(getAddress().equals("ARRAY")){
			VarSTO array = (VarSTO) ((VarSTO) this).getInit();
			VarSTO number = (VarSTO) ((VarSTO) this).getInit2();
			array.writeAddress(a1, writer);
			number.writeVal(tempAdd, writer);
			for(int i = 0; i < ((ArrayType) array.getType()).getSubtype().getSize(); i++)
				writer.addOp(a1, tempAdd, a1, false);*/
		/*}else if(getAddress().equals(DEFAULT)){
			writeStructField(writer, a1);*/
		if(getAddress().isOnFP()){
			writer.set(address, tempAdd);
			writer.set(offset, a1);
			writer.minusOp(tempAdd, a1, a1);
		}
		else if(!getAddress().isLocal()){
			writer.set(getAddress(), a1);
		}else if(isParameter(writer.symTab.getFunc()) && this instanceof VarSTO && (((VarSTO) this).isRef())){
			writer.set(address, a1);
		}else{
			writer.set(offset.toString(), tempAdd);
			writer.minusOp(Address.FP, tempAdd, a1);
		}
		
		tempAdd.release();
		return a1;
	}
	
	public String stringField = "";
	private void writeStructField(Writer w, Address result){
		Vector<STO> v = w.symTab.m_stkScopes.get(1).getVector();
		STO s = null;
		
		Integer size = 0;
		for(int i = 1; i < v.size(); i++ ){
			s = v.get(i);
			if(s.getName().equals(getName())){
				Address a = w.getAddressManager().getAddress();
				
				w.set(size.toString(), a);
				w.addOp(Address.I0, a, result, false);
				
				a.release();
				
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
	

	public void writeVal(Address res, Writer writer) {
		Address a = writer.getAddressManager().getAddress();
		writeAddress(a, writer);
		writer.ld(a, res);
		
		a.release();
	}


	
	public void writeComment(){
		
	}

	public void store(STO from, Writer writer) {
		Address from_a = writer.getAddressManager().getAddress();
		Address to_a   = writer.getAddressManager().getAddress();
		from.writeVal(from_a, writer);
		
		writer.store(from_a, to_a);
		
		to_a.release();
		from_a.release();
	}

	public void store(Address value_a, Writer writer) {
		Address sto_a = writer.getAddressManager().getAddress();
		
		writeAddress(sto_a, writer);
		writer.store(value_a, sto_a);
		
		sto_a.release();
	}
	
}


public class FuncPtrSTO extends STO{

	
	public boolean isFunc() {
		return true;
	}

	
	public FuncPtrSTO(String strName, Type typ){
		super(new String(strName), typ.newType());
		
	}

	public STO newSTO(){
		STO s = new FuncPtrSTO(getName(), getType());
		return super.newSTO(s);
	}

	
}


public class PointerType extends PointerGroupType {

	
	public PointerType(Type t) {
		super(t.getName() + "*", t, 4);
	}
	
	public PointerType(String name, Type t, int size){
		super(name, t, 4);
	}
	public boolean isPointerType(){
		return true;
	}

	public boolean isAssignable(Type t){
		if(t == null)
			return true;
		if(t instanceof ArrointType){
			if(t instanceof NullPointerType)
				return true;
			ArrointType pt = (ArrointType) t;
			return getType().isEquivalent(pt.getType());
		}
		return false;
	}

	public boolean isEquivalent(Type type) {
		if(type instanceof PointerType){
			if(type instanceof NullPointerType)
				return true;
			PointerType pt = (PointerType) type;
			return getType().isEquivalent(pt.getType());
		}
		return false;
	}


	
	public Type newType() {
		return new PointerType(getType().newType());
		
	}

	
	
	

}


public class VoidType extends Type {

	public VoidType() {
		super("void", 4);
	}
	
	public boolean isAssignable(Type type) {
		if(type instanceof VoidType)
			return true;
		return false;
	}

	public Type newType() {
		return new VoidType();
	}

}

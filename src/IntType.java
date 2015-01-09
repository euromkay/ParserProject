
public class IntType extends NumericType{

	public static final String NAME = "int";

	public IntType() {
		super(NAME, 4);
	}

	public boolean isAssignable(Type type) {
		return type.isFloat() || super.isAssignable(type);
		
	}

	public Type newType() {
		return new IntType();
	}
	
	public boolean isInt(){
		return true;
	}
}

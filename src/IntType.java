
public class IntType extends NumericType{

	public static final String NAME = "int";

	public IntType() {
		super(NAME, 4);
	}

	public boolean isAssignable(Type type) {
		return (type == null) || type instanceof IntType;
		
	}
	public boolean isEquivalent(Type type) {
		return type instanceof IntType;
	}

	public Type newType() {
		return new IntType();
	}
}

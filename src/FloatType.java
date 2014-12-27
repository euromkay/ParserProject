
public class FloatType extends NumericType {

	public static final String NAME = "float";
	
	public FloatType() {
		super(NAME, 4);
	}

	public boolean isAssignable(Type type) {
		return (type == null) || type instanceof NumericType;
	}

	public boolean isEquivalent(Type type) {
		return type instanceof FloatType;
	}

	public Type newType() {
		return new FloatType();
	}
}

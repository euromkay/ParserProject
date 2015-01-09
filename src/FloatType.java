
public class FloatType extends NumericType {

	public static final String NAME = "float";
	
	public FloatType() {
		super(NAME, 4);
	}

	public Type newType() {
		return new FloatType();
	}
	
	public boolean isFloat(){
		return true;
	}
}

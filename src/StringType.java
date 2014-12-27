
public class StringType extends Type{

	public StringType() {
		super("string", 4);
		
	}

	@Override
	public boolean isAssignable(Type type) {
		return false;
	}

	public Type newType() {
		return new StringType();
	}

}

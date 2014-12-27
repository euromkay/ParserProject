
public class NullType extends Type {

	public NullType() {
		super("null", 4);
	}

	public boolean isAssignable(Type type) {
		return false;
	}

	public Type newType() {
		return new NullType();
	}

}

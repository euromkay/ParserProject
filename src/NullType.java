
public class NullType extends Type {

	public NullType() {
		super("null", 4);
	}

	public boolean isEquivalent(Type type) {
		return true;
	}

	public Type newType() {
		return new NullType();
	}

}


public class VoidType extends Type {

	public VoidType() {
		super("void", 4);
	}
	
	public boolean isEquivalent(Type type) {
		return false;
	}

	public Type newType() {
		return new VoidType();
	}

}

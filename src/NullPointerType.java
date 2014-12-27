
public class NullPointerType extends PointerType {

	@Override
	public Type newType() {
		return new NullPointerType();
	}
	public NullPointerType() {
		super("nullptr", new NullType(), 4);
	}
	public boolean isEquivalent(Type type) {
		return type instanceof PointerType;
	}
	
	public boolean isAssignable(Type type) {
		return type instanceof PointerType;
	}
}

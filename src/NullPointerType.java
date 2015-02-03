
public class NullPointerType extends PointerType {

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

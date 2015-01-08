
public class RValType extends Type {

	public RValType(String strName, int size) {
		super(strName, size);
		this.size = size;
		this.strName = strName;
	}

	String strName;
	int size;
	
	public boolean isAssignable(Type type) {

		return false;
	}

	public Type newType() {
		return new RValType(new String(strName), size);
	}
	
}

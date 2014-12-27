
public abstract class BasicType extends Type {

	public BasicType(String strName, int size) {
		super(strName, size);
	}

	public boolean isBasicType() {
		return true;
	}
}

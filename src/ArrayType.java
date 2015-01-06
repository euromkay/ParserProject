
public class ArrayType extends ArrointType {

	public static final int NO_ARRAY = -1;
	
	private Integer length;
	
	public ArrayType(Type type, int size) {
		super(type.getName() + "[" + size + "]", type, size*type.getSize());
		length = size;
	}
	
	public String getLength(){
		return length.toString();		
	}

	public boolean isArrayType(){
		return true;
	}

	public boolean isEquivalent(Type type) {
		if(!(type instanceof ArrayType)){
			return false;
		}
		ArrayType aT = (ArrayType) type;
		if(aT.length != length)
			return false;
		return (getSubtype().isEquivalent(aT.getSubtype()));
	}

	public Type newType() {
		return new ArrayType(getSubtype(), length); 
	}

	public boolean isAssignable(Type type) {
		if(type == null)
			return true;
		return isEquivalent(type);
	}
	
}

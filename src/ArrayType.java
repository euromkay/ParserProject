
public class ArrayType extends ArrointType {

	public static final int NO_ARRAY = -1;
	
	private int length;
	Type t;
	
	public ArrayType(Type type, int size) {
		super(type.getName() + "[" + size + "]", type, size*type.getSize());
		length = size;
		this.t = type;
	}
	
	public Integer getLength(){
		return length;		
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
		return (this.t.isEquivalent(aT.t));
	}

	public Type newType() {
		return new ArrayType(t.newType(), length); 
	}

	public boolean isAssignable(Type type) {
		if(type == null)
			return true;
		return isEquivalent(type);
	}
	
}

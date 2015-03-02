
public class ArrayType extends ArrointType {

	public static final int NO_ARRAY = -1;
	
	private Integer length;
	
	public ArrayType(Type subType, int size) {
		super(subType.getName() + "$" + size + "$", subType, size*subType.getSize());
		if(subType.isArray()){
			String name = subType.getName();
			int index =name.indexOf("$");
			setName(name.substring(0, index) + "$" + size + "$" + name.substring(index, name.length()));
		}
			
		length = size;
	}
	
	public String getLength(){
		return length.toString();		
	}

	public boolean isArray(){
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

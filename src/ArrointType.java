
public abstract class ArrointType extends CompositeType{

	private Type subType;
	
	
	public ArrointType(String strName, Type subType, int size) {
		super(strName, size);
		this.subType = subType;
	}
	
	public Type getType(){
		return subType;
	}

}

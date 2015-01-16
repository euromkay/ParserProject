import java.util.Vector;

//---------------------------------------------------------------------
// This is the top of the Type hierarchy. You most likely will need to
// create sub-classes (since this one is abstract) that handle specific
// types, such as IntType, FloatType, ArrayType, etc.
//---------------------------------------------------------------------

abstract class Type
{
	// Name of the Type (e.g., int, bool, or some typedef
	private String m_typeName;
	private int m_size;
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Type(String strName, int size)
	{
		setName(strName);
		setSize(size);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public String getName()
	{
		return m_typeName;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	protected void setName(String str)
	{
		m_typeName = str;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Integer getSize()
	{
		return m_size;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	void setSize(int size)
	{
		m_size = size;
	}

	//----------------------------------------------------------------
	//	It will be helpful to ask a Type what specific Type it is.
	//	The Java operator instanceof will do this, but you may
	//	also want to implement methods like isNumeric(), isInt(),
	//	etc. Below is an example of isInt(). Feel free to
	//	change this around.
	//----------------------------------------------------------------
	public boolean	isInt ()	{ return false; }

	public boolean isBool() { return false; }
	
	public boolean isFloat() { return false; }

	public boolean isArray(){
		return false;
	}
	
	public boolean isPointer(){
		return false;
	}
	
	public boolean isAssignable(Type type){
		return isEquivalent(type);
	}

	public boolean isEquivalent(Type type) {
		return getName().equals(type.getName());
	}

	public boolean isBasicType() {
		return false;
	}

	
	

	

	public abstract Type newType();

	public static Type mergeType(Type t, int pointerSize, Vector<STO> arraySTOs) {
		t = mergeType(t, pointerSize);
		t = mergeType(t, arraySTOs);
		return t;
	}
	
	public static Type mergeType(Type t, Vector<STO> arraySTOs) {
		for(int i = arraySTOs.size() - 1; i >= 0; i--){
   	 		STO c = arraySTOs.get(i);
   	 		t = new ArrayType(t, ((ConstSTO) c).getIntValue());
   	 	}
   	 	return t;
	}
	
	public static Type mergeType(Type t, int pointerSize) {
		for(int i = 0; i < pointerSize; i++){
   	 		t = new PointerType(t);
   	 	}
   	 	return t;
	}
}

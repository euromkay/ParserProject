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

	public boolean isBool() {
		return false;
	}

	public abstract boolean isAssignable(Type type);

	public boolean isEquivalent(Type type) {
		return getClass() == type.getClass();
	}

	public boolean isBasicType() {
		return false;
	}

	public boolean isIntAssignable() {
		//TODO
		return this instanceof IntType;
	}
	
	

	public boolean isArrayType(){
		return false;
	}
	
	public boolean isPointerType(){
		return false;
	}

	public abstract Type newType();
}

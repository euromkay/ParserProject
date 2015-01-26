//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class ErrorType extends Type
{
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public ErrorType()
	{
		super("Error", 0);
	}

	//----------------------------------------------------------------
	//	There are times where it is an error if the Type is not a 
	//	assignable, equivalent,  or something else. However, if
	//	the Type is already an error, nothing should be said. To
	//	supress that error, we would have to check if the Type is
	//	not an ErrorType as well as what we want it to be.  Rather
	//	than 2 checks we'll have the ErrorType always return true
	//	for every check.
	//----------------------------------------------------------------
    public boolean isError()            { return true; }



	public Type newType() {
		return this;
	}
	
	public boolean isEquivalent(Type t){
		return true;
	}
	
	public boolean isAssignable(Type t){
		return true;
	}
}

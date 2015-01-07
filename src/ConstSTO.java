import java.math.BigDecimal;

//---------------------------------------------------------------------
//
//---------------------------------------------------------------------

class ConstSTO extends STO
{
    //----------------------------------------------------------------
    //	Constants have a value, so you should store them here.
    //	Note: We suggest using Java's Double class, which can hold
    //	floats and ints. You can then do .floatValue() or 
    //	.intValue() to get the corresponding value based on the
    //	type. Booleans/Ptrs can easily be handled by ints.
    //	Feel free to change this if you don't like it!
    //----------------------------------------------------------------
    private BigDecimal	m_value;
	STO init, init2;
	
	public void setSTOInit(STO s){
		init = s.newSTO();
	}
	
	public STO newSTO() {
		
		ConstSTO c = new ConstSTO(getName(), getType(), m_value);
		if(init != null)
			c.init = init.newSTO();
		if(init2 != null)
			c.init2 = init2.newSTO();
		
		return super.newSTO(c);
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	/*public ConstSTO(String strName)
	{
		super(strName);
		m_value = null; // fix this
	}*/

    /*
	public ConstSTO(String strName, Type typ)
	{
		super(strName, typ);
		m_value = null; // fix this
                // You may want to change the isModifiable and isAddressable
                // fields as necessary
	}
*/

	public ConstSTO(String strName, Type typ, BigDecimal dub)
	{
		super(new String(strName), typ.newType());
		m_value = dub;/*
		if(init != null)
			c.init = init.newSTO();
		if(init2 != null)
			c.init2 = ini2.newSTO();*/
	}
	
	public ConstSTO(String strName)
	{
		super(strName);
		m_value = null; // fix this
		// You may want to change the isModifiable and isAddressable
		// fields as necessary
	}

	public ConstSTO(String strName, Type typ)
	{
		super(strName, typ);
		m_value = null; // fix this
		// You may want to change the isModifiable and isAddressable
		// fields as necessary
	}

	public ConstSTO(String strName, Type typ, int val)
	{
		super(strName, typ);
		m_value = new BigDecimal(val);
		// You may want to change the isModifiable and isAddressable
		// fields as necessary
	}

	public ConstSTO(String strName, Type typ, double val)
	{
		super(strName, typ);
		m_value = new BigDecimal(val);
		// You may want to change the isModifiable and isAddressable
		// fields as necessary
	}
	
	/*
	public ConstSTO(String strName, BoolType typ, boolean result) {
		super(strName, typ);
		m_value = 0.;
	}	*/

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public boolean isConst() 
	{
		return true;
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public BigDecimal getValue() 
	{
		return m_value;
	}

	
	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Integer getIntValue() 
	{
		return m_value.intValue();
	}

	//----------------------------------------------------------------
	//
	//----------------------------------------------------------------
	public Float getFloatValue() 
	{
		return m_value.floatValue();
	}

	public Boolean getBoolValue() 
	{
		return !BigDecimal.ZERO.equals(m_value);;
	}


	
	
	public STO getSTOInit(){
		if(init == null)
			return null;
		return init.newSTO();
	}

	public STO getSTOInit2() {
		if(init == null)
			return null;
		return init2;
	}

	
	

}

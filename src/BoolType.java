import java.math.BigDecimal;


public class BoolType extends BasicType {

	public static final String NAME = "bool";
	public static final BigDecimal TRUE = BigDecimal.ONE;
	public static final BigDecimal FALSE = BigDecimal.ZERO;;

	public BoolType() {
		super("bool", 4);
	}
	

	public static STO getSTO(STO a){
		if(a instanceof ConstSTO)
			return new ConstSTO(a.getName(), a.getType(), BoolType.reverse(a));
		else
			return new ExprSTO(a.getName(), new BoolType());
	}


	public boolean isBool() {
		return true;
	}

	public boolean isAssignable(Type type) {
		return (type == null) || type instanceof BoolType;
		
	}
	
	private static BigDecimal reverse(STO s){
		ConstSTO sto = (ConstSTO) s;
		if(sto.getValue().equals(FALSE))
			return TRUE;
		else
			return FALSE;
	}


	public boolean isEquivalent(Type type) {
		return type instanceof BoolType;
	}


	public static BigDecimal dub(boolean result) {
		if(result)
			return TRUE;
		else
			return FALSE;
	}

	public Type newType() {
		return new BoolType();
	}
	
}

import java.math.BigDecimal;

public abstract class BitWiseOp extends ArithmeticOp {

	public BitWiseOp(String op_symbol, String r_symbol) {
		super(op_symbol, r_symbol);
	}

	public STO checkOperands(STO a, STO b) {
		Type aType = a.getType();
		Type bType = b.getType();
		
		
		if(!(aType instanceof IntType)){
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1w_Expr, aType.getName(), getName(), new IntType().getName()));
		}
		else if(!(bType instanceof IntType)){
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1w_Expr, bType.getName(), getName(), new IntType().getName()));
		}
		else {
			//Returning IntType Result
			if(aType instanceof IntType && bType instanceof IntType){
				if(a instanceof ConstSTO && b instanceof ConstSTO){
					BigDecimal result = getResult((ConstSTO) a,  (ConstSTO) b);
					return new ConstSTO(a.getName() + b.getName(), new IntType(), result);	//Result should be intType
				}
			}
			//Resolve to ExprSTO
			return new ExprSTO(a.getName() + getName() + b.getName(), new IntType());
		}
	}

	
	
	private BigDecimal getResult(ConstSTO aa, ConstSTO bb){
		String op = getName();
		BigDecimal result;
		
		if(op.equals(MOD)){
			result = aa.getValue().remainder(bb.getValue());
		}
		else if(op.equals(CARET)){	
			result = new BigDecimal(aa.getValue().intValue() ^ bb.getIntValue());
		}
		else if(op.equals(AMPERSAND)){
			result = new BigDecimal(aa.getValue().intValue() & bb.getIntValue());
		}
		else {
			result = new BigDecimal(aa.getValue().intValue() | bb.getIntValue());
		}
		return result;
	}
	

}

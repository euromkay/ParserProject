
public class ComparisonOp extends BooleanOp {

	// >=
	
	public ComparisonOp(String op_symbol, String r_symbol) {
		super(op_symbol, r_symbol);
	}

	public STO checkOperands(STO a, STO b) {
		Type aType = a.getType();
		Type bType = b.getType();
		
		
		if(!(aType instanceof NumericType)){
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, aType.getName(), getName()));
		}
		if(!(bType instanceof NumericType)){
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, bType.getName(), getName()));
		}
		else{
			if(a instanceof ConstSTO && b instanceof ConstSTO){
				boolean result = getResult((ConstSTO) a, (ConstSTO) b); 
				return new ConstSTO(a.getName() + " " + getSymbol() + " " + b.getName(), new BoolType(), BoolType.dub(result));
			}
			else
				return new ExprSTO(a.getName() + " " + getSymbol() + " " + b.getName(), new BoolType());
		}
	}

	private boolean getResult(ConstSTO aa, ConstSTO bb){
		String op = getName();
		boolean result;
		
		if(op.equals(LT)){
			result = aa.getValue().compareTo(bb.getValue()) < 0;
		}
		else if(op.equals(LTE)){
			result = aa.getValue().compareTo(bb.getValue()) <= 0;
		}
		else if(op.equals(GT)){
			result = aa.getValue().compareTo(bb.getValue()) >= 0;
		}
		else {
			result = aa.getValue().compareTo(bb.getValue()) >= 0;
		}
		return result;
	}
}

public class BitWiseOp extends ArithmeticOp {

	public BitWiseOp(String op_symbol) {
		super(op_symbol);
	}

	public STO checkOperands(STO a, STO b) {
		Type aType = a.getType();
		Type bType = b.getType();
		
		
		if(!(aType instanceof IntType)){
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, aType.getName(), getName()));
		}
		else if(!(bType instanceof IntType)){
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, bType.getName(), getName()));
		}
		else {
			//Returning IntType Result
			if(aType instanceof IntType && bType instanceof IntType){
				if(a instanceof ConstSTO && b instanceof ConstSTO){
					double result = getResult((ConstSTO) a,  (ConstSTO) b);
					return new ConstSTO(a.getName() + b.getName(), new IntType(), result);	//Result should be intType
				}
			}
			//Resolve to ExprSTO
			return new ExprSTO(a.getName() + getName() + b.getName(), new IntType());
		}
	}

	
	
	private int getResult(ConstSTO aa, ConstSTO bb){
		String op = getName();
		int result;
		
		if(op.equals(MOD)){
			result = (int) ((aa.getValue()) % (bb.getValue()));
		}
		else if(op.equals(CARET)){		
			result = ((aa.getIntValue()) ^ (bb.getIntValue()));
		}
		else if(op.equals(AMPERSAND)){
			result = ((aa.getIntValue()) & (bb.getIntValue()));
		}
		else {
			result = ((aa.getIntValue()) | (bb.getIntValue()));
		}
		return result;
	}
	

}

import java.math.BigDecimal;
import java.math.RoundingMode;


public abstract class ArithmeticOp extends BinaryOp {

	public ArithmeticOp(String op_symbol, String r_symbol) {
		super(op_symbol, r_symbol);
	}
	
	public STO checkOperands(STO a, STO b) {
		Type aType = a.getType();
		Type bType = b.getType();
		
		//Check STOs are NumericTypes, report appropriate errors
		if(!(aType instanceof NumericType))
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, aType.getName(), getName()));
		
		
		else if(!(bType instanceof NumericType))
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, bType.getName(), getName()));
		
		
		else {
			//Returning IntType Result
			if(aType instanceof IntType && bType instanceof IntType){
				if(a instanceof ConstSTO && b instanceof ConstSTO){
					try{
						BigDecimal result = getResult((ConstSTO) a, (ConstSTO) b);
						return new ConstSTO(a.getName() + " " + getSymbol() + " " + b.getName(), new IntType(), result);
					}
					catch (ArithmeticException e){
						return new ErrorSTO(ErrorMsg.error8_Arithmetic);
					}
				}
				else	//Resolve to ExprSTO
					return new ExprSTO(a.getName() + " " + getSymbol() + " " + b.getName(), new IntType());
			}
			//Returning FloatType Result
			else{
				if(a instanceof ConstSTO && b instanceof ConstSTO){
					try{
						BigDecimal result = getResult((ConstSTO) a, (ConstSTO) b);
						return new ConstSTO(a.getName() + " " + getSymbol() + " " + b.getName(), new FloatType(), result);
					}
					catch (ArithmeticException e){
						return new ErrorSTO(ErrorMsg.error8_Arithmetic);
					}
				}
				else{	//Resolve to ExprSTO
					return new ExprSTO(a.getName() + " " + getSymbol() + " " + b.getName(), new FloatType());
				}
			}
		}
	}
		
	private BigDecimal getResult(ConstSTO aa, ConstSTO bb){
		String op = getName();
		BigDecimal result;
		
		if(op.equals(PLUS)){
			result = aa.getValue().add(bb.getValue());
		}
		else if(op.equals(MINUS)){
			result = aa.getValue().subtract(bb.getValue());
		}
		else if(op.equals(STAR)){
			result = aa.getValue().multiply(bb.getValue());
		}
		else {
			if (bb.getValue().equals(BigDecimal.ZERO))
				throw new ArithmeticException();
			result = aa.getValue().divide(bb.getValue(), RoundingMode.HALF_UP);
		}
		return result;
	}
	
	
}

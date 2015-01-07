
public abstract class ArithmeticOp extends BinaryOp {

	public ArithmeticOp(String op_symbol) {
		super(op_symbol);
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
						double result = getResult((ConstSTO) a, (ConstSTO) b);
						return new ConstSTO(a.getName() + " " + getName() + " " + b.getName(), new IntType(), result);
					}
					catch (ArithmeticException e){
						return new ErrorSTO(ErrorMsg.error8_Arithmetic);
					}
				}
				else	//Resolve to ExprSTO
					return new ExprSTO(a.getName() + " " + getName() + " " + b.getName(), new IntType());
			}
			//Returning FloatType Result
			else{
				if(a instanceof ConstSTO && b instanceof ConstSTO){
					try{
						double result = getResult((ConstSTO) a, (ConstSTO) b);
						return new ConstSTO(a.getName() + " " + getName() + " " + b.getName(), new FloatType(), result);
					}
					catch (ArithmeticException e){
						return new ErrorSTO(ErrorMsg.error8_Arithmetic);
					}
				}
				else{	//Resolve to ExprSTO
					return new ExprSTO(a.getName() + " " + getName() + " " + b.getName(), new FloatType());
				}
			}
		}
	}
		
	private double getResult(ConstSTO aa, ConstSTO bb){
		String op = getName();
		double result;
		
		if(op.equals(PLUS)){
			result = aa.getValue() + bb.getValue();
			//System.out.println("Plus" + " result: " + result + "\n");
		}
		else if(op.equals(MINUS)){
			result = aa.getValue() - bb.getValue();
			//System.out.println("Minus" + " result: " + result + "\n");
		}
		else if(op.equals(STAR)){
			result = aa.getIntValue() * bb.getIntValue();
			//System.out.println("Multiply" + " result: " + result + "\n");
		}
		else {
			if (bb.getValue() == 0 || bb.getValue() == 0.0)
				throw new ArithmeticException();
			result = aa.getValue() / bb.getValue();
			//System.out.println("Divide" + " result: " + result + "\n");
		}
		return result;
	}
	
	
}

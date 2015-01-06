
public abstract class UnaryOp extends Operator {

	public UnaryOp(String op_symbol) {
		super(op_symbol);
	}

	//b is ignored. a needs to be a numeric type to inc or dec
	public STO checkOperands(STO a, STO b) {
		return checkOperands(a);
	}
	
	private STO checkOperands(STO a){
		String operand = getName();
		Type aType = a.getType();
		if(operand.equals(PLUS_POST) || operand.equals(MINUS_PRE) || operand.equals(MINUS_POST) || operand.equals(PLUS_PRE)){
			if(!(aType instanceof NumericType) && !(aType instanceof PointerType)){
				return new ErrorSTO(Formatter.toString(ErrorMsg.error2_Type, aType.getName(), getName()));
			}
			else if(!a.isModLValue()){
				return new ErrorSTO(Formatter.toString(ErrorMsg.error2_Lval, operand));
			}
			else
				return new ExprSTO(a.getName() + operand, aType);
		}
		else{
			if(!(aType instanceof NumericType) && !(aType instanceof PointerType)){
				return new ErrorSTO(Formatter.toString(ErrorMsg.error2_Type, aType.getName(), getName()));
			}
			else{ 
				if(a instanceof ConstSTO){
					ConstSTO cSTO = (ConstSTO) a;
					if(getName().equals(U_MINUS)){
						return new ConstSTO(a.getName(), a.getType(), cSTO.getValue() * -1);
					}
				}
				return new ExprSTO(getName() + a.getName(), a.getType());
			}
		}
			
	}

}

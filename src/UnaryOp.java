
public abstract class UnaryOp extends Operator {

	public UnaryOp(String op_symbol, String r_symbol) {
		super(op_symbol, r_symbol);
	}

	//b is ignored. a needs to be a numeric type to inc or dec
	public STO checkOperands(STO a, STO b) {
		return checkOperands(a);
	}
	
	private STO checkOperands(STO a){
		String operand = getName();
		Type aType = a.getType();
		boolean post = operand.equals(PLUS_POST) || operand.equals(MINUS_POST);
		if(operand.equals(PLUS_POST) || operand.equals(MINUS_PRE) || operand.equals(MINUS_POST) || operand.equals(PLUS_PRE)){
			if(!(aType instanceof NumericType) && !(aType instanceof PointerType)){
				return new ErrorSTO(Formatter.toString(ErrorMsg.error2_Type, aType.getName(), getSymbol()));
			}
			else if(!a.isModLValue()){
				return new ErrorSTO(Formatter.toString(ErrorMsg.error2_Lval, getSymbol()));
			}
			else{
				String name = a.getName();
				if(post)
					name += getSymbol();
				else
					name = getSymbol() + name;
				return new ExprSTO(name, aType);
			}
		}
		else{
			if(!(aType instanceof NumericType) && !(aType instanceof PointerType)){
				return new ErrorSTO(Formatter.toString(ErrorMsg.error2_Type, aType.getName(), getSymbol()));
			}
			else{ 
				if(a instanceof ConstSTO){
					ConstSTO cSTO = (ConstSTO) a;
					if(getName().equals(U_MINUS)){
						return new ConstSTO(getSymbol() + a.getName(), a.getType(), cSTO.getValue().negate());
					}
				}
				return new ExprSTO(getSymbol() + a.getName(), a.getType());
			}
		}
			
	}

}

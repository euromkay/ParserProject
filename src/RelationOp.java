
public abstract class RelationOp extends BinaryOp {

	public RelationOp(String op_symbol) {
		super(op_symbol);
	}

	@Override
	public STO checkOperands(STO a, STO b) {
		Type aType = a.getType();
		Type bType = b.getType();
		
		if(!(aType instanceof BoolType)){
			return 
				new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, aType.getName(), getName()));
		}
		if(!(bType instanceof BoolType)){
			return 
				new ErrorSTO(Formatter.toString(ErrorMsg.error1n_Expr, bType.getName(), getName()));
		}
		//trying to return a float type
		else{
			if(a instanceof ConstSTO && b instanceof ConstSTO){
				boolean result = getResult((ConstSTO) a, (ConstSTO) b);
				return new ConstSTO(a.getName() + b.getName(), new BoolType(), BoolType.dub(result));
			}
			return 
				new ExprSTO(a.getName() + b.getName(), new BoolType());
		}
			
		
	}

	private boolean  getResult(ConstSTO aa, ConstSTO bb){
		String op = getName();
		boolean result = false;
		if(op.equals(OR)){
			result = aa.getBoolValue() || bb.getBoolValue();
		}
		else if(op.equals(AND)){
			result = aa.getBoolValue() && bb.getBoolValue();
		}
		return result;
	}
}

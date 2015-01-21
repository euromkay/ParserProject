
public class EqualityOp extends BooleanOp{

	//!=
	//==
	
	public EqualityOp(String op_symbol) {
		super(op_symbol, op_symbol);
	}

	public STO checkOperands(STO a, STO b) {
		Type aType = a.getType();
		Type bType = b.getType();
		
		if((aType instanceof NumericType && bType instanceof NumericType) ||
			(bType instanceof BoolType && aType instanceof BoolType) ||
			(bType instanceof PointerGroupType && aType instanceof PointerGroupType)){
			if(bType instanceof PointerGroupType && aType instanceof PointerGroupType){
				if(!bType.isEquivalent(aType))
					return new ErrorSTO(Formatter.toString(ErrorMsg.error17_Expr, getName(), aType.getName(), bType.getName()));
			}
			if(a instanceof ConstSTO && b instanceof ConstSTO){
				ConstSTO aa = (ConstSTO) a;
				ConstSTO bb = (ConstSTO) b;
				boolean bbb = aa.getValue() == bb.getValue();
				return new ConstSTO(a.getName() + getName() + b.getName(), new BoolType(), BoolType.dub(bbb));
			}
			return new ExprSTO(a.getName() + getName() + b.getName(), new BoolType());//What is the default name we need here for the strName?
		}
		
		else {
			if(aType instanceof PointerType || bType instanceof PointerType)
				return new ErrorSTO(Formatter.toString(ErrorMsg.error17_Expr, getName(), aType.getName(), bType.getName()));
			
			return new ErrorSTO(Formatter.toString(ErrorMsg.error1b_Expr, aType.getName(), getName(), bType.getName()));
		}
		
	}

}

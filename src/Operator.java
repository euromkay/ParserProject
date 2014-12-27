
public abstract class Operator {

	public static final String PLUS = "+";
	public static final String SLASH = "/";
	public static final String STAR = "*";
	public static final String MINUS = "-";
	public static final String OR = "||";
	public static final String AND = "&&";
	public static final String LT = "<";
	public static final String LTE = "<=";
	public static final String GT = ">";
	public static final String GTE = ">=";
	public static final String EQ = "==";
	public static final String NEQ = "!=";
	public static final String MOD = "%";
	public static final String CARET = "^";
	public static final String AMPERSAND = "&";
	public static final String BAR = "|";
	public static final String U_MINUS = ".-";
	public static final String U_PLUS = ".+";
	public static final String PLUS_POST = "P+";
	public static final String PLUS_PRE = "+P";
	public static final String MINUS_PRE = "-P";
	public static final String MINUS_POST = "P-";
	
	
	private String op_symbol;
	
	public String getName(){
		return op_symbol;
	}
	
	public Operator(String op_symbol){
		this.op_symbol = op_symbol;
	}
	
	public abstract STO checkOperands(STO a, STO b);

	public static Operator newOp(String string) {
		if(isEqual(string, PLUS, MINUS, SLASH, STAR))
			return new ArithmeticOp(string);
		
		else if(isEqual(string, MOD, CARET, AMPERSAND, BAR))
			return new BitWiseOp(string);

		else if(isEqual(string, LT, GT, LTE, GTE))
			return new ComparisonOp(string);
		
		else if(isEqual(string, EQ, NEQ))
			return new RelationOp(string);
		
		else if(isEqual(string, OR, AND))
			return new BooleanOp(string);
		
		else if(isEqual(string, U_PLUS, U_MINUS))
			return new UnaryOp(string);
		
		else if(isEqual(string, PLUS_PRE, PLUS_POST, MINUS_PRE, MINUS_POST)) 
			return new UnaryOp(string);
		
		throw new ParserException(string);
		
		
	}

	public static boolean isEqual(String input, String ... list){
		for(String possible: list){
			if(input.equals(possible))
				return true;
		}
		return false;
	}

	public String getTemplate(boolean flag) {
		String s = op_symbol;
		if(op_symbol.equals(LT))
			s = Template.BLT;
		else if(op_symbol.equals(GT))
			s= Template.BGT;
		else if(op_symbol.equals(GTE))
			s = Template.BGE;
		else if(op_symbol.equals(LTE))
			s = Template.BLE;
		else if(op_symbol.equals(NEQ))
			s = Template.BNE;
		else if(op_symbol.equals(EQ))
			s = Template.BE;
		
		if(!flag)
			return s;
		else
			return s.substring(0, 1) + "f" + s.substring(1);
	}
	
}

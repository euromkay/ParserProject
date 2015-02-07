
public abstract class Operator {

	public static final String PLUS = "+";
	public static final String SLASH = "/";//divide
	public static final String STAR = "*";//multiply
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
	public static final String U_MINUS = ".-";//negate
	public static final String U_PLUS = ".+";//doesnt do anything
	public static final String PLUS_POST = "P+";
	public static final String PLUS_PRE = "+P";
	public static final String MINUS_PRE = "-P";
	public static final String MINUS_POST = "P-";
	
	
	private String op_symbol;
	
	public String getName(){
		return op_symbol;
	}
	
	public Operator(String op_symbol, String r_symbol){
		this.op_symbol = op_symbol;
		symbol = r_symbol;
	}
	
	public abstract STO checkOperands(STO a, STO b);

	

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

	public abstract void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p);

	public static Operator newOp(String string) {
		if(string.equals(Operator.PLUS_POST))
			return new PlusPostOp();
		if(string.equals(Operator.PLUS_PRE))
			return new PlusPreOp();
		if(string.equals(Operator.MINUS_PRE))
			return new MinusPreOp();
		else
			return new MinusPostOp();
	}

	private String symbol;
	public String getSymbol(){
		return symbol;
	}
	
}

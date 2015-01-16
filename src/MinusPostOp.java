public class MinusPostOp extends UnaryOp{

	public MinusPostOp() {
		super(Operator.MINUS_POST, "--");
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		result.store(a1, writer);
		MyParser.incDecHelper(a, a1, MyParser.DEC, f_flag, writer);
		a.store(a1, writer);
	}
		
}
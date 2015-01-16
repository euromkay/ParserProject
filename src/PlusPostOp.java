public class PlusPostOp extends UnaryOp{

	public PlusPostOp() {
		super(Operator.PLUS_POST, "++");
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		result.store(a1, writer);
		MyParser.incDecHelper(a, a1, MyParser.INC, f_flag, writer);
		a.store(a1, writer);
	}
		
}
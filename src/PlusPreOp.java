public class PlusPreOp extends UnaryOp{

	public PlusPreOp() {
		super(Operator.PLUS_PRE);
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		MyParser.incDecHelper(a, a1, MyParser.INC, f_flag, writer);
		result.store(a1, writer);
		a.store(a1, writer);
	}
		
}
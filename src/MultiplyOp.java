public class MultiplyOp extends ArithmeticOp{

	public MultiplyOp() {
		super(Operator.STAR, "*");
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		writer.multiOp(a1, a2, res, f_flag);
		result.store(res, writer);
	}
		
}
public class PlusOp extends ArithmeticOp{

	public PlusOp() {
		super(Operator.PLUS);
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		writer.addOp(a1, a2, res, f_flag);
		result.store(res, writer);
	}
		
}
public class DivideOp extends ArithmeticOp{

	public DivideOp() {
		super(Operator.SLASH);
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		writer.divOp(a1, a2, res, f_flag);
		result.store(res, writer);
	}
		
}
public class BarOp extends BitWiseOp{

	public BarOp() {
		super(Operator.BAR, "|");
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		writer.orOp(a1, a2, res);
		result.store(res, writer);
	}
		
}
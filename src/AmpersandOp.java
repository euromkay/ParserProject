public class AmpersandOp extends BitWiseOp{

	public AmpersandOp() {
		super(Operator.AMPERSAND);
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		writer.andOp(a1, a2, res);
		result.store(res, writer);
	}
		
}
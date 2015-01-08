public class PosOp extends UnaryOp{

	public PosOp() {
		super(Operator.U_PLUS);
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		result.store(res, writer);
	}
		
}
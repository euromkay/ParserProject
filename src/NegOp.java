public class NegOp extends UnaryOp{

	public NegOp() {
		super(Operator.U_MINUS, "-");
	}

	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		writer.negOp(a1, res, f_flag);		
		result.store(res, writer);
	}
		
}
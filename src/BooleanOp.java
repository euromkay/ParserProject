
public abstract class BooleanOp extends BinaryOp {

	public BooleanOp(String op_symbol, String r_symbol) {
		super(op_symbol, r_symbol);
	}


	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		Address res_a = writer.getAddressManager().getAddress();
		writer.cmp(a1, a2, f_flag);
		
		writer.write_special(getTemplate(f_flag), MyParser.BRANCH + p.literalCount);
		writer.nop();
		writer.newLine();
		
		writer.set("0", res_a);
		writer.ba(MyParser.BRANCH + p.literalCount + ".DONE");
		writer.newLine();
		writer.label(MyParser.BRANCH + p.literalCount);
		writer.set("1", res_a);
		writer.label(MyParser.BRANCH + p.literalCount + ".DONE");
		p.literalCount++;	

		p.store(result, res_a);
		
		res_a.release();
	}

}

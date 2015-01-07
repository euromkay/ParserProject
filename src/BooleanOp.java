
public abstract class BooleanOp extends BinaryOp {

	public BooleanOp(String op_symbol) {
		super(op_symbol);
	}


	public void writeSparc(STO a, STO b, STO result, Address a1, Address a2, Address res, Boolean f_flag, MyParser p) {
		Writer writer = p.getWriter();
		Address res_a = writer.getAddressManager().getAddress();
		writer.cmp(a1, a2, f_flag);
		
		writer.write(getTemplate(f_flag), MyParser.BRANCH + p.literalCount);
		writer.write(Template.NOP);
		writer.newLine();
		
		writer.set("0", res_a);
		writer.ba(MyParser.BRANCH + p.literalCount + ".DONE");
		writer.newLine();
		writer.label(MyParser.BRANCH + p.literalCount);
		writer.set("1", res_a);
		writer.label(MyParser.BRANCH + p.literalCount + ".DONE");
		p.literalCount++;	

		res_a.release();
	}

}

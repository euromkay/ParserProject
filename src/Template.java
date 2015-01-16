
public class Template {
	private static final String align = "\t.align 4\n";
	//SECTIONS
	public static final String TEXT = "\t.section \".text\"\n" + align;
	public static final String DATA = "\t.section \".data\"\n";
	//public static final String BSS = "\t.section \".bss\"\n"+ align;
	public static final String RODATA = "\t.section \".rodata\"\n"+ align;

	public static final String GLOBAL = "\t.global %s\n";
	public static final String ALIGN = "\t.align %s\n";				//for sizeof, sometimes not 4

	public static final String INT_VAR_DECL = "%s:\t.word %s\n"; 		//ex.	x:	.word 0
	public static final String FLOAT_VAR_DECL = "%s:\t.single 0r%s\n"; 		//ex.	x:	.word 0
	public static final String SKIP = "%s:\t.skip %s\n";
	
	public static final String LABEL_COMMENT = "! ---%s---\n";
	public static final String LABEL = "%s:\n";						

	public static final String MOV   = "\tmov\t\t\t%s, %s\n";;
	public static final String SET   = "\tset\t\t\t%s, %s\n";
	public static final String STORE = "\tst\t\t\t%s, [%s]\n";
	public static final String LOAD  = "\tld\t\t\t[%s], %s\n";
	public static final String SAVE  = "\tsave\t\t%s, %s, %s\n";
	public static final String SAVEFUNC = "\tSAVE.%s = -(92 + %s) & -8\n";;
	
	//ARITHMETIC OPS
	public static final String ADD = "\tadd\t\t\t%s, %s, %s\n";;
	public static final String SUB = "\tsub\t\t\t%s, %s, %s\n";;
	public static final String XOR = "\txor\t\t\t%s, %s, %s\n";;
	public static final String AND = "\tand\t\t\t%s, %s, %s\n";;
	public static final String OR  = "\tor\t\t\t%s, %s, %s\n";;
	public static final String INC = "\tinc\t\t\t%s\n";
	public static final String DEC = "\tdec\t\t\t%s\n";
	
	public static final String NEG = "\tneg\t\t\t%s, %s\n";
	
	//FLOAT OPS
	public static final String FADD = "\tfadds\t\t%s, %s, %s\n";
	public static final String FSUB = "\tfsubs\t\t%s, %s, %s\n";
	public static final String FMUL = "\tfmuls\t\t%s, %s, %s\n";
	public static final String FDIV = "\tfdivs\t\t%s, %s, %s\n";
	public static final String FNEG = "\tfnegs\t\t\t%s, %s \n";
	public static final String FABS = "\tfabss\t\t\t%s, %s \n";

	public static final String FSTOI = "\tfstoi\t\t%s, %s\n";
	public static final String FITOS = "\tfitos\t\t\t%s, %s\n";
	public static final String FMOVS = "\tfmovs\t\t\t%s, %s\n";
	public static final String FCMPS = "\tfcmps\t\t\t%s, %s\n";
	
	
	public static final String CALL = "\tcall\t\t%s\n";
	public static final String NOP = "\tnop\n";
	

	public static final String RET = "\tret\n";
	public static final String RESTORE = "\trestore\n\n";
	
	//ASCIZ
	public static final String ASCIZ = "%s:\t.asciz %s\n";
	public static final String STRFORMAT = "_strFmt";
	public static final String INTFORMAT = "_intFmt";
	public static final String BOOLT = "_boolT";
	public static final String BOOLF = "_boolF";
	public static final String ARRAY_ERROR_FORMAT = "arrrray_error";
	public static final String ARRAY_DEL_FORMAT = "del_errrror";
	
	//BRANCHES
	public static final String BNE = "\tbne\t\t\t%s\n";
	public static final String BE = "\tbe\t\t\t%s\n";
	public static final String BGE = "\tbge\t\t\t%s\n";
	public static final String BGT = "\tbg\t\t\t%s\n";
	public static final String BLT = "\tbl\t\t\t%s\n";
	public static final String BLE = "\tble\t\t\t%s\n";
	public static final String BA = "\tba\t\t\t%s\n";
	
	

	public static final String CMP = "\tcmp\t\t\t%s, %s\n";
	
	//REGISTERS
	public static final String SP = "%sp";
	public static final String FP = "%fp";
	
	public static final String ENDL = "\"\\n\"";
	

	
	//Global Registers
	public static final String G0 = "%g0";	//Always Returns 0
	public static final String G1 = "%g1";
	public static final String G2 = "%g2";
	public static final String G3 = "%g3";
	public static final String G4 = "%g4";
	public static final String G5 = "%g5";
	public static final String G6 = "%g6";
	public static final String G7 = "%g7";
	
	
	
	//FuncArg Registers
	public static final String I0 = "%i0";
	public static final String I1 = "%i1";
	public static final String I2 = "%i2";
	public static final String I3 = "%i3";
	public static final String I4 = "%i4";
	public static final String I5 = "%i5";
	
	//FuncReturn Address
	public static final String I7 = "%i7";
	
	//SubArg Registers
	public static final String O0 = "%o0";
	public static final String O1 = "%o1";
	public static final String O2 = "%o2";
	public static final String O3 = "%o3";
	public static final String O4 = "%o4";
	public static final String O5 = "%o5";

	//Subroutine Return Address
	public static final String F0 = "%f0";
	public static final String F1 = "%f1";
	public static final String F2 = "%f2";
	public static final String F3 = "%f3";
	public static final String F4 = "%f4";
	public static final String F5 = "%f5";
	public static final String F6 = "%f6";
	public static final String F7 = "%f7";
	public static final String F8 = "%f8";
	public static final String F9 = "%f9";
	public static final String F10 = "%f10";
	public static final String F11 = "%f11";
	public static final String F12 = "%f12";
	public static final String F13 = "%f13";
	public static final String F14 = "%f14";
	public static final String F15 = "%f15";
	public static final String F16 = "%f16";
	public static final String F17 = "%f17";
	public static final String F18 = "%f18";
	public static final String F19 = "%f19";
	public static final String F20 = "%f20";
	public static final String F21 = "%f21";
	public static final String F22 = "%f22";
	public static final String F23 = "%f23";
	public static final String F24 = "%f24";
	public static final String F25 = "%f25";
	public static final String F26 = "%f26";
	public static final String F27 = "%f27";
	public static final String F28 = "%f28";
	public static final String F29 = "%f29";
	public static final String F30 = "%f30";
	public static final String F31 = "%f31";
	
	
	
	
			
			
}

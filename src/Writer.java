
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 * An Assembly Code Generator example emphasizing well thought design and
 * the use of java 1.5 constructs.
 * 
 * Disclaimer : This is not code meant for you to use directly but rather
 * an example from which I hope you can learn useful constructs and
 * conventions.
 * 
 * Topics of importance (Corrosponding to the inline comment numbers below)
 * 
 * 1) We will use this variable to denote the current level of indentation.
 *    For assembly there is usually only one or two levels of nesting.
 * 
 * 2) A collection of static final error messages.  This isn't so important in
 *    your project but is useful.  The static keyword means we only make 3 
 *    strings, shared across potentially multiple Writer(s).
 *    It is Java convention to spell constant variables with upper casing.
 *    
 * 3) FileWriter is a basic IO class which can write basic types such as
 *    Strings to a file.  For performance you may want to look into the
 *    BufferedWriter class.
 *    
 * 4) This is a template for our file header.  It is very basic consisting only
 *    of a time stamp.
 *    
 * 5) This is the string we will use as an indentation seperator.  We are 
 *    encapsulating this seperator into one variable so we only need to change
 *    the initialization if we want to change our spacing to say 4 spaces for
 *    example.  Imagine if you simply used the literal "\t" in 500 places and
 *    then you decide you want to change it to 4 spaces!  Aside from regular 
 *    expressions you have a lot of work ahead of you.
 *    
 * 6) These are constant String templates that will be used for code
 *    generation.  It is nice to isolate these in one place or even in another
 *    file so that we can quickly make universal changes if needed.  You will
 *    notice that we could generate an entirely different language by simply
 *    changing the construct definitions.  I recommend defining all operations
 *    as well as formats.  Operations are things like add, mul, set, etc.
 *    Formats are like {OPERATION} {REG_1}, {REG_2}, {REG_3} etc.
 *    
 * 7) Here we are making a call to writeAssembly to write our header with the
 *    current time.  writeAssembly explained later.
 *    
 * 8) These methods are used to increase or decrease our current indentation
 *    level.  You might ask why make a method for a simple inc/dec?  We are
 *    encapsulating the notion of adjusting indentation.  It just so happens
 *    that this current implementation is just a variable increment or 
 *    decrement, but who is to say that the operation won't be more advanced
 *    in the future.  Maybe we want to log a message everytime we increment
 *    or decrement indentation.  We wouldn't want to add the logging code
 *    everywhere we were incrementing the variable (if we didn't have the
 *    methods).
 *    
 * 9) This signature may look foreign to you.  What is says is that we have 
 *    public method named writeAssembly which takes as parameters a String
 *    followed by 1 or more strings.  This construct is called "VarArgs" and
 *    is a Java 1.5 feature.  This allows you to write one method which can
 *    be applied to any number of parameters.  This method simply takes in 
 *    a template and all the strings that will be substituted into the 
 *    template.  When you are actually in the method, the parameter 
 *    String ... params will be an array of strings.
 *    
 * 10) This is where we use our indent_level.  We will indent indent_level levels
 *     of indentation.  That is an awkward sentence isn't it!  StringBuilder is
 *     an efficient class to build strings from concatentations.  If your 
 *     concatenations span multiple lines of code, using a StringBuilder can
 *     offer signifigant performance when compared to using the + operator.
 *     This topic can get fairly detailed, send me an email or come talk to me
 *     in the lab for more details.
 * 
 * 11) Here we are writing the message to file, notice we are using the 
 *     String.format method which takes a printf like format string followed
 *     by an array of Objects which are the parameters to the format string.
 *     
 * 12) Main is just a small demo that will create a tiny assembly file in the
 *     current directory called "output.s".  This file doesn't compile and is
 *     not meant to.
 * 
 * @author Evan Worley
 */
// 12
/*public static void main(String args[]) {
    Writer myAsWriter = new Writer("output.s");

    /*myAsWriter.increaseIndent();
    myAsWriter.writeAssembly(TWO_PARAM, SET_OP, String.valueOf(4095), "%l0");
    myAsWriter.increaseIndent();
    myAsWriter.writeAssembly(TWO_PARAM, SET_OP, String.valueOf(1024), "%l1");
    myAsWriter.decreaseIndent();
    
    myAsWriter.writeAssembly(TWO_PARAM, SET_OP, String.valueOf(512), "%l2");
    
    myAsWriter.decreaseIndent();
    myAsWriter.dispose();
}*/
public class Writer {
    // 1
    private int indent_level = 0;
    
    // 2
    private static final String ERROR_IO_CLOSE = 
        "Unable to close fileWriter";
    private static final String ERROR_IO_CONSTRUCT = 
        "Unable to construct FileWriter for file %s";
    private static final String ERROR_IO_WRITE = 
        "Unable to write to fileWriter";

    // 3
    private FileWriter fileWriter;
    
    // 4
    private static final String FILE_HEADER = 
        "/*\n" +
        " * Generated %s\n" + 
        " */\n\n";
        
    // 5
    private static final String SEPARATOR = "\t";
    
    
    private SymbolTable symTab;
    private AddressManager am;
    
    public AddressManager getAddressManager(){
    	return am;
    }
    
    public Writer(SymbolTable m_symtab, AddressManager am){
    	this("rc.s");
    	symTab = m_symtab;
    	writeUsefulAsciz();
    	
    	write(Template.DATA);
		write(Template.ALIGN, "4");
		write(Template.INT_VAR_DECL, GLOBAL_INIT, "0");
		newLine();
    }
    public static final String GLOBAL_INIT = ".globalInit";
    
    
    
    public void writeUsefulAsciz(){
    	write(Template.RODATA);
    	write(Template.ASCIZ, "_endl", Template.ENDL);
    	write(Template.ASCIZ, Template.INTFORMAT, "\"%d\"");
    	write(Template.ASCIZ, Template.STRFORMAT, "\"%s\"");
    	write(Template.ASCIZ, Template.BOOLT, "\"true\"");
    	write(Template.ASCIZ, Template.BOOLF, "\"false\"");
    	write(Template.ASCIZ, Template.ARRAY_ERROR_FORMAT, "\"Index value of %d is outside legal range [0,%d).\\n\"");
    	write(Template.ASCIZ, Template.ARRAY_DEL_FORMAT, "\"Attempt to dereference NULL pointer.\\n\"");
    	newLine();
    }
    
    public Writer(String fileToWrite) {
        try {
            fileWriter = new FileWriter(fileToWrite);
            
            // 7
            write(FILE_HEADER, (new Date()).toString());
        } catch (IOException e) {
            System.err.printf(ERROR_IO_CONSTRUCT, fileToWrite);
            e.printStackTrace();
            System.exit(1);
        }
    }
    

    // 8
    public void decreaseIndent() {
        indent_level--;
    }
    
    public void finish(){
    	  try {
              fileWriter.close();
          } catch (IOException e) {
              System.err.println(ERROR_IO_CLOSE);
              e.printStackTrace();
              System.exit(1);
          }
    }
    
    public void dispose() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.err.println(ERROR_IO_CLOSE);
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    public void increaseIndent() {
        indent_level++;
    }
    

    
    // 9
    private void writeNow(String template, String ... params) {
        
    	String asStmt;
        if(params.length == 0)
        	asStmt = template;
        else
        	asStmt = convert(template, params);
        
        
        try {
            fileWriter.write(asStmt);
        } catch (IOException e) {
            System.err.println(ERROR_IO_WRITE);
            e.printStackTrace();
        }
    }
    
    private String convert(String template, String ... params){
        StringBuilder asStmt = new StringBuilder();

        // 10
        for (int i=0; i < indent_level; i++) {
            asStmt.append(SEPARATOR);
        }
        
        // 11
        asStmt.append(String.format(template, (Object[])params));
        
        return asStmt.toString();
    }
    
    public void newLine(){
    	write("\n");
    }
    
    
  

	public boolean isGlobal() {
		if(symTab == null)
			return false;
		return symTab.getLevel() == 1;
	}

	Integer localVarSpace = 0;
	
	public void addLocalVars(ArrayList<STO> list) {
		for(STO s: list){
			addSTO(s);
		}
		
	}
	
	public void addSTO(STO s){
		if(s instanceof ErrorSTO)
			return;
		if(isGlobal()){
			if(symTab.accessGlobal(s.getName()) != null){
				s.setAddress(s.getName());
			}else{
				globalVarSpace += s.getType().getSize();
				s.setAddress("%fp-" + globalVarSpace);
				s.setOffset(globalVarSpace.toString());
			}
		}
		else{
			localVarSpace += s.getType().getSize();
			s.setAddress("%fp-" + localVarSpace);
			s.setOffset(localVarSpace.toString());
		}
	}
	
	
	
	


	public boolean writeLater = false;
	
	
	public void write(String template, String ... params){
		if(writeLater  || isGlobal() && getSection() == TEXT)
			pWrites.add(convert(template, params));
		else
			writeNow(template, params);
	}
	

	public static final int RODATA = 1, DATA = 2, TEXT = 3;
	private int section;
	public int getSection() {
		return section;
	}

	public void changeSection(int sec) {
		this.section = sec;
		if(section == DATA){
			write(Template.DATA);
		}else if(section == RODATA)
			write(Template.RODATA);
		else
			write(Template.TEXT);
	}

	public void call(String function) {
		write(Template.CALL, function);
		write(Template.NOP);
	}

	//bracketed a1, nonbrack a2
	public void ld(Address a1, Address a2) {
		write(Template.LOAD, a1.toString(), a2.toString());
		
	}
	
	public void load(Address a1, Address a2) {
		ld(a1, a2);
		
	}

	public void set(String a1, Address a2) {
		if(a1.startsWith("%") && a2.isRegister()){ // means both registers. can't set from reg to reg
			write(Template.MOV, a1, a2.toString());
		}
		else
			write(Template.SET, a1, a2.toString());
	}
	
	public void set(Address a1, Address a2){
		write(Template.MOV, a1.toString(), a2.toString());
	}
	
	public void store(Address a1, Address a2){
		write(Template.STORE, a1.toString(), a2.toString());
	}

	public void label(String string) {
		write(Template.LABEL, string);		
	}

	public void bne(String string) {
		write(Template.BNE, string);
		write(Template.NOP);
	}
	
	public void be(String string) {
		write(Template.BE, string);
		write(Template.NOP);
	}

	public void bl(String string) {
		write(Template.BLT, string);
		write(Template.NOP);
	}
	
	public void ba(String s) {
		write(Template.BA, s);
		write(Template.NOP);
	}
	
	public void cmp(Address a1, Address a2, boolean flag) {
		if(flag)
			fcmp(a1, a2);
		else
			fcmp(a1, a2);
	}

	public void fcmp(Address a1, Address a2){
		write(Template.FCMPS, a1.toString(), a2.toString());
		write(Template.NOP);
	}
	
	public void cmp(Address s1, Address s2){
		write(Template.CMP, s1.toString(), s2.toString());
	}
	
	public void fset(String f31, String f0) {
		write(Template.FMOVS, f31, f0);
		
	}

	public boolean isLocal() {
		return !isGlobal();
	}

	public void addOp(Address a1, Address a2, Address res) {
		write(Template.ADD, a1.toString(), a2.toString(), res.toString());		
	}
	
	public void fAddOp(Address a1, Address a2, Address result){
		write(Template.FADD, a1.toString(), a2.toString(), result.toString());
	}
	
	public void addOp(Address a1, Address a2, Address res, boolean f){
		if(f)
			fAddOp(a1, a2, res);
		else
			addOp(a1, a2, res);
	}
	
	public void fminusOp(Address a1, Address a2, Address res){
		write(Template.FSUB, a1.toString(), a2.toString(), res.toString());
	}
	
	public void minusOp(Address a1, Address a2, Address res, Boolean f_flag) {
		if(f_flag)
			fminusOp(a1, a2, res);
		else
			minusOp(a1, a2, res);	
	}
	
	public void minusOp(Address a1, Address a2, Address res) {
		write(Template.SUB, a1.toString(), a2.toString(), res.toString());		
	}
	
	public void multiOp(Address a1, Address a2, Address res, Boolean f_flag) {
		if(f_flag)
			write(Template.FMUL, a1.toString(), a2.toString(), res.toString());
		else
			call(".mul");
		
	}
	
	public void divOp(Address a1, Address a2, Address res, Boolean f_flag) {
		if(f_flag)
			write(Template.FDIV, a1.toString(), a2.toString(), res.toString());
		else
			call(".div");	
	}
	
	public void modOp(Address a1, Address a2, Address res) {
			call(".rem");
	}

	public void save(String fname, String s1 ) {
		write(Template.SAVEFUNC, fname, s1);
	}

	public String getStackSize() {
		return localVarSpace.toString();
	}

	public void fitos(Address f0, Address f1) {
		write(Template.FITOS, f0.toString(), f1.toString());
	}

	public void fstoi(String f0, String f1) {
		write(Template.FSTOI, f0, f1);
	}
	
	public void negOp(Address a1, Address res, boolean f_flag) {
		if(f_flag)
			write(Template.FNEG, a1.toString(), res.toString());
		else
			write(Template.NEG, a1.toString(), res.toString());		
	}

	public void orOp(Address a1, Address a2, Address res) {
		write(Template.OR, a1.toString(), a2.toString(), res.toString());	
	}
	
	public void andOp(Address a1, Address a2, Address res) {
		write(Template.AND, a1.toString(), a2.toString(), res.toString());	
	}

	public void xorOp(Address a1, Address a2, Address res) {
		write(Template.XOR, a1.toString(), a2.toString(), res.toString());	
	}
	

	public void returnstmt() {
		write(Template.RET);
		write(Template.RESTORE);	
		newLine();
	}

	private ArrayList<String> pWrites = new ArrayList<String>();
	private Integer globalVarSpace = 0;

	public void writeGlobalInits() {
		for(String s: pWrites){
			write(s);
		}
		
		pWrites.clear();
		localVarSpace += globalVarSpace;
		
	}

	public void neg(String a, String res) {
		negOp(a, res, false);
		//set("0", Template.L4);
		//andOp(Template.L4, res, res);
		
	}

	public void inc(Address a1, boolean floawt) {
		STO s = new VoidSTO();
		addSTO(s);
		if(!floawt){
			write(Template.INC, a1);
			return;
		}
		set("1", Template.L3);
		store(Template.L3, s.getAddress());
		ld(s.getAddress(), Template.F22);
		fitos(Template.F22, Template.F22);
		addOp(a1, Template.F22, a1, floawt);
		
	}
	
	public void dec(Address a1, boolean floawt) {
		STO s = new VoidSTO();
		addSTO(s);
		if(!floawt){
			write(Template.DEC, a1);
			return;
		}
		Address _1 = am.getAddress();
		set("1", _1);
		store(_1, s.getAddress());
		ld(s.getAddress(), Template.F22);
		fitos(Template.F22, Template.F22);
		minusOp(a1, Template.F22, a1, floawt);
		
	}

	public void fmov(String f31, String string) {
		write(Template.FMOVS, f31, string);
		
	}

	public void comment(String s) {
		write("!" + s + "\n");
	}
	

}

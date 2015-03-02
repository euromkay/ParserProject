import java.util.ArrayList;
import java.util.Vector;


public class StructType extends CompositeType {

	private Vector<VarSTO> vars = new Vector<VarSTO>(); 
	private Vector<FuncSTO> funcs = new Vector<FuncSTO>();
	private Vector<FuncSTO> ctrs = new Vector<FuncSTO>();
	
	

	
	public StructType(String id) {
		super(id, 0);
	}
	
	public boolean isAssignable(Type t){
		return t == null || isEquivalent(t);
	}
	public boolean isEquivalent(Type type) {
		if(type instanceof StructType)
			return getName().equals(((StructType)type).getName());
		return false;
	}

	public Type newType() {
		
		return this;
	}

	public Vector<VarSTO> getVars() {
		return vars;
	}
	
	public Vector<FuncSTO> getFuncs(){
		return funcs;
	}
	
	public Vector<FuncSTO> getCtors(){
		return ctrs;
	}
	
	public void addFunc(FuncSTO f){
		funcs.add(f);
	}
	
	public void addCtor(FuncSTO f){
		ctrs.add(f);
		addFunc(f);
	}
	
	public ArrayList<STO> getMembers(){
		ArrayList<STO> members = new ArrayList<STO>();
		members.addAll(vars);
		members.addAll(funcs);
		
		return members;
	}

	public boolean contains(String name) {
		for(STO s: vars){
			if(s.getName().equals(name))
				return true;
		}
		return false;
	}
	

	ArrayList<String> funcs_string = new ArrayList<String>();
	public boolean contains_ctor(String name) {
		if(funcs_string.contains(name))
			return true;
		
		return false;
	}
	public void addCtorString(String name){
		funcs_string.add(name);
	}
	

	public void addVar(VarSTO v) {
		vars.add(v);
		v.setOffset(getSize().toString());
		setSize(getSize() + v.getType().getSize());
	}
	
	
	private boolean hasDestruct = false;
	public boolean hasDestruct(){
		return hasDestruct;
	}
	
	public void setDestruct(){
		hasDestruct = true;
	}
	
	public boolean isStruct() {
		return true;
	}
}

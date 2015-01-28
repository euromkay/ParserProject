import java.util.ArrayList;
import java.util.Vector;


public class StructType extends CompositeType {

	private Vector<STO> vars = new Vector<STO>(); 
	private Vector<STO> funcs = new Vector<STO>();
	private Vector<STO> ctrs = new Vector<STO>();
	
	

	
	public StructType(String id) {
		super(id, -1);
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

	public Vector<STO> getVars() {
		return vars;
	}
	
	public Vector<STO> getFuncs(){
		return funcs;
	}
	
	public Vector<STO> getCtors(){
		return ctrs;
	}
	
	public void addFunc(STO f){
		funcs.add(f);
	}
	
	public void addCtor(STO f){
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

	public void addVar(VarSTO v) {
		vars.add(v);
		v.setOffset(getSize().toString());
		setSize(getSize() + v.getType().getSize());
	}
}

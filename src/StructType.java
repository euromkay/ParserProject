import java.util.ArrayList;
import java.util.Vector;


public class StructType extends CompositeType {

	private Vector<STO> vars = new Vector<STO>(); 
	private Vector<STO> funcs = new Vector<STO>();
	
	

	
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

	public void setStructVars(Vector<STO> vars, int size) {
		this.vars = vars;
		setSize(size);
		
	}

	public Vector<STO> getVars() {
		return vars;
	}
	
	public Vector<STO> getFuncs(){
		return funcs;
	}
	
	public void addFunc(STO f){
		funcs.add(f);
	}
	
	public ArrayList<STO> getMembers(){
		ArrayList<STO> members = new ArrayList<STO>();
		members.addAll(vars);
		members.addAll(funcs);
		
		return members;
	}
}

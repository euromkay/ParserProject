import java.util.Vector;


public class StructType extends CompositeType {

	private Vector<STO> vars = new Vector<STO>(); 
	private Vector<STO> fields = new Vector<STO>();
	
	

	
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
		Vector<STO> list = new Vector<STO>();
		for(STO s: vars)
			list.add(s.newSTO());
		StructType s = new StructType(new String(getName()));
		s.setStructVars(list, getSize());
		
		return s;
	}

	public void setStructVars(Vector<STO> vars, int size) {
		this.vars = vars;
		setSize(size);
		
	}

	public Vector<STO> getVars() {
		return vars;
	}
	
	public Vector<STO> getFields(){
		return fields;
	}
	
	public void addFunc(STO f){
		fields.add(f);
	}
}

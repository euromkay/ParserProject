import java.util.Vector;


public class StructType extends CompositeType {

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
		for(STO s: fields)
			list.add(s.newSTO());
		StructType s = new StructType(new String(getName()));
		s.setFields(list, getSize());
		
		return s;
	}

	public void setFields(Vector<STO> fields, int size) {
		this.fields = fields;
		setSize(size);
		
	}

	public Vector<STO> getFields() {
		return fields;
	}
	
	
	
}

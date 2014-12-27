
public class RValSTO extends STO {

	/*public RValSTO(String strName) {
		super(strName);
	}*/
	
	public RValSTO(String strName, Type t) {
		super(strName, t);
		this.strName = strName;
		this.t = t;
	}

	String strName;
	Type t;
	public STO newSTO() {
		STO s = new RValSTO(new String(strName), t.newType());
		s.setAddress(new String(getAddress()));
		return s;
	}

	
}

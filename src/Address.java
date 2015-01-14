
public class Address implements Comparable<Address> {

	public static final Address G0 = new Address("%g0");
	
	public static final Address O0 = new Address("%o0");
	public static final Address O1 = new Address("%o1");
	public static final Address O2 = new Address("%o2");
	
	public static final Address F0 = new Address("%f0");
	public static final Address F1 = new Address("%f1");
	public static final Address F2 = new Address("%f2");

	public static final Address I0 = new Address("%i0");

	public static final Address FP = new Address("%fp");


	private String s;
	private AddressManager am;
	
	public Address(String s, AddressManager am){
		this.s = s;
		this.am = am;
	}
	
	public Address(String s){
		this.s = s;
		am = null;
	}

	
	public void release(){
		if(am != null)
			am.add(this);
	}


	public int compareTo(Address a) {
		return s.compareTo(a.s);
	}
	
	public String toString(){
		return s;
	}

	public boolean isLocal() {
		return s.startsWith("%l");
	}

	public boolean isRegister() {
		return s.startsWith("%");
	}

	public boolean isOnFP() {
		return s.startsWith("%fp");
	}
}


public class Address implements Comparable<Address> {

	public static final Address G0 = new Address("%g0");
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
		am.add(this);
	}


	public int compareTo(Address a) {
		return s.compareTo(a.s);
	}
	
	public String toString(){
		return s;
	}

	public boolean isLocal() {
		return s.startsWith("%");
	}
}

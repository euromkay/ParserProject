import java.util.ArrayList;
import java.util.Collections;


public class AddressManager {

	//Local Registers
	public static final String L0 = "%l0";
	public static final String L1 = "%l1";
	public static final String L2 = "%l2";
	public static final String L3 = "%l3";
	public static final String L4 = "%l4";
	public static final String L5 = "%l5";
	public static final String L6 = "%l6";
	public static final String L7 = "%l7";
	
	private ArrayList<Address> locals = new ArrayList<Address>();
	
	public void add(Address address) {
		locals.add(address);
		sort();
	}

	private void sort() {
		Collections.sort(locals);
	}
	
	public Address getAddress(){
		return locals.remove(0);
	}
	
}

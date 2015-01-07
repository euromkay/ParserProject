import java.util.ArrayList;
import java.util.Collections;


public class AddressManager {

	public AddressManager(){
		locals.add(new Address(L0, this));
		locals.add(new Address(L1, this));
		locals.add(new Address(L2, this));
		locals.add(new Address(L3, this));
		locals.add(new Address(L4, this));
		locals.add(new Address(L5, this));
		locals.add(new Address(L6, this));
		locals.add(new Address(L7, this));
		
		Collections.shuffle(locals);
	}
	
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
		if(!address.toString().startsWith("%l"))
			return;
		//System.out.println("Returning : " + address.toString());
		locals.add(address);
		//sort();
	}

	private void sort() {
		Collections.sort(locals);
	}
	
	public Address getAddress(){
		//System.out.println("Checking out : " + locals.get(0).toString());
		return locals.remove(0);
	}

	public int size() {
		return locals.size();
	}
	
}

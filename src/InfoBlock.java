import java.util.ArrayList;
import java.util.Vector;


public class InfoBlock {

	ArrayList<Triplet> list = new ArrayList<Triplet>();
	
	
	
	public class Triplet{
		public Triplet(String name2, STO init2, int arraySize2, int pointer2) {
			name = name2;
			init = init2;
			arraySize = arraySize2;
			pointer = pointer2;
		}
		public STO init;
		public String name;
		public int arraySize;
		public int pointer;
	}
	
	public void addId(String name, STO init, int arraySize, int pointer){
		list.add(new Triplet(name, init, arraySize, pointer));
	}
	
	public ArrayList<Triplet> getList(){
		return list;
	}
	/*
		Vector v;  //of numbers
		int arraySize, pointers;
	
		public VectorNumber(Vector v, int i, int j){
			this.v = v;
			this.arraySize = i;
			this.pointers = j;
		}
		
		public VectorNumber(STONum s){
			v = new Vector();
			v.add(s.getSTO());
			arraySize = s.getNumber();
		}
		
		public VectorNumber(Vector u, int i){
			this.v = u;
			this.arraySize = i;		
		}
		
		public Vector getList(){
			return v;
		}
		
		public int getArraySize(){
			return arraySize;
		}
		
		public int getPointerNumber(){
			return pointers;
		}
		
		
		
		public void addElement(STONum s){
			if(arraySize == MyParser.NONE)
				arraySize = s.getNumber();
				
			v.add(s.getSTO());
		}
		
		public void addElement(STO s){				
				v.add(s);
		}
		*/
}

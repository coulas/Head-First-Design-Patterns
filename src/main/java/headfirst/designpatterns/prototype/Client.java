package headfirst.designpatterns.prototype;

public class Client {
	public static void main(String[] args) {
		Monster p1 = new Dragon("FireStorm", true);
		Monster p2 = new Drakon("HeadHache", 3, true);
		
		// ... later ...
		operation(p1);
		operation(p2);
	}
	
	public static Monster operation(Monster p) {
		// This code doesn't know or care what the concrete type of p is
		Monster pCopy = null;
		try {
			pCopy = p.copy();
			// do something useful with pCopy
			System.out.println("Operating with pCopy!");
			System.out.println("copied monster is "+pCopy.name);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return pCopy;
	}
}
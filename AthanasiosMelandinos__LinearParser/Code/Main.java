import java.io.IOException;

public class Main {

	public static void main(String[] args) {		
		try {
			new Lp_Handler("Files/input.txt");
		} catch (IOException e) {
			System.out.println("File not found!");
		}
		
	}
	

}

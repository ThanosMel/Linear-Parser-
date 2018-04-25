import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class InputFileReader {
	private BufferedReader br;
	
	public InputFileReader(String path){
		
		try {
			br = new BufferedReader(new FileReader(path));
			
		} catch (FileNotFoundException e) {
			
			System.out.println("File not found!" + path);
		}
		
	}

	public BufferedReader getBr(){
		return br;
	}
	public void setBr(BufferedReader br){
		this.br=br;
	}

}

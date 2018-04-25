import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;

public class OutputFile {
	public OutputFile(int minmax,ArrayList<ArrayList<String>> A,ArrayList<String> c, ArrayList<String> b, ArrayList<String> Eqin){
		fileWriter(minmax,A,c,b,Eqin);
		System.out.println("File created successfully");
	}
	public void fileWriter(int minmax,ArrayList<ArrayList<String>> A,ArrayList<String> c, ArrayList<String> b, ArrayList<String> Eqin){
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("Files/output.txt"), "utf-8"))) {
				writer.write("MinMax: " +minmax +"\n");
				writer.write("\nc: "+ c +"\n");
				writer.write("\nA: ");
				for(int i = 0 ; i<A.size(); i++){
					if(i==0){
						writer.write(A.get(i)+"\n");
					}
					else{
						writer.write("   "+A.get(i)+"\n");
					}
					
					
				}
				writer.write("\nb: "+b+"\n");
				writer.write("\nEquin: "+ Eqin);
				
			} catch (UnsupportedEncodingException e) {
				System.out.println("Unsupported encoding!");
			} catch (FileNotFoundException e) {
				System.out.println("File not found!");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
	  
	}
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lp_Handler {	
	private InputFileReader file;
	//rawC , rawA contains coefficients and variables in the same position e.g [-3.1x1,+4x2,-9x3].
	private ArrayList<String> rawC ;
	private ArrayList<String> rawAa = new ArrayList<String>();
	private ArrayList<String> innerA = new ArrayList<String>();
	
	private int  minmax;
	private ArrayList<String> Eqin = new ArrayList<String>();
	private ArrayList<String> c = new ArrayList<String>();
	private ArrayList<ArrayList<String>> A = new ArrayList<ArrayList<String>>();
	private ArrayList<String> b = new ArrayList<String>();
	//regular expressions to check if a variable and it's coefficient is in the proper form.
	//e.g +5x1 is correct
	//+5ax2 is wrong.
	//3 forms are acceptable.
	private final String regex1 ="(\\+|\\-)(\\d+)(x)(\\d+)";
	private final String regex2 ="(\\+|\\-)(\\d+)(\\.)(\\d+)(x)(\\d+)";
	private final String regex3 ="(\\+|\\-)(x)(\\d+)";
	//globalVarMax stores the 'greater' coefficient eg x1<x5 ,globalVarMax->5
	private int globalVarMax =-1;
	private int	cVarMax =-1;
	private int globalCounter =0;
	
	public Lp_Handler(String path) throws IOException{
		
	
		file = new InputFileReader(path);	
		lineReader();
	
		getAllArrays();
		
		
		
	}
	
	/*
	 * Check if keyword 'end' is missing and read all the lines from the file one by one.
	 */
	public void lineReader() throws IOException{
		String currLine, lastLine= "", tempCurrLine;
		//lineCounter holds the line number we are reading.
		int lineCounter = 0;
		//keyword 'end' checking
		while((tempCurrLine = file.getBr().readLine()) != null){
			lastLine = tempCurrLine;
		}
		lastLine=lastLine.replaceAll("\\s","").substring(0,3);
		
		if(!lastLine.equals("end")){
			System.out.println("Keyword 'end' missing!");
			System.exit(1);
		}
		
		
		file.setBr(new BufferedReader(new FileReader("Files/input.txt")));
		//file reading
		while(true){
			currLine = file.getBr().readLine();
			
			if(currLine.equals("")){
				System.out.println("Line is empty skipping!");//or System.out.println("Don't leave empty lines!"); System.exit(1);
			}else{
				if(currLine.equals(lastLine)){
					break;
				}
				
				generalHandler(lineCounter,currLine);
				addRemainingElsToC(c);
				
				lineCounter++;
				
			}           
        }  	
	}
	/*
	 * handles each line of the file individually.
	 */
	
	public void generalHandler(int lineCounter,String line){
		//check for min or max and fill c array.
		if(lineCounter == 0){
			
			String tempLine = line.replaceAll("\\s" , "");
			tempLine = tempLine.substring(3,tempLine.length());
			minmaxDetecter(line);
			rawC=takeRawElements(tempLine);
			elementsChecker(rawC);
			c=fillArrays(rawC);
			
		}
		else{
			//check for st keyword
			if(lineCounter==1){
				
				stDetecter(line);
			}
			//fill A array.
			rawAa= new ArrayList<String>();
			rawAa=takeRawElements(stStringSpliter(line).get(0));
			elementsChecker(rawAa);
			innerA=new ArrayList<String>();
			innerA=fillArrays(rawAa);
			A.add(innerA);
			
			checkRightSideSt(stStringSpliter(line).get(1));	
			//fill Eqin array.
			fillEqin(stStringSpliter(line).get(1));
			//fill b array.
			fillB(stStringSpliter(line).get(1));
			
			
		}
		
	}
	
	/*
	 * takes an arraylist and fill another arraylist.
	 * eg input arraylist : [-3.1x1, +4x2, -9x3, +x5]
	 *    output arraylist : [-3.1, +4, -9 , 1]
	 */
	public ArrayList<String> fillArrays(ArrayList<String> ar){
		//hasmap that containts the variable number as a key and the coefficient as a value.(hashmap is good because it auto arrange its keys in ascending order)
		/*
		 	eg hasmap <1,-3.1>
		 			  <2,+4>
		 			  <3,-9>
		 			  <5,+1>
		 */
		HashMap<String,String> varsNcoefs= new HashMap<String,String>();
		ArrayList<String> tempAr = new ArrayList<String>();
		int varMax=-1;	
		
		for(String s:ar){
			   
			if(s.matches(regex1)){
	    	    Pattern p = Pattern.compile(regex1);
	    	    Matcher m = p.matcher(s);
	    	 
	    	    if(m.find()) {
	    	    	String sign=m.group(1);
	    	        String coef=m.group(2);
	    	        String varNum=m.group(4);	        
	    	        varsNcoefs.put(varNum,sign+coef);
	    	    	if(varMax<Integer.parseInt(varNum)){
	    	    		varMax=Integer.parseInt(varNum);
	    	    	}
	    	    }
			}
			else if(s.matches(regex2)){
				 Pattern p = Pattern.compile(regex2);
		    	 Matcher m = p.matcher(s);
		    	 
		    	  if(m.find()){
		    		  String sign=m.group(1);
			    	  String coef=m.group(2);
			    	  String dot=m.group(3);
			    	  String coef2=m.group(4);
			    	  String varNum=m.group(6);
			    	  varsNcoefs.put(varNum,sign+coef+dot+coef2);
			    	
			    	  if(varMax<Integer.parseInt(varNum)){
			    		  varMax=Integer.parseInt(varNum);
			    	  }
		    	  } 	
			}
			else if(s.matches(regex3)){	
				Pattern p = Pattern.compile(regex3);
		    	Matcher m = p.matcher(s);
		    	 
		    	if(m.find()){
		    		String sign=m.group(1);
		    		String coef="1";
			    	String varNum=m.group(3);
		    	    varsNcoefs.put(varNum,sign+coef);
			    	if(varMax<Integer.parseInt(varNum)){
		    	    	varMax=Integer.parseInt(varNum);
			    	}		    	    	
		    	}
			}
		
		}
		if(globalVarMax<varMax){
			globalVarMax=varMax;
		}
		if(globalCounter==0){
			cVarMax=varMax;
		}		
		globalCounter++;
	     for(int i=1; i <=varMax; i++){
	    	 if(varsNcoefs.containsKey(Integer.toString(i))){
	    		 tempAr.add(varsNcoefs.get(Integer.toString(i)));
	    	 }
	    	 else{
	    		 tempAr.add("0");
	    	 }
	     }
	     return tempAr;
	}
	
	/*
	 * Given a string this function fills an arraylist.
	 * eg. string : -3.1x1 +  4x2 - 9x3 + x5
	 * 	   arraylist : [-3.1x1, +4x2, -9x3, +x5]
	 */
	public ArrayList<String> takeRawElements(String line){
		    String[] ar = new String[line.length()];
		    ArrayList<String> rawEls= new ArrayList<String>();
		    String temp="";
		    int counter=0;
		    for(int i=0; i<line.length(); i++) {
		    	if(!((""+line.charAt(i)).equals("+")|| (""+line.charAt(i)).equals("-"))||counter==0) {
		    		temp+=""+line.charAt(i);
		    		ar[counter]=temp;
		    
		    	}
		    	if((""+line.charAt(i)).equals("+")|| (""+line.charAt(i)).equals("-")){
		    			counter++;
			    		temp=""+line.charAt(i);
		    		
		    		
		    	}
		    }
		    for(int i=0; i<ar.length; i++){
		    	if(ar[i]!=null){
		    		if(!(ar[i].equals("+")||ar[i].equals("-"))){
		    			rawEls.add(ar[i]);
		    		}
		    	
		    	}
		    }
		    return rawEls;
	}
	/*
	 * Check if a the elements of an arraylist are on the proper form using regular expressions. 
	 */
	
	public void elementsChecker(ArrayList<String> ar){
		for(String str: ar){
			if(!(str.matches(regex1) || str.matches(regex2) || str.matches(regex3))){
				System.out.println("Invalid variable form:" + str);
				System.out.println("Your variable can either be like:(+/-)(number)(x)(number)");
				System.out.println("Or like:(+/-)(number)(.)(number)(x)(number)");
				System.out.println("Or like :(+/-)(x)(number)");
				System.exit(1);
			}
		}
		
	}
	
	
	public void fillB(String str){
		String tempStr=null;
		if(str.contains("<")&&str.contains("=")){
			tempStr=str.substring(2,str.length());
		}
		else if(str.contains(">")&&str.contains("=")){
			tempStr=str.substring(2,str.length());
		}
		else if(str.contains("=")){
			tempStr=str.substring(1,str.length());
		}
		b.add(tempStr);
		
	}
	/*
	 * If a constraint have more variables from objective function, adds the remaining elements to objective function.
	 */
	public void addRemainingElsToC(ArrayList<String> c){
		if(cVarMax<globalVarMax){
			for(int i = c.size(); i<globalVarMax; i++){
				c.add("0");
			}
		}
	}
	/*
	 * Splits the constraint after the 'st' keyword.
	 * Checks if a constrait contains > < symbols at the same time.
	 * 
	 */
	public ArrayList<String> stStringSpliter(String line){
		String tempLine=line.replaceAll("\\s","");
		
		if(tempLine.substring(0,2).equals("st")){
			tempLine = tempLine.substring(2,tempLine.length());
		}
		else if(tempLine.substring(0,3).equals("s.t")){
			tempLine = tempLine.substring(3,tempLine.length());
		}
		else if(tempLine.substring(0,8).equals("subject")){
			tempLine = tempLine.substring(8,tempLine.length());
		}
		
		//We store either > , <  or = .
		String splitter = null; 
		
		if(tempLine.contains("<") && tempLine.contains(">")){
			System.out.println("A constrait can't have > < at the same time!");
			System.exit(1);
		}
		else if(tempLine.contains(">") && tempLine.contains("<")){
			System.out.println("A constrait can't have > < at the same time!");
			System.exit(1);
		}
		else if(tempLine.contains("=>")||tempLine.contains("=<")){
			System.out.println("Don't use = symbol before < or >");
			System.exit(1);
		}
		if(tempLine.contains("<")){
			splitter="<";
		}
		else if(tempLine.contains(">")){
			splitter=">";
		}
		else if(tempLine.contains("=")){
			splitter="=";
		}
		
		String tempLineLeftSide = tempLine.substring(0,tempLine.indexOf(splitter));
		String tempLineRightSide = tempLine.substring(tempLine.indexOf(splitter),tempLine.length());
		//allLine contains the left side of a constraint on the first position and the right side of a constraint in the second. 
		//e.g +5x1 + 7x2 -x3 <= +5 allLine = [+5x1 + 7x2 -x3, +5]/
		ArrayList<String> allLine = new ArrayList<String>();
		allLine.add(tempLineLeftSide);
		allLine.add(tempLineRightSide);
		
		return allLine;
		
		
	}
	
	public void checkRightSideSt(String str){
		//regular expression to check if the right side of a constraint is on the proper form.
		String regex = "(\\<=||\\>=||\\=)([-+]\\d+)";
		
		if(!str.matches(regex)){
			System.out.println("Right side of your constraint is wrong " + str);
			System.exit(1);
		}
		
	}	
	/*
	 * Fills the eqin array.
	 */
	public void fillEqin(String str){
		
		if(str.contains("<")){
			Eqin.add("-1");
		}
		else if(str.contains(">")){
			Eqin.add("1");
		}
		else if(str.contains("=")){
			Eqin.add("0");
		}
		
		
	}
	/*
	 * checks if the first constraint contains the allowed keywords('st,'s.t','subject').
	 */
	public void stDetecter(String line){
		String tempLine = line.replaceAll("\\s" , "");
		boolean condition=tempLine.substring(0,2).equals("st") || tempLine.substring(0,3).equals("s.t") || tempLine.substring(0,8).equals("subject");
		
		if(!condition){
			System.out.println("Keyword st or s.t or subject not found!Please add one of those keywords before your constraints.");
			System.exit(1);
		}
	}
	/*
	 * check for min max.
	 */
	public void minmaxDetecter(String line ){
		if(line.replaceAll("\\s","").substring(0,3).equals("max")){
			minmax= 1;
		}
		else if(line.replaceAll("\\s","").substring(0,3).equals("min")){
			minmax=-1;
		}
		else{
			System.out.println("min or max missing!");
			System.exit(1);
		}
		
	}
	/*
	 * parse all the arrays to the constructor of OutputFile.
	 */
	public void getAllArrays(){
		new OutputFile(minmax,A,c,b,Eqin);
	}
	
	
}

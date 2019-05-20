package mainpackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PasswordChecker {
	
	private static boolean OK = false;
	
	private static ArrayList<ArrayList<String>> createList() {
			
			ArrayList< ArrayList<String>> finalArray = new ArrayList<ArrayList<String>>();
			ArrayList<String> possibleChose = new ArrayList<String>(Arrays.asList("0","1","2","3","4","5","6","7","8","9"));
		
			int bound = possibleChose.size();
			int i = 0;
			String taken;
			Random random = new Random();
			
			for( i = 0; i<5 ; i++) {
				ArrayList<String> chosen = new ArrayList<String>();
				taken  = possibleChose.get( random.nextInt(bound));
			    possibleChose.remove(taken);
			    bound = bound-1;
			    chosen.add(taken);
			    taken  = possibleChose.get( random.nextInt(bound));
			    possibleChose.remove(taken);
			    bound = bound-1;
			    chosen.add(taken);
			    finalArray.add(new ArrayList<String>(chosen));
			    chosen.clear();
			}
			
			return finalArray;
		}
	
	public static ArrayList<ArrayList<String>> RequestForPassword() {
		Scanner scanner = new Scanner(System.in);
		StringBuffer sb = new StringBuffer();
		ArrayList<ArrayList<String>> digited = new ArrayList<ArrayList<String>>();
		ArrayList<String> tmp = new ArrayList<String>();
		int x = 0;
		boolean finished = false;
		while( x  < 8 && !finished ) {
			ArrayList<ArrayList<String>> buttons = createList();
				for(int i=0; i<5; i++) {
					System.out.print(" button"+i+": "+buttons.get(i).get(0)+buttons.get(i).get(1));
				}
			System.out.print("\nPress 5 for OK or insert button number:")	;
			int n =Integer.parseInt(scanner.nextLine());
			while( n<0 || n>5  ) {
				System.out.println("\n #Button IS NOT VALID PLEASE INSERT A NUMBER BETWEEN 0 AND 5:")	;
				System.out.print("\ninsert button number:");
				n = Integer.parseInt(scanner.nextLine());
			}
			if(n == 5) {
				finished =true;
				
			}
				else {
					tmp.add(buttons.get(n).get(0));
					tmp.add(buttons.get(n).get(1));
					digited.add(new ArrayList<String>(tmp));
					tmp.clear();
					x++;
					
//					for (ArrayList<String> g : digited) {
//						System.out.print(g.get(0)+g.get(1)+" ");
//						}
				}
		}
		scanner.close();
		return digited;
	}
	
	private static boolean verify(String salt, String hexPassword, String actual) throws NoSuchAlgorithmException {
	
		
//		System.out.println("tryal "+actual);
//		System.out.println("salt "+salt);
		
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] toCheck = (salt+actual).getBytes();
		md.update(toCheck);
		byte[] mdBytes =md.digest();
		/*convert it in HEX*/
//		System.out.println("passbefore HEX "+mdBytes.toString());
		StringBuffer buf = new StringBuffer();
	    for(int i = 0; i < mdBytes.length; i++) {
	       String hex = Integer.toHexString(0x0100 + (mdBytes[i] & 0x00FF)).substring(1);
	       buf.append((hex.length() < 2 ? "0" : "") + hex);
	    }
	    
		String nuova = buf.toString();
//		System.out.println("calculated:"+nuova+" original "+hexPassword);
		return nuova.contentEquals(hexPassword);
	}
	
	public static boolean isPasswordValid(ArrayList<ArrayList<String>> digited,String salt, String hexPassword) throws NoSuchAlgorithmException {
		
		ArrayList<String> result = new ArrayList<String>();
		 recursiveVerify(digited,0,result,salt,hexPassword); 
		
		return OK;
	}
	
	private static void recursiveVerify(ArrayList<ArrayList<String>> digited, int k, ArrayList<String> result, String salt, String hexPassword) throws NoSuchAlgorithmException {
		
		if(k == digited.size() && !OK) {
			//System.out.print("Solution:");
			String tryal = new String();
			for(String g: result) {
//				System.out.print(g);
				tryal = tryal+g;
				
			}
			//System.out.println("");
			//System.out.println("tryal before verify "+tryal);
			OK =verify(salt,hexPassword,tryal);
			
			return;
		}else if (OK) {
			return;
		}
		
		ArrayList<String> newResult = new ArrayList<String>(result);
		newResult.add(digited.get(k).get(0));
	    recursiveVerify(digited,k+1,newResult,salt,hexPassword);
		
	    newResult = new ArrayList<String>(result);
		newResult.add(digited.get(k).get(1));
		recursiveVerify(digited,k+1,newResult,salt,hexPassword);
		
	}
	
	public static String readPassword (String prompt) {
	      EraserThread et = new EraserThread(prompt);
	      Thread mask = new Thread(et);
	      mask.start();

	      BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	      String password = "";

	      try {
	         password = in.readLine();
	      } catch (IOException ioe) {
	        ioe.printStackTrace();
	      }
	      // stop masking
	      et.stopMasking();
	      // return the password entered by the user
	      return password;
	   }

}

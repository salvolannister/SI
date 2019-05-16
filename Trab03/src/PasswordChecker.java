import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class PasswordChecker {

	public static ArrayList<ArrayList<String>> createList() {
			
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
	
	public static void RequestForPassword() {
		Scanner scanner = new Scanner(System.in);
		StringBuffer sb = new StringBuffer();
		ArrayList<ArrayList<String>> digited = new ArrayList<ArrayList<String>>();
		ArrayList<String> tmp = new ArrayList<String>();
		int x = 0;
		while( x  < 5) {
		ArrayList<ArrayList<String>> buttons = createList();
			for(int i=0; i<5; i++) {
				System.out.print(" button"+i+": "+buttons.get(i).get(0)+buttons.get(i).get(1));
			}
		System.out.print("\ninsert button number:")	;
		String b= new String (scanner.nextLine());
		int n = Integer.parseInt(b);
		while( n<0 || n>4 ) {
			System.out.println("\n VALUE IS NOT VALID PLEASE INSERT A NUMBER BETWEEN 0 AND 4:")	;
			System.out.print("\ninsert button number:");
			b = scanner.nextLine();
			n = Integer.parseInt(b);
		}
		tmp.add(buttons.get(n).get(0));
		tmp.add(buttons.get(n).get(1));
		digited.add(new ArrayList<String>(tmp));
		tmp.clear();
		x++;
		}
		for (ArrayList<String> g : digited) {
			System.out.print(g.get(0)+g.get(1)+" ");
		}
	}
	
//	public static List<List<List<Integer>>> combinations(List<List<Integer>> input) {
//	    return step(input, input.size(), new ArrayList<>());
//	}
//	
//	public static List<List<Integer>> step(List<Integer> input, 
//            int k, 
//            List<List<Integer>> result) {
//
//		// We're done
//		if (k == 0) {
//		return result;
//		}
//		
//		// Start with [[1], [2], [3]] in result
//		if (result.size() == 0) {
//			for (Integer i : input) {
//				ArrayList<Integer> subList = new ArrayList<>();
//				subList.add(i);
//				result.add(subList);
//			}
//			
//		// Around we go again.  
//		return step(input, k - 1, result);
//		}
//		
//		// Cross result with input.  Taking us to 2 entries per sub list.  Then 3. Then... 
//		List<List<Integer>> newResult = new ArrayList<>();
//		for (List<Integer> subList : result) {
//		for(Integer i : input) {
//		List<Integer> newSubList = new ArrayList<>();
//		newSubList.addAll(subList);
//		newSubList.add(i);
//		newResult.add(newSubList);
//		}
//		}
//		
//		// Around we go again.  
//		return step(input, k - 1, newResult);
//		}
//	
}

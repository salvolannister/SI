import java.util.ArrayList;
import java.util.List;

public class PasswordChecker {

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

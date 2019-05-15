import java.util.Random;

public class Main {

	public static void main(String[] args) {
		System.out.println("###################################################\n"+
						   "		       LOGIN PAGE\n"+ 		   
						   "email:");
		String email = new String ( System.in.toString());
		/* check from the db if the email is valid or not */
//		System.out.println("###################################################\n"+
//							"		       LOGIN PAGE\n"+ 		   
//				   "email:"+email+"\n");
	    /* display buttons*/		
		
	}

	private static String generateSalt() {
		String vocabulary = new String("0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM");
		String salt = "";
		int bound = vocabulary.length();
		int i = 0;
		Random random = new Random();
		for( i = 0; i<10 ; i++) {
			
			salt = salt + vocabulary.charAt(random.nextInt(bound));
		}
		return salt;
		
	}
	
}

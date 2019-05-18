import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Main {

	public static void main(String[] args) {
		Database.setDatabase("./database/trabalho3.db");
		Scanner scanner = new Scanner(System.in);
		boolean exit = false;
		while(!exit ) {
		System.out.print("###################################################\n"+
						   "		      LOGIN PAGE\n"+		   
						   "			 email:");
		String email = new String (scanner.nextLine());
		User u = new User();
		/* check from the db if the email is valid or not */
		try {
			boolean alreadyExist= Database.checkUserExistence(email) ;
			/* scoprire come leggere un resultSEt senzza che dia errore*/
			if(!alreadyExist) {
				System.out.println(" you are not registred, ask the admin to register you");
				System.out.println("Insert a password of 5 digit all different");
				System.out.print("password:");
				String password = new String (scanner.nextLine());
				try {
					u.registerUser("./Pacote_T3/Keys/admin-x509.crt",0,password);
				} catch (InvalidNameException | CertificateException | NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				/* crea instance di user*/
				/* the user is registred so...*/
				
				PasswordChecker.RequestForPassword();
				/* read the password and check if it is correct*/
				String[] values = u.getPandS();
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
//		System.out.println("###################################################\n"+
//							"		       LOGIN PAGE\n"+ 		   
//				   "email:"+email+"\n");
	    /* display buttons*/		
		}	
	}

	private static ArrayList<ArrayList<Integer>> createList() {
		
		ArrayList< ArrayList<Integer>> finalArray = null;
		ArrayList<Integer> possibleChose = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9));
	
		int bound = possibleChose.size();
		int i = 0;
		int taken;
		Random random = new Random();
		for( i = 0; i<5 ; i++) {
			ArrayList<Integer> chosen = new ArrayList<Integer>();
			taken  = possibleChose.get( random.nextInt(bound));
		    possibleChose.remove(taken);
		    bound--;
		    chosen.add(taken);
		    taken  = possibleChose.get( random.nextInt(bound));
		    possibleChose.remove(taken);
		    bound--;
		    chosen.add(taken);
		    finalArray.add(chosen);
		}
		
		
		return finalArray;
		
	}

	
	




	
}

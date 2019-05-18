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
		boolean valid = false;
		while(!exit ) {
		System.out.print("###################################################\n"+
						   "		      LOGIN PAGE\n"+		   
						   "			 email:");
		String email = new String (scanner.nextLine());
		User u = new User() ;
		/* check from the db if the email is valid or not */
		try {
			boolean alreadyExist= Database.checkUserExistence(email) ;
			/* scoprire come leggere un resultSEt senzza che dia errore*/
			if(!alreadyExist) {
				System.out.println("You are not registred, ask the admin to register you");
				System.out.println("Insert a password of 5 digit all different");
				System.out.print("password:");
				String password = new String (scanner.nextLine());
				try {
					System.out.print("###################################################\n"+
							   "Working Directory is"+ System.getProperty("user.dir")+		   
							   "\n	certificate path:");
					String path = new String (scanner.nextLine());
					/*percorso da sostituire con path*/
					u.registerUser("./Pacote_T3/Keys/admin-x509.crt",0,password);
					
				} catch (InvalidNameException | CertificateException | NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				/* crea instance di user*/
				u = new User(email);
				int count = 0;
				/* read the password and check if it is correct*/
				String[] values = u.getPandS();
				/* continue until the password is correct or tentatives are less than 3*/
			while(!valid && count<3) {
				ArrayList<ArrayList<String>> digited =PasswordChecker.RequestForPassword();
				valid = PasswordChecker.isPasswordValid(digited,values[1],values[0]);
				count++;
			}
			/*password is correct*/
			if(valid) {
				/*3 ETAPA*/
				count = 0;
				/*verification of private key*/
				System.out.print("###################################################\n"+
						   "Working Directory is"+ System.getProperty("user.dir")+		   
						   "\n	certificate path:");
				String path = new String (scanner.nextLine());
				int gid = u.getGID();
				if(gid == 0) {
					/*administrator interface*/
					
				}else {
					/* normal user interface*/
				}
				
			}
			}
		} catch (SQLException | NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	
		}	
	}


	




	
}

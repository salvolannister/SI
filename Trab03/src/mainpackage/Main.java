package mainpackage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
						System.out.println("You are not registred, ask the admin to register. conitnue:0 exit:1");
						String choice = new String(scanner.nextLine());
							if(choice.equals("1")) {
								exit = true;
								System.out.println("                       GOODBYE");
							}
					}else {
						/* crea instance di user*/
						u = new User(email);
						int block = u.getBlock();
						if(block == 0) {
						tryLog(u);
						}
						else {
							if(checkTime(u)) {
								u.block(0);
								u.setAttempt(0);
								tryLog(u);
							}else {
								System.out.println("You are still blocked");
							}
						}
						
					
					
					}
				} catch (SQLException | NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		
		}	
	}


	private static boolean checkTime(User u) {
		LocalDateTime dateTime = LocalDateTime.now();
		//String dataNow = dateTime.toString();
		
		//System.out.println("Checking time, now: "+dataNow );
		String time =u.getTime();
		LocalDateTime dateBlocked = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
		int diff = dateTime.getMinute() - dateBlocked.getMinute();
	
		return diff>2;
	}


	private static void tryLog(User u ) throws SQLException, NoSuchAlgorithmException {
	/* read the password and check if it is correct*/
	Scanner scanner = new Scanner(System.in);
	boolean valid =false;
	int count = u.getAttempt();
	String[] values = u.getPandS();
	/* continue until the password is correct or tentatives are less than 3*/
		while(!valid && count<3) {
			if( count!= 0) {
				System.out.print("###################################################\n"+
						         "  Wrong password, try again\n"	   
						   );			
			}
		ArrayList<ArrayList<String>> digited =PasswordChecker.RequestForPassword();
		valid = PasswordChecker.isPasswordValid(digited,values[1],values[0]);
		u.addAttempt();
		count ++;
		}
			/*password is correct*/
			if(valid) {
				/*3 ETAPA*/
				count = 0;
				/*verification of private key*/
				try {
					privateKeyVerification(u);
					if(u.getBlock() == 0) {
						/* show interface for group and usuarios*/
						int GID =u.getGID();
						//printHeader(u);
						if(GID == 0) {
							/*user interface*/
//							printBodyOne(u,GID);
//							printBodyTwo(GID)
						}else {
							/*admin interface*/
//							printBodyOne(u,GID);
//							printBodyTwo(GID);
						}
					}
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}else {
				/*block user*/
				System.out.print("###################################################\n"+
						   		 "You have been blocked wait 2 minutes and try again\n" );
				u.block(1);
				
			}
			scanner.close();
	}

	
	private static void privateKeyVerification(User u) throws CertificateException, SQLException {
		// DA VERIFICARE SE BLOCCA VERAMENTE O CRASCIA TUTTO
		int attempt = 0;
		boolean OK = false;
		Scanner scanner = new Scanner(System.in);
		while(attempt< 3  && !OK) {
			System.out.print("###################################################\n"+
					   "Working Directory is"+ System.getProperty("user.dir")+		   
					   "\n	Bynary file path:");
		
			String binFilepath = new String (scanner.nextLine());
			System.out.print("###################################################\n"+
					   "password:");	   
			String secretPhrase = new String (scanner.nextLine());
			
			
			System.out.print("###################################################\n"+
					   "\n	certificate path:");
			String certifPath = new String (scanner.nextLine());
			
			OK = PrivateKeyVerification.CheckPrivateKey(new String[] {binFilepath,secretPhrase,certifPath});
		}
		if(!OK) {
			System.out.print("###################################################\n"+
					   "You have been blocked, wait 2 minutes and try again\n" );
			u.block(1);
		}
		scanner.close();
	}

	
	
}

package test;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.naming.InvalidNameException;
import javax.security.cert.CertificateException;

import mainpackage.Database;
import mainpackage.PasswordChecker;
import mainpackage.User;

/* non aggiorno i tentativi perché li conto nell'oggetto user
 * ma questa cosa va migliorata, bisogna affidarsi solo al database*/
public class test2 {
	public static void main(String[] args) {
		// Register the user
		Database.setDatabase("./database/trabalho3.db");
		Scanner scanner = new Scanner(System.in);
		
		User u;
		try {
			u = new User("admin@inf1416.puc-rio.br");
			int block = u.getBlock();
			if(block == 0) {
			try {
				tryLog(u);
			} catch (NoSuchAlgorithmException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}
			else {
				if(checkTime(u)) {
					u.block(0);
					u.setAttempt(0);
					try {
						tryLog(u);
					} catch (NoSuchAlgorithmException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	

	private static boolean checkTime(User u) {
		LocalDateTime dateTime = LocalDateTime.now();
		String dataNow = dateTime.toString();
		
		System.out.println("Checking time, now: "+dataNow );
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
			
			}else {
				/*block user*/
				System.out.print("###################################################\n"+
						   "You have been blocked wait 2 minutes and try again\n" );
				u.block(1);
				
			}
			scanner.close();
	}
}

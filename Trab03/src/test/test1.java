package test;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Scanner;
import mainpackage.*;
import javax.naming.InvalidNameException;
import javax.security.cert.CertificateException;

import mainpackage.User;

public class test1 {

	public static void main(String[] args) throws SQLException {
		// Register the user
		Database.setDatabase("./database/trabalho3.db");
		Scanner scanner = new Scanner(System.in);
		System.out.println("Insert a password of 5 digit all different");
		System.out.print("password:");
		String password = new String (scanner.nextLine());
		User u = new User();
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
	}

}

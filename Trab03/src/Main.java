import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Main {

	public static void main(String[] args) {
		Database.setDatabase("./database/trabalho3.db");
		System.out.println("###################################################\n"+
						   "		       LOGIN PAGE\n"+ 		   
						   "email:");
		String email = new String ( System.in.toString());
		/* check from the db if the email is valid or not */
		try {
			ResultSet resultSet= User.getUserResultSet(email) ;
			/* scoprire come leggere un resultSEt senzza che dia errore*/
			if(resultSet.wasNull()) {
				System.out.println(" you are not registred ask the admin to register you");
				try {
					User u = new User();
					u.registerUser("./Pacote_T3/Keys/admin-x509.crt",0,"admin");
				} catch (InvalidNameException | CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				/* the user is registred so...*/
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

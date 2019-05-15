import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Random;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Main {

	public static void main(String[] args) {
		System.out.println("###################################################\n"+
						   "		       LOGIN PAGE\n"+ 		   
						   "email:");
		String email = new String ( System.in.toString());
		/* check from the db if the email is valid or not */
//		try {
//			User.registerUser("./Pacote_T3/Keys/admin-x509.crt",0,"admin");
//		} catch (InvalidNameException | CertificateException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		System.out.println("###################################################\n"+
//							"		       LOGIN PAGE\n"+ 		   
//				   "email:"+email+"\n");
	    /* display buttons*/		
		
	}

	
	
	private static byte[] ReadArquive(Path pFile) {
		
		if(Files.exists(pFile) == false) {
			System.err.print("FILE DOESN'T EXIST, EXITING \n");
			System.exit(2);
		}
		
		try {
			System.out.println("lendo" +pFile.toString());
			byte[] fileBytes = Files.readAllBytes(pFile);
			return fileBytes;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
}

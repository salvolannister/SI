import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class User {
	
	String Salt;
	private String email;
	String hashPassword;
	String password;
	String time;
	private int GID; 
	
	public User() {}
	
	public String getEmail() {
		return email;
	}
	
	private void setEmail(String path) throws CertificateException, InvalidNameException {
		byte[] certificate = ReadArquive(Paths.get(path));
		X509Certificate x509Certificate = X509Certificate.getInstance(certificate);
		String dn = x509Certificate.getSubjectDN().getName();
		LdapName ldapDN = new LdapName(dn);
		String email = new String( ldapDN.get(5));
		email = email.replace("EMAILADDRESS=", "");
		this.email = email;
		
	}
	public void setGroup(int GID) {
		this.GID = GID;
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
	
	public void registerUser(String path,int GID,String password) throws InvalidNameException, CertificateException {
		setEmail(path);
		String salt = generateSalt();
		setGroup(GID);
		/*scrivi nel DB la mail  e il salt*/
		/*genera l'hash code del salt e password e memorizza*/ 
		
//		System.out.println(email);
//		for(Rdn rdn: ldapDN.getRdns()) {
//		    System.out.println(rdn.getType() + " -> " + rdn.getValue());
//		}
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

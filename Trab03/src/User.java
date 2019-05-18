import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class User {
	
	String salt;
	private String email;
	String hexPassword;
	String password;
	String time;
	private int GID; 
	private int access;
	private String path;
	private int block;
	
	public User() {}
	
	public String getEmail() {
		return email;
	}
	
	public int getBlock() {
		return block;
	}

	public String getSalt() {
		return salt;
	}	
	
	private void setEmail(String path) throws CertificateException, InvalidNameException {
		byte[] certificate = Arquives.ReadArquive(Paths.get(path));
		X509Certificate x509Certificate = X509Certificate.getInstance(certificate);
		String dn = x509Certificate.getSubjectDN().getName();
		LdapName ldapDN = new LdapName(dn);
		String email = new String( ldapDN.get(5));
		System.out.println(email);
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
	

	
	public void registerUser(String path,int GID,String password) throws InvalidNameException, CertificateException, NoSuchAlgorithmException {
		this.path = path;
		setEmail(path);
		this.salt = generateSalt();
		setGroup(GID);
		/*genera l'hash code del salt e password e memorizza*/ 
		MessageDigest md = MessageDigest.getInstance("SHA1");
		byte[] toCheck = (salt+password).getBytes();
		md.update(toCheck);
		byte[] mdBytes =md.digest();
		/*convert it in HEX*/
		StringBuffer buf = new StringBuffer();
	    for(int i = 0; i < mdBytes.length; i++) {
	       String hex = Integer.toHexString(0x0100 + (mdBytes[i] & 0x00FF)).substring(1);
	       buf.append((hex.length() < 2 ? "0" : "") + hex);
	    }
	    
		this.hexPassword = buf.toString();
		this.access = 0;
		this.password = password;
		this.GID = GID;
		this.block = 0;
		Database.addUser(this);
		/*scrivi nel DB la mail, il salt, la password il numero di accessi*/
		
//		System.out.println(email);
//		for(Rdn rdn: ldapDN.getRdns()) {
//		    System.out.println(rdn.getType() + " -> " + rdn.getValue());
//		}
	}

	public String getHexPassword() {
		return hexPassword;
	}

	public int getGID() {
		return GID;
	}

	public String getTime() {
		// TODO Auto-generated method stub
		return time;
	}

	public int getAccess() {
		
		return access;
	}

	public String getDigitalPath() {
		
		return path;
	}
	
	public String[] getPandS() throws SQLException {
		ResultSet rs = null;
		String[] values = new String[2];
		
		if(hexPassword != null & salt!=null) {
			values[0] = hexPassword;
			values[1] = salt;
			return values;
		}else {
			this.hexPassword = rs.getString("password");
			this.salt = rs.getString("salt");
			values[0] = hexPassword;
			values[1] = salt;
			return values;
		}
	}

//	private String calculatePassword(String salt2, String hashPassword2) throws NoSuchAlgorithmException {
//		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//		return null;
//	}

	
	
}

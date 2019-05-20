package mainpackage;
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
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class User {
	
	String name; 
	String salt;
	private String email;
	String hexPassword;
	String password;
	String time;
	private int GID; 
	private int access;
	private byte[] certificate;
	private int block;
	int attempt;
	private String groupName;
	/*used in the sign in*/
	public User() {
		
	}
	
	public String getName() {
		return name;
	}
	
	public User(String email) throws SQLException {
		ResultSet rs = Database.getUser(email);
		salt = rs.getString("salt");
		GID = rs.getInt("gid");
		block = rs.getInt("block");
		access = rs.getInt("access");
		hexPassword = rs.getString("password");
		attempt = rs.getInt("attempt");
		if (block!=0)
			time = rs.getString("time");
		certificate = rs.getBytes("certificate");
		this.email = email;
		name = rs.getString("name");
		rs = Database.getGroupName(GID);
		groupName=rs.getString(1);
	}
	
	public String getEmail() {
		return email;
	}
	
	public int getBlock() {
		return block;
	}

	public String getSalt() {
		return salt;
	}	
	
	public void setEmail(byte[] certificate) throws CertificateException, InvalidNameException, SQLException {
		
		String[] values = Arquives.readCertificate(certificate);
		String email = values[5];
		String name = values[4];
		System.out.println(name);
		System.out.println(email);
		this.name =  name;
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
	

	/*add a user to the database and set a new instance of USER*/
	public void registerUser(String path,int GID,String password) throws InvalidNameException, CertificateException, NoSuchAlgorithmException, SQLException {
		this.certificate = Arquives.ReadArquive(Paths.get(path));
		setEmail(certificate);
		
		setGroup(GID);
		/*genera l'hash code del salt e password e memorizza*/
		
	    
		this.hexPassword = generateHexPassword(password);
		this.access = 0;
		this.password = password;
		this.GID = GID;
		this.block = 0;
		this.attempt= 0;
		ResultSet rs = Database.getGroupName(GID);
		groupName=rs.getString(1);
		Database.addUser(this);
		/*scrivi nel DB la mail, il salt, la password il numero di accessi*/
		
//		System.out.println(email);
//		for(Rdn rdn: ldapDN.getRdns()) {
//		    System.out.println(rdn.getType() + " -> " + rdn.getValue());
//		}
	}

	public String generateHexPassword(String password) throws NoSuchAlgorithmException {
		this.salt = generateSalt();
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
		return buf.toString();
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

	public byte[] getCertificate() {
		
		return certificate;
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

	public int getAttempt() {
		
		return attempt;
	}

	public void addAttempt() throws SQLException {
		attempt++;
	    Database.addAttempt(email,attempt);
		
	}

	public void block(int i) throws SQLException {
	     Database.changeBlockStatus(this,i);
		
	}

	public void setAttempt(int i) {
		attempt = i;
		
	}

	public String getGroupName() {
		// TODO Auto-generated method stub
		return groupName;
	}

	public void addAccess() throws SQLException {
		this.access = access + 1;
		Database.updateAccess(access,email);
		
	}

//	private String calculatePassword(String salt2, String hashPassword2) throws NoSuchAlgorithmException {
//		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//		return null;
//	}

	
	
}

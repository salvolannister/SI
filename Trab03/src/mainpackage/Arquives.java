package mainpackage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.sql.SQLException;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class Arquives {
	
	public static byte[] ReadArquive(Path pFile) {
		
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
	
	/* possible improvement: create a object certificate*/
	public static String[] readCertificate(byte[] certificate) throws CertificateException, InvalidNameException, SQLException {
		
		X509Certificate x509Certificate = X509Certificate.getInstance(certificate);
		x509Certificate.getVersion();
		x509Certificate.getSerialNumber().toString();
		x509Certificate.getNotAfter().toString();
		x509Certificate.getNotBefore().toString();
		x509Certificate.getSigAlgName().toString();
		x509Certificate.getIssuerDN().getName();
		String dn = x509Certificate.getSubjectDN().getName();
		LdapName ldapDN = new LdapName(dn);
		String[] values = new String[6];
		int  i = 0;
		for(Rdn rdn: ldapDN.getRdns()) {
		   values[i] = new String( rdn.getValue().toString());
		   i++;
		}
		return values;
		
		
	}
}

package test;

import java.nio.file.Paths;
import java.sql.SQLException;

import javax.naming.InvalidNameException;
import javax.security.cert.CertificateException;

import mainpackage.*;

public class test3 {

	public static void main(String[] args) throws CertificateException, InvalidNameException, SQLException {
		
		User u = new User();
		Object OK = PrivateKeyVerification.CheckPrivateKey(new String[] {"./Pacote_T3/Keys/user01-pkcs8-des.pem","user01","./Pacote_T3/Keys/user01-x509.crt" },u);
		/* verifica il funzionamento della lettura del certificato*/
		//byte[] certificate = Arquives.ReadArquive(Paths.get("./Pacote_T3/Keys/user01-x509.crt"));
		
		//u.setEmail(certificate);
	}

	

}

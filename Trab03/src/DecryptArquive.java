import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

public class DecryptArquive {

	private Path pEnv;
	private Path pEnc;
	private Path pAsd;
	PrivateKey k;
	PublicKey puK;
	Key secretKey; 
	
	public DecryptArquive(String path, PrivateKey prK, PublicKey puK) {
		String p = new String(path + ".enc");
		pEnc = Paths.get(p);
		p = new String(path +".env");
		pEnv = Paths.get(p);
		p = new String(path +".asd");
		pAsd = Paths.get(p);
		this.k = prK;
		this.puK = puK;
	}
	
	public byte[] decrypt() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
		
//		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		System.out.println( "\n" + cipher.getProvider().getInfo() );
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] cipherText = Arquives.ReadArquive(pEnv);
		byte[] seed = cipher.doFinal(cipherText);
		byte[] encText; 
		byte[] signatureText;	
		
		KeyGenerator key = KeyGenerator.getInstance("DES");
		SecureRandom random;
				
		random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(seed);
		key.init(56,random);
		/*with this key you will be able to decrypt every file*/
		secretKey =  key.generateKey();
		encText = Arquives.ReadArquive(pEnc);			
		cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);	
		byte[] fileText = cipher.doFinal(encText);
		/*verify signature: we verify the digest of .enc is the same of .asd */
		signatureText = Arquives.ReadArquive(pAsd);
		Signature sign = Signature.getInstance("MD5withRSA");
		sign.initVerify(puK);
		sign.update(fileText);
		boolean verified = sign.verify(signatureText);
	    if(verified) {
	    	System.out.println(" worked");
		return fileText;
	    }
	    else {
	    	System.out.println("Signature is wrong");
	    	return null;
	    }
	}
	
	
	
	
	
}

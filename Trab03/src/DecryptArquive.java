import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;

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
	Key secretKey; 
	
	public DecryptArquive(String path, PrivateKey k) {
		String p = new String(path + ".enc");
		pEnc = Paths.get(p);
		p = new String(path +".env");
		pEnv = Paths.get(p);
		p = new String(path +".asd");
		pEnv = Paths.get(p);
		this.k = k;
	}
	
	public byte[] decrypt() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] cipherText = ReadArquive(pEnv);
		byte[] seed = cipher.doFinal(cipherText);
		byte[] encText; 
		byte[] signatureText;	
		KeyGenerator key = KeyGenerator.getInstance("DES");
		SecureRandom random;
				
		random = SecureRandom.getInstance("SHA1PRNG");
		random.setSeed(seed);
		key.init(56,random);
		/*with this key youwill be able to decrypt every file*/
		secretKey =  key.generateKey();
		encText = ReadArquive(pEnc);			
		
		cipher.getInstance("DES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);	
		/*verify signature */
		signatureText = ReadArquive(pAsd);
		Signature sign = Signature.getInstance("MD5withRSA");
		
		return cipher.doFinal(encText);
	}
	
	
	
	private static byte[] ReadArquive(Path pFile) {
		
		if(Files.exists(pFile) == false) {
			System.err.print("FILE DOESN'T EXIST, EXITING \n");
			System.exit(2);
		}
		
		try {
			byte[] fileBytes = Files.readAllBytes(pFile);
			return fileBytes;
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
}

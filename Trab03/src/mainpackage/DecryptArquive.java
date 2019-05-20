package mainpackage;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
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
	
	public byte[] decrypt()  {
		
//		Security.addProvider(new BouncyCastleProvider());
		Cipher cipher;
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			System.out.println( "\n" + cipher.getProvider().getInfo() );
			try {
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
				cipher =Cipher.getInstance("DES/ECB/PKCS5Padding");
				cipher.init(Cipher.DECRYPT_MODE, secretKey);	
				byte[] fileText = cipher.doFinal(encText);
				/*verify signature: we verify the digest of .enc is the same of .asd */
				signatureText = Arquives.ReadArquive(pAsd);
				Signature sign = Signature.getInstance("MD5withRSA");
				sign.initVerify(puK);
				boolean verified = false;
				try {
					sign.update(fileText);
					verified = sign.verify(signatureText);
				} catch (SignatureException e) {
					System.out.println("Signature verification failed");
					e.printStackTrace();
				}
				
			    if(verified) {
			    	System.out.println("Signature verified");
				return fileText;
			    }
			    else {
			    	System.out.println("Signature is wrong");
			    	return null;
			    }
			} catch (InvalidKeyException e) {
				System.out.println("PrivateKey is wrong");
				return null;
				
			} catch (IllegalBlockSizeException e) {
				System.out.println("Block size is wrong cipher.dofinal didn't work");
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			
			
			System.out.println("Algorithm or Padding error in cipher decrypting the envelope");
			return null;
		}
		return null;	
	}

}

package mainpackage;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class PrivateKeyVerification {

	public static boolean CheckPrivateKey(String args[], User u) throws CertificateException {
		
		if(args.length < 2) {
			/*.pem file, password, .crt file*/
			System.out.println("Usage: BinaryfilePath secretPhrase ");
			System.exit(1);
		}
		boolean state = false;
		
		try {
			Cipher chipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			byte [] seed = args[1].getBytes();
			byte [] certificate = null;
			Key k = generateSecretKey(seed);
			byte[] base64Text;
			byte[] encryptedText;
			Path pFile = Paths.get(args[0]) ;
			String privateKey64encoded;
			byte[] pK64decoded;
			/* after generating the key from the secret password decrypt!*/
			encryptedText = Arquives.ReadArquive(pFile);
//			pFile = Paths.get(args[2]);
			certificate = u.getCertificate();
//			System.out.println( new String(certificate.toString()));
			
			try {
				base64Text = decryptDES(k,chipher,encryptedText);
				/* plainText contains the private Key and some additional phrase*/
				privateKey64encoded = parsePrivateKey(base64Text);
				/* now we have the 64BASE encoded String of the key and we decode using MIME that
				 * each line of the output is no longer than 76 characters and ends with a carriage 
				 * return followed by a linefeed (\r\n): cause with just getDecode() doesn't work :(*/
				
				pK64decoded = Base64.getMimeDecoder().decode(privateKey64encoded);
				
				PKCS8EncodedKeySpec Keyspec = new PKCS8EncodedKeySpec(pK64decoded);
				/*RSA is the standard for Asymmetric Key*/
				KeyFactory keyF = KeyFactory.getInstance("RSA");
				try {
					
					PrivateKey privateKey = keyF.generatePrivate(Keyspec);
					try {
						state = CheckSignature(privateKey,certificate);
					} catch (SignatureException | CertificateException e) {
						System.out.println(" Signature is not valid");
						return state;
					}
					if(state) {
						System.out.println("digital sig verification:"+ state);
						
						/*this code has to be deleted after*/
						X509Certificate x509Certificate = X509Certificate.getInstance(certificate);
						PublicKey publicKey = x509Certificate.getPublicKey();
						
						u.setPrivateKey(privateKey);
						u.setPublicKey(publicKey);
//						
//						String path = "./Pacote_T3/Files/" +"index";
//						/*controll validity of path after*/
//						DecryptArquive Da = new DecryptArquive(path, u.getPrk(),u.getPub());
//						try {
//								byte [] arquiveText = Da.decrypt();
//								System.out.println(new String(arquiveText, "UTF-8"));
//								} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
//										| IllegalBlockSizeException | BadPaddingException |SignatureException | NoSuchProviderException  e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								} catch (UnsupportedEncodingException e) {
//									// TODO Auto-generated catch block
//									e.printStackTrace();
//								}
						
					}
					else {
						return state;
					}
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException
					| UnsupportedEncodingException e) {
				
				System.out.println("PrivateKey is invalid or there is a problem with padding ");
				//e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e) {
			System.out.println("You can't use this algorithm");
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			
			e.printStackTrace();
		}
	
		return state;
	}
	
	private static boolean CheckSignature(PrivateKey privateKey, byte[] certificate) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		X509Certificate x509Certificate = X509Certificate.getInstance(certificate);
		PublicKey publicKey = x509Certificate.getPublicKey();
		/* now encrypt a random array with a privateKey and decrypt it using the public key to check DS*/
		byte[] message = new byte[2048];
		
		Signature signature = Signature.getInstance("MD5withRSA");
	    signature.initSign(privateKey);
        signature.update(message);
        byte[] cipherMessage = signature.sign(); /* digital signature*/

        signature.initVerify(publicKey);
        signature.update(message); /* original message*/

        if(signature.verify(cipherMessage))
        	return true;
	
		return false;
	}

	/* it removes BEGIN key and --END KEY--*/
	private static String parsePrivateKey(byte[] plainText) {
		String decrypted = new String(plainText);
		int i = 0;
		String[] parts = decrypted.split("\n"); 
		StringBuffer sb = new StringBuffer();
		for(String s: parts) {
			if(i == 0 || i ==( parts.length-1)) {
				
			}else {
				sb.append(s+"\n");
			}
			i++;
		}
	//	System.out.println(sb);
		return sb.toString();
	}

	

	private static Key generateSecretKey(byte[] seed) {
		// how do I use SHA1PRNG??
		
		try {
			KeyGenerator key = KeyGenerator.getInstance("DES");
			SecureRandom random;
			try {
				random = SecureRandom.getInstance("SHA1PRNG");
				random.setSeed(seed);
				key.init(56,random);
				return key.generateKey();
				
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		return null;
	}

	public static byte[] decryptDES(Key k,  Cipher cipher,byte[] cipherText) throws IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException  {
	
		try {
			cipher.init(Cipher.DECRYPT_MODE, k);
		} catch (InvalidKeyException e) {
			System.out.println("Secret Key is wrong");
		}
		byte[] newPlainText = cipher.doFinal(cipherText);
//		System.out.println( "Finish decryption: " );
//		System.out.println( new String(newPlainText, "UTF-8") );
		return newPlainText;
	}


	
//	public static String convertToString(byte[] fileBytes) {
//		String string = new String();
//		if(fileBytes != null) {
//			for(int i = 0; i < fileBytes.length; i++)
//				string = string + String.format("%02X", fileBytes[i]);
//		}
//		return string;
//	}
}

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class DecryptArquive {

	private Path pEnv;
	private Path pEnc;
	private Path pAsd;
	PrivateKey k;
	
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
		 Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, k);
		byte[] cipherText = ReadArquive(pEnv);
		byte[] newPlainText = cipher.doFinal(cipherText);
        
		return newPlainText;
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

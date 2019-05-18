package mainpackage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}

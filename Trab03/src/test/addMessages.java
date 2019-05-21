package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Paths;
import java.sql.SQLException;

import mainpackage.Arquives;
import mainpackage.Database;

public class addMessages {
 
	public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
		Database.setDatabase("./database/trabalho3.db");
		
		
		try(BufferedReader br = new BufferedReader(new FileReader("messages.txt"))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    int i = 0;
		    int code = 0 ;
		    String message = null;
		    while (line != null) {
		    	if((i%2) == 0) {
		    	 code = Integer.parseInt(line);
		    	}else {
		    		message= line;
		    		Database.addMessage(code, message);
		    	}
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		        i++;
		        
		    }
//		    String everything = sb.toString();
		   
;		} catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
		
		
	}
}

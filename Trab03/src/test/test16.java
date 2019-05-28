package test;

import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mainpackage.Arquives;
import mainpackage.Database;

public class test16 {
	
	public static void main(String[] args) {
	Database.setDatabase("./database/trabalho3.db");
	
	String sql = "UPDATE userdata SET certificate = ? WHERE email = ?;";
	PreparedStatement preparedStatement = null;
	try {
		preparedStatement = Database.connection.prepareStatement(sql);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	byte[] certificate = Arquives.ReadArquive(Paths.get("./Pacote_T3/Keys/admin-x509.crt"));
	try {
		preparedStatement.setBytes(1,certificate);
		preparedStatement.setString(2,"admin@inf1416.puc-rio.br");
	    preparedStatement.executeUpdate();
	    preparedStatement.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	
	}
}

package test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import mainpackage.Database;

public class logView {

	public static void main(String[] args) throws SQLException {
		Database.setDatabase("./database/trabalho3.db");
		Database.printLogs();
		String sql = "Select MAX(id) from records";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
		
		
		ResultSet rs = preparedStatement.executeQuery();
		int i =rs.getInt(1);
		System.out.println(i);
		preparedStatement.close();
	}

}

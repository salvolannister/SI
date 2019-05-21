package test;

import java.sql.SQLException;

import mainpackage.Database;

public class testCount {
	
	
	
	public static void main(String[] args) throws SQLException {
		Database.setDatabase("./database/trabalho3.db");
		Database.getTotalAccess();

	}

}

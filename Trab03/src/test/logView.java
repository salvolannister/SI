package test;

import mainpackage.Database;

public class logView {

	public static void main(String[] args) {
		Database.setDatabase("./database/trabalho3.db");
		Database.printLogs();

	}

}

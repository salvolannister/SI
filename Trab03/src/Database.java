import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
	
	private static Database DB ;
	public static Connection connection; 
	
	public void Database() {
		DB = null;
	}
	
	public static void setDatabase(String path) {
		Database DB = new Database();
		String url = "jdbc:sqlite:";
	     url =url +path.replaceFirst("./","");
	     System.out.println(url);

       try {
		DB.connection = DriverManager.getConnection(url);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	


}

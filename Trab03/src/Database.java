import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	
	public static boolean addUser(User u) {
		String sql ="INSERT INTO userdata(email, password, gid, time, access, digitalpath, block, salt) VALUES (?, ?, ?, ?, ?, ?, ?,?)";
		try {
			PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
			preparedStatement.setString(1, u.getEmail());
			preparedStatement.setString(2, u.getHexPassword());
			preparedStatement.setInt(3, u.getGID());
			preparedStatement.setString(4, u.getTime());
			preparedStatement.setInt(5, u.getAccess());
			preparedStatement.setString(6, u.getDigitalPath());
			preparedStatement.setInt(7, u.getBlock());
			preparedStatement.setString(8, u.getSalt());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		
	}

	public static boolean checkUserExistence(String email2) throws SQLException {
		boolean exist = false;
		String sql = "SELECT COUNT(*) as total FROM userdata WHERE email = ?;";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setString(1, email2);

        ResultSet resultSet = preparedStatement.executeQuery();
      if(  resultSet.getInt("total")!= 0 ) 
    	  return true;
        else 
        	return false;
        
	}
	
	public static ResultSet retriveUser(String email2) throws SQLException {
		String sql = "SELECT * FROM userdata WHERE email =?";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setString(1, email2);

        ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet;
		
	}
}

package mainpackage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

public class Database {
	
	
	public static Connection connection; 
	public static void setDatabase(String path) {
		
		String url = "jdbc:sqlite:";
	     url =url +path.replaceFirst("./","");
	     System.out.println(url);

       try {
		Database.connection = DriverManager.getConnection(url);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	}
	
	public static boolean addUser(User u) {
		String sql ="INSERT INTO userdata(email, password, gid, time, access, certificate, block, salt, attempt, name) VALUES (?,?, ?, ?, ?, ?, ?, ?,?,?)";
		try {
			PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
			preparedStatement.setString(1, u.getEmail());
			preparedStatement.setString(2, u.getHexPassword());
			preparedStatement.setInt(3, u.getGID());
			preparedStatement.setString(4, u.getTime());
			preparedStatement.setInt(5, u.getAccess());
			preparedStatement.setBytes(6, u.getCertificate());
			preparedStatement.setInt(7, u.getBlock());
			preparedStatement.setString(8, u.getSalt());
			preparedStatement.setInt(9, 0);
			preparedStatement.setString(10, u.getName());
			preparedStatement.executeUpdate();
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		
	}

	
	
	public static boolean checkUserExistence(String email2) throws SQLException {
		String sql = "SELECT COUNT(*) as total FROM userdata WHERE email = ?;";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setString(1, email2);

        ResultSet resultSet = preparedStatement.executeQuery();
      if(  resultSet.getInt("total")!= 0 ) 
    	  return true;
        else 
        	return false;
        
	}
	
	public static ResultSet getUser(String email2) throws SQLException {
		String sql = "SELECT * FROM userdata WHERE email =?";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setString(1, email2);

        ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet;
		
	}

	public static void addAttempt(String email2, int attempt) throws SQLException {
		String sql = "UPDATE userdata SET attempt = ? WHERE email = ?;";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setString(1, email2);
        preparedStatement.setInt(2,attempt);
        preparedStatement.executeUpdate();
        preparedStatement.close();
		
	}

	public static void blockUser(String email2) {
		
		
	}

	public static void changeBlockStatus(User u, int i) throws SQLException {
		if(i == 0) {
		String sql = "UPDATE userdata SET block = ?, attempt = ? WHERE email = ?;";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
		preparedStatement.setInt(1, i);
		preparedStatement.setInt(2, 0);
		preparedStatement.setString(3, u.getEmail());
        preparedStatement.executeUpdate();
        preparedStatement.close();
		}else {
			// i =1 ; set also the time 
			LocalDateTime dateTime = LocalDateTime.now();
			String data = dateTime.toString();
			System.out.println("data= "+data);
			String sql = "UPDATE userdata SET block = ?, time= ? WHERE email = ?;";
			PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
			preparedStatement.setInt(1, i);
			preparedStatement.setString(2, data);
			preparedStatement.setString(3, u.getEmail());
	        preparedStatement.executeUpdate();
	        preparedStatement.close();
		}
	}

	public static ResultSet getGroupName(int i) throws SQLException {
		String sql = "SELECT name FROM groups WHERE gid =?";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setInt(1, i);

        ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet;
		
	}

}

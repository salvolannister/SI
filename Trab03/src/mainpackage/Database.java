package mainpackage;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
			preparedStatement.close();
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
	
	public static void getUser(String email2, User u) throws SQLException {
		String sql = "SELECT * FROM userdata WHERE email =?";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setString(1, email2);
        ResultSet rs = preparedStatement.executeQuery();
        
        u.setSalt(rs.getString("salt"));
        int GID = rs.getInt("gid");
		u.setGroup(GID);
		int block = rs.getInt("block");
		u.setBlock( block);
		u.setAccess(rs.getInt("access"));
		u.setHex(rs.getString("password"));
		u.setAttempt(rs.getInt("attempt"));
		if (block!=0)
			u.setTime(rs.getString("time"));
		u.setCertificate(rs.getBytes("certificate"));
		
		u.setName(rs.getString("name"));
		rs.close();
		preparedStatement.close();
		Database.getGroupName(GID, u);
		
		
       
		
		
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

	public static  void getGroupName(int i, User u) throws SQLException {
		String sql = "SELECT name FROM groups WHERE gid =?";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
        preparedStatement.setInt(1, i);
       
        ResultSet resultSet = preparedStatement.executeQuery();
        u.setGroupName(resultSet.getString(1));
		preparedStatement.close();
		
	}
	
	/*to be checked*/
	public static void updateAccess(int access, String email2) throws SQLException {
		String sql = "UPDATE userdata SET access = ? WHERE email = ?;";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
		preparedStatement.setInt(1,access);
		preparedStatement.setString(2, email2);
        preparedStatement.executeUpdate();
        preparedStatement.close();
		
	}
	
	public static void updatePassword(User u, String newPassword, String newPath)throws SQLException, NoSuchAlgorithmException {
		String sql = "UPDATE userdata SET password = ?, salt= ?, certificate=? WHERE email = ?;";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
		String hexPassword = u.generateHexPassword(newPassword);
		preparedStatement.setString(1,hexPassword);
		preparedStatement.setString(2,u.getSalt()) ;
		byte[] certificate = Arquives.ReadArquive(Paths.get(newPath));
		preparedStatement.setBytes(3,certificate) ;
		preparedStatement.setString(4,u.getEmail()) ;
        preparedStatement.executeUpdate();
        preparedStatement.close();
		
	}

	public static int getTotalAccess() throws SQLException {
		String sql = "SELECT COUNT(*) as numU FROM userdata";
		PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
	    ResultSet resultSet = preparedStatement.executeQuery();
        int numU = resultSet.getInt(1);
//        System.out.println("numU: "+numU);
        preparedStatement.close();
		return numU;
	}

	public static void addMessage(int code, String line) {
		String sql ="INSERT INTO messages(recordID, operation) VALUES (?,?)";
		try {
			PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
			preparedStatement.setInt(1, code);
			preparedStatement.setString(2,line);
			
			preparedStatement.executeUpdate();
			preparedStatement.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void addLog(int code) {
		 LocalDateTime dateTime = LocalDateTime.now();
		 String data = dateTime.format( DateTimeFormatter.ISO_DATE_TIME);
		 String sql ="INSERT INTO records(recordID, date) VALUES (?,?)";
			try {
				PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
				preparedStatement.setInt(1, code);
				preparedStatement.setString(2,data);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	}
	
	public static void addLog(int code, String email, String fileName) {
		 LocalDateTime dateTime = LocalDateTime.now();
		 String data = dateTime.format( DateTimeFormatter.ISO_DATE_TIME);
		 String sql ="INSERT INTO records(recordID, date, email, file,id) VALUES (?,?,?,?,?)";
		 int maxID = getMaxID();
			try {
				PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
				preparedStatement.setInt(1, code);
				preparedStatement.setString(2,data);
				preparedStatement.setString(3,email);
				preparedStatement.setString(4,email);
				preparedStatement.setInt(5, maxID);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	}

	public static void addLog(int code, String email) {
		LocalDateTime dateTime = LocalDateTime.now();
		 String data = dateTime.format( DateTimeFormatter.ISO_DATE_TIME);
		 String sql ="INSERT INTO records(recordID, date, email,id) VALUES (?,?,?,?)";
		 int maxID = getMaxID();
			try {
				PreparedStatement preparedStatement = Database.connection.prepareStatement(sql);
				preparedStatement.setInt(1, code);
				preparedStatement.setString(2,data);
				preparedStatement.setString(3,email);
				preparedStatement.setInt(4, maxID);
				preparedStatement.executeUpdate();
				preparedStatement.close();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}

	public static  void printLogs()
	{
		String query = "select * from records, messages where messages.recordID = records.recordID order by records.id";
		
		
		try {
			PreparedStatement preparedStatement = Database.connection.prepareStatement(query);
			ResultSet rs =preparedStatement.executeQuery();
			
			
			int i = 1;
			while(rs.next())
			{
				String mensagem = rs.getString("operation");
				if(rs.getString("email")!= null) {
				mensagem = mensagem.replaceAll("<login_name>", rs.getString("email"));
				if(rs.getString("file")!= null)
				mensagem = mensagem.replaceAll("<arq_name>", rs.getString(3));
				}
				System.out.println(i + "\t" + rs.getString(1) + "\t" + mensagem);
				i++;
			}
			preparedStatement.close();
			rs.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		
	}

	private static int getMaxID() {
		String sql = "Select MAX(id) from records";
		PreparedStatement preparedStatement;
		int i=0;
		try {
			preparedStatement = Database.connection.prepareStatement(sql);
			
			ResultSet rs;
			
				rs = preparedStatement.executeQuery();
				i = rs.getInt(1);
				preparedStatement.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	
		return i;
	}

}

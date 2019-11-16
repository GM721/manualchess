import java.sql.*;
import java.io.*;
import waghstrategy.*;

class Database
{
	static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String DB_URL = "jdbc:mysql://localhost/manualchess";
	static String DB_USER = "user";
	static String DB_PASS = "pass";
	Connection conn;
	Database() throws SQLException
	{
		conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
	}
	public void execUpdate(String sql)
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	public void adduser(PlayerRegister user) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO users (nickname, email, password, connectionKey) VALUES (" + user.nickname + "," + user.email + "," + user.password + "," + user.connectionKey + ");");
		stmt.close();
	}
	public Boolean isFree(String nickname) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE nickname=" + nickname + ";");
		if (!rs.next())
			return true;
		else
			return false;
	}
	//template execQuery function
	/*public User getUser(int id)
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.execQuery("SELECT * FROM manualchess WHERE id=" + id);
		rs.next();
		User user = new User(rs.getInt("id"), rs.getString("nickname"), rs,getString("email"), rs.getString("connectionKey");
	}*/
}


/*class Main
{
	public static void main(String[] args)
	{
		try
		{
			String JDBC_DRIVER = "com.mysql.jdbc.Driver";
			String DB_URL = "jdbc:mysql://localhost/manualchess";
			Class.forName(JDBC_DRIVER);
			Connection conn = DriverManager.getConnection(DB_URL, "user", "pass");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO users VALUES (1, 'QWERTY', 'MALI', 123);");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}*/

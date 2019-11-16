package DataBase;

import java.sql.*;
import java.io.*;
import CommonClasses.*;

public class DataBase
{
	static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static String DB_URL = "jdbc:mysql://localhost/manualchess";
	static String DB_USER = "user";
	static String DB_PASS = "pass";
	Connection conn;
	public DataBase() throws SQLException, ClassNotFoundException, InstantiationException
	{
		try{
		DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());
		conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
		}
		catch (Exception e){};
	}
	public void execUpdate(String sql) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(sql);
		stmt.close();
	}
	public void addUser(PlayerRegister user, int connKey) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("INSERT INTO users (nickname, email, password, connectionKey) VALUES ('" + user.nickname + "','" + user.email + "','" + user.password + "'," + connKey + ");");
		stmt.close();
	}
	public Boolean isFree(String nickname) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE nickname='" + nickname + "';");
		stmt.close();
		if (!rs.next())
			return true;
		else
			return false;
	}
	public Boolean checkUser(PlayerAuthorise user) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT password FROM users WHERE nickname='" + user.nickname + "';");
		rs.next();
		return user.password.equals(rs.getString("password"));
	}
	public int getConnectionKey(String nickname) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT connectionKey FROM users WHERE nickname='" + nickname + "';");
		rs.next();
		return rs.getInt("connectionKey");
	}
	public void setConnKey(String nickname, int connKey) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET connectionKey=" + connKey + " WHERE nickname='" + nickname + "';");
		stmt.close();
	}
	public void dropConnKey(String nickname) throws SQLException
	{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET connectionKey = 0 WHERE nickname = '" + nickname + "';");
		stmt.close();
	}
}

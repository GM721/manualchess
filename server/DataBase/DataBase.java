package DataBase;

import CommonClasses.*;

import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.time.*;

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
		catch (Exception e){	
			e.printStackTrace();
		};
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
	public Boolean freeNickname (String nickname) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE nickname='" + nickname + "';");
		if (!rs.next()) {
			stmt.close();
			rs.close();
			return true;
		}
		else{
			stmt.close();
			rs.close();
			return false;
		}
	}
	public Boolean freeEmail (String  email) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email='" + email + "';");
		if (!rs.next()) {
			stmt.close();
			rs.close();
			return true;
		}
		else{
			stmt.close();
			rs.close();
			return false;
		}
	}
	public Boolean freePassword (String  pass) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE password='" + pass + "';");
		if (!rs.next()) {
			stmt.close();
			rs.close();
			return true;
		}
		else{
			stmt.close();
			rs.close();
			return false;
		}

	}
	public Boolean checkUser(PlayerAuthorise user) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT password FROM users WHERE nickname='" + user.nickname + "';");
		rs.next();
		Boolean res = user.password.equals(rs.getString("password"));
		rs.close();
		stmt.close();
		return res;
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

	public java.util.Date addMessage(MessageQuery message) throws SQLException, IOException {
		Statement stmt = conn.createStatement();
		String time= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		stmt.executeUpdate("INSERT INTO messages (sender,receiver, date, message) VALUES ('"+ message.nickname +"','"+ message.collocutor +"','"+time +"','"+ message.message +"');");
		ResultSet rs = stmt.executeQuery("SELECT date FROM messages WHERE sender  = '" + message.nickname+"' AND  receiver = '" + message.collocutor+ "' AND date = '"+time+"' ;");
		//MessageSender.send(message);
		if( rs.next()) {
			java.util.Date re = rs.getDate("date");
			rs.close();
			stmt.close();
			return re;
		}
		else {
			rs.close();
			stmt.close();
			return null;
		}
	}

	public void setOnline (String name) throws  SQLException{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET online = NULL  WHERE nickname = '" + name + "';");
		stmt.close();
	}

	public void setOffline (String name) throws  SQLException{
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("UPDATE users SET online = NOW()  WHERE nickname ='"+ name + "';");
		stmt.close();
	}

	public Date checkOnline (String name) throws SQLException {
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT online FROM users WHERE nickname = '" + name + "';");
		rs.next();
		Date res = rs.getDate("online");
		rs.close();
		stmt.close();
		return res;
	}
	public MessagesAnswer getNewMessages(RequestMessagesQuery rmq)
	{
		MessagesAnswer ans = new MessagesAnswer();
		try
		{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from messages where date > '" + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")).format(rmq.date.toDate()) +
					"' and (sender = '" + rmq.nickname + "' or receiver = '" + rmq.nickname + "');");
			while (rs.next())
			{
				Calendar date = Calendar.getInstance();
				java.util.Date fromSql = rs.getTimestamp("date");
				LocalDate localDate = fromSql.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
				date.set(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), fromSql.getHours(), fromSql.getMinutes(), fromSql.getSeconds());
				ans.messages.add(new Message(rs.getString("sender"), rs.getString("receiver"), rs.getString("message"), date));
			}
			rs.close();
			stmt.close();
			ans.isOk = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ans.isOk = false;
			ans.description = "Something went wrong. :(";
		}
		return ans;
	}
	public Boolean isUser(String user) throws SQLException
	{
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery("select * from users where nickname = '" + user + "';");
		Boolean res = rs.next();
		rs.close();
		stmt.close();
		return res;
	}
	/*private void test () throws SQLException{
	  Statement stmt = conn.createStatement();
	  ResultSet rs = stmt.executeQuery("NOW();");
	  System.out.println( rs);
	}

	public static void main (String [] args) throws ClassNotFoundException, SQLException, InstantiationException {
	DataBase db = new DataBase();
	db.test();
	}*/

}



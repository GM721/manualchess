package DataBase;

import CommonClasses.*;

import  java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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
            ResultSet rs = stmt.executeQuery("SELECT password, connectionKey FROM users WHERE nickname='" + user.nickname + "';");
            rs.next();
            return user.password.equals(rs.getString("password")) && rs.getInt("connectionKey") == 0;
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

        public Date addMessage(MessageQuery message) throws SQLException, IOException {
            Statement stmt = conn.createStatement();
            String time= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //Date time = new Date();
            stmt.executeUpdate("INSERT INTO messages (sender,receiver,message_time, message) VALUES ('"+ message.nickname +"','"+ message.collocutor +"','"+time +"','"+ message.message +"');");
            ResultSet rs = stmt.executeQuery("SELECT message_time FROM messages WHERE sender  = '" + message.nickname+"' AND  receiver = '" + message.collocutor+ "' AND message_time = '"+time+"' ;");
	    MessageSender.send(message);
            if( rs.next()) {
                Date re =rs.getDate("message_time");
                stmt.close();
                return re;
            }
            else {
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
            stmt.close();
            return (rs.getDate("online"));
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



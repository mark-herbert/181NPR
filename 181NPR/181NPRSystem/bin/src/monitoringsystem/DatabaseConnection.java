package monitoringsystem;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Kenneth
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private Connection con;
    private Statement stmt;
    private ResultSet rs;
    
    public void connectDatabase () throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/database?user='root'&password''");
    }
    public void query (String statement) throws SQLException {
        stmt = con.createStatement();
        rs = stmt.executeQuery(statement);
    }
    
    public ResultSet getResult (){
        return rs;
    }
    
    public void insertDatabase (String statement) throws SQLException {
        stmt = con.createStatement();
        stmt.execute(statement);
    }
}

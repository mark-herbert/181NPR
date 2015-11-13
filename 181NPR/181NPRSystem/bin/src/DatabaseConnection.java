

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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection con;
    private PreparedStatement stmt;
    private ResultSet rs;
    
    public void connectDatabase () throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection("jdbc:mysql://localhost/181nprdb?user='root'&password''");
    }
    public void query (String statement) throws SQLException {
        stmt = con.prepareStatement(statement);
        rs = stmt.executeQuery();
    }
    
    public ResultSet getResult (){
        return rs;
    }
    
    public void insertDatabase (String statement) throws SQLException {
        stmt = con.prepareStatement(statement);
        stmt.executeUpdate();
    }
}

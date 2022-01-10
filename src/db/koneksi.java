package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
        
public class koneksi {
    
    private final String URL = "jdbc:mysql://localhost:3306/db_perpus";
    private final String USER = "root";
    private final String PASS = "";
    
    public Connection getConnection(){
        Connection con;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(URL,USER,PASS);
            System.out.println("Koneksi Berhasil");
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("Koneksi gagal");
            return con=null;
        }
    }
    
    public static void main(String[] args) {
        koneksi Koneksi = new koneksi();
        Koneksi.getConnection();
    }
} 

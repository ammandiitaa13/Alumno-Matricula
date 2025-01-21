package alumnomatricula;
import java.sql.*;

public class Conexion {
    private Connection conexion;
    private final String url = "";
    
    public void conectar() {   
        try { 
            conexion = DriverManager.getConnection(url, "", "");
            if (conexion == null || conexion.isClosed())
                System.out.println("Error al conectar");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Connection getConexion() { return conexion; }
    
    public void desconectar() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }   
    }
}

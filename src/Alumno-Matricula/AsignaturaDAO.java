package alumnomatricula;
import java.util.*;
import java.sql.*;
public class AsignaturaDAO {
    private static Scanner sc = new Scanner(System.in);
    private Connection conexion;
    
    public AsignaturaDAO(Connection conexion) { this.conexion = conexion; }
    
    public void insertar() {
        String codAsig, nombreAsig;
        String consultaSQL = "INSERT INTO asignaturas (codAsig, nombreAsig) VALUES (?, ?)";
        System.out.println("Introduce el código de la asignatura: ");
        codAsig = sc.nextLine();
        //Comprobar que la asignatura no exista
        if (!leerPorId(codAsig)) {
            System.out.println("Introduce el nombre de la asignatura: ");
            nombreAsig = sc.nextLine();
            try (PreparedStatement statement = conexion.prepareStatement(consultaSQL)) {
                statement.setString(1, codAsig);
                statement.setString(2, nombreAsig);
                statement.executeUpdate();
                System.out.println("Asignatura insertada correctamente.\n");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("Error: la asignatura ya existe \n"); 
    }
    
    public boolean leerPorId(String codAsig) {
        String consultaSQL = "SELECT * FROM asignaturas WHERE codAsig = ?";
        boolean encontrado = false;
        try (PreparedStatement stmt = conexion.prepareStatement(consultaSQL)) {
            stmt.setString(1, codAsig);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                encontrado = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return encontrado;
    }
}


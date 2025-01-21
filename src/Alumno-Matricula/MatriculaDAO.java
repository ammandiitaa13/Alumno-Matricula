package alumnomatricula;
import java.util.*;
import java.sql.*;

public class MatriculaDAO {
    private static Scanner sc = new Scanner(System.in);
    private AlumnoDAO alumnoDAO;
    private AsignaturaDAO asignaturaDAO;
    private Connection conexion;

    public MatriculaDAO(AlumnoDAO alumnoDAO, AsignaturaDAO asignaturaDAO, Connection conexion) {
        this.alumnoDAO = alumnoDAO;
        this.asignaturaDAO = asignaturaDAO;
        this.conexion = conexion;
    }
  
    public void insertar() {
        String codMatricula, dni, codAsig;
        String consultaSQL = "INSERT INTO matriculas (codMatricula, dni, codAsig) VALUES (?, ?, ?)";
        System.out.println("Introduce el código de matrícula: ");
        codMatricula = sc.nextLine();
        //Comprobar que no exista el codigo de matricula 
        if (!leerPorId(codMatricula)) {
            System.out.println("Introduce el DNI del alumno: ");
            dni = sc.nextLine();
            //Comprobar que existe el alumno
            if (alumnoDAO.leerPorId(dni)) { 
                System.out.println("Introduce el código de la asignatura: ");
                codAsig = sc.nextLine();
                //Comprobar que existe la asignatura
                if (asignaturaDAO.leerPorId(codAsig)) { 
                    try (PreparedStatement statement = conexion.prepareStatement(consultaSQL)) {
                        statement.setString(1, codMatricula);
                        statement.setString(2, dni);
                        statement.setString(3, codAsig);
                        statement.executeUpdate();
                        System.out.println("Matrícula insertada correctamente.\n");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Error: la asignatura no existe.\n");
                }
            } else {
                System.out.println("Error: el alumno con ese DNI no existe.\n");
            }
        } else {
            System.out.println("Error: el código de matrícula ya existe.\n");
        }
    }
    
    public boolean leerPorId(String codMatricula) {
        String consultaSQL = "SELECT * FROM matriculas WHERE codMatricula = ?";
        boolean encontrado = false;
        try (PreparedStatement stmt = conexion.prepareStatement(consultaSQL)) {
            stmt.setString(1, codMatricula);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                encontrado = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } 
        return encontrado;
    }
}



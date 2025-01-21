package alumnomatricula;
import java.sql.*;
import java.io.*;
import java.util.*;

public class AlumnoDAO {
    private Connection conexion;
    private static Scanner sc = new Scanner(System.in);

    public AlumnoDAO(Connection conexion) { this.conexion = conexion; }
    
    public void insertar() {
        String dni, nombre, fecha_nacimiento, direccion;
        String consultaSQL = "INSERT INTO alumnos (dni, nombre, fecha_nacimiento, direccion) VALUES (?, ?, ?, ?)";
        System.out.println("Introduce el DNI del alumno: ");
        dni = sc.nextLine();
        //Comprobar que no existe el alumno
        if (!leerPorId(dni)) {
            System.out.println("Introduce el nombre del alumno: ");
            nombre = sc.nextLine();
            System.out.println("Introduce la fecha de nacimiento (YYYY-MM-DD): ");
            fecha_nacimiento = sc.nextLine();
            //Comprobar que el formato de fecha es correcto
            if (fecha_nacimiento.matches("\\d{4}-\\d{2}-\\d{2}")) {
                System.out.println("Introduce la dirección: ");
                direccion = sc.nextLine();
                try (PreparedStatement statement = conexion.prepareStatement(consultaSQL)) {
                    statement.setString(1, dni);
                    statement.setString(2, nombre);
                    statement.setString(3, fecha_nacimiento);
                    statement.setString(4, direccion);
                    statement.executeUpdate();
                    System.out.println("Alumno insertado correctamente \n");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Formato de fecha incorrecto \n");
        } else
            System.out.println("Ya existe ese alumno \n");
    }

    public boolean leerPorId(String dni) {
        String consultaSQL = "SELECT * FROM alumnos WHERE dni = ?";
        boolean encontrado = false;
        try (PreparedStatement stmt = conexion.prepareStatement(consultaSQL)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();
            if (rs.next())
                encontrado = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return encontrado;
    }
    
    public void mostrarInfo() {
        String dni;
        String consultaSQL = "SELECT a.dni, a.nombre, a.fecha_nacimiento, a.direccion, " +
                             "m.codMatricula, m.codAsig, s.nombreAsig " +
                             "FROM alumnos a " +
                             "LEFT JOIN matriculas m ON a.dni = m.dni " +
                             "LEFT JOIN asignaturas s ON m.codAsig = s.codAsig " +
                             "WHERE a.dni = ?";
        boolean encontrado = false;
        System.out.println("Introduce el DNI del alumno: ");
        dni = sc.nextLine();
        try (PreparedStatement stmt = conexion.prepareStatement(consultaSQL)) {
            stmt.setString(1, dni);
            ResultSet rs = stmt.executeQuery();
            //Se comprueba que el dni exista en la BBDD
            while (rs.next()) {
                if (!encontrado) {
                    encontrado = true;
                    // Muestra informacion del alumno
                    System.out.println("\nDNI: " + rs.getString("dni"));
                    System.out.println("Nombre: " + rs.getString("nombre"));
                    System.out.println("Fecha de Nacimiento: " + rs.getString("fecha_nacimiento"));
                    System.out.println("Dirección: " + rs.getString("direccion") + "\n");
                }
                // Muestra matrículas y asignaturas (si existen)
                if (rs.getString("codMatricula") != null) {
                    System.out.println("Código Matrícula: " + rs.getInt("codMatricula"));
                    System.out.println("Código Asignatura: " + rs.getString("codAsig"));
                    System.out.println("Nombre Asignatura: " + rs.getString("nombreAsig") + "\n");
                }
                else 
                    System.out.println("No hay matriculas \n");
            }
            if (!encontrado) {
                System.out.println("No se encontró un alumno con ese DNI.\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ocurrió un error al buscar la información del alumno.\n");
        }
    }
    
    public void volcar() {
        String dniActual = "";
        BufferedWriter writer = null;
        boolean hayRegistros = false;
        String consultaSQL = "SELECT a.dni, a.nombre, a.fecha_nacimiento, a.direccion, " +
                             "m.codMatricula, m.codAsig, s.nombreAsig " +
                             "FROM alumnos a " +
                             "LEFT JOIN matriculas m ON a.dni = m.dni " +
                             "LEFT JOIN asignaturas s ON m.codAsig = s.codAsig " +
                             "ORDER BY a.dni";
        try (PreparedStatement stmt = conexion.prepareStatement(consultaSQL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                hayRegistros = true; // Hay al menos un registro
                String dni = rs.getString("dni");
                // Si es un nuevo alumno, cerrar archivo anterior (si existe) y abrir uno nuevo
                if (!dni.equals(dniActual)) {
                    if (writer != null) {
                        writer.close();
                    }
                    dniActual = dni;
                    writer = new BufferedWriter(new FileWriter(dni + ".txt"));
                }
                // Escribir los datos del alumno en el archivo correspondiente
                writer.write("DNI: " + (dni != null ? dni : "DNI no disponible"));
                writer.newLine();
                writer.write("Nombre: " + (rs.getString("nombre") != null ? rs.getString("nombre") : "Nombre no disponible"));
                writer.newLine();
                writer.write("Fecha de Nacimiento: " + (rs.getString("fecha_nacimiento") != null ? rs.getString("fecha_nacimiento") : "Fecha de nacimiento no disponible"));
                writer.newLine();
                writer.write("Dirección: " + (rs.getString("direccion") != null ? rs.getString("direccion") : "Dirección no disponible"));
                writer.newLine();
                writer.write("Código Matrícula: " + (rs.getObject("codMatricula") != null ? rs.getInt("codMatricula") : "Código de matrícula no disponible"));
                writer.newLine();
                writer.write("Código Asignatura: " + (rs.getString("codAsig") != null ? rs.getString("codAsig") : "Código de asignatura no disponible"));
                writer.newLine();
                writer.write("Nombre Asignatura: " + (rs.getString("nombreAsig") != null ? rs.getString("nombreAsig") : "Nombre de asignatura no disponible"));
                writer.newLine();
            }
            // Cerrar el último archivo si hubo alumnos
            if (writer != null) {
                writer.close();
            }
            if (!hayRegistros) {
                System.out.println("No hay alumnos registrados para volcar.\n");
            } else {
                System.out.println("Los archivos han sido creados exitosamente.\n");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println("Error al volcar.\n");
        }
    }

}



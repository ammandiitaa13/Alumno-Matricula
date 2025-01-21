package alumnomatricula;
import java.io.*;
import java.util.*;
import java.sql.*;
public class Main {
    private static Scanner sc = new Scanner(System.in);
    
    public static void main(String[] args) throws Exception {
        //Inicializacion de variables
        int opcion = 0, opcion2 = 0;
        Conexion conexion = new Conexion();
        conexion.conectar();
        Connection conn = conexion.getConexion();
        AlumnoDAO alumnoDAO = new AlumnoDAO(conn);
        AsignaturaDAO asignaturaDAO = new AsignaturaDAO(conn);
        MatriculaDAO matriculaDAO = new MatriculaDAO(alumnoDAO, asignaturaDAO, conn);
        // Menu
        do {
            mostrarMenu();
            opcion = sc.nextInt();
            sc.nextLine(); 
            
            switch (opcion) {
                case 1: alumnoDAO.insertar(); break;
                case 2: matriculaDAO.insertar(); break;
                case 3: asignaturaDAO.insertar(); break;
                case 4: alumnoDAO.mostrarInfo(); break;
                case 5: alumnoDAO.volcar(); break;
                case 6: System.out.println("Que desea eliminar: ");
                    System.out.println("1. Ficheros");
                    System.out.println("2. Base de datos ");
                    opcion2 = sc.nextInt();
                    sc.nextLine();
                    if (opcion2 == 1)
                        eliminarArchivos();
                    else if (opcion2 == 2)
                      eliminarBaseDeDatos();  
                    else 
                        System.out.println("Opcion no valida\n");
                    break;
                case 7: System.out.println("Adios "); break;
                default: System.out.println("Opción no válida \n");
            }
        } while (opcion != 7);
        conexion.desconectar();
    }
    //Metodo para el menu
    private static void mostrarMenu() {
        System.out.println("Bienvenido, que desea hacer: \n" +
                       "1. Agregar Alumno\n" +
                       "2. Insertar Matriculas\n" +
                       "3. Insertar Asignaturas\n" +
                       "4. Mostrar info de alumno\n" +
                       "5. Volcar alumnos a fichero\n" +
                       "6. Borrar \n" +
                       "7. Salir");
    }
    //Metodo para eliminacion de archivos
    private static void eliminarArchivos() {
        String borrarArchivos;
        System.out.println("¿Desea realmente borrar todos los archivos? ");
        System.out.println("Escriba si o no: "); 
        borrarArchivos = sc.nextLine();
        if (borrarArchivos.equalsIgnoreCase("si")) {
            File directorio = new File("."); 
            //Buscar archivos acabados en .txt
            File[] archivosTxt = directorio.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (archivosTxt == null || archivosTxt.length <= 0)
                System.out.println("Error al eliminar o no se encuentran archivos \n");
            else {
                for (File archivoTxt : archivosTxt) {
                    if (!archivoTxt.delete())  
                        System.out.println("No se pudo eliminar " + archivoTxt.getName() + "\n"); 
                    else
                        System.out.println("Archivos " + archivoTxt.getName() + " borrados correctamente \n");
                }
            }   
        } 
    }
    //Metodo para el vaciado de BBDD
    private static void eliminarBaseDeDatos() {
        Conexion conexion = new Conexion();
        conexion.conectar();
        String consultaTablas = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'jdbc'";
        String nombreBaseDeDatos = "jdbc";
        try (Connection conn = conexion.getConexion();
             Statement stmt = conn.createStatement()) {
            System.out.println("Vaciando tablas de la base de datos " + nombreBaseDeDatos);
            // Desactivar las comprobaciones de claves foráneas
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            try (ResultSet rs = stmt.executeQuery(consultaTablas)) {
                while (rs.next()) {
                    String tabla = rs.getString("TABLE_NAME");
                    String consultaTruncate = "TRUNCATE TABLE " + tabla;
                    try (Statement truncateStmt = conn.createStatement()) {
                        truncateStmt.executeUpdate(consultaTruncate);
                        System.out.println("Tabla '" + tabla + "' vaciada correctamente.");
                    } catch (SQLException e) {
                        System.out.println("Error al intentar vaciar la tabla '" + tabla + "': " + e.getMessage());
                    }
                }
            } 
            // Restaurar las comprobaciones de claves foráneas
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            System.out.println("Todas las tablas han sido vaciadas exitosamente.\n");
        } catch (SQLException e) {
            System.out.println("Error al obtener las tablas de la base de datos o al ejecutar el proceso.\n");
            e.printStackTrace();
        } finally {
            conexion.desconectar();
        }
    }
}

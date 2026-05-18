
package Conexion_Base_Datos;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase que gestiona la conexión a la base de datos MySQL.
 * Implementa el patrón Singleton para asegurar una única instancia de conexión.
 * 
 * @author Tu Nombre
 * @version 1.0
 * @since 2026-05-11
 */
public class ConexionBD {
    
    private final String BD = "taller_Informatica";
    private final String url = "jdbc:mysql://localhost:3306/" + BD;
    private final String contraseña = "mysql";
    private final String user = "root";
    private Connection con = null;

    /**
     * Constructor de la clase ConexionBD.
     * Inicializa y abre la conexión a la base de datos.
     */
    public ConexionBD() {
        abrirConexion();
    }

    /**
     * Establece la conexión con la base de datos.
     */
    private void abrirConexion() {
        try {
            Properties properties = new Properties();
            properties.setProperty("user", user);
            properties.setProperty("password", contraseña);
            properties.setProperty("useSSL", "false");
            properties.setProperty("serverTimezone", "Europe/Madrid");

            con = DriverManager.getConnection(url, properties);
            System.out.println("Conexión correcta a la base de datos.");

        } catch (SQLException ex) {
            System.out.println("Error al conectar con la base de datos.");
            System.out.println("Mensajee: " + ex.getMessage());
            con = null;
        }
    }

    /**
     * Obtiene la única instancia de la clase ConexionBD.
     * Implementa el patrón Singleton con holder.
     * 
     * @return La instancia única de ConexionBD
     */
    public static Conexion_Base_Datos.ConexionBD getInstance() {
        return AccesoBaseDatosHolder.INSTANCE;
    }

    /**
     * Clase holder para la implementación del patrón Singleton.
     */
    private static class AccesoBaseDatosHolder {
        private static final Conexion_Base_Datos.ConexionBD INSTANCE = new Conexion_Base_Datos.ConexionBD();
    }

    /**
     * Obtiene la conexión activa a la base de datos.
     * Si la conexión está cerrada o es nula, intenta reabrirla.
     * 
     * @return Conexión activa a la base de datos, o {@code null} si no se pudo conectar
     */
    public Connection getConn() {
        try {
            if (con == null || con.isClosed()) {
                abrirConexion();
            }
        } catch (SQLException ex) {
            System.out.println("Error al comprobar el estado de la conexión.");
            System.out.println("Mensaje: " + ex.getMessage());
        }
        return con;
    }

    /**
     * Cierra la conexión actual con la base de datos.
     * 
     * @return {@code true} si la conexión se cerró correctamente,
     *         {@code false} si ocurrió un error o la conexión ya estaba cerrada
     */
    public boolean cerrar() {
        if (con == null) {
            return true;
        }

        try {
            con.close();
            return true;
        } catch (SQLException ex) {
            System.out.println("Error al cerrar la conexión.");
            System.out.println("Mensaje: " + ex.getMessage());
            return false;
        }
    }
    
}
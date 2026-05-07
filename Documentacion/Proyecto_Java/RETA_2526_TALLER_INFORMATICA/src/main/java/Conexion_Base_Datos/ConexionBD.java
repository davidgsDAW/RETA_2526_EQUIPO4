
package Conexion_Base_Datos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionBD {
    
    
    private final String BD = "Taller";//confirmar nombre base de datos
    private final String url = "jdbc:mysql://localhost:3306/" + BD;
    private final String contraseña = "mysql";
    private final String user = "root";
    private Connection con = null;

    public ConexionBD() {
        abrirConexion();
    }

    private void abrirConexion() {
        try {
            Properties properties = new Properties();
            properties.setProperty("usuario", user);
            properties.setProperty("contraseña", contraseña);
            properties.setProperty("useSSL", "false");
            properties.setProperty("serverTimezone", "Europe/Madrid");

            con = DriverManager.getConnection(url, properties);
            System.out.println("Conexión correcta a la base de datos.");

        } catch (SQLException ex) {
            System.out.println("Error al conectar con la base de datos.");
            System.out.println("Mensaje: " + ex.getMessage());
            con = null;
        }
    }

    public static Conexion_Base_Datos.ConexionBD getInstance() {
        return AccesoBaseDatosHolder.INSTANCE;
    }

    private static class AccesoBaseDatosHolder {

        private static final Conexion_Base_Datos.ConexionBD INSTANCE = new Conexion_Base_Datos.ConexionBD ();
    }

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

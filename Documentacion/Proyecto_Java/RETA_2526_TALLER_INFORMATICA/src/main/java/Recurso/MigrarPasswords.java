package recurso;

import Conexion_Base_Datos.ConexionBD;
import java.sql.*;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Convierte las contraseñas en texto plano que ya existan en la tabla
 * 'usuarios' al formato BCrypt, necesario para que
 * LoginFrame pueda autenticar correctamente y encriptar las contraseñas para mas seguridad.
 *
 * @author David Gómez
 * @version 1.0
 */
public class MigrarPasswords {

  
    private static final int BCRYPT_COST = 12;

    public static void main(String[] args) {

        System.out.println("  Migración de contraseñas → BCrypt (cost " + BCRYPT_COST + ")");


        String sqlSelect = "SELECT id, usuario, password FROM usuarios";
        String sqlUpdate = "UPDATE usuarios SET password = ? WHERE id = ?";

        try (Connection con = ConexionBD.getInstance().getConn(); PreparedStatement psSelect = con.prepareStatement(sqlSelect); PreparedStatement psUpdate = con.prepareStatement(sqlUpdate)) {

            ResultSet rs = psSelect.executeQuery();
            int migrados = 0;
            int omitidos = 0;
            int errores = 0;

            while (rs.next()) {
                int id = rs.getInt("id");
                String usuario = rs.getString("usuario");
                String passActual = rs.getString("password");

                // Detectar si ya es un hash BCrypt válido 
                if (esBCrypt(passActual)) {
                    System.out.printf("  [OMITIDO]  %-20s  ya tiene hash BCrypt%n", usuario);
                    omitidos++;
                    continue;
                }

                try {
                    // Generar hash BCrypt a partir del texto plano
                    String hash = BCrypt.hashpw(passActual, BCrypt.gensalt(BCRYPT_COST));

                    psUpdate.setString(1, hash);
                    psUpdate.setInt(2, id);
                    psUpdate.executeUpdate();

                    System.out.printf("  [OK]       %-20s → hash generado%n", usuario);
                    migrados++;

                } catch (Exception ex) {
                    System.err.printf("  [ERROR]    %-20s → %s%n", usuario, ex.getMessage());
                    errores++;
                }
            }

 
            System.out.printf("  Migrados: %d | Omitidos: %d | Errores: %d%n",
                    migrados, omitidos, errores);


            if (migrados > 0) {
                System.out.println("  ✓ Migración completada. LoginFrame ya puede");
                System.out.println("    autenticar con BCrypt.");
            } else if (omitidos > 0 && errores == 0) {
                System.out.println("  ✓ Todos los usuarios ya tenían hash BCrypt.");
                System.out.println("    No era necesaria ninguna migración.");
            }

        } catch (SQLException ex) {
            System.err.println("Error de conexión a la BD: " + ex.getMessage());
        }
    }

    /**
     * Comprueba si una cadena es un hash BCrypt válido. Los hashes BCrypt
     * siempre empiezan por "$2a$", "$2b$" o "$2y$".
     *
     * @param valor cadena a comprobar
     * @return true si ya es un hash BCrypt
     */
    private static boolean esBCrypt(String valor) {
        if (valor == null || valor.length() < 7) {
            return false;
        }
        return valor.startsWith("$2a$") || valor.startsWith("$2b$") || valor.startsWith("$2y$");
    }
}

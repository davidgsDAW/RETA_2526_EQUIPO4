package Model_DAO;

import Clases.Prestamo;
import Clases.Usuario;
import Conexion_Base_Datos.ConexionBD;
import Interfaces.Repo_Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la entidad Usuario.Proporciona métodos para realizar operaciones CRUD y autenticación.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-12
 */
public class UsuarioDAO implements Repo_Usuario<Usuario> {
    
    private final Connection getConexion;
    
    /**
     * Constructor de la clase UsuarioDAO.
     * Obtiene la conexión a la base de datos mediante el Singleton ConexionBD.
     */
    public UsuarioDAO() {
        this.getConexion = ConexionBD.getInstance().getConn();
    }
    
    /**
     * Lista todos los usuarios registrados en el sistema.
     * 
     * @return Lista con todos los usuarios
     */
    @Override
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario,nombre,contrasena,id_rol FROM usuario";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId_usuario(rs.getInt("id_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setContraseña(rs.getString("contrasena"));
                usuarios.add(u);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }
    
    /**
     * Inserta un nuevo usuario en la base de datos.
     * 
     * @param u Objeto Usuario con los datos a insertar
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario
     */
    @Override
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuario(nombre, contrasena, id_rol) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setString(1, u.getNombre());
            pstmt.setString(2, u.getContraseña());
            pstmt.setString(3, u.getRol().name());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un usuario de la base de datos por su ID.
     * 
     * @param id Identificador único del usuario a eliminar
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario
     */
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Autentica a un usuario verificando sus credenciales.
     * 
     * @param u Usuario con nombre y contraseña para autenticar
     * @param p Préstamo asociado (puede ser {@code null} si no aplica)
     * @return Usuario autenticado si las credenciales son correctas,
     *         {@code null} en caso contrario
     */
    @Override
    public Usuario autenticar(Usuario u) {
        String sql = "SELECT * FROM usuario WHERE nombre = ? AND contrasena = ?";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setString(1, u.getNombre());
            pstmt.setString(2, u.getContraseña());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId_usuario(rs.getInt("id_usuario"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setContraseña(rs.getString("contrasena"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en autenticación: " + e.getMessage());
        }
        return null;
    }
}
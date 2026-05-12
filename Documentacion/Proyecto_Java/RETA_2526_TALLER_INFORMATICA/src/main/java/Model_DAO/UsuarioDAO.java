
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


public class UsuarioDAO implements Repo_Usuario<Usuario,Prestamo> {
    
    private final Connection getConexion;
    
    public UsuarioDAO() {
        this.getConexion = ConexionBD.getInstance().getConn();
    }
    
    @Override
    public List<Usuario> listar() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id_usuario,nombre,contrasena,id_rol FROM usuarios";
        
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
    
    @Override
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, contrasena, id_rol) VALUES (?, ?, ?)";
        
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
    
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public Usuario autenticar(Usuario u, Prestamo p) {
        String sql = "SELECT * FROM usuarios WHERE nombre = ? AND contrasena = ?";
        
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
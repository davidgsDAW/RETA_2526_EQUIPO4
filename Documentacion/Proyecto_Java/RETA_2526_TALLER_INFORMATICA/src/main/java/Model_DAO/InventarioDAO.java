package Model_DAO;

import Clases.Categoria;
import Clases.ElementoInventario;
import Conexion_Base_Datos.ConexionBD;
import Enumerados.Estado_Elemento;
import Enumerados.Localizacion;
import Interfaces.Repo_Inventario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO (Data Access Object) para la entidad ElementoInventario.
 * Proporciona métodos para realizar operaciones CRUD en la base de datos.
 * 
 * @author David Gómez
 * @version 1.0
 * @since 2026-05-12
 */
public class InventarioDAO implements Repo_Inventario {
    private Connection getConexion;
    
    /**
     * Constructor de la clase InventarioDAO.
     * Obtiene la conexión a la base de datos mediante el Singleton ConexionBD.
     */
    public InventarioDAO() {
        this.getConexion = ConexionBD.getInstance().getConn();
    }
    
    /**
     * Lista todos los elementos del inventario.
     * 
     * @return Lista con todos los elementos del inventario
     */
    @Override
    public List<ElementoInventario> listar() {
        List<ElementoInventario> elementos = new ArrayList<>();
        String sql = "SELECT e.id_material,e.nombre,e.descripcion,e.cantidad,e.fecha_alta,e.observaciones,e.id_categoria,e.id_estado,e.id_ubicacion"
                + ", c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM material e " +
                     "LEFT JOIN categoria c ON e.id_categoria = c.id_categoria";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                ElementoInventario e = new ElementoInventario();
                e.setId_elemento(rs.getInt("id_material"));
                e.setNombre(rs.getString("e.nombre"));
                e.setDescripcion(rs.getString("e.descripcion"));
                e.setCantidad(rs.getInt("e.cantidad"));
                
                String estadoStr = rs.getString("e.id_estado");
                if (estadoStr != null) {
                    e.setEstado(Estado_Elemento.valueOf(estadoStr));
                }
                
                String localizacionStr = rs.getString("id_ubicacion");
                if (localizacionStr != null) {
                    e.setLocalizacion(Localizacion.valueOf(localizacionStr));
                }
                
                Categoria categoria = new Categoria();
                categoria.setId_categoria(rs.getInt("id_categoria"));
                categoria.setNombre(rs.getString("categoria_nombre"));
                categoria.setDescripcion(rs.getString("categoria_descripcion"));
                e.setCategoria(categoria);
                
                elementos.add(e);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar inventario: " + e.getMessage());
        }
        return elementos;
    }
    
    /**
     * Busca un elemento del inventario por su ID.
     * 
     * @param id Identificador único del elemento
     * @return Elemento encontrado, o {@code null} si no existe
     */
    @Override
    public ElementoInventario buscarPorid(int id) {
        String sql = "SELECT  e.id_material,e.nombre,e.descripcion,e.cantidad,e.fecha_alta,e.observaciones,e.id_categoria,e.id_estado,e.id_ubicacion"
                + ", c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM material e " +
                     "LEFT JOIN categorias c ON e.id_categoria = c.id_categoria " +
                     "WHERE e.id_material = ?";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ElementoInventario e = new ElementoInventario();
                    e.setId_elemento(rs.getInt("id_material"));
                    e.setNombre(rs.getString("e.nombre"));
                    e.setDescripcion(rs.getString("e.descripcion"));
                    e.setCantidad(rs.getInt("e.cantidad"));
                    
                    String estadoStr = rs.getString("e.id_estado");
                    if (estadoStr != null) {
                        e.setEstado(Estado_Elemento.valueOf(estadoStr));
                    }
                    
                    String localizacionStr = rs.getString("e.id_ubicacion");
                    if (localizacionStr != null) {
                        e.setLocalizacion(Localizacion.valueOf(localizacionStr));
                    }
                    
                    Categoria categoria = new Categoria();
                    categoria.setId_categoria(rs.getInt("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    categoria.setDescripcion(rs.getString("categoria_descripcion"));
                    e.setCategoria(categoria);
                    
                    return e;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar elemento por id: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Busca un elemento del inventario por su estado.
     * 
     * @param estado Estado del elemento a buscar
     * @return Primer elemento encontrado con ese estado, o {@code null} si no existe
     */
    @Override
    public ElementoInventario buscarPorEstado(Estado_Elemento estado) {
        String sql = "SELECT  e.id_material,e.nombre,e.descripcion,e.cantidad,e.fecha_alta,e.observaciones,e.id_categoria,e.id_estado,e.id_ubicacion"
                + ", c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM material e " +
                     "LEFT JOIN categorias c ON e.id_categoria = c.id_categoria " +
                     "WHERE e.id_estado = ? LIMIT 1";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setString(1, estado.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ElementoInventario e = new ElementoInventario();
                    e.setId_elemento(rs.getInt("e.id_material"));
                    e.setNombre(rs.getString("e.nombre"));
                    e.setDescripcion(rs.getString("e.descripcion"));
                    e.setCantidad(rs.getInt("e.cantidad"));
                    e.setEstado(estado);
                    
                    String localizacionStr = rs.getString("e.id_ubicacion");
                    if (localizacionStr != null) {
                        e.setLocalizacion(Localizacion.valueOf(localizacionStr));
                    }
                    
                    Categoria categoria = new Categoria();
                    categoria.setId_categoria(rs.getInt("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    categoria.setDescripcion(rs.getString("categoria_descripcion"));
                    e.setCategoria(categoria);
                    
                    return e;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar elemento por estado: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Busca un elemento del inventario por su ubicación.
     * 
     * @param ubicacion Ubicación donde se encuentra el elemento
     * @return Primer elemento encontrado en esa ubicación, o {@code null} si no existe
     */
    @Override
    public ElementoInventario buscarPorLocalizacion(Localizacion ubicacion) {
        String sql = "SELECT  e.id_material,e.nombre,e.descripcion,e.cantidad,e.fecha_alta,e.observaciones,e.id_categoria,e.id_estado,e.id_ubicacion"
                + ", c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM material e " +
                     "LEFT JOIN categorias c ON e.id_categoria = c.id_categoria " +
                     "WHERE e.id_ubicacion = ? LIMIT 1";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setString(1, ubicacion.name());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ElementoInventario e = new ElementoInventario();
                    e.setId_elemento(rs.getInt("e.id_elemento"));
                    e.setNombre(rs.getString("e.nombre"));
                    e.setDescripcion(rs.getString("e.descripcion"));
                    e.setCantidad(rs.getInt("e.cantidad"));
                    
                    String estadoStr = rs.getString("e.id_estado");
                    if (estadoStr != null) {
                        e.setEstado(Estado_Elemento.valueOf(estadoStr));
                    }
                    
                    e.setLocalizacion(ubicacion);
                    
                    Categoria categoria = new Categoria();
                    categoria.setId_categoria(rs.getInt("id_categoria"));
                    categoria.setNombre(rs.getString("categoria_nombre"));
                    categoria.setDescripcion(rs.getString("categoria_descripcion"));
                    e.setCategoria(categoria);
                    
                    return e;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar elemento por ubicación: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Busca un elemento del inventario por su categoría.
     * 
     * @param categoria Categoría a la que pertenece el elemento
     * @return Primer elemento encontrado en esa categoría, o {@code null} si no existe
     */
    @Override
    public ElementoInventario buscarPorCategoria(Categoria categoria) {
        String sql = "SELECT  e.id_material,e.nombre,e.descripcion,e.cantidad,e.fecha_alta,e.observaciones,e.id_categoria,e.id_estado,e.id_ubicacion"
                + ", c.nombre as categoria_nombre, c.descripcion as categoria_descripcion " +
                     "FROM material e " +
                     "LEFT JOIN categorias c ON e.id_categoria = c.id_categoria " +
                     "WHERE e.id_categoria = ? LIMIT 1";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setInt(1, categoria.getId_categoria());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ElementoInventario e = new ElementoInventario();
                    e.setId_elemento(rs.getInt("e.id_elemento"));
                    e.setNombre(rs.getString("e.nombre"));
                    e.setDescripcion(rs.getString("e.descripcion"));
                    e.setCantidad(rs.getInt("e.cantidad"));
                    
                    String estadoStr = rs.getString("e.id_estado");
                    if (estadoStr != null) {
                        e.setEstado(Estado_Elemento.valueOf(estadoStr));
                    }
                    
                    String localizacionStr = rs.getString("e.ubicacion");
                    if (localizacionStr != null) {
                        e.setLocalizacion(Localizacion.valueOf(localizacionStr));
                    }
                    
                    e.setCategoria(categoria);
                    
                    return e;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar elemento por categoría: " + e.getMessage());
        }
        return null;
    }
    
    /**
     * Inserta un nuevo elemento en el inventario.
     * 
     * @param e Elemento a insertar
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario
     */
    @Override
    public boolean insertar(ElementoInventario e) {
        String sql = "INSERT INTO material (nombre, descripcion, cantidad, id_categoria, id_estado, id_ubicacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setString(1, e.getNombre());
            pstmt.setString(2, e.getDescripcion());
            pstmt.setInt(3, e.getCantidad());
            pstmt.setInt(4, e.getCategoria().getId_categoria());
            pstmt.setString(5, e.getEstado().name());
            pstmt.setString(6, e.getLocalizacion().name());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al insertar elemento: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza la información de un elemento existente.
     * 
     * @param e Elemento con los datos actualizados
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario
     */
    @Override
    public boolean actualizar(ElementoInventario e) {
        String sql = "UPDATE material SET nombre = ?, descripcion = ?, cantidad = ?, " +
                     "id_categoria = ?, id_estado = ?, id_ubicacion = ? WHERE id_elemento = ?";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setString(1, e.getNombre());
            pstmt.setString(2, e.getDescripcion());
            pstmt.setInt(3, e.getCantidad());
            pstmt.setInt(4, e.getCategoria().getId_categoria());
            pstmt.setString(5, e.getEstado().name());
            pstmt.setString(6, e.getLocalizacion().name());
            pstmt.setInt(7, e.getId_elemento());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error al actualizar elemento: " + ex.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un elemento del inventario por su ID.
     * 
     * @param id Identificador único del elemento a eliminar
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario
     */
    @Override
    public boolean eliminar(int id) {
        String sql = "DELETE FROM material WHERE id_elemento = ?";
        
        try (PreparedStatement pstmt = getConexion.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar elemento: " + e.getMessage());
            return false;
        }
    }
}
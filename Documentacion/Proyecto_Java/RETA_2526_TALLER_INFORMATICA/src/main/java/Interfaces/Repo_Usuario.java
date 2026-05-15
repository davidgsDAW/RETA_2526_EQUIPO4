package Interfaces;

import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD para el repositorio de usuarios.
 * Proporciona los métodos estándar para la gestión de usuarios y su autenticación.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-13
 * @param <T> Tipo de entidad Usuario
 * @param <P> Tipo de entidad Prestamo
 */
public interface Repo_Usuario <T>{
    
    /**
     * Lista todos los registros de la tabla de usuarios.
     * Obtiene una lista completa de todos los usuarios registrados en el sistema.
     * 
     * @return Lista con todos los registros de usuarios
     */
    public List<T> listar (); 
    // método para listar todos los objetos T
    // para listar todos los registros de una tabla
    
    /**
     * Inserta un nuevo usuario en la base de datos.
     * Realiza la inserción del objeto Usuario como un nuevo registro en la tabla.
     * 
     * @param u Objeto Usuario con los datos a insertar
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario
     */
    public boolean insertar(T u);
    //insertar un registro en la tabla y realiza la insercion del objeto
    
    /**
     * Elimina un usuario de la base de datos por su identificador.
     * Borra el registro de la base de datos utilizando la clave primaria.
     * 
     * @param id Identificador único del usuario a eliminar
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario
     */
    public boolean eliminar(int id);
    // método para borrar un objeto por su ID
    // nos permite borrar un registro de la base de datos por clave primaria
    
    /**
     * Autentica a un usuario verificando sus credenciales.
     * Permite validar el acceso al sistema mediante usuario y préstamo asociado.
     * 
     * @param u Objeto Usuario con nombre y contraseña para autenticar
     * @param p Objeto Prestamo asociado a la autenticación
     * @return Usuario autenticado si las credenciales son correctas,
     *         {@code null} en caso contrario
     */
    public T autenticar(T u);
    //metodo que nos permite aunteticar al usuario por usuario
}
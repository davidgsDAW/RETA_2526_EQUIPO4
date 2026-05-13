package Clases;

import Enumerados.Rol_Usuario;

/**
 * Representa un usuario del sistema de gestión de inventario.
 * Contiene la información de autenticación y el rol del usuario.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-12
 */
public class Usuario {
    
    private int id_usuario;
    private String nombre;
    private String contraseña;
    private Rol_Usuario rol;

    /**
     * Constructor por defecto de la clase Usuario.
     */
    public Usuario() {
    }
    
    /**
     * Constructor parametrizado de la clase Usuario.
     * 
     * @param id_usuario Identificador único del usuario
     * @param nombre Nombre de usuario para el login
     * @param contraseña Contraseña del usuario
     * @param rol Rol o permiso del usuario en el sistema
     */
    public Usuario(int id_usuario, String nombre, String contraseña, Rol_Usuario rol) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    /**
     * Obtiene el identificador único del usuario.
     * 
     * @return ID del usuario
     */
    public int getId_usuario() {
        return id_usuario;
    }

    /**
     * Establece el identificador único del usuario.
     * 
     * @param id_usuario Nuevo ID para el usuario
     */
    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    /**
     * Obtiene el nombre de usuario.
     * 
     * @return Nombre del usuario
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de usuario.
     * 
     * @param nombre Nuevo nombre para el usuario
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la contraseña del usuario.
     * 
     * @return Contraseña del usuario
     */
    public String getContraseña() {
        return contraseña;
    }

    /**
     * Establece la contraseña del usuario.
     * 
     * @param contraseña Nueva contraseña para el usuario
     */
    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    /**
     * Obtiene el rol del usuario.
     * 
     * @return Rol del usuario
     */
    public Rol_Usuario getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario.
     * 
     * @param rol Nuevo rol para el usuario
     */
    public void setRol(Rol_Usuario rol) {
        this.rol = rol;
    }
    
    /**
     * Autentica al usuario comparando las credenciales proporcionadas.
     * 
     * @param nombre Nombre de usuario ingresado
     * @param contraseña Contraseña ingresada
     * @return {@code true} si las credenciales son correctas,
     *         {@code false} en caso contrario
     */
    public boolean login(String nombre, String contraseña) {
        return this.nombre != null && 
               this.nombre.equals(nombre) && 
               this.contraseña != null && 
               this.contraseña.equals(contraseña);
    }

    /**
     * Devuelve una representación en cadena del usuario.
     * 
     * @return Cadena con todos los atributos del usuario
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Usuario{");
        sb.append("id_usuario=").append(id_usuario);
        sb.append(", nombre=").append(nombre);
        sb.append(", contrase\u00f1a=").append(contraseña);
        sb.append(", rol=").append(rol);
        sb.append('}');
        return sb.toString();
    }
}
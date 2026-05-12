
package Clases;

import Enumerados.Rol_Usuario;


public class Usuario {
    
    private int id_usuario;
    private String nombre;
    private String contraseña;
    private Rol_Usuario rol;

    public Usuario() {
    }
    
    public Usuario(int id_usuario, String nombre, String contraseña, Rol_Usuario rol) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.contraseña = contraseña;
        this.rol = rol;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Rol_Usuario getRol() {
        return rol;
    }

    public void setRol(Rol_Usuario rol) {
        this.rol = rol;
    }
    
    
    /**
     * Método de login que verifica las credenciales del usuario
     * @param nombre Nombre de usuario ingresado
     * @param contraseña Contraseña ingresada
     * @return true si las credenciales son correctas, false en caso contrario
     */
    public boolean login(String nombre, String contraseña) {
        return this.nombre != null && 
               this.nombre.equals(nombre) && 
               this.contraseña != null && 
               this.contraseña.equals(contraseña);
    }

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

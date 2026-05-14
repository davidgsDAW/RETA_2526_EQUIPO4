package Enumerados;

/**
 * Enum que define los roles disponibles para los usuarios del sistema.
 * Cada rol determina los permisos y accesos dentro de la aplicación.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-12
 */
public enum Rol_Usuario {
    
    /** Administrador: acceso total al sistema, puede gestionar usuarios, elementos y préstamos */
    ADMINISTRADOR,
    
    /** Profesor: puede gestionar préstamos y consultar inventario, pero no administrar usuarios */
    PROFESOR;
}
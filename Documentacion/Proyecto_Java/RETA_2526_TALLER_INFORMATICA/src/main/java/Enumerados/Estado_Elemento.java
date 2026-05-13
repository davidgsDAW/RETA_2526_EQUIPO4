
package Enumerados;

/**
 * Enum que define los posibles estados de un elemento en el inventario.
 * Controla la disponibilidad y el ciclo de vida de los elementos del taller.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-12
 */
public enum Estado_Elemento {
    
    /** Elemento disponible para préstamo o uso inmediato */
    DISPONIBLE,
    
    /** Elemento actualmente en préstamo a un usuario */
    PRESTADO,
    
    /** Elemento en proceso de reparación o mantenimiento técnico */
    EN_REPARACION;
}
package Enumerados;

/**
 * Enum que define las ubicaciones físicas disponibles para almacenar elementos.
 * Permite organizar el inventario según la posición física de cada elemento.
 * 
 * @author DAVID GÓMEZ 
 * @version 1.0
 * @since 2026-05-12
 */
public enum Localizacion {
    
    /** Elemento guardado en un cajón del almacén o escritorio */
    CAJON,
    
    /** Elemento guardado en un armario o estante cerrado */
    ARMARIO,
    
    /** Elemento colocado en una balda o estante abierto */
    BALDA;
}
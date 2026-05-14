package Clases;

import Enumerados.Localizacion;

/**
 * Representa una ubicación física dentro de la institución.
 * Gestiona la localización de los elementos del inventario.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-13
 */
public class Ubicacion {
    
    private int id_ubicacion;
    private Localizacion localizacion;

    /**
     * Constructor por defecto de la clase Ubicacion.
     */
    public Ubicacion() {
    }

    /**
     * Constructor parametrizado de la clase Ubicacion.
     * 
     * @param id_ubicacion Identificador único de la ubicación
     * @param localizacion Enumerado con la ubicación física
     */
    public Ubicacion(int id_ubicacion, Localizacion localizacion) {
        this.id_ubicacion = id_ubicacion;
        this.localizacion = localizacion;
    }

    /**
     * Obtiene el identificador único de la ubicación.
     * 
     * @return ID de la ubicación
     */
    public int getId_ubicacion() {
        return id_ubicacion;
    }

    /**
     * Establece el identificador único de la ubicación.
     * 
     * @param id_ubicacion Nuevo ID para la ubicación
     */
    public void setId_ubicacion(int id_ubicacion) {
        this.id_ubicacion = id_ubicacion;
    }

    /**
     * Obtiene la localización física.
     * 
     * @return Enumerado con la ubicación
     */
    public Localizacion getLocalizacion() {
        return localizacion;
    }

    /**
     * Establece la localización física.
     * 
     * @param localizacion Nueva ubicación
     */
    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }
    
}
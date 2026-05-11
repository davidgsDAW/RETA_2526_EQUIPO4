
package Clases;

import Enumerados.Localizacion;


public class Ubicacion {
    
    private int id_ubicacion;
    private Localizacion localizacion;

    public Ubicacion() {
    }

    public Ubicacion(int id_ubicacion, Localizacion localizacion) {
        this.id_ubicacion = id_ubicacion;
        this.localizacion = localizacion;
    }

    public int getId_ubicacion() {
        return id_ubicacion;
    }

    public void setId_ubicacion(int id_ubicacion) {
        this.id_ubicacion = id_ubicacion;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }
    
    
    
    
}

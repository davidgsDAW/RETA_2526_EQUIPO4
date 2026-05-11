
package Clases;

import Enumerados.Estado_Elemento;
import Enumerados.Localizacion;

public class ElementoInventario {
    
    
    private int id_elemento;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private Categoria categoria;
    private Estado_Elemento estado;
    private Localizacion localizacion;

    public ElementoInventario() {
    }

    public ElementoInventario(int id_elemento, String nombre, String descripcion, int cantidad, Categoria categoria, Estado_Elemento estado, Localizacion localizacion) {
        this.id_elemento = id_elemento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.estado = estado;
        this.localizacion = localizacion;
    }

    public int getId_elemento() {
        return id_elemento;
    }

    public void setId_elemento(int id_elemento) {
        this.id_elemento = id_elemento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Estado_Elemento getEstado() {
        return estado;
    }

    public void setEstado(Estado_Elemento estado) {
        this.estado = estado;
    }

    public Localizacion getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }
    
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ElementoInventario{");
        sb.append("id_elemento=").append(id_elemento);
        sb.append(", nombre=").append(nombre);
        sb.append(", descripcion=").append(descripcion);
        sb.append(", cantidad=").append(cantidad);
        sb.append(", categoria=").append(categoria);
        sb.append(", estado=").append(estado);
        sb.append(", localizacion=").append(localizacion);
        sb.append('}');
        return sb.toString();
    }
    
}

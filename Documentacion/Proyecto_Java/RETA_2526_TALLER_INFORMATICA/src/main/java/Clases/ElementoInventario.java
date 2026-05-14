package Clases;

import Enumerados.Estado_Elemento;
import Enumerados.Localizacion;

/**
 * Representa un elemento físico del inventario del taller informático.
 * Contiene toda la información necesaria para la gestión de materiales y equipos.
 * 
 * @author DAVID GÓMEZ 
 * @version 1.0
 * @since 2026-05-13
 */
public class ElementoInventario {
    
    private int id_elemento;
    private String nombre;
    private String descripcion;
    private int cantidad;
    private Categoria categoria;
    private Estado_Elemento estado;
    private Localizacion localizacion;

    /**
     * Constructor por defecto de la clase ElementoInventario.
     */
    public ElementoInventario() {
    }

    /**
     * Constructor parametrizado de la clase ElementoInventario.
     * 
     * @param id_elemento Identificador único del elemento
     * @param nombre Nombre del elemento
     * @param descripcion Descripción detallada del elemento
     * @param cantidad Cantidad disponible en inventario
     * @param categoria Categoría a la que pertenece el elemento
     * @param estado Estado actual del elemento (DISPONIBLE, PRESTADO, etc.)
     * @param localizacion Ubicación física donde se encuentra el elemento
     */
    public ElementoInventario(int id_elemento, String nombre, String descripcion, int cantidad, Categoria categoria, Estado_Elemento estado, Localizacion localizacion) {
        this.id_elemento = id_elemento;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.categoria = categoria;
        this.estado = estado;
        this.localizacion = localizacion;
    }

    /**
     * Obtiene el identificador único del elemento.
     * 
     * @return ID del elemento
     */
    public int getId_elemento() {
        return id_elemento;
    }

    /**
     * Establece el identificador único del elemento.
     * 
     * @param id_elemento Nuevo ID para el elemento
     */
    public void setId_elemento(int id_elemento) {
        this.id_elemento = id_elemento;
    }

    /**
     * Obtiene el nombre del elemento.
     * 
     * @return Nombre del elemento
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del elemento.
     * 
     * @param nombre Nuevo nombre para el elemento
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción del elemento.
     * 
     * @return Descripción del elemento
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción del elemento.
     * 
     * @param descripcion Nueva descripción para el elemento
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la cantidad disponible del elemento.
     * 
     * @return Cantidad en inventario
     */
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Establece la cantidad disponible del elemento.
     * 
     * @param cantidad Nueva cantidad para el elemento
     */
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * Obtiene la categoría del elemento.
     * 
     * @return Categoría del elemento
     */
    public Categoria getCategoria() {
        return categoria;
    }

    /**
     * Establece la categoría del elemento.
     * 
     * @param categoria Nueva categoría para el elemento
     */
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    /**
     * Obtiene el estado actual del elemento.
     * 
     * @return Estado del elemento
     */
    public Estado_Elemento getEstado() {
        return estado;
    }

    /**
     * Establece el estado actual del elemento.
     * 
     * @param estado Nuevo estado para el elemento
     */
    public void setEstado(Estado_Elemento estado) {
        this.estado = estado;
    }

    /**
     * Obtiene la ubicación del elemento.
     * 
     * @return Ubicación del elemento
     */
    public Localizacion getLocalizacion() {
        return localizacion;
    }

    /**
     * Establece la ubicación del elemento.
     * 
     * @param localizacion Nueva ubicación para el elemento
     */
    public void setLocalizacion(Localizacion localizacion) {
        this.localizacion = localizacion;
    }
    

    /**
     * Devuelve una representación en cadena del elemento.
     * 
     * @return Cadena con todos los atributos del elemento
     */
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
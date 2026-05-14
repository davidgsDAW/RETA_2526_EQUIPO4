package Clases;

/**
 * Representa una categoría para clasificar los elementos del inventario.
 * Permite organizar los elementos por tipo o área de uso.
 * 
 * @author DAVID GÓMEZ 
 * @version 1.0
 * @since 2026-05-13
 */
public class Categoria {
    
    private int id_categoria;
    private String nombre;
    private String descripcion;

    /**
     * Constructor por defecto de la clase Categoria.
     */
    public Categoria() {
    }

        /**
     * Constructor parametrizado de la clase Categoria.
     * 
     * @param id_categoria Identificador único de la categoría
     * @param nombre Nombre de la categoría
     * @param descripcion Descripción detallada de la categoría
     */
    public Categoria(int id_categoria, String nombre, String descripcion) {
        this.id_categoria = id_categoria;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el identificador único de la categoría.
     * 
     * @return ID de la categoría
     */
    public int getId_categoria() {
        return id_categoria;
    }

    /**
     * Establece el identificador único de la categoría.
     * 
     * @param id_categoria Nuevo ID para la categoría
     */
    public void setId_categoria(int id_categoria) {
        this.id_categoria = id_categoria;
    }

    /**
     * Obtiene el nombre de la categoría.
     * 
     * @return Nombre de la categoría
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de la categoría.
     * 
     * @param nombre Nuevo nombre para la categoría
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene la descripción de la categoría.
     * 
     * @return Descripción de la categoría
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Establece la descripción de la categoría.
     * 
     * @param descripcion Nueva descripción para la categoría
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
}
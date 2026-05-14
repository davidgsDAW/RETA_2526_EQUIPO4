package Clases;

import java.util.Date;

/**
 * Representa un préstamo de un elemento del inventario a un usuario.
 * Gestiona las fechas de préstamo y devolución.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-13
 */
public class Prestamo {
    
    private int id_prestamo;
    private ElementoInventario elemento;
    private Usuario usuario;
    private Date fecha_prestamo;
    private Date fecha_devolucion;

    /**
     * Constructor por defecto de la clase Prestamo.
     */
    public Prestamo() {
    }
    

    /**
     * Constructor parametrizado de la clase Prestamo.
     * 
     * @param id_prestamo Identificador único del préstamo
     * @param elemento Elemento que se presta
     * @param usuario Usuario que recibe el préstamo
     * @param fecha_prestamo Fecha en que se realizó el préstamo
     * @param fecha_devolucion Fecha en que se devolvió el elemento (puede ser null)
     */
    public Prestamo(int id_prestamo, ElementoInventario elemento, Usuario usuario, Date fecha_prestamo, Date fecha_devolucion) {
        this.id_prestamo = id_prestamo;
        this.elemento = elemento;
        this.usuario = usuario;
        this.fecha_prestamo = fecha_prestamo;
        this.fecha_devolucion = fecha_devolucion;
    }

    /**
     * Obtiene el identificador único del préstamo.
     * 
     * @return ID del préstamo
     */
    public int getId_prestamo() {
        return id_prestamo;
    }

    /**
     * Establece el identificador único del préstamo.
     * 
     * @param id_prestamo Nuevo ID para el préstamo
     */
    public void setId_prestamo(int id_prestamo) {
        this.id_prestamo = id_prestamo;
    }

    /**
     * Obtiene el elemento prestado.
     * 
     * @return Elemento del préstamo
     */
    public ElementoInventario getElemento() {
        return elemento;
    }

    /**
     * Establece el elemento prestado.
     * 
     * @param elemento Nuevo elemento para el préstamo
     */
    public void setElemento(ElementoInventario elemento) {
        this.elemento = elemento;
    }

    /**
     * Obtiene el usuario que recibe el préstamo.
     * 
     * @return Usuario del préstamo
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario que recibe el préstamo.
     * 
     * @param usuario Nuevo usuario para el préstamo
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene la fecha del préstamo.
     * 
     * @return Fecha del préstamo
     */
    public Date getFecha_prestamo() {
        return fecha_prestamo;
    }

    /**
     * Establece la fecha del préstamo.
     * 
     * @param fecha_prestamo Nueva fecha de préstamo
     */
    public void setFecha_prestamo(Date fecha_prestamo) {
        this.fecha_prestamo = fecha_prestamo;
    }

    /**
     * Obtiene la fecha de devolución.
     * 
     * @return Fecha de devolución (puede ser null si aún no se ha devuelto)
     */
    public Date getFecha_devolucion() {
        return fecha_devolucion;
    }

    /**
     * Establece la fecha de devolución.
     * 
     * @param fecha_devolucion Nueva fecha de devolución
     */
    public void setFecha_devolucion(Date fecha_devolucion) {
        this.fecha_devolucion = fecha_devolucion;
    }
    

    /**
     * Registra un nuevo préstamo estableciendo la fecha actual.
     * Este método se llama cuando se crea un préstamo nuevo.
     */
    public void registrar() {
        this.fecha_prestamo = new Date();
        System.out.println("Préstamo registrado: " + this.id_prestamo);
    }
    
    /**
     * Registra la devolución de un elemento estableciendo la fecha actual.
     * Este método se llama cuando el usuario devuelve el elemento.
     */
    public void devolver() {
        this.fecha_devolucion = new Date();
        System.out.println("Elemento devuelto: " + this.elemento.getNombre());
    }
    
}
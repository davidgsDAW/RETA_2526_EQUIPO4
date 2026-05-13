package Clases;

import java.util.Date;

/**
 * Representa el historial de movimientos de los elementos del inventario.
 * Registra cada préstamo, devolución o cambio de estado de un elemento.
 * 
 * @author DAVID GÓMEZ 
 * @version 1.0
 * @since 2026-05-12
 */
public class HistorialMovimiento {
    
    private int id_movimiento;
    private ElementoInventario elemento;
    private Usuario usuario;
    private String tipoMovimiento;
    private Date fecha;

    /**
     * Constructor por defecto de la clase HistorialMovimiento.
     */
    public HistorialMovimiento() {
    }

    /**
     * Constructor parametrizado de la clase HistorialMovimiento.
     * 
     * @param id_movimiento Identificador único del movimiento
     * @param elemento Elemento involucrado en el movimiento
     * @param usuario Usuario que realizó el movimiento
     * @param tipoMovimiento Tipo de movimiento (PRESTAMO, DEVOLUCION, etc.)
     * @param fecha Fecha y hora en que ocurrió el movimiento
     */
    public HistorialMovimiento(int id_movimiento, ElementoInventario elemento, Usuario usuario, String tipoMovimiento, Date fecha) {
        this.id_movimiento = id_movimiento;
        this.elemento = elemento;
        this.usuario = usuario;
        this.tipoMovimiento = tipoMovimiento;
        this.fecha = fecha;
    }

    /**
     * Obtiene el identificador único del movimiento.
     * 
     * @return ID del movimiento
     */
    public int getId_movimiento() {
        return id_movimiento;
    }

    /**
     * Establece el identificador único del movimiento.
     * 
     * @param id_movimiento Nuevo ID para el movimiento
     */
    public void setId_movimiento(int id_movimiento) {
        this.id_movimiento = id_movimiento;
    }

    /**
     * Obtiene el elemento involucrado en el movimiento.
     * 
     * @return Elemento del movimiento
     */
    public ElementoInventario getElemento() {
        return elemento;
    }

    /**
     * Establece el elemento involucrado en el movimiento.
     * 
     * @param elemento Nuevo elemento para el movimiento
     */
    public void setElemento(ElementoInventario elemento) {
        this.elemento = elemento;
    }

    /**
     * Obtiene el usuario que realizó el movimiento.
     * 
     * @return Usuario del movimiento
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * Establece el usuario que realizó el movimiento.
     * 
     * @param usuario Nuevo usuario para el movimiento
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    /**
     * Obtiene el tipo de movimiento.
     * 
     * @return Tipo de movimiento
     */
    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    /**
     * Establece el tipo de movimiento.
     * 
     * @param tipoMovimiento Nuevo tipo de movimiento
     */
    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    /**
     * Obtiene la fecha del movimiento.
     * 
     * @return Fecha del movimiento
     */
    public Date getFecha() {
        return fecha;
    }

    /**
     * Establece la fecha del movimiento.
     * 
     * @param fecha Nueva fecha para el movimiento
     */
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
}

package Clases;

import java.util.Date;


public class HistorialMovimiento {
    
    
    private int id_movimiento;
    private ElementoInventario elemento;
    private Usuario usuario;
    private String tipoMovimiento;
    private Date fecha;

    public HistorialMovimiento() {
    }

    public HistorialMovimiento(int id_movimiento, ElementoInventario elemento, Usuario usuario, String tipoMovimiento, Date fecha) {
        this.id_movimiento = id_movimiento;
        this.elemento = elemento;
        this.usuario = usuario;
        this.tipoMovimiento = tipoMovimiento;
        this.fecha = fecha;
    }

    public int getId_movimiento() {
        return id_movimiento;
    }

    public void setId_movimiento(int id_movimiento) {
        this.id_movimiento = id_movimiento;
    }

    public ElementoInventario getElemento() {
        return elemento;
    }

    public void setElemento(ElementoInventario elemento) {
        this.elemento = elemento;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
}

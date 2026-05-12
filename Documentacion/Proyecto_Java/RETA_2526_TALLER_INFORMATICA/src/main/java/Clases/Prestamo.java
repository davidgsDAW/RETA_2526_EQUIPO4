
package Clases;

import java.util.Date;

public class Prestamo {
    
    
    private int id_prestamo;
    private ElementoInventario elemento;
    private Usuario usuario;
    private Date fecha_prestamo;
    private Date fecha_devolucion;

    public Prestamo() {
    }
    

    public Prestamo(int id_prestamo, ElementoInventario elemento, Usuario usuario, Date fecha_prestamo, Date fecha_devolucion) {
        this.id_prestamo = id_prestamo;
        this.elemento = elemento;
        this.usuario = usuario;
        this.fecha_prestamo = fecha_prestamo;
        this.fecha_devolucion = fecha_devolucion;
    }

    public int getId_prestamo() {
        return id_prestamo;
    }

    public void setId_prestamo(int id_prestamo) {
        this.id_prestamo = id_prestamo;
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

    public Date getFecha_prestamo() {
        return fecha_prestamo;
    }

    public void setFecha_prestamo(Date fecha_prestamo) {
        this.fecha_prestamo = fecha_prestamo;
    }

    public Date getFecha_devolucion() {
        return fecha_devolucion;
    }

    public void setFecha_devolucion(Date fecha_devolucion) {
        this.fecha_devolucion = fecha_devolucion;
    }
    
    

     public void registrar() {
        this.fecha_prestamo = new Date();
        System.out.println("Préstamo registrado: " + this.id_prestamo);
    }
    
    public void devolver() {
        this.fecha_devolucion = new Date();
        System.out.println("Elemento devuelto: " + this.elemento.getNombre());
    }
    
}

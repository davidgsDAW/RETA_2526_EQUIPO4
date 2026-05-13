package Interfaces;

import Clases.Categoria;
import Clases.ElementoInventario;
import Enumerados.Estado_Elemento;
import Enumerados.Localizacion;
import java.util.List;

/**
 * Interfaz genérica que define las operaciones CRUD para el repositorio de inventario.
 * Proporciona los métodos estándar para la gestión de elementos en el inventario.
 * 
 * @author DAVID GÓMEZ
 * @version 1.0
 * @since 2026-05-13
 * @param <T> Tipo de entidad que manejará el repositorio (generalmente ElementoInventario)
 */
public interface Repo_Inventario<T> {
    
    /**
     * Lista todos los registros de la tabla de inventario.
     * Obtiene una lista completa de todos los elementos almacenados.
     * 
     * @return Lista con todos los registros de la tabla
     */
    public List<T> listar();
    //metodo para listar todos los registros de una tabla
    
    /**
     * Busca un elemento en el inventario por su estado.
     * Permite localizar el primer elemento que coincida con el estado proporcionado.
     * 
     * @param e Estado del elemento a buscar (DISPONIBLE, PRESTADO, EN_REPARACION)
     * @return ElementoInventario encontrado, o {@code null} si no existe
     */
    public ElementoInventario buscarPorEstado(Estado_Elemento e);
    //metodo para buscar un elemento y ver si existe
    
    /**
     * Busca un elemento en el inventario por su localización.
     * Permite localizar el primer elemento que se encuentre en la ubicación especificada.
     * 
     * @param l Localización donde se encuentra el elemento (CAJON, ARMARIO, BALDA)
     * @return ElementoInventario encontrado, o {@code null} si no existe
     */
    public ElementoInventario buscarPorLocalizacion(Localizacion l);
    //metodo para buscar un elemento por su localizacion y ver si existe
    
    /**
     * Busca un elemento en el inventario por su categoría.
     * Permite localizar el primer elemento que pertenezca a la categoría especificada.
     * 
     * @param c Categoría a la que pertenece el elemento
     * @return ElementoInventario encontrado, o {@code null} si no existe
     */
    public ElementoInventario buscarPorCategoria(Categoria c);
    //metodo para buscar un elemento por categoria y ver si existe
    
    /**
     * Busca un elemento en el inventario por su identificador único.
     * Realiza una búsqueda exacta por la clave primaria del elemento.
     * 
     * @param id Identificador único del elemento a buscar
     * @return ElementoInventario encontrado, o {@code null} si no existe
     */
    public ElementoInventario buscarPorid(int id);
    //metodo para buscar un elemento por su id y ver si existe
    
    /**
     * Inserta un nuevo elemento en el inventario.
     * Agrega un nuevo registro a la tabla de inventario.
     * 
     * @param e ElementoInventario a insertar en la base de datos
     * @return {@code true} si la inserción fue exitosa, {@code false} en caso contrario
     */
    public boolean insertar(ElementoInventario e);
    //metodo para insertar un elemento
    
    /**
     * Actualiza los datos de un elemento existente en el inventario.
     * Modifica el registro correspondiente al ID del elemento proporcionado.
     * 
     * @param e ElementoInventario con los datos actualizados
     * @return {@code true} si la actualización fue exitosa, {@code false} en caso contrario
     */
    public boolean actualizar(ElementoInventario e);
    //metodo para actualizar un elemento
    
    /**
     * Elimina un elemento del inventario por su identificador.
     * Borra el registro de la base de datos utilizando la clave primaria.
     * 
     * @param id Identificador único del elemento a eliminar
     * @return {@code true} si la eliminación fue exitosa, {@code false} en caso contrario
     */
    public boolean eliminar(int id);
    //metodo para eliminar un elemento por su id
    
}
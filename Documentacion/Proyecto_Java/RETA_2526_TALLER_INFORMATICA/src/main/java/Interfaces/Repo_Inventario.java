package Interfaces;

import Clases.Categoria;
import Clases.ElementoInventario;
import Enumerados.Estado_Elemento;
import Enumerados.Localizacion;
import java.util.List;

public interface Repo_Inventario<T> {
    
    public List<T> listar();
    //metodo para listar todos los registros de una tabla
    public ElementoInventario buscarPorEstado(Estado_Elemento e);
    //metodo para buscar un elemento y ver si existe
    public ElementoInventario buscarPorLocalizacion(Localizacion l);
    //metodo para buscar un elemento por su localizacion y ver si existe
    public ElementoInventario buscarPorCategoria(Categoria c);
    //metodo para buscar un elemento por categoria y ver si existe
    public ElementoInventario buscarPorid(int id);
    //metodo para buscar un elemento por su id y ver si existe
    public boolean insertar(ElementoInventario e);
    //metodo para insertar un elemento
    public boolean actualizar(ElementoInventario e);
    //metodo para actualizar un elemento
    public boolean eliminar(int id);
    //metodo para eliminar un elemento por su id
    
}

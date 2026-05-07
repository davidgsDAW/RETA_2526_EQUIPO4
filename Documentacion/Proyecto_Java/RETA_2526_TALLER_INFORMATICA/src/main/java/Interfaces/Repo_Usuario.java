
package Interfaces;

import java.util.List;

public interface Repo_Usuario <T,P>{
    
    public List<T>listar (); 
    // método para listar todos los objetos T
    // para listar todos los registros de una tabla
    
    
    public boolean insertar(T u);
    //insertar un registro en la tabla y realiza la insercion del objeto
    
    public boolean eliminar(int id);
    // método para borrar un objeto por su ID
    // nos permite borrar un registro de la base de datos por clave primaria
    
    public  T autenticar(T u , P p);
    //metodo que nos permite aunteticar al usuario por prestamos y por usuario
}

package proyecto_transversal.AccesoDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author pablo
 */
public class Conexion {
    //por convención las constantes van en mayuscula
    private static String URL="jdbc:mysql://localhost/";
    /*conexión utilizando jdbc a una base de datos MySql en la misma PC (localhost); si la base de
datos se encontrara en otro host, reemplazaríamos localhost por el ip o nombre del equipo en
dónde se encuentra.*/
    private static String DB="proyecto_transversal";
    //nombre de la base de datos
    private static String USUARIO="root";
    private static String PASSWORD="";
    //configuración por defecto de usuario y contraseña.
    private static Conexion conexion=null;
    
    //Contructor privado, para no crear instancias de esta clase.
     private Conexion() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            //Se cargan los driver de conexión al gestor de base de datos;
            
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Clase Conexion: Error al cargar Driver");
            //La invocación Class.forName() podría lanzar la ClassNotFoundException si no cargamos previamente dicha librería.
        }
    }

     //Método que devuelve un objeto de tipo Connection. 
    public static Connection getConexion() {
        Connection con=null;
      if(conexion == null){
          //única instancia de esta clase.
           conexion= new Conexion();
        }
        try {
            // Setup the connection with the DB
            con = DriverManager.getConnection(URL+DB + "?useLegacyDatetimeCode=false&serverTimezone=UTC" + "&user=" + USUARIO + "&password=" + PASSWORD);
            //Método de la clase DriverManager.
        }catch (SQLException ex) {
            //si la cadena que le pasamos como parámetro es incorrecta
            JOptionPane.showMessageDialog(null, "Error de conexion ");
        }
        //retorna el objeto Connection.
        return con;
    }
    
}


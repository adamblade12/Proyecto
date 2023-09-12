/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
    private static String DB="tp2_ejercicio1";
    private static String USUARIO="root";
    private static String PASSWORD="";

   
    private static Conexion conexion=null;
    
     private Conexion() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            
        } catch (ClassNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "Clase Conexion: Error al cargar Driver");
        }
    }


    
    public static Connection getConexion() {
        Connection con=null;
      if(conexion == null){
          
           conexion= new Conexion();
        }
        try {
            // Setup the connection with the DB
            con = DriverManager.getConnection(URL+DB + "?useLegacyDatetimeCode=false&serverTimezone=UTC" + "&user=" + USUARIO + "&password=" + PASSWORD);
            
        }catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error de conexion ");
        }
        
        return con;
    }
    
}


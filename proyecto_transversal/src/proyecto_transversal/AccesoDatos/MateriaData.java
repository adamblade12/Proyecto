/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_transversal.AccesoDatos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import proyecto_transversal.Entidades.Materia;

/**
 *
 * @author pablo
 */
public class MateriaData {
    private Connection con;
    
    public MateriaData(){
        con=Conexion.getConexion();
    }
    
    public void guardarMateria(Materia materia){
    String sql="INSERT INTO materia (nombre, año, estado) VALUES(?,?,?)";
        try{
            PreparedStatement ps= con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, materia.getNombre());
            ps.setInt(2, materia.getAnioMateria());
            ps.setBoolean(3, materia.isActivo());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                materia.setIdMateria(rs.getInt("id_materia"));
                JOptionPane.showMessageDialog(null, "Materia añadida con exito");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al añadir materia "+ex.getMessage());
        }
    }
    
    public Materia buscarMateria(int idMateria){
        Materia materia = null;
        String sql="SELECT id_materia,nombre,año,estado FROM materia "
                + "WHERE id_materia = ? AND estado = 1";
        PreparedStatement ps = null;
        
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                materia = new Materia();
                materia.setIdMateria(rs.getInt("id_materia"));
                materia.setNombre(rs.getString("nombre"));
                materia.setAnioMateria(rs.getInt("año"));
                materia.setActivo(true);
            }else{
                JOptionPane.showMessageDialog(null, "No existe la materia");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la lista de materias "+ex.getMessage());
        }
        return materia;
    }
    
    public void modificarMateria(Materia materia){
        String sql = "UPDATE materia "
                + "SET nombre=?, año=?, estado=? "
                + "WHERE id_materia=?";
        PreparedStatement ps = null;
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, materia.getNombre());
            ps.setInt(2, materia.getAnioMateria());
            ps.setBoolean(3, materia.isActivo());
            ps.setInt(4, materia.getIdMateria());
            int exito = ps.executeUpdate();
            
            if(exito == 1){
                JOptionPane.showMessageDialog(null, "Modificado exitosamente");
            }else{
                JOptionPane.showMessageDialog(null, "La materia no existe");
            }
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al acceder a la lista de materias "+ex.getMessage());
        }
    }
    
    public void eliminarMateria(int idMateria){
        try{
            String sql="UPDATE materia SET estado = 0 WHERE id_materia = ?";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            int fila = ps.executeUpdate();
            
            if(fila == 1){
                JOptionPane.showMessageDialog(null, "Se elimino la materia");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la lista de materias "+ex.getMessage());
        }
    }
    
    public List<Materia> listarMaterias(){
        List<Materia> materias = new ArrayList<>();
        try{
            String sql = "SELECT * FROM materia WHERE estado = 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Materia materia = new Materia();
                
                materia.setIdMateria(rs.getInt("id_materia"));
                materia.setNombre(rs.getString("nombre"));
                materia.setAnioMateria(rs.getInt("año"));
                materia.setActivo(rs.getBoolean("estado"));
                materias.add(materia);
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la lista de materias "+ex.getMessage());
        }
        return materias;
    }
}

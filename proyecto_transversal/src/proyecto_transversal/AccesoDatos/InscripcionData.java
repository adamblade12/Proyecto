/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto_transversal.AccesoDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import proyecto_transversal.Entidades.Alumno;
import proyecto_transversal.Entidades.Inscripcion;
import proyecto_transversal.Entidades.Materia;

/**
 *
 * @author pablo
 */
public class InscripcionData {
    Connection con;
    MateriaData matData;
    AlumnoData alumnoData;
    
    public InscripcionData(){
    con= Conexion.getConexion();
    }
    
    public void guardarInscripcion(Inscripcion inscripcion){
        String sql="INSERT INTO inscripcion (nota, id_alumno, id_materia ,estado) VALUES(?,?,?)";
        //Ingreso de nuevo dato de incripcion, que viene por parámetro (agregado por la interfaz gráfica sin id), a mi base de datos.
        try{
            PreparedStatement ps= con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            //devuelve el id de inscripcion generado automaticamente.
            ps.setDouble(1, inscripcion.getNota());
            //carga el primer signo de pregunta con el valor de la nota.
            ps.setInt(2, inscripcion.getAlumno().getIdAlumno());
            //carga el segundo signo de pregunta con el valor del id_alumno.
            ps.setInt(3, inscripcion.getMateria().getIdMateria());
            //carga el tercer signo de pregunto con el valor del id_materia.
            ps.setBoolean(4, inscripcion.isActivo());
            ps.executeUpdate();
            //ejecuta la sentencia
            ResultSet rs = ps.getGeneratedKeys();
            //Obtiene el valor del id.
            if(rs.next()){
                //Guardo el id generado automaticamente en mi instancia inscripcion.
                inscripcion.setIdInscripcion(rs.getInt(1));//si le ponia el nombre de la columna no la reconocia
                JOptionPane.showMessageDialog(null, "Inscripcion añadida con exito");
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al conectarse con base de datos "+ex.getMessage());
        }
    }
    
    public List<Inscripcion> obtenerInscripciones(){
        List<Inscripcion> inscripciones = new ArrayList<>();
        try{
            String sql = "SELECT * FROM inscripcion WHERE estado = 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            //Devuelve los valores de la tabla que quiero mostrar.
            while(rs.next()){
                Inscripcion inscripcion = new Inscripcion();
                AlumnoData aData = new AlumnoData();
                MateriaData mData = new MateriaData();
                inscripcion.setIdInscripcion(rs.getInt("id_inscripcion"));
                inscripcion.setNota(rs.getDouble("nota"));
                inscripcion.setAlumno(aData.buscarAlumno(rs.getInt("id_alumno")));
                inscripcion.setMateria(mData.buscarMateria(rs.getInt("id_materia")));
                inscripcion.setActivo(rs.getBoolean("estado"));
                inscripciones.add(inscripcion);
            }
            ps.close();
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al conectarse con base de datos "+ex.getMessage());
        }
        return inscripciones;
    }
    
    public List<Inscripcion> obtenerInscripcionesPorAlumno(int dni){
        Inscripcion inscripcion=null;
        List <Inscripcion> inscripciones = new ArrayList<>();
        String sql="SELECT inscripcion.id_inscripcion, inscripcion.nota,inscripcion.id_alumno,inscripcion.estado FROM inscripcion JOIN alumno "
                + "WHERE dni = ?";
        // el signo de pregunta en dni es un dato dinamico.
        PreparedStatement ps=null;
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, dni);
            //guardamos en el signo de pregunta el idAlumno, (no seria el dni?)
            ResultSet rs = ps.executeQuery();
            AlumnoData aData = new AlumnoData();
            MateriaData mData = new MateriaData();
            
            while(rs.next()){
                inscripcion = new Inscripcion();
                inscripcion.setIdInscripcion(rs.getInt("id_inscripcion"));
                inscripcion.setNota(rs.getInt("nota"));
                inscripcion.setAlumno(aData.buscarAlumno(rs.getInt("id_alumno")));
                inscripcion.setMateria(mData.buscarMateria(rs.getInt("id_materia")));
                inscripcion.setActivo(rs.getBoolean("estado"));
                inscripciones.add(inscripcion);
                //se recuperan los campos de la base de datos para mostrarlos.
            }
            ps.close();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al acceder a la lista de inscripciones "+ex.getMessage());
        }catch (NullPointerException ex){
        JOptionPane.showMessageDialog(null, "El alumno no esta inscripto a ninguna materia "+ ex.getMessage());
        }
        return inscripciones;
    }
    
    public List<Materia> obtenerMateriasCursadas(int idMateria){
        Materia materia = null;
        List <Materia> materias = new ArrayList<>();
        String sql="SELECT * FROM inscripcion "
                + "WHERE id_materia=? AND estado = 1";
        PreparedStatement ps=null;
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            ResultSet rs = ps.executeQuery();
            MateriaData mData = new MateriaData();
            
            while(rs.next()){
                materia = new Materia();
                materia.setIdMateria(rs.getInt("id_materia"));
                materia.setNombre(mData.buscarMateria(idMateria).getNombre());
                materia.setAnioMateria(mData.buscarMateria(idMateria).getAnioMateria());
                materia.setActivo(mData.buscarMateria(idMateria).isActivo());
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return materias;
    }
    
    //obtener materias cursadas por el alumno
    public List <Materia> obtenerMateriasCursadasV2(int idAlumno){
        List <Materia> listaMaterias= new ArrayList<>();
        String sql= "SELECT * FROM  materia m JOIN inscripcion i ON m.ID_materia= i.ID_materia where ID_alumno= ?;";
        try {
            PreparedStatement ps= con.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            ResultSet rs= ps.executeQuery();
            while(rs.next()){//si le pongo if solo me traeria la primer fila y no veria si hay más materias
                Materia mat= new Materia();
                mat.setIdMateria(rs.getInt("id_materia"));
                mat.setNombre(rs.getString("nombre"));
                mat.setAnioMateria(rs.getInt("año"));
                mat.setActivo(rs.getBoolean("estado"));
                listaMaterias.add(mat);
            }
            ps.close();
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al acceder a base de datos "+ ex.getMessage());
        }catch(NullPointerException e){//si el resultado trae una lista vacia dará error de null, por eso se agrega esta excepcion
        JOptionPane.showMessageDialog(null, "El alumno no ha cursado ninguna materia "+ e.getMessage());
        }
    
    
        return listaMaterias;
    }
    
     //obtener materias no cursadas por el alumno buscando por idALumno
    public List <Materia> obtenerMateriasNoCursadasV2(int idAlumno){
        List <Materia> listaMaterias= new ArrayList<>();
        String sql= "SELECT * FROM materia m LEFT JOIN inscripcion i ON m.id_materia= i.id_materia "
               + "AND i.id_alumno= ? WHERE i.id_materia IS NULL";
        try {
            PreparedStatement ps= con.prepareStatement(sql);
            ps.setInt(1, idAlumno);
            ResultSet rs= ps.executeQuery();
            while(rs.next()){
                Materia mat= new Materia();
                mat.setIdMateria(rs.getInt("id_materia"));
                mat.setNombre(rs.getString("nombre"));
                mat.setAnioMateria(rs.getInt("año"));
                mat.setActivo(rs.getBoolean("estado"));
                listaMaterias.add(mat);
            }
            ps.close();
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al acceder a base de datos");
        }catch(NullPointerException e){
        JOptionPane.showMessageDialog(null, "El alumno ha cursado todas las materias "+ e.getMessage());
        }
    
        return listaMaterias;
    }
    
    public List<Materia> obtenerMateriasNoCursadas(int idMateria){
        Materia materia = null;
        List <Materia> materias = new ArrayList();
        String sql="SELECT * FROM inscripcion "
                + "WHERE id_materia=? AND estado = 0";
        PreparedStatement ps=null;
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            ResultSet rs = ps.executeQuery();
            MateriaData mData = new MateriaData();
            
            if(rs.next()){
                materia = new Materia();
                materia.setIdMateria(rs.getInt("id_materia"));
                materia.setNombre(mData.buscarMateria(idMateria).getNombre());
                materia.setAnioMateria(mData.buscarMateria(idMateria).getAnioMateria());
                materia.setActivo(mData.buscarMateria(idMateria).isActivo());
            }else{
                JOptionPane.showMessageDialog(null, "La materia no existe");
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
        }
        return materias;
    }
    
    public void borrarInscripcionPorAlumno(int idAlumno, int idMateria){
        try{
            String sql="UPDATE inscripcion SET estado = 0 "
                    + "WHERE id_materia = ? AND id_alumno = ?";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            ps.setInt(2, idAlumno);
            int fila = ps.executeUpdate();
            
            if(fila == 1){
                JOptionPane.showMessageDialog(null, "Se elimino la inscripcion");
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void actualizarNota(int idAlumno, int idMateria, double nota){
        try{
            String sql="UPDATE inscripcion SET nota = ? WHERE id_materia = ? AND id_alumno = ?";
            PreparedStatement ps=con.prepareStatement(sql);
            ps.setDouble(1, nota);
            ps.setInt(2, idMateria);
            ps.setInt(3, idAlumno);
            int fila = ps.executeUpdate();
            
            if(fila == 1){
                JOptionPane.showMessageDialog(null, "Se actualizo la nota");
            }
            ps.close();
        } catch (SQLException ex) {
             JOptionPane.showMessageDialog(null, "Error al acceder a base de datos de notas "+ ex.getMessage());
        }
    }
    
    public List<Alumno> obtenerAlumnoXMateria(int idMateria){
        Alumno alumno = null;
        List<Alumno> alumnos = new ArrayList();
        String sql="SELECT * FROM inscripcion  WHERE id_materia = ? ";
        PreparedStatement ps=null;
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, idMateria);
            ResultSet rs = ps.executeQuery();
            AlumnoData aData = new AlumnoData();
            
            while(rs.next()){
                alumno = new Alumno();
                alumno.setIdAlumno(rs.getInt("id_alumno"));
                alumno.setDni(aData.buscarAlumno(rs.getInt("id_alumno")).getDni());
                alumno.setNombre(aData.buscarAlumno(rs.getInt("id_alumno")).getNombre());
                alumno.setApellido(aData.buscarAlumno(rs.getInt("id_alumno")).getApellido());
                alumno.setFechaNac(aData.buscarAlumno(rs.getInt("id_alumno")).getFechaNac());
                alumno.setActivo(true);
                alumnos.add(alumno);
            }
                    
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(AlumnoData.class.getName()).log(Level.SEVERE, null, ex);
        }catch (NullPointerException ex){
        JOptionPane.showMessageDialog(null, "No hay alumnos inscriptos en la materia "+ ex.getMessage());
        }
        return alumnos;
    }
    
    public Materia obtenerMateriaXInscripcion(int idInscripcion){
        String sql = "SSELECT materia.id_materia,materia.nombre,materia.año,materia.estado "
                + "FROM materia JOIN inscripcion "
                + "WHERE materia.id_materia = inscripcion.id_materia "
                + "AND inscripcion.id_inscripcion = ?";
        Materia materia = new Materia();
        try{
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idInscripcion);
            ResultSet rs= ps.executeQuery();          
            if(rs.next()){
                materia.setIdMateria(rs.getInt("id_materia"));
                materia.setNombre(rs.getString("nombre"));
                materia.setAnioMateria(rs.getInt("año"));
                materia.setActivo(rs.getBoolean("estado"));
            }
        }catch(SQLException err){
            JOptionPane.showMessageDialog(null, "Error al comunicarse con base de datos");
        }
        return materia;
    }
    
}

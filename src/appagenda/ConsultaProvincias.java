/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package appagenda;

import entidades.Provincia;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author usuario
 */
public class ConsultaProvincias {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("AppAgendaPU");
        EntityManager em = emf.createEntityManager();
        
        Query queryProvincias = em.createNamedQuery("Provincia.findAll");
        List<Provincia> listProvincias = queryProvincias.getResultList();
        
        for(Provincia provincia : listProvincias){
            System.out.println(provincia.getNombre());
            
        }

        Query queryProvinciaCadiz = em.createNamedQuery("Provincia.findByNombre");
        queryProvinciaCadiz.setParameter("nombre", "Cádiz");
        List<Provincia> listProvinciasCadiz =queryProvinciaCadiz.getResultList();
        
        em.getTransaction().begin();
        
        for(Provincia provinciaCadiz : listProvinciasCadiz){
            provinciaCadiz.setCodigo("CA");
            em.merge(provinciaCadiz);
            
        }
        
        em.getTransaction().commit();
        
        Provincia provinciaId15 = em.find(Provincia.class, 15);
        
        em.getTransaction().begin();
        
        if (provinciaId15 != null){
            em.remove(provinciaId15);
            
        }
        
        else {
            System.out.println("No hay ninguna provincia con ID=15");
            
        }
        
        em.getTransaction().commit();
        
        em.close();
        emf.close();
        try {
            DriverManager.getConnection("jdbc:derby:BDAgenda;shutdown=true");
            
        } catch (SQLException ex) {
            Logger.getLogger(AppAgenda.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
    }
    
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.clientesrosaprima;

import com.clientes.Clientes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 *
 * @author Usuario
 */
public class ClientesRosaprima {

    public static void main(String[] args) {
        try(EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_ClientesRosaprima_jar_1.0-SNAPSHOTPU") ){
            EntityManager ma =emf.createEntityManager();
            ma.getTransaction().begin();
            
            Clientes c = new Clientes("luis","luis@gmail.com","0945789632","otavalo","ecuador");
            ma.persist(c);
            ma.getTransaction().commit();
            ma.close();
            emf.close();
        }
}
    }

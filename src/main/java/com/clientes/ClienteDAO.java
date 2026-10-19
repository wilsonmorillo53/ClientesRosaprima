package com.clientes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

import java.util.List;

/**
 * Data Access Object para operaciones CRUD sobre la entidad Clientes.
 */
public class ClienteDAO {

    private static final String PERSISTENCE_UNIT =
            "com.mycompany_ClientesRosaprima_jar_1.0-SNAPSHOTPU";

    private static EntityManagerFactory emf;

    // -------------------------------------------------------
    // Inicialización de la fábrica de EntityManagers
    // -------------------------------------------------------
    public static void init() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    private EntityManager getEM() {
        init();
        return emf.createEntityManager();
    }

    // -------------------------------------------------------
    // Crea
    // -------------------------------------------------------
    public void guardar(Clientes cliente) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------
    // Lee todos todos los clientes
    // -------------------------------------------------------
    public List<Clientes> listarTodos() {
        EntityManager em = getEM();
        try {
            TypedQuery<Clientes> q =
                    em.createQuery("SELECT c FROM Clientes c ORDER BY c.idCliente", Clientes.class);
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------
    // READ por ID
    // -------------------------------------------------------
    public Clientes buscarPorId(Long id) {
        EntityManager em = getEM();
        try {
            return em.find(Clientes.class, id);
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------
    //  búsqueda por nombre (like)
    // -------------------------------------------------------
    public List<Clientes> buscarPorNombre(String nombre) {
        EntityManager em = getEM();
        try {
            TypedQuery<Clientes> q = em.createQuery(
                    "SELECT c FROM Clientes c WHERE LOWER(c.nombre) LIKE :nombre ORDER BY c.nombre",
                    Clientes.class);
            q.setParameter("nombre", "%" + nombre.toLowerCase() + "%");
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------
    public void actualizar(Clientes cliente) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            em.merge(cliente);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // -------------------------------------------------------
    // DELETE
    // -------------------------------------------------------
    public void eliminar(Long id) {
        EntityManager em = getEM();
        try {
            em.getTransaction().begin();
            Clientes c = em.find(Clientes.class, id);
            if (c != null) em.remove(c);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}

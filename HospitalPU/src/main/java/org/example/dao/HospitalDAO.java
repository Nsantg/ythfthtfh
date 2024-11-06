package org.example.dao;

import org.example.model.Medico;
import org.example.model.Atencion;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class HospitalDAO {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("HospitalPU");

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    // Métodos CRUD para Medico
    public void guardarMedico(Medico medico) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(medico);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Medico buscarMedicoPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Medico.class, id);
        } finally {
            em.close();
        }
    }

    public List<Medico> listarMedicos() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Medico> query = em.createQuery("SELECT m FROM Medico m", Medico.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void actualizarMedico(Medico medico) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(medico);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminarMedico(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Medico medico = em.find(Medico.class, id);
            if (medico != null) {
                em.remove(medico);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Métodos CRUD para Atencion
    public void guardarAtencion(Atencion atencion) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(atencion);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Atencion buscarAtencionPorId(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Atencion.class, id);
        } finally {
            em.close();
        }
    }

    public List<Atencion> listarAtenciones() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Atencion> query = em.createQuery("SELECT a FROM Atencion a", Atencion.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<Atencion> listarAtencionesPorMedico(Medico medico) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Atencion> query = em.createQuery(
                    "SELECT a FROM Atencion a WHERE a.medico = :medico", Atencion.class);
            query.setParameter("medico", medico);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void actualizarAtencion(Atencion atencion) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(atencion);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void eliminarAtencion(Long id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Atencion atencion = em.find(Atencion.class, id);
            if (atencion != null) {
                em.remove(atencion);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Método para limpiar todas las atenciones
    public void limpiarAtenciones() {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Atencion").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
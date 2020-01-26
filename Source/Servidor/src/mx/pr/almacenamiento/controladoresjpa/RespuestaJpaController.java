/*
 * Copyright (C) 2020 Carlos Onorio
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mx.pr.almacenamiento.controladoresjpa;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.pr.almacenamiento.controladoresjpa.exceptions.NonexistentEntityException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.PreexistingEntityException;
import mx.pr.almacenamiento.entidadesjpa.Pregunta;
import mx.pr.almacenamiento.entidadesjpa.Cuestionario;
import mx.pr.almacenamiento.entidadesjpa.Respuesta;
import mx.pr.almacenamiento.entidadesjpa.RespuestaPK;

/**
 *
 * @author Carlos Onorio
 */
public class RespuestaJpaController implements Serializable {

    public RespuestaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Respuesta respuesta) throws PreexistingEntityException, Exception {
        if (respuesta.getRespuestaPK() == null) {
            respuesta.setRespuestaPK(new RespuestaPK());
        }
        respuesta.getRespuestaPK().setCuestionario(respuesta.getCuestionario1().getId());
        respuesta.getRespuestaPK().setPregunta(respuesta.getPregunta1().getPreguntaPK().getNumero());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pregunta pregunta1 = respuesta.getPregunta1();
            if (pregunta1 != null) {
                pregunta1 = em.getReference(pregunta1.getClass(), pregunta1.getPreguntaPK());
                respuesta.setPregunta1(pregunta1);
            }
            Cuestionario cuestionario1 = respuesta.getCuestionario1();
            if (cuestionario1 != null) {
                cuestionario1 = em.getReference(cuestionario1.getClass(), cuestionario1.getId());
                respuesta.setCuestionario1(cuestionario1);
            }
            em.persist(respuesta);
            if (pregunta1 != null) {
                pregunta1.getRespuestaCollection().add(respuesta);
                pregunta1 = em.merge(pregunta1);
            }
            if (cuestionario1 != null) {
                cuestionario1.getRespuestaCollection().add(respuesta);
                cuestionario1 = em.merge(cuestionario1);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findRespuesta(respuesta.getRespuestaPK()) != null) {
                throw new PreexistingEntityException("Respuesta " + respuesta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Respuesta respuesta) throws NonexistentEntityException, Exception {
        respuesta.getRespuestaPK().setCuestionario(respuesta.getCuestionario1().getId());
        respuesta.getRespuestaPK().setPregunta(respuesta.getPregunta1().getPreguntaPK().getNumero());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Respuesta persistentRespuesta = em.find(Respuesta.class, respuesta.getRespuestaPK());
            Pregunta pregunta1Old = persistentRespuesta.getPregunta1();
            Pregunta pregunta1New = respuesta.getPregunta1();
            Cuestionario cuestionario1Old = persistentRespuesta.getCuestionario1();
            Cuestionario cuestionario1New = respuesta.getCuestionario1();
            if (pregunta1New != null) {
                pregunta1New = em.getReference(pregunta1New.getClass(), pregunta1New.getPreguntaPK());
                respuesta.setPregunta1(pregunta1New);
            }
            if (cuestionario1New != null) {
                cuestionario1New = em.getReference(cuestionario1New.getClass(), cuestionario1New.getId());
                respuesta.setCuestionario1(cuestionario1New);
            }
            respuesta = em.merge(respuesta);
            if (pregunta1Old != null && !pregunta1Old.equals(pregunta1New)) {
                pregunta1Old.getRespuestaCollection().remove(respuesta);
                pregunta1Old = em.merge(pregunta1Old);
            }
            if (pregunta1New != null && !pregunta1New.equals(pregunta1Old)) {
                pregunta1New.getRespuestaCollection().add(respuesta);
                pregunta1New = em.merge(pregunta1New);
            }
            if (cuestionario1Old != null && !cuestionario1Old.equals(cuestionario1New)) {
                cuestionario1Old.getRespuestaCollection().remove(respuesta);
                cuestionario1Old = em.merge(cuestionario1Old);
            }
            if (cuestionario1New != null && !cuestionario1New.equals(cuestionario1Old)) {
                cuestionario1New.getRespuestaCollection().add(respuesta);
                cuestionario1New = em.merge(cuestionario1New);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                RespuestaPK id = respuesta.getRespuestaPK();
                if (findRespuesta(id) == null) {
                    throw new NonexistentEntityException("The respuesta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(RespuestaPK id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Respuesta respuesta;
            try {
                respuesta = em.getReference(Respuesta.class, id);
                respuesta.getRespuestaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The respuesta with id " + id + " no longer exists.", enfe);
            }
            Pregunta pregunta1 = respuesta.getPregunta1();
            if (pregunta1 != null) {
                pregunta1.getRespuestaCollection().remove(respuesta);
                pregunta1 = em.merge(pregunta1);
            }
            Cuestionario cuestionario1 = respuesta.getCuestionario1();
            if (cuestionario1 != null) {
                cuestionario1.getRespuestaCollection().remove(respuesta);
                cuestionario1 = em.merge(cuestionario1);
            }
            em.remove(respuesta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Respuesta> findRespuestaEntities() {
        return findRespuestaEntities(true, -1, -1);
    }

    public List<Respuesta> findRespuestaEntities(int maxResults, int firstResult) {
        return findRespuestaEntities(false, maxResults, firstResult);
    }

    private List<Respuesta> findRespuestaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Respuesta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Respuesta findRespuesta(RespuestaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Respuesta.class, id);
        } finally {
            em.close();
        }
    }

    public int getRespuestaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Respuesta> rt = cq.from(Respuesta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

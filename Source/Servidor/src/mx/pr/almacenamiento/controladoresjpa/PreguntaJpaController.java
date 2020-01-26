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
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import mx.pr.almacenamiento.entidadesjpa.Cuestionario;
import mx.pr.almacenamiento.entidadesjpa.Respuesta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import mx.pr.almacenamiento.controladoresjpa.exceptions.IllegalOrphanException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.NonexistentEntityException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.PreexistingEntityException;
import mx.pr.almacenamiento.entidadesjpa.Pregunta;
import mx.pr.almacenamiento.entidadesjpa.PreguntaPK;

/**
 *
 * @author Carlos Onorio
 */
public class PreguntaJpaController implements Serializable {

    public PreguntaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pregunta pregunta) throws PreexistingEntityException, Exception {
        if (pregunta.getPreguntaPK() == null) {
            pregunta.setPreguntaPK(new PreguntaPK());
        }
        if (pregunta.getRespuestaCollection() == null) {
            pregunta.setRespuestaCollection(new ArrayList<Respuesta>());
        }
        pregunta.getPreguntaPK().setCuestionario(pregunta.getCuestionario1().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuestionario cuestionario1 = pregunta.getCuestionario1();
            if (cuestionario1 != null) {
                cuestionario1 = em.getReference(cuestionario1.getClass(), cuestionario1.getId());
                pregunta.setCuestionario1(cuestionario1);
            }
            Collection<Respuesta> attachedRespuestaCollection = new ArrayList<Respuesta>();
            for (Respuesta respuestaCollectionRespuestaToAttach : pregunta.getRespuestaCollection()) {
                respuestaCollectionRespuestaToAttach = em.getReference(respuestaCollectionRespuestaToAttach.getClass(), respuestaCollectionRespuestaToAttach.getRespuestaPK());
                attachedRespuestaCollection.add(respuestaCollectionRespuestaToAttach);
            }
            pregunta.setRespuestaCollection(attachedRespuestaCollection);
            em.persist(pregunta);
            if (cuestionario1 != null) {
                cuestionario1.getPreguntaCollection().add(pregunta);
                cuestionario1 = em.merge(cuestionario1);
            }
            for (Respuesta respuestaCollectionRespuesta : pregunta.getRespuestaCollection()) {
                Pregunta oldPregunta1OfRespuestaCollectionRespuesta = respuestaCollectionRespuesta.getPregunta1();
                respuestaCollectionRespuesta.setPregunta1(pregunta);
                respuestaCollectionRespuesta = em.merge(respuestaCollectionRespuesta);
                if (oldPregunta1OfRespuestaCollectionRespuesta != null) {
                    oldPregunta1OfRespuestaCollectionRespuesta.getRespuestaCollection().remove(respuestaCollectionRespuesta);
                    oldPregunta1OfRespuestaCollectionRespuesta = em.merge(oldPregunta1OfRespuestaCollectionRespuesta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findPregunta(pregunta.getPreguntaPK()) != null) {
                throw new PreexistingEntityException("Pregunta " + pregunta + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pregunta pregunta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        pregunta.getPreguntaPK().setCuestionario(pregunta.getCuestionario1().getId());
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pregunta persistentPregunta = em.find(Pregunta.class, pregunta.getPreguntaPK());
            Cuestionario cuestionario1Old = persistentPregunta.getCuestionario1();
            Cuestionario cuestionario1New = pregunta.getCuestionario1();
            Collection<Respuesta> respuestaCollectionOld = persistentPregunta.getRespuestaCollection();
            Collection<Respuesta> respuestaCollectionNew = pregunta.getRespuestaCollection();
            List<String> illegalOrphanMessages = null;
            for (Respuesta respuestaCollectionOldRespuesta : respuestaCollectionOld) {
                if (!respuestaCollectionNew.contains(respuestaCollectionOldRespuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Respuesta " + respuestaCollectionOldRespuesta + " since its pregunta1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (cuestionario1New != null) {
                cuestionario1New = em.getReference(cuestionario1New.getClass(), cuestionario1New.getId());
                pregunta.setCuestionario1(cuestionario1New);
            }
            Collection<Respuesta> attachedRespuestaCollectionNew = new ArrayList<Respuesta>();
            for (Respuesta respuestaCollectionNewRespuestaToAttach : respuestaCollectionNew) {
                respuestaCollectionNewRespuestaToAttach = em.getReference(respuestaCollectionNewRespuestaToAttach.getClass(), respuestaCollectionNewRespuestaToAttach.getRespuestaPK());
                attachedRespuestaCollectionNew.add(respuestaCollectionNewRespuestaToAttach);
            }
            respuestaCollectionNew = attachedRespuestaCollectionNew;
            pregunta.setRespuestaCollection(respuestaCollectionNew);
            pregunta = em.merge(pregunta);
            if (cuestionario1Old != null && !cuestionario1Old.equals(cuestionario1New)) {
                cuestionario1Old.getPreguntaCollection().remove(pregunta);
                cuestionario1Old = em.merge(cuestionario1Old);
            }
            if (cuestionario1New != null && !cuestionario1New.equals(cuestionario1Old)) {
                cuestionario1New.getPreguntaCollection().add(pregunta);
                cuestionario1New = em.merge(cuestionario1New);
            }
            for (Respuesta respuestaCollectionNewRespuesta : respuestaCollectionNew) {
                if (!respuestaCollectionOld.contains(respuestaCollectionNewRespuesta)) {
                    Pregunta oldPregunta1OfRespuestaCollectionNewRespuesta = respuestaCollectionNewRespuesta.getPregunta1();
                    respuestaCollectionNewRespuesta.setPregunta1(pregunta);
                    respuestaCollectionNewRespuesta = em.merge(respuestaCollectionNewRespuesta);
                    if (oldPregunta1OfRespuestaCollectionNewRespuesta != null && !oldPregunta1OfRespuestaCollectionNewRespuesta.equals(pregunta)) {
                        oldPregunta1OfRespuestaCollectionNewRespuesta.getRespuestaCollection().remove(respuestaCollectionNewRespuesta);
                        oldPregunta1OfRespuestaCollectionNewRespuesta = em.merge(oldPregunta1OfRespuestaCollectionNewRespuesta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                PreguntaPK id = pregunta.getPreguntaPK();
                if (findPregunta(id) == null) {
                    throw new NonexistentEntityException("The pregunta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(PreguntaPK id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pregunta pregunta;
            try {
                pregunta = em.getReference(Pregunta.class, id);
                pregunta.getPreguntaPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pregunta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Respuesta> respuestaCollectionOrphanCheck = pregunta.getRespuestaCollection();
            for (Respuesta respuestaCollectionOrphanCheckRespuesta : respuestaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pregunta (" + pregunta + ") cannot be destroyed since the Respuesta " + respuestaCollectionOrphanCheckRespuesta + " in its respuestaCollection field has a non-nullable pregunta1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Cuestionario cuestionario1 = pregunta.getCuestionario1();
            if (cuestionario1 != null) {
                cuestionario1.getPreguntaCollection().remove(pregunta);
                cuestionario1 = em.merge(cuestionario1);
            }
            em.remove(pregunta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pregunta> findPreguntaEntities() {
        return findPreguntaEntities(true, -1, -1);
    }

    public List<Pregunta> findPreguntaEntities(int maxResults, int firstResult) {
        return findPreguntaEntities(false, maxResults, firstResult);
    }

    private List<Pregunta> findPreguntaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pregunta.class));
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

    public Pregunta findPregunta(PreguntaPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pregunta.class, id);
        } finally {
            em.close();
        }
    }

    public int getPreguntaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pregunta> rt = cq.from(Pregunta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

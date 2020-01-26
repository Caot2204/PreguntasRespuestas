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
import mx.pr.almacenamiento.entidadesjpa.Usuario;
import mx.pr.almacenamiento.entidadesjpa.Respuesta;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import mx.pr.almacenamiento.controladoresjpa.exceptions.IllegalOrphanException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.NonexistentEntityException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.PreexistingEntityException;
import mx.pr.almacenamiento.entidadesjpa.Cuestionario;
import mx.pr.almacenamiento.entidadesjpa.Pregunta;

/**
 *
 * @author Carlos Onorio
 */
public class CuestionarioJpaController implements Serializable {

    public CuestionarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cuestionario cuestionario) throws PreexistingEntityException, Exception {
        if (cuestionario.getRespuestaCollection() == null) {
            cuestionario.setRespuestaCollection(new ArrayList<Respuesta>());
        }
        if (cuestionario.getPreguntaCollection() == null) {
            cuestionario.setPreguntaCollection(new ArrayList<Pregunta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario creador = cuestionario.getCreador();
            if (creador != null) {
                creador = em.getReference(creador.getClass(), creador.getNombre());
                cuestionario.setCreador(creador);
            }
            Collection<Respuesta> attachedRespuestaCollection = new ArrayList<Respuesta>();
            for (Respuesta respuestaCollectionRespuestaToAttach : cuestionario.getRespuestaCollection()) {
                respuestaCollectionRespuestaToAttach = em.getReference(respuestaCollectionRespuestaToAttach.getClass(), respuestaCollectionRespuestaToAttach.getRespuestaPK());
                attachedRespuestaCollection.add(respuestaCollectionRespuestaToAttach);
            }
            cuestionario.setRespuestaCollection(attachedRespuestaCollection);
            Collection<Pregunta> attachedPreguntaCollection = new ArrayList<Pregunta>();
            for (Pregunta preguntaCollectionPreguntaToAttach : cuestionario.getPreguntaCollection()) {
                preguntaCollectionPreguntaToAttach = em.getReference(preguntaCollectionPreguntaToAttach.getClass(), preguntaCollectionPreguntaToAttach.getPreguntaPK());
                attachedPreguntaCollection.add(preguntaCollectionPreguntaToAttach);
            }
            cuestionario.setPreguntaCollection(attachedPreguntaCollection);
            em.persist(cuestionario);
            if (creador != null) {
                creador.getCuestionarioCollection().add(cuestionario);
                creador = em.merge(creador);
            }
            for (Respuesta respuestaCollectionRespuesta : cuestionario.getRespuestaCollection()) {
                Cuestionario oldCuestionario1OfRespuestaCollectionRespuesta = respuestaCollectionRespuesta.getCuestionario1();
                respuestaCollectionRespuesta.setCuestionario1(cuestionario);
                respuestaCollectionRespuesta = em.merge(respuestaCollectionRespuesta);
                if (oldCuestionario1OfRespuestaCollectionRespuesta != null) {
                    oldCuestionario1OfRespuestaCollectionRespuesta.getRespuestaCollection().remove(respuestaCollectionRespuesta);
                    oldCuestionario1OfRespuestaCollectionRespuesta = em.merge(oldCuestionario1OfRespuestaCollectionRespuesta);
                }
            }
            for (Pregunta preguntaCollectionPregunta : cuestionario.getPreguntaCollection()) {
                Cuestionario oldCuestionario1OfPreguntaCollectionPregunta = preguntaCollectionPregunta.getCuestionario1();
                preguntaCollectionPregunta.setCuestionario1(cuestionario);
                preguntaCollectionPregunta = em.merge(preguntaCollectionPregunta);
                if (oldCuestionario1OfPreguntaCollectionPregunta != null) {
                    oldCuestionario1OfPreguntaCollectionPregunta.getPreguntaCollection().remove(preguntaCollectionPregunta);
                    oldCuestionario1OfPreguntaCollectionPregunta = em.merge(oldCuestionario1OfPreguntaCollectionPregunta);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findCuestionario(cuestionario.getId()) != null) {
                throw new PreexistingEntityException("Cuestionario " + cuestionario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cuestionario cuestionario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuestionario persistentCuestionario = em.find(Cuestionario.class, cuestionario.getId());
            Usuario creadorOld = persistentCuestionario.getCreador();
            Usuario creadorNew = cuestionario.getCreador();
            Collection<Respuesta> respuestaCollectionOld = persistentCuestionario.getRespuestaCollection();
            Collection<Respuesta> respuestaCollectionNew = cuestionario.getRespuestaCollection();
            Collection<Pregunta> preguntaCollectionOld = persistentCuestionario.getPreguntaCollection();
            Collection<Pregunta> preguntaCollectionNew = cuestionario.getPreguntaCollection();
            List<String> illegalOrphanMessages = null;
            for (Respuesta respuestaCollectionOldRespuesta : respuestaCollectionOld) {
                if (!respuestaCollectionNew.contains(respuestaCollectionOldRespuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Respuesta " + respuestaCollectionOldRespuesta + " since its cuestionario1 field is not nullable.");
                }
            }
            for (Pregunta preguntaCollectionOldPregunta : preguntaCollectionOld) {
                if (!preguntaCollectionNew.contains(preguntaCollectionOldPregunta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pregunta " + preguntaCollectionOldPregunta + " since its cuestionario1 field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (creadorNew != null) {
                creadorNew = em.getReference(creadorNew.getClass(), creadorNew.getNombre());
                cuestionario.setCreador(creadorNew);
            }
            Collection<Respuesta> attachedRespuestaCollectionNew = new ArrayList<Respuesta>();
            for (Respuesta respuestaCollectionNewRespuestaToAttach : respuestaCollectionNew) {
                respuestaCollectionNewRespuestaToAttach = em.getReference(respuestaCollectionNewRespuestaToAttach.getClass(), respuestaCollectionNewRespuestaToAttach.getRespuestaPK());
                attachedRespuestaCollectionNew.add(respuestaCollectionNewRespuestaToAttach);
            }
            respuestaCollectionNew = attachedRespuestaCollectionNew;
            cuestionario.setRespuestaCollection(respuestaCollectionNew);
            Collection<Pregunta> attachedPreguntaCollectionNew = new ArrayList<Pregunta>();
            for (Pregunta preguntaCollectionNewPreguntaToAttach : preguntaCollectionNew) {
                preguntaCollectionNewPreguntaToAttach = em.getReference(preguntaCollectionNewPreguntaToAttach.getClass(), preguntaCollectionNewPreguntaToAttach.getPreguntaPK());
                attachedPreguntaCollectionNew.add(preguntaCollectionNewPreguntaToAttach);
            }
            preguntaCollectionNew = attachedPreguntaCollectionNew;
            cuestionario.setPreguntaCollection(preguntaCollectionNew);
            cuestionario = em.merge(cuestionario);
            if (creadorOld != null && !creadorOld.equals(creadorNew)) {
                creadorOld.getCuestionarioCollection().remove(cuestionario);
                creadorOld = em.merge(creadorOld);
            }
            if (creadorNew != null && !creadorNew.equals(creadorOld)) {
                creadorNew.getCuestionarioCollection().add(cuestionario);
                creadorNew = em.merge(creadorNew);
            }
            for (Respuesta respuestaCollectionNewRespuesta : respuestaCollectionNew) {
                if (!respuestaCollectionOld.contains(respuestaCollectionNewRespuesta)) {
                    Cuestionario oldCuestionario1OfRespuestaCollectionNewRespuesta = respuestaCollectionNewRespuesta.getCuestionario1();
                    respuestaCollectionNewRespuesta.setCuestionario1(cuestionario);
                    respuestaCollectionNewRespuesta = em.merge(respuestaCollectionNewRespuesta);
                    if (oldCuestionario1OfRespuestaCollectionNewRespuesta != null && !oldCuestionario1OfRespuestaCollectionNewRespuesta.equals(cuestionario)) {
                        oldCuestionario1OfRespuestaCollectionNewRespuesta.getRespuestaCollection().remove(respuestaCollectionNewRespuesta);
                        oldCuestionario1OfRespuestaCollectionNewRespuesta = em.merge(oldCuestionario1OfRespuestaCollectionNewRespuesta);
                    }
                }
            }
            for (Pregunta preguntaCollectionNewPregunta : preguntaCollectionNew) {
                if (!preguntaCollectionOld.contains(preguntaCollectionNewPregunta)) {
                    Cuestionario oldCuestionario1OfPreguntaCollectionNewPregunta = preguntaCollectionNewPregunta.getCuestionario1();
                    preguntaCollectionNewPregunta.setCuestionario1(cuestionario);
                    preguntaCollectionNewPregunta = em.merge(preguntaCollectionNewPregunta);
                    if (oldCuestionario1OfPreguntaCollectionNewPregunta != null && !oldCuestionario1OfPreguntaCollectionNewPregunta.equals(cuestionario)) {
                        oldCuestionario1OfPreguntaCollectionNewPregunta.getPreguntaCollection().remove(preguntaCollectionNewPregunta);
                        oldCuestionario1OfPreguntaCollectionNewPregunta = em.merge(oldCuestionario1OfPreguntaCollectionNewPregunta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Object id = cuestionario.getId();
                if (findCuestionario(id) == null) {
                    throw new NonexistentEntityException("The cuestionario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Object id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuestionario cuestionario;
            try {
                cuestionario = em.getReference(Cuestionario.class, id);
                cuestionario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuestionario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Respuesta> respuestaCollectionOrphanCheck = cuestionario.getRespuestaCollection();
            for (Respuesta respuestaCollectionOrphanCheckRespuesta : respuestaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cuestionario (" + cuestionario + ") cannot be destroyed since the Respuesta " + respuestaCollectionOrphanCheckRespuesta + " in its respuestaCollection field has a non-nullable cuestionario1 field.");
            }
            Collection<Pregunta> preguntaCollectionOrphanCheck = cuestionario.getPreguntaCollection();
            for (Pregunta preguntaCollectionOrphanCheckPregunta : preguntaCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Cuestionario (" + cuestionario + ") cannot be destroyed since the Pregunta " + preguntaCollectionOrphanCheckPregunta + " in its preguntaCollection field has a non-nullable cuestionario1 field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario creador = cuestionario.getCreador();
            if (creador != null) {
                creador.getCuestionarioCollection().remove(cuestionario);
                creador = em.merge(creador);
            }
            em.remove(cuestionario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cuestionario> findCuestionarioEntities() {
        return findCuestionarioEntities(true, -1, -1);
    }

    public List<Cuestionario> findCuestionarioEntities(int maxResults, int firstResult) {
        return findCuestionarioEntities(false, maxResults, firstResult);
    }

    private List<Cuestionario> findCuestionarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cuestionario.class));
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

    public Cuestionario findCuestionario(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cuestionario.class, id);
        } finally {
            em.close();
        }
    }

    public int getCuestionarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cuestionario> rt = cq.from(Cuestionario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

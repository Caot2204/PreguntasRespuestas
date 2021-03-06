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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import mx.pr.almacenamiento.controladoresjpa.exceptions.IllegalOrphanException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.NonexistentEntityException;
import mx.pr.almacenamiento.controladoresjpa.exceptions.PreexistingEntityException;
import mx.pr.almacenamiento.entidadesjpa.Usuario;

/**
 *
 * @author Carlos Onorio
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws PreexistingEntityException, Exception {
        if (usuario.getCuestionarioCollection() == null) {
            usuario.setCuestionarioCollection(new ArrayList<Cuestionario>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Cuestionario> attachedCuestionarioCollection = new ArrayList<Cuestionario>();
            for (Cuestionario cuestionarioCollectionCuestionarioToAttach : usuario.getCuestionarioCollection()) {
                cuestionarioCollectionCuestionarioToAttach = em.getReference(cuestionarioCollectionCuestionarioToAttach.getClass(), cuestionarioCollectionCuestionarioToAttach.getId());
                attachedCuestionarioCollection.add(cuestionarioCollectionCuestionarioToAttach);
            }
            usuario.setCuestionarioCollection(attachedCuestionarioCollection);
            em.persist(usuario);
            for (Cuestionario cuestionarioCollectionCuestionario : usuario.getCuestionarioCollection()) {
                Usuario oldCreadorOfCuestionarioCollectionCuestionario = cuestionarioCollectionCuestionario.getCreador();
                cuestionarioCollectionCuestionario.setCreador(usuario);
                cuestionarioCollectionCuestionario = em.merge(cuestionarioCollectionCuestionario);
                if (oldCreadorOfCuestionarioCollectionCuestionario != null) {
                    oldCreadorOfCuestionarioCollectionCuestionario.getCuestionarioCollection().remove(cuestionarioCollectionCuestionario);
                    oldCreadorOfCuestionarioCollectionCuestionario = em.merge(oldCreadorOfCuestionarioCollectionCuestionario);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            if (findUsuario(usuario.getNombre()) != null) {
                throw new PreexistingEntityException("Usuario " + usuario + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getNombre());
            Collection<Cuestionario> cuestionarioCollectionOld = persistentUsuario.getCuestionarioCollection();
            Collection<Cuestionario> cuestionarioCollectionNew = usuario.getCuestionarioCollection();
            List<String> illegalOrphanMessages = null;
            for (Cuestionario cuestionarioCollectionOldCuestionario : cuestionarioCollectionOld) {
                if (!cuestionarioCollectionNew.contains(cuestionarioCollectionOldCuestionario)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Cuestionario " + cuestionarioCollectionOldCuestionario + " since its creador field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Cuestionario> attachedCuestionarioCollectionNew = new ArrayList<Cuestionario>();
            for (Cuestionario cuestionarioCollectionNewCuestionarioToAttach : cuestionarioCollectionNew) {
                cuestionarioCollectionNewCuestionarioToAttach = em.getReference(cuestionarioCollectionNewCuestionarioToAttach.getClass(), cuestionarioCollectionNewCuestionarioToAttach.getId());
                attachedCuestionarioCollectionNew.add(cuestionarioCollectionNewCuestionarioToAttach);
            }
            cuestionarioCollectionNew = attachedCuestionarioCollectionNew;
            usuario.setCuestionarioCollection(cuestionarioCollectionNew);
            usuario = em.merge(usuario);
            for (Cuestionario cuestionarioCollectionNewCuestionario : cuestionarioCollectionNew) {
                if (!cuestionarioCollectionOld.contains(cuestionarioCollectionNewCuestionario)) {
                    Usuario oldCreadorOfCuestionarioCollectionNewCuestionario = cuestionarioCollectionNewCuestionario.getCreador();
                    cuestionarioCollectionNewCuestionario.setCreador(usuario);
                    cuestionarioCollectionNewCuestionario = em.merge(cuestionarioCollectionNewCuestionario);
                    if (oldCreadorOfCuestionarioCollectionNewCuestionario != null && !oldCreadorOfCuestionarioCollectionNewCuestionario.equals(usuario)) {
                        oldCreadorOfCuestionarioCollectionNewCuestionario.getCuestionarioCollection().remove(cuestionarioCollectionNewCuestionario);
                        oldCreadorOfCuestionarioCollectionNewCuestionario = em.merge(oldCreadorOfCuestionarioCollectionNewCuestionario);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Object id = usuario.getNombre();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getNombre();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Cuestionario> cuestionarioCollectionOrphanCheck = usuario.getCuestionarioCollection();
            for (Cuestionario cuestionarioCollectionOrphanCheckCuestionario : cuestionarioCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Cuestionario " + cuestionarioCollectionOrphanCheckCuestionario + " in its cuestionarioCollection field has a non-nullable creador field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

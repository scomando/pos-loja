/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladoras;

import controladoras.exceptions.NonexistentEntityException;
import entidades.ProductoVenda;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Productos;
import entidades.Venda;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Sunil Comando
 */
public class ProductoVendaJpaController implements Serializable {

    public ProductoVendaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("EstabelecimentoUmPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(ProductoVenda productoVenda) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos productoId = productoVenda.getProductoId();
            if (productoId != null) {
                productoId = em.getReference(productoId.getClass(), productoId.getProductoId());
                productoVenda.setProductoId(productoId);
            }
            Venda vendaId = productoVenda.getVendaId();
            if (vendaId != null) {
                vendaId = em.getReference(vendaId.getClass(), vendaId.getVendaId());
                productoVenda.setVendaId(vendaId);
            }
            em.persist(productoVenda);
            if (productoId != null) {
                productoId.getProductoVendaList().add(productoVenda);
                productoId = em.merge(productoId);
            }
            if (vendaId != null) {
                vendaId.getProductoVendaList().add(productoVenda);
                vendaId = em.merge(vendaId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(ProductoVenda productoVenda) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProductoVenda persistentProductoVenda = em.find(ProductoVenda.class, productoVenda.getProductoVendaId());
            Productos productoIdOld = persistentProductoVenda.getProductoId();
            Productos productoIdNew = productoVenda.getProductoId();
            Venda vendaIdOld = persistentProductoVenda.getVendaId();
            Venda vendaIdNew = productoVenda.getVendaId();
            if (productoIdNew != null) {
                productoIdNew = em.getReference(productoIdNew.getClass(), productoIdNew.getProductoId());
                productoVenda.setProductoId(productoIdNew);
            }
            if (vendaIdNew != null) {
                vendaIdNew = em.getReference(vendaIdNew.getClass(), vendaIdNew.getVendaId());
                productoVenda.setVendaId(vendaIdNew);
            }
            productoVenda = em.merge(productoVenda);
            if (productoIdOld != null && !productoIdOld.equals(productoIdNew)) {
                productoIdOld.getProductoVendaList().remove(productoVenda);
                productoIdOld = em.merge(productoIdOld);
            }
            if (productoIdNew != null && !productoIdNew.equals(productoIdOld)) {
                productoIdNew.getProductoVendaList().add(productoVenda);
                productoIdNew = em.merge(productoIdNew);
            }
            if (vendaIdOld != null && !vendaIdOld.equals(vendaIdNew)) {
                vendaIdOld.getProductoVendaList().remove(productoVenda);
                vendaIdOld = em.merge(vendaIdOld);
            }
            if (vendaIdNew != null && !vendaIdNew.equals(vendaIdOld)) {
                vendaIdNew.getProductoVendaList().add(productoVenda);
                vendaIdNew = em.merge(vendaIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = productoVenda.getProductoVendaId();
                if (findProductoVenda(id) == null) {
                    throw new NonexistentEntityException("The productoVenda with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            ProductoVenda productoVenda;
            try {
                productoVenda = em.getReference(ProductoVenda.class, id);
                productoVenda.getProductoVendaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productoVenda with id " + id + " no longer exists.", enfe);
            }
            Productos productoId = productoVenda.getProductoId();
            if (productoId != null) {
                productoId.getProductoVendaList().remove(productoVenda);
                productoId = em.merge(productoId);
            }
            Venda vendaId = productoVenda.getVendaId();
            if (vendaId != null) {
                vendaId.getProductoVendaList().remove(productoVenda);
                vendaId = em.merge(vendaId);
            }
            em.remove(productoVenda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<ProductoVenda> findProductoVendaEntities() {
        return findProductoVendaEntities(true, -1, -1);
    }

    public List<ProductoVenda> findProductoVendaEntities(int maxResults, int firstResult) {
        return findProductoVendaEntities(false, maxResults, firstResult);
    }

    private List<ProductoVenda> findProductoVendaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(ProductoVenda.class));
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

    public ProductoVenda findProductoVenda(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(ProductoVenda.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductoVendaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<ProductoVenda> rt = cq.from(ProductoVenda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

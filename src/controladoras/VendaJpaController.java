/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladoras;

import controladoras.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Utilizadores;
import entidades.ProductoVenda;
import entidades.Venda;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Sunil Comando
 */
public class VendaJpaController implements Serializable {

    public VendaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("EstabelecimentoUmPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Venda venda) {
        if (venda.getProductoVendaList() == null) {
            venda.setProductoVendaList(new ArrayList<ProductoVenda>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Utilizadores utilizadorId = venda.getUtilizadorId();
            if (utilizadorId != null) {
                utilizadorId = em.getReference(utilizadorId.getClass(), utilizadorId.getIdUtilizador());
                venda.setUtilizadorId(utilizadorId);
            }
            List<ProductoVenda> attachedProductoVendaList = new ArrayList<ProductoVenda>();
            for (ProductoVenda productoVendaListProductoVendaToAttach : venda.getProductoVendaList()) {
                productoVendaListProductoVendaToAttach = em.getReference(productoVendaListProductoVendaToAttach.getClass(), productoVendaListProductoVendaToAttach.getProductoVendaId());
                attachedProductoVendaList.add(productoVendaListProductoVendaToAttach);
            }
            venda.setProductoVendaList(attachedProductoVendaList);
            em.persist(venda);
            if (utilizadorId != null) {
                utilizadorId.getVendaList().add(venda);
                utilizadorId = em.merge(utilizadorId);
            }
            for (ProductoVenda productoVendaListProductoVenda : venda.getProductoVendaList()) {
                Venda oldVendaIdOfProductoVendaListProductoVenda = productoVendaListProductoVenda.getVendaId();
                productoVendaListProductoVenda.setVendaId(venda);
                productoVendaListProductoVenda = em.merge(productoVendaListProductoVenda);
                if (oldVendaIdOfProductoVendaListProductoVenda != null) {
                    oldVendaIdOfProductoVendaListProductoVenda.getProductoVendaList().remove(productoVendaListProductoVenda);
                    oldVendaIdOfProductoVendaListProductoVenda = em.merge(oldVendaIdOfProductoVendaListProductoVenda);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Venda venda) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Venda persistentVenda = em.find(Venda.class, venda.getVendaId());
            Utilizadores utilizadorIdOld = persistentVenda.getUtilizadorId();
            Utilizadores utilizadorIdNew = venda.getUtilizadorId();
            List<ProductoVenda> productoVendaListOld = persistentVenda.getProductoVendaList();
            List<ProductoVenda> productoVendaListNew = venda.getProductoVendaList();
            if (utilizadorIdNew != null) {
                utilizadorIdNew = em.getReference(utilizadorIdNew.getClass(), utilizadorIdNew.getIdUtilizador());
                venda.setUtilizadorId(utilizadorIdNew);
            }
            List<ProductoVenda> attachedProductoVendaListNew = new ArrayList<ProductoVenda>();
            for (ProductoVenda productoVendaListNewProductoVendaToAttach : productoVendaListNew) {
                productoVendaListNewProductoVendaToAttach = em.getReference(productoVendaListNewProductoVendaToAttach.getClass(), productoVendaListNewProductoVendaToAttach.getProductoVendaId());
                attachedProductoVendaListNew.add(productoVendaListNewProductoVendaToAttach);
            }
            productoVendaListNew = attachedProductoVendaListNew;
            venda.setProductoVendaList(productoVendaListNew);
            venda = em.merge(venda);
            if (utilizadorIdOld != null && !utilizadorIdOld.equals(utilizadorIdNew)) {
                utilizadorIdOld.getVendaList().remove(venda);
                utilizadorIdOld = em.merge(utilizadorIdOld);
            }
            if (utilizadorIdNew != null && !utilizadorIdNew.equals(utilizadorIdOld)) {
                utilizadorIdNew.getVendaList().add(venda);
                utilizadorIdNew = em.merge(utilizadorIdNew);
            }
            for (ProductoVenda productoVendaListOldProductoVenda : productoVendaListOld) {
                if (!productoVendaListNew.contains(productoVendaListOldProductoVenda)) {
                    productoVendaListOldProductoVenda.setVendaId(null);
                    productoVendaListOldProductoVenda = em.merge(productoVendaListOldProductoVenda);
                }
            }
            for (ProductoVenda productoVendaListNewProductoVenda : productoVendaListNew) {
                if (!productoVendaListOld.contains(productoVendaListNewProductoVenda)) {
                    Venda oldVendaIdOfProductoVendaListNewProductoVenda = productoVendaListNewProductoVenda.getVendaId();
                    productoVendaListNewProductoVenda.setVendaId(venda);
                    productoVendaListNewProductoVenda = em.merge(productoVendaListNewProductoVenda);
                    if (oldVendaIdOfProductoVendaListNewProductoVenda != null && !oldVendaIdOfProductoVendaListNewProductoVenda.equals(venda)) {
                        oldVendaIdOfProductoVendaListNewProductoVenda.getProductoVendaList().remove(productoVendaListNewProductoVenda);
                        oldVendaIdOfProductoVendaListNewProductoVenda = em.merge(oldVendaIdOfProductoVendaListNewProductoVenda);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = venda.getVendaId();
                if (findVenda(id) == null) {
                    throw new NonexistentEntityException("The venda with id " + id + " no longer exists.");
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
            Venda venda;
            try {
                venda = em.getReference(Venda.class, id);
                venda.getVendaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The venda with id " + id + " no longer exists.", enfe);
            }
            Utilizadores utilizadorId = venda.getUtilizadorId();
            if (utilizadorId != null) {
                utilizadorId.getVendaList().remove(venda);
                utilizadorId = em.merge(utilizadorId);
            }
            List<ProductoVenda> productoVendaList = venda.getProductoVendaList();
            for (ProductoVenda productoVendaListProductoVenda : productoVendaList) {
                productoVendaListProductoVenda.setVendaId(null);
                productoVendaListProductoVenda = em.merge(productoVendaListProductoVenda);
            }
            em.remove(venda);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Venda> findVendaEntities() {
        return findVendaEntities(true, -1, -1);
    }

    public List<Venda> findVendaEntities(int maxResults, int firstResult) {
        return findVendaEntities(false, maxResults, firstResult);
    }

    private List<Venda> findVendaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Venda.class));
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

    public Venda findVenda(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Venda.class, id);
        } finally {
            em.close();
        }
    }

    public int getVendaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Venda> rt = cq.from(Venda.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

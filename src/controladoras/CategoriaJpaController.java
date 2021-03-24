/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controladoras;

import controladoras.exceptions.NonexistentEntityException;
import entidades.Categoria;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entidades.Estabelecimento;
import entidades.Productos;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Sunil Comando
 */
public class CategoriaJpaController implements Serializable {

    public CategoriaJpaController() {
        this.emf = Persistence.createEntityManagerFactory("EstabelecimentoUmPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Categoria categoria) {
        if (categoria.getProductosList() == null) {
            categoria.setProductosList(new ArrayList<Productos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estabelecimento estabelecimentoId = categoria.getEstabelecimentoId();
            if (estabelecimentoId != null) {
                estabelecimentoId = em.getReference(estabelecimentoId.getClass(), estabelecimentoId.getEstabelecimentoId());
                categoria.setEstabelecimentoId(estabelecimentoId);
            }
            List<Productos> attachedProductosList = new ArrayList<Productos>();
            for (Productos productosListProductosToAttach : categoria.getProductosList()) {
                productosListProductosToAttach = em.getReference(productosListProductosToAttach.getClass(), productosListProductosToAttach.getProductoId());
                attachedProductosList.add(productosListProductosToAttach);
            }
            categoria.setProductosList(attachedProductosList);
            em.persist(categoria);
            if (estabelecimentoId != null) {
                estabelecimentoId.getCategoriaList().add(categoria);
                estabelecimentoId = em.merge(estabelecimentoId);
            }
            for (Productos productosListProductos : categoria.getProductosList()) {
                Categoria oldCategoriaIdOfProductosListProductos = productosListProductos.getCategoriaId();
                productosListProductos.setCategoriaId(categoria);
                productosListProductos = em.merge(productosListProductos);
                if (oldCategoriaIdOfProductosListProductos != null) {
                    oldCategoriaIdOfProductosListProductos.getProductosList().remove(productosListProductos);
                    oldCategoriaIdOfProductosListProductos = em.merge(oldCategoriaIdOfProductosListProductos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Categoria categoria) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria persistentCategoria = em.find(Categoria.class, categoria.getCategoriaId());
            Estabelecimento estabelecimentoIdOld = persistentCategoria.getEstabelecimentoId();
            Estabelecimento estabelecimentoIdNew = categoria.getEstabelecimentoId();
            List<Productos> productosListOld = persistentCategoria.getProductosList();
            List<Productos> productosListNew = categoria.getProductosList();
            if (estabelecimentoIdNew != null) {
                estabelecimentoIdNew = em.getReference(estabelecimentoIdNew.getClass(), estabelecimentoIdNew.getEstabelecimentoId());
                categoria.setEstabelecimentoId(estabelecimentoIdNew);
            }
            List<Productos> attachedProductosListNew = new ArrayList<Productos>();
            for (Productos productosListNewProductosToAttach : productosListNew) {
                productosListNewProductosToAttach = em.getReference(productosListNewProductosToAttach.getClass(), productosListNewProductosToAttach.getProductoId());
                attachedProductosListNew.add(productosListNewProductosToAttach);
            }
            productosListNew = attachedProductosListNew;
            categoria.setProductosList(productosListNew);
            categoria = em.merge(categoria);
            if (estabelecimentoIdOld != null && !estabelecimentoIdOld.equals(estabelecimentoIdNew)) {
                estabelecimentoIdOld.getCategoriaList().remove(categoria);
                estabelecimentoIdOld = em.merge(estabelecimentoIdOld);
            }
            if (estabelecimentoIdNew != null && !estabelecimentoIdNew.equals(estabelecimentoIdOld)) {
                estabelecimentoIdNew.getCategoriaList().add(categoria);
                estabelecimentoIdNew = em.merge(estabelecimentoIdNew);
            }
            for (Productos productosListOldProductos : productosListOld) {
                if (!productosListNew.contains(productosListOldProductos)) {
                    productosListOldProductos.setCategoriaId(null);
                    productosListOldProductos = em.merge(productosListOldProductos);
                }
            }
            for (Productos productosListNewProductos : productosListNew) {
                if (!productosListOld.contains(productosListNewProductos)) {
                    Categoria oldCategoriaIdOfProductosListNewProductos = productosListNewProductos.getCategoriaId();
                    productosListNewProductos.setCategoriaId(categoria);
                    productosListNewProductos = em.merge(productosListNewProductos);
                    if (oldCategoriaIdOfProductosListNewProductos != null && !oldCategoriaIdOfProductosListNewProductos.equals(categoria)) {
                        oldCategoriaIdOfProductosListNewProductos.getProductosList().remove(productosListNewProductos);
                        oldCategoriaIdOfProductosListNewProductos = em.merge(oldCategoriaIdOfProductosListNewProductos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = categoria.getCategoriaId();
                if (findCategoria(id) == null) {
                    throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.");
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
            Categoria categoria;
            try {
                categoria = em.getReference(Categoria.class, id);
                categoria.getCategoriaId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The categoria with id " + id + " no longer exists.", enfe);
            }
            Estabelecimento estabelecimentoId = categoria.getEstabelecimentoId();
            if (estabelecimentoId != null) {
                estabelecimentoId.getCategoriaList().remove(categoria);
                estabelecimentoId = em.merge(estabelecimentoId);
            }
            List<Productos> productosList = categoria.getProductosList();
            for (Productos productosListProductos : productosList) {
                productosListProductos.setCategoriaId(null);
                productosListProductos = em.merge(productosListProductos);
            }
            em.remove(categoria);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Categoria> findCategoriaEntities() {
        return findCategoriaEntities(true, -1, -1);
    }

    public List<Categoria> findCategoriaEntities(int maxResults, int firstResult) {
        return findCategoriaEntities(false, maxResults, firstResult);
    }

    private List<Categoria> findCategoriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Categoria.class));
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

    public Categoria findCategoria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Categoria.class, id);
        } finally {
            em.close();
        }
    }

    public int getCategoriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Categoria> rt = cq.from(Categoria.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

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
import entidades.Categoria;
import entidades.Estabelecimento;
import entidades.ProductoVenda;
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
public class ProductosJpaController implements Serializable {

    public ProductosJpaController() {
        this.emf = Persistence.createEntityManagerFactory("EstabelecimentoUmPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Productos productos) {
        if (productos.getProductoVendaList() == null) {
            productos.setProductoVendaList(new ArrayList<ProductoVenda>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Categoria categoriaId = productos.getCategoriaId();
            if (categoriaId != null) {
                categoriaId = em.getReference(categoriaId.getClass(), categoriaId.getCategoriaId());
                productos.setCategoriaId(categoriaId);
            }
            Estabelecimento estabelecimentoId = productos.getEstabelecimentoId();
            if (estabelecimentoId != null) {
                estabelecimentoId = em.getReference(estabelecimentoId.getClass(), estabelecimentoId.getEstabelecimentoId());
                productos.setEstabelecimentoId(estabelecimentoId);
            }
            List<ProductoVenda> attachedProductoVendaList = new ArrayList<ProductoVenda>();
            for (ProductoVenda productoVendaListProductoVendaToAttach : productos.getProductoVendaList()) {
                productoVendaListProductoVendaToAttach = em.getReference(productoVendaListProductoVendaToAttach.getClass(), productoVendaListProductoVendaToAttach.getProductoVendaId());
                attachedProductoVendaList.add(productoVendaListProductoVendaToAttach);
            }
            productos.setProductoVendaList(attachedProductoVendaList);
            em.persist(productos);
            if (categoriaId != null) {
                categoriaId.getProductosList().add(productos);
                categoriaId = em.merge(categoriaId);
            }
            if (estabelecimentoId != null) {
                estabelecimentoId.getProductosList().add(productos);
                estabelecimentoId = em.merge(estabelecimentoId);
            }
            for (ProductoVenda productoVendaListProductoVenda : productos.getProductoVendaList()) {
                Productos oldProductoIdOfProductoVendaListProductoVenda = productoVendaListProductoVenda.getProductoId();
                productoVendaListProductoVenda.setProductoId(productos);
                productoVendaListProductoVenda = em.merge(productoVendaListProductoVenda);
                if (oldProductoIdOfProductoVendaListProductoVenda != null) {
                    oldProductoIdOfProductoVendaListProductoVenda.getProductoVendaList().remove(productoVendaListProductoVenda);
                    oldProductoIdOfProductoVendaListProductoVenda = em.merge(oldProductoIdOfProductoVendaListProductoVenda);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Productos productos) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Productos persistentProductos = em.find(Productos.class, productos.getProductoId());
            Categoria categoriaIdOld = persistentProductos.getCategoriaId();
            Categoria categoriaIdNew = productos.getCategoriaId();
            Estabelecimento estabelecimentoIdOld = persistentProductos.getEstabelecimentoId();
            Estabelecimento estabelecimentoIdNew = productos.getEstabelecimentoId();
            List<ProductoVenda> productoVendaListOld = persistentProductos.getProductoVendaList();
            List<ProductoVenda> productoVendaListNew = productos.getProductoVendaList();
            if (categoriaIdNew != null) {
                categoriaIdNew = em.getReference(categoriaIdNew.getClass(), categoriaIdNew.getCategoriaId());
                productos.setCategoriaId(categoriaIdNew);
            }
            if (estabelecimentoIdNew != null) {
                estabelecimentoIdNew = em.getReference(estabelecimentoIdNew.getClass(), estabelecimentoIdNew.getEstabelecimentoId());
                productos.setEstabelecimentoId(estabelecimentoIdNew);
            }
            List<ProductoVenda> attachedProductoVendaListNew = new ArrayList<ProductoVenda>();
            for (ProductoVenda productoVendaListNewProductoVendaToAttach : productoVendaListNew) {
                productoVendaListNewProductoVendaToAttach = em.getReference(productoVendaListNewProductoVendaToAttach.getClass(), productoVendaListNewProductoVendaToAttach.getProductoVendaId());
                attachedProductoVendaListNew.add(productoVendaListNewProductoVendaToAttach);
            }
            productoVendaListNew = attachedProductoVendaListNew;
            productos.setProductoVendaList(productoVendaListNew);
            productos = em.merge(productos);
            if (categoriaIdOld != null && !categoriaIdOld.equals(categoriaIdNew)) {
                categoriaIdOld.getProductosList().remove(productos);
                categoriaIdOld = em.merge(categoriaIdOld);
            }
            if (categoriaIdNew != null && !categoriaIdNew.equals(categoriaIdOld)) {
                categoriaIdNew.getProductosList().add(productos);
                categoriaIdNew = em.merge(categoriaIdNew);
            }
            if (estabelecimentoIdOld != null && !estabelecimentoIdOld.equals(estabelecimentoIdNew)) {
                estabelecimentoIdOld.getProductosList().remove(productos);
                estabelecimentoIdOld = em.merge(estabelecimentoIdOld);
            }
            if (estabelecimentoIdNew != null && !estabelecimentoIdNew.equals(estabelecimentoIdOld)) {
                estabelecimentoIdNew.getProductosList().add(productos);
                estabelecimentoIdNew = em.merge(estabelecimentoIdNew);
            }
            for (ProductoVenda productoVendaListOldProductoVenda : productoVendaListOld) {
                if (!productoVendaListNew.contains(productoVendaListOldProductoVenda)) {
                    productoVendaListOldProductoVenda.setProductoId(null);
                    productoVendaListOldProductoVenda = em.merge(productoVendaListOldProductoVenda);
                }
            }
            for (ProductoVenda productoVendaListNewProductoVenda : productoVendaListNew) {
                if (!productoVendaListOld.contains(productoVendaListNewProductoVenda)) {
                    Productos oldProductoIdOfProductoVendaListNewProductoVenda = productoVendaListNewProductoVenda.getProductoId();
                    productoVendaListNewProductoVenda.setProductoId(productos);
                    productoVendaListNewProductoVenda = em.merge(productoVendaListNewProductoVenda);
                    if (oldProductoIdOfProductoVendaListNewProductoVenda != null && !oldProductoIdOfProductoVendaListNewProductoVenda.equals(productos)) {
                        oldProductoIdOfProductoVendaListNewProductoVenda.getProductoVendaList().remove(productoVendaListNewProductoVenda);
                        oldProductoIdOfProductoVendaListNewProductoVenda = em.merge(oldProductoIdOfProductoVendaListNewProductoVenda);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = productos.getProductoId();
                if (findProductos(id) == null) {
                    throw new NonexistentEntityException("The productos with id " + id + " no longer exists.");
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
            Productos productos;
            try {
                productos = em.getReference(Productos.class, id);
                productos.getProductoId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productos with id " + id + " no longer exists.", enfe);
            }
            Categoria categoriaId = productos.getCategoriaId();
            if (categoriaId != null) {
                categoriaId.getProductosList().remove(productos);
                categoriaId = em.merge(categoriaId);
            }
            Estabelecimento estabelecimentoId = productos.getEstabelecimentoId();
            if (estabelecimentoId != null) {
                estabelecimentoId.getProductosList().remove(productos);
                estabelecimentoId = em.merge(estabelecimentoId);
            }
            List<ProductoVenda> productoVendaList = productos.getProductoVendaList();
            for (ProductoVenda productoVendaListProductoVenda : productoVendaList) {
                productoVendaListProductoVenda.setProductoId(null);
                productoVendaListProductoVenda = em.merge(productoVendaListProductoVenda);
            }
            em.remove(productos);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Productos> findProductosEntities() {
        return findProductosEntities(true, -1, -1);
    }

    public List<Productos> findProductosEntities(int maxResults, int firstResult) {
        return findProductosEntities(false, maxResults, firstResult);
    }

    private List<Productos> findProductosEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productos.class));
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

    public Productos findProductos(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Productos.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductosCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productos> rt = cq.from(Productos.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

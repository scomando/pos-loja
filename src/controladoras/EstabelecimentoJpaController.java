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
import java.util.ArrayList;
import java.util.List;
import entidades.Utilizadores;
import entidades.Productos;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Sunil Comando
 */
public class EstabelecimentoJpaController implements Serializable {

    public EstabelecimentoJpaController() {
        this.emf = Persistence.createEntityManagerFactory("EstabelecimentoUmPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Estabelecimento estabelecimento) {
        if (estabelecimento.getCategoriaList() == null) {
            estabelecimento.setCategoriaList(new ArrayList<Categoria>());
        }
        if (estabelecimento.getUtilizadoresList() == null) {
            estabelecimento.setUtilizadoresList(new ArrayList<Utilizadores>());
        }
        if (estabelecimento.getProductosList() == null) {
            estabelecimento.setProductosList(new ArrayList<Productos>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Categoria> attachedCategoriaList = new ArrayList<Categoria>();
            for (Categoria categoriaListCategoriaToAttach : estabelecimento.getCategoriaList()) {
                categoriaListCategoriaToAttach = em.getReference(categoriaListCategoriaToAttach.getClass(), categoriaListCategoriaToAttach.getCategoriaId());
                attachedCategoriaList.add(categoriaListCategoriaToAttach);
            }
            estabelecimento.setCategoriaList(attachedCategoriaList);
            List<Utilizadores> attachedUtilizadoresList = new ArrayList<Utilizadores>();
            for (Utilizadores utilizadoresListUtilizadoresToAttach : estabelecimento.getUtilizadoresList()) {
                utilizadoresListUtilizadoresToAttach = em.getReference(utilizadoresListUtilizadoresToAttach.getClass(), utilizadoresListUtilizadoresToAttach.getIdUtilizador());
                attachedUtilizadoresList.add(utilizadoresListUtilizadoresToAttach);
            }
            estabelecimento.setUtilizadoresList(attachedUtilizadoresList);
            List<Productos> attachedProductosList = new ArrayList<Productos>();
            for (Productos productosListProductosToAttach : estabelecimento.getProductosList()) {
                productosListProductosToAttach = em.getReference(productosListProductosToAttach.getClass(), productosListProductosToAttach.getProductoId());
                attachedProductosList.add(productosListProductosToAttach);
            }
            estabelecimento.setProductosList(attachedProductosList);
            em.persist(estabelecimento);
            for (Categoria categoriaListCategoria : estabelecimento.getCategoriaList()) {
                Estabelecimento oldEstabelecimentoIdOfCategoriaListCategoria = categoriaListCategoria.getEstabelecimentoId();
                categoriaListCategoria.setEstabelecimentoId(estabelecimento);
                categoriaListCategoria = em.merge(categoriaListCategoria);
                if (oldEstabelecimentoIdOfCategoriaListCategoria != null) {
                    oldEstabelecimentoIdOfCategoriaListCategoria.getCategoriaList().remove(categoriaListCategoria);
                    oldEstabelecimentoIdOfCategoriaListCategoria = em.merge(oldEstabelecimentoIdOfCategoriaListCategoria);
                }
            }
            for (Utilizadores utilizadoresListUtilizadores : estabelecimento.getUtilizadoresList()) {
                Estabelecimento oldEstabelecimentoIdOfUtilizadoresListUtilizadores = utilizadoresListUtilizadores.getEstabelecimentoId();
                utilizadoresListUtilizadores.setEstabelecimentoId(estabelecimento);
                utilizadoresListUtilizadores = em.merge(utilizadoresListUtilizadores);
                if (oldEstabelecimentoIdOfUtilizadoresListUtilizadores != null) {
                    oldEstabelecimentoIdOfUtilizadoresListUtilizadores.getUtilizadoresList().remove(utilizadoresListUtilizadores);
                    oldEstabelecimentoIdOfUtilizadoresListUtilizadores = em.merge(oldEstabelecimentoIdOfUtilizadoresListUtilizadores);
                }
            }
            for (Productos productosListProductos : estabelecimento.getProductosList()) {
                Estabelecimento oldEstabelecimentoIdOfProductosListProductos = productosListProductos.getEstabelecimentoId();
                productosListProductos.setEstabelecimentoId(estabelecimento);
                productosListProductos = em.merge(productosListProductos);
                if (oldEstabelecimentoIdOfProductosListProductos != null) {
                    oldEstabelecimentoIdOfProductosListProductos.getProductosList().remove(productosListProductos);
                    oldEstabelecimentoIdOfProductosListProductos = em.merge(oldEstabelecimentoIdOfProductosListProductos);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Estabelecimento estabelecimento) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estabelecimento persistentEstabelecimento = em.find(Estabelecimento.class, estabelecimento.getEstabelecimentoId());
            List<Categoria> categoriaListOld = persistentEstabelecimento.getCategoriaList();
            List<Categoria> categoriaListNew = estabelecimento.getCategoriaList();
            List<Utilizadores> utilizadoresListOld = persistentEstabelecimento.getUtilizadoresList();
            List<Utilizadores> utilizadoresListNew = estabelecimento.getUtilizadoresList();
            List<Productos> productosListOld = persistentEstabelecimento.getProductosList();
            List<Productos> productosListNew = estabelecimento.getProductosList();
            List<Categoria> attachedCategoriaListNew = new ArrayList<Categoria>();
            for (Categoria categoriaListNewCategoriaToAttach : categoriaListNew) {
                categoriaListNewCategoriaToAttach = em.getReference(categoriaListNewCategoriaToAttach.getClass(), categoriaListNewCategoriaToAttach.getCategoriaId());
                attachedCategoriaListNew.add(categoriaListNewCategoriaToAttach);
            }
            categoriaListNew = attachedCategoriaListNew;
            estabelecimento.setCategoriaList(categoriaListNew);
            List<Utilizadores> attachedUtilizadoresListNew = new ArrayList<Utilizadores>();
            for (Utilizadores utilizadoresListNewUtilizadoresToAttach : utilizadoresListNew) {
                utilizadoresListNewUtilizadoresToAttach = em.getReference(utilizadoresListNewUtilizadoresToAttach.getClass(), utilizadoresListNewUtilizadoresToAttach.getIdUtilizador());
                attachedUtilizadoresListNew.add(utilizadoresListNewUtilizadoresToAttach);
            }
            utilizadoresListNew = attachedUtilizadoresListNew;
            estabelecimento.setUtilizadoresList(utilizadoresListNew);
            List<Productos> attachedProductosListNew = new ArrayList<Productos>();
            for (Productos productosListNewProductosToAttach : productosListNew) {
                productosListNewProductosToAttach = em.getReference(productosListNewProductosToAttach.getClass(), productosListNewProductosToAttach.getProductoId());
                attachedProductosListNew.add(productosListNewProductosToAttach);
            }
            productosListNew = attachedProductosListNew;
            estabelecimento.setProductosList(productosListNew);
            estabelecimento = em.merge(estabelecimento);
            for (Categoria categoriaListOldCategoria : categoriaListOld) {
                if (!categoriaListNew.contains(categoriaListOldCategoria)) {
                    categoriaListOldCategoria.setEstabelecimentoId(null);
                    categoriaListOldCategoria = em.merge(categoriaListOldCategoria);
                }
            }
            for (Categoria categoriaListNewCategoria : categoriaListNew) {
                if (!categoriaListOld.contains(categoriaListNewCategoria)) {
                    Estabelecimento oldEstabelecimentoIdOfCategoriaListNewCategoria = categoriaListNewCategoria.getEstabelecimentoId();
                    categoriaListNewCategoria.setEstabelecimentoId(estabelecimento);
                    categoriaListNewCategoria = em.merge(categoriaListNewCategoria);
                    if (oldEstabelecimentoIdOfCategoriaListNewCategoria != null && !oldEstabelecimentoIdOfCategoriaListNewCategoria.equals(estabelecimento)) {
                        oldEstabelecimentoIdOfCategoriaListNewCategoria.getCategoriaList().remove(categoriaListNewCategoria);
                        oldEstabelecimentoIdOfCategoriaListNewCategoria = em.merge(oldEstabelecimentoIdOfCategoriaListNewCategoria);
                    }
                }
            }
            for (Utilizadores utilizadoresListOldUtilizadores : utilizadoresListOld) {
                if (!utilizadoresListNew.contains(utilizadoresListOldUtilizadores)) {
                    utilizadoresListOldUtilizadores.setEstabelecimentoId(null);
                    utilizadoresListOldUtilizadores = em.merge(utilizadoresListOldUtilizadores);
                }
            }
            for (Utilizadores utilizadoresListNewUtilizadores : utilizadoresListNew) {
                if (!utilizadoresListOld.contains(utilizadoresListNewUtilizadores)) {
                    Estabelecimento oldEstabelecimentoIdOfUtilizadoresListNewUtilizadores = utilizadoresListNewUtilizadores.getEstabelecimentoId();
                    utilizadoresListNewUtilizadores.setEstabelecimentoId(estabelecimento);
                    utilizadoresListNewUtilizadores = em.merge(utilizadoresListNewUtilizadores);
                    if (oldEstabelecimentoIdOfUtilizadoresListNewUtilizadores != null && !oldEstabelecimentoIdOfUtilizadoresListNewUtilizadores.equals(estabelecimento)) {
                        oldEstabelecimentoIdOfUtilizadoresListNewUtilizadores.getUtilizadoresList().remove(utilizadoresListNewUtilizadores);
                        oldEstabelecimentoIdOfUtilizadoresListNewUtilizadores = em.merge(oldEstabelecimentoIdOfUtilizadoresListNewUtilizadores);
                    }
                }
            }
            for (Productos productosListOldProductos : productosListOld) {
                if (!productosListNew.contains(productosListOldProductos)) {
                    productosListOldProductos.setEstabelecimentoId(null);
                    productosListOldProductos = em.merge(productosListOldProductos);
                }
            }
            for (Productos productosListNewProductos : productosListNew) {
                if (!productosListOld.contains(productosListNewProductos)) {
                    Estabelecimento oldEstabelecimentoIdOfProductosListNewProductos = productosListNewProductos.getEstabelecimentoId();
                    productosListNewProductos.setEstabelecimentoId(estabelecimento);
                    productosListNewProductos = em.merge(productosListNewProductos);
                    if (oldEstabelecimentoIdOfProductosListNewProductos != null && !oldEstabelecimentoIdOfProductosListNewProductos.equals(estabelecimento)) {
                        oldEstabelecimentoIdOfProductosListNewProductos.getProductosList().remove(productosListNewProductos);
                        oldEstabelecimentoIdOfProductosListNewProductos = em.merge(oldEstabelecimentoIdOfProductosListNewProductos);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = estabelecimento.getEstabelecimentoId();
                if (findEstabelecimento(id) == null) {
                    throw new NonexistentEntityException("The estabelecimento with id " + id + " no longer exists.");
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
            Estabelecimento estabelecimento;
            try {
                estabelecimento = em.getReference(Estabelecimento.class, id);
                estabelecimento.getEstabelecimentoId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The estabelecimento with id " + id + " no longer exists.", enfe);
            }
            List<Categoria> categoriaList = estabelecimento.getCategoriaList();
            for (Categoria categoriaListCategoria : categoriaList) {
                categoriaListCategoria.setEstabelecimentoId(null);
                categoriaListCategoria = em.merge(categoriaListCategoria);
            }
            List<Utilizadores> utilizadoresList = estabelecimento.getUtilizadoresList();
            for (Utilizadores utilizadoresListUtilizadores : utilizadoresList) {
                utilizadoresListUtilizadores.setEstabelecimentoId(null);
                utilizadoresListUtilizadores = em.merge(utilizadoresListUtilizadores);
            }
            List<Productos> productosList = estabelecimento.getProductosList();
            for (Productos productosListProductos : productosList) {
                productosListProductos.setEstabelecimentoId(null);
                productosListProductos = em.merge(productosListProductos);
            }
            em.remove(estabelecimento);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Estabelecimento> findEstabelecimentoEntities() {
        return findEstabelecimentoEntities(true, -1, -1);
    }

    public List<Estabelecimento> findEstabelecimentoEntities(int maxResults, int firstResult) {
        return findEstabelecimentoEntities(false, maxResults, firstResult);
    }

    private List<Estabelecimento> findEstabelecimentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Estabelecimento.class));
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

    public Estabelecimento findEstabelecimento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estabelecimento.class, id);
        } finally {
            em.close();
        }
    }

    public int getEstabelecimentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Estabelecimento> rt = cq.from(Estabelecimento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

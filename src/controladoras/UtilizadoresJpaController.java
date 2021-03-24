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
import entidades.Estabelecimento;
import entidades.Utilizadores;
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
public class UtilizadoresJpaController implements Serializable {

    public UtilizadoresJpaController() {
        this.emf = Persistence.createEntityManagerFactory("EstabelecimentoUmPU");
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Utilizadores utilizadores) {
        if (utilizadores.getVendaList() == null) {
            utilizadores.setVendaList(new ArrayList<Venda>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Estabelecimento estabelecimentoId = utilizadores.getEstabelecimentoId();
            if (estabelecimentoId != null) {
                estabelecimentoId = em.getReference(estabelecimentoId.getClass(), estabelecimentoId.getEstabelecimentoId());
                utilizadores.setEstabelecimentoId(estabelecimentoId);
            }
            List<Venda> attachedVendaList = new ArrayList<Venda>();
            for (Venda vendaListVendaToAttach : utilizadores.getVendaList()) {
                vendaListVendaToAttach = em.getReference(vendaListVendaToAttach.getClass(), vendaListVendaToAttach.getVendaId());
                attachedVendaList.add(vendaListVendaToAttach);
            }
            utilizadores.setVendaList(attachedVendaList);
            em.persist(utilizadores);
            if (estabelecimentoId != null) {
                estabelecimentoId.getUtilizadoresList().add(utilizadores);
                estabelecimentoId = em.merge(estabelecimentoId);
            }
            for (Venda vendaListVenda : utilizadores.getVendaList()) {
                Utilizadores oldUtilizadorIdOfVendaListVenda = vendaListVenda.getUtilizadorId();
                vendaListVenda.setUtilizadorId(utilizadores);
                vendaListVenda = em.merge(vendaListVenda);
                if (oldUtilizadorIdOfVendaListVenda != null) {
                    oldUtilizadorIdOfVendaListVenda.getVendaList().remove(vendaListVenda);
                    oldUtilizadorIdOfVendaListVenda = em.merge(oldUtilizadorIdOfVendaListVenda);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Utilizadores utilizadores) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Utilizadores persistentUtilizadores = em.find(Utilizadores.class, utilizadores.getIdUtilizador());
            Estabelecimento estabelecimentoIdOld = persistentUtilizadores.getEstabelecimentoId();
            Estabelecimento estabelecimentoIdNew = utilizadores.getEstabelecimentoId();
            List<Venda> vendaListOld = persistentUtilizadores.getVendaList();
            List<Venda> vendaListNew = utilizadores.getVendaList();
            if (estabelecimentoIdNew != null) {
                estabelecimentoIdNew = em.getReference(estabelecimentoIdNew.getClass(), estabelecimentoIdNew.getEstabelecimentoId());
                utilizadores.setEstabelecimentoId(estabelecimentoIdNew);
            }
            List<Venda> attachedVendaListNew = new ArrayList<Venda>();
            for (Venda vendaListNewVendaToAttach : vendaListNew) {
                vendaListNewVendaToAttach = em.getReference(vendaListNewVendaToAttach.getClass(), vendaListNewVendaToAttach.getVendaId());
                attachedVendaListNew.add(vendaListNewVendaToAttach);
            }
            vendaListNew = attachedVendaListNew;
            utilizadores.setVendaList(vendaListNew);
            utilizadores = em.merge(utilizadores);
            if (estabelecimentoIdOld != null && !estabelecimentoIdOld.equals(estabelecimentoIdNew)) {
                estabelecimentoIdOld.getUtilizadoresList().remove(utilizadores);
                estabelecimentoIdOld = em.merge(estabelecimentoIdOld);
            }
            if (estabelecimentoIdNew != null && !estabelecimentoIdNew.equals(estabelecimentoIdOld)) {
                estabelecimentoIdNew.getUtilizadoresList().add(utilizadores);
                estabelecimentoIdNew = em.merge(estabelecimentoIdNew);
            }
            for (Venda vendaListOldVenda : vendaListOld) {
                if (!vendaListNew.contains(vendaListOldVenda)) {
                    vendaListOldVenda.setUtilizadorId(null);
                    vendaListOldVenda = em.merge(vendaListOldVenda);
                }
            }
            for (Venda vendaListNewVenda : vendaListNew) {
                if (!vendaListOld.contains(vendaListNewVenda)) {
                    Utilizadores oldUtilizadorIdOfVendaListNewVenda = vendaListNewVenda.getUtilizadorId();
                    vendaListNewVenda.setUtilizadorId(utilizadores);
                    vendaListNewVenda = em.merge(vendaListNewVenda);
                    if (oldUtilizadorIdOfVendaListNewVenda != null && !oldUtilizadorIdOfVendaListNewVenda.equals(utilizadores)) {
                        oldUtilizadorIdOfVendaListNewVenda.getVendaList().remove(vendaListNewVenda);
                        oldUtilizadorIdOfVendaListNewVenda = em.merge(oldUtilizadorIdOfVendaListNewVenda);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = utilizadores.getIdUtilizador();
                if (findUtilizadores(id) == null) {
                    throw new NonexistentEntityException("The utilizadores with id " + id + " no longer exists.");
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
            Utilizadores utilizadores;
            try {
                utilizadores = em.getReference(Utilizadores.class, id);
                utilizadores.getIdUtilizador();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The utilizadores with id " + id + " no longer exists.", enfe);
            }
            Estabelecimento estabelecimentoId = utilizadores.getEstabelecimentoId();
            if (estabelecimentoId != null) {
                estabelecimentoId.getUtilizadoresList().remove(utilizadores);
                estabelecimentoId = em.merge(estabelecimentoId);
            }
            List<Venda> vendaList = utilizadores.getVendaList();
            for (Venda vendaListVenda : vendaList) {
                vendaListVenda.setUtilizadorId(null);
                vendaListVenda = em.merge(vendaListVenda);
            }
            em.remove(utilizadores);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Utilizadores> findUtilizadoresEntities() {
        return findUtilizadoresEntities(true, -1, -1);
    }

    public List<Utilizadores> findUtilizadoresEntities(int maxResults, int firstResult) {
        return findUtilizadoresEntities(false, maxResults, firstResult);
    }

    private List<Utilizadores> findUtilizadoresEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Utilizadores.class));
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

    public Utilizadores findUtilizadores(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Utilizadores.class, id);
        } finally {
            em.close();
        }
    }

    public int getUtilizadoresCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Utilizadores> rt = cq.from(Utilizadores.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}

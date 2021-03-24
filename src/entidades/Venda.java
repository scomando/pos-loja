/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author Sunil Comando
 */
@Entity
@Table(catalog = "estabelecimentoum", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Venda.findAll", query = "SELECT v FROM Venda v"),
    @NamedQuery(name = "Venda.findByDataVenda", query = "SELECT v FROM Venda v WHERE v.dataVenda = :dataVenda"),
    @NamedQuery(name = "Venda.findByReferencia", query = "SELECT v FROM Venda v WHERE v.referencia = :referencia"),
    @NamedQuery(name = "Venda.findByVendaId", query = "SELECT v FROM Venda v WHERE v.vendaId = :vendaId")})
public class Venda implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "data_venda")
    @Temporal(TemporalType.DATE)
    private Date dataVenda;
    @Size(max = 255)
    private String referencia;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "venda_id")
    private Integer vendaId;
    @OneToMany(mappedBy = "vendaId", fetch = FetchType.EAGER)
    private List<ProductoVenda> productoVendaList;
    @JoinColumn(name = "utilizador_id", referencedColumnName = "id_utilizador")
    @ManyToOne(fetch = FetchType.EAGER)
    private Utilizadores utilizadorId;

    public Venda() {
    }

    public Venda(Integer vendaId) {
        this.vendaId = vendaId;
    }

    public Date getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(Date dataVenda) {
        this.dataVenda = dataVenda;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public Integer getVendaId() {
        return vendaId;
    }

    public void setVendaId(Integer vendaId) {
        this.vendaId = vendaId;
    }

    @XmlTransient
    public List<ProductoVenda> getProductoVendaList() {
        return productoVendaList;
    }

    public void setProductoVendaList(List<ProductoVenda> productoVendaList) {
        this.productoVendaList = productoVendaList;
    }

    public Utilizadores getUtilizadorId() {
        return utilizadorId;
    }

    public void setUtilizadorId(Utilizadores utilizadorId) {
        this.utilizadorId = utilizadorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (vendaId != null ? vendaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Venda)) {
            return false;
        }
        Venda other = (Venda) object;
        if ((this.vendaId == null && other.vendaId != null) || (this.vendaId != null && !this.vendaId.equals(other.vendaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Venda[ vendaId=" + vendaId + " ]";
    }
    
}

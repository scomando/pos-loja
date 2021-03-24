/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigInteger;
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
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sunil Comando
 */
@Entity
@Table(name = "producto_venda", catalog = "estabelecimentoum", schema = "public")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProductoVenda.findAll", query = "SELECT p FROM ProductoVenda p"),
    @NamedQuery(name = "ProductoVenda.findByPreco", query = "SELECT p FROM ProductoVenda p WHERE p.preco = :preco"),
    @NamedQuery(name = "ProductoVenda.findByPrecoTotal", query = "SELECT p FROM ProductoVenda p WHERE p.precoTotal = :precoTotal"),
    @NamedQuery(name = "ProductoVenda.findByQuantidade", query = "SELECT p FROM ProductoVenda p WHERE p.quantidade = :quantidade"),
    @NamedQuery(name = "ProductoVenda.findByProductoVendaId", query = "SELECT p FROM ProductoVenda p WHERE p.productoVendaId = :productoVendaId")})
public class ProductoVenda implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigInteger preco;
    @Column(name = "preco_total")
    private BigInteger precoTotal;
    @Size(max = 255)
    private String quantidade;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "producto_venda_id")
    private Integer productoVendaId;
    @JoinColumn(name = "producto_id", referencedColumnName = "producto_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Productos productoId;
    @JoinColumn(name = "venda_id", referencedColumnName = "venda_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Venda vendaId;

    public ProductoVenda() {
    }

    public ProductoVenda(Integer productoVendaId) {
        this.productoVendaId = productoVendaId;
    }

    public BigInteger getPreco() {
        return preco;
    }

    public void setPreco(BigInteger preco) {
        this.preco = preco;
    }

    public BigInteger getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(BigInteger precoTotal) {
        this.precoTotal = precoTotal;
    }

    public String getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(String quantidade) {
        this.quantidade = quantidade;
    }

    public Integer getProductoVendaId() {
        return productoVendaId;
    }

    public void setProductoVendaId(Integer productoVendaId) {
        this.productoVendaId = productoVendaId;
    }

    public Productos getProductoId() {
        return productoId;
    }

    public void setProductoId(Productos productoId) {
        this.productoId = productoId;
    }

    public Venda getVendaId() {
        return vendaId;
    }

    public void setVendaId(Venda vendaId) {
        this.vendaId = vendaId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productoVendaId != null ? productoVendaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProductoVenda)) {
            return false;
        }
        ProductoVenda other = (ProductoVenda) object;
        if ((this.productoVendaId == null && other.productoVendaId != null) || (this.productoVendaId != null && !this.productoVendaId.equals(other.productoVendaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.ProductoVenda[ productoVendaId=" + productoVendaId + " ]";
    }
    
}

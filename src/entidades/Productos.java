/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
    @NamedQuery(name = "Productos.findAll", query = "SELECT p FROM Productos p"),
    @NamedQuery(name = "Productos.findByCategoriaCodigo", query = "SELECT p FROM Productos p WHERE p.categoriaCodigo = :categoriaCodigo"),
    @NamedQuery(name = "Productos.findByCodigoBarras", query = "SELECT p FROM Productos p WHERE p.codigoBarras = :codigoBarras"),
    @NamedQuery(name = "Productos.findByDescricaoProducto", query = "SELECT p FROM Productos p WHERE p.descricaoProducto = :descricaoProducto"),
    @NamedQuery(name = "Productos.findByEstabelecimentoCodigo", query = "SELECT p FROM Productos p WHERE p.estabelecimentoCodigo = :estabelecimentoCodigo"),
    @NamedQuery(name = "Productos.findByNomeProducto", query = "SELECT p FROM Productos p WHERE p.nomeProducto = :nomeProducto"),
    @NamedQuery(name = "Productos.findByPrecoProducto", query = "SELECT p FROM Productos p WHERE p.precoProducto = :precoProducto"),
    @NamedQuery(name = "Productos.findByQuantidadeProducto", query = "SELECT p FROM Productos p WHERE p.quantidadeProducto = :quantidadeProducto"),
    @NamedQuery(name = "Productos.findByProductoId", query = "SELECT p FROM Productos p WHERE p.productoId = :productoId")})
public class Productos implements Serializable {

    private static final long serialVersionUID = 1L;
    @Size(max = 255)
    @Column(name = "categoria_codigo")
    private String categoriaCodigo;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "codigo_barras")
    private String codigoBarras;
    @Size(max = 255)
    @Column(name = "descricao_producto")
    private String descricaoProducto;
    @Size(max = 255)
    @Column(name = "estabelecimento_codigo")
    private String estabelecimentoCodigo;
    @Size(max = 255)
    @Column(name = "nome_producto")
    private String nomeProducto;
    @Column(name = "preco_producto")
    private BigInteger precoProducto;
    @Size(max = 255)
    @Column(name = "quantidade_producto")
    private String quantidadeProducto;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "producto_id")
    private Integer productoId;
    @Lob
    private byte[] imagem;
    @OneToMany(mappedBy = "productoId", fetch = FetchType.EAGER)
    private List<ProductoVenda> productoVendaList;
    @JoinColumn(name = "categoria_id", referencedColumnName = "categoria_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Categoria categoriaId;
    @JoinColumn(name = "estabelecimento_id", referencedColumnName = "estabelecimento_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Estabelecimento estabelecimentoId;

    public Productos() {
    }

    public Productos(Integer productoId) {
        this.productoId = productoId;
    }

    public Productos(Integer productoId, String codigoBarras) {
        this.productoId = productoId;
        this.codigoBarras = codigoBarras;
    }

    public String getCategoriaCodigo() {
        return categoriaCodigo;
    }

    public void setCategoriaCodigo(String categoriaCodigo) {
        this.categoriaCodigo = categoriaCodigo;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getDescricaoProducto() {
        return descricaoProducto;
    }

    public void setDescricaoProducto(String descricaoProducto) {
        this.descricaoProducto = descricaoProducto;
    }

    public String getEstabelecimentoCodigo() {
        return estabelecimentoCodigo;
    }

    public void setEstabelecimentoCodigo(String estabelecimentoCodigo) {
        this.estabelecimentoCodigo = estabelecimentoCodigo;
    }

    public String getNomeProducto() {
        return nomeProducto;
    }

    public void setNomeProducto(String nomeProducto) {
        this.nomeProducto = nomeProducto;
    }

    public BigInteger getPrecoProducto() {
        return precoProducto;
    }

    public void setPrecoProducto(BigInteger precoProducto) {
        this.precoProducto = precoProducto;
    }

    public String getQuantidadeProducto() {
        return quantidadeProducto;
    }

    public void setQuantidadeProducto(String quantidadeProducto) {
        this.quantidadeProducto = quantidadeProducto;
    }

    public Integer getProductoId() {
        return productoId;
    }

    public void setProductoId(Integer productoId) {
        this.productoId = productoId;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    @XmlTransient
    public List<ProductoVenda> getProductoVendaList() {
        return productoVendaList;
    }

    public void setProductoVendaList(List<ProductoVenda> productoVendaList) {
        this.productoVendaList = productoVendaList;
    }

    public Categoria getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Categoria categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Estabelecimento getEstabelecimentoId() {
        return estabelecimentoId;
    }

    public void setEstabelecimentoId(Estabelecimento estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (productoId != null ? productoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Productos)) {
            return false;
        }
        Productos other = (Productos) object;
        if ((this.productoId == null && other.productoId != null) || (this.productoId != null && !this.productoId.equals(other.productoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Productos[ productoId=" + productoId + " ]";
    }
    
}

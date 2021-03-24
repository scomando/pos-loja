/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entidades;

import java.io.Serializable;
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
    @NamedQuery(name = "Categoria.findAll", query = "SELECT c FROM Categoria c"),
    @NamedQuery(name = "Categoria.findByCodigoCategoria", query = "SELECT c FROM Categoria c WHERE c.codigoCategoria = :codigoCategoria"),
    @NamedQuery(name = "Categoria.findByCodigoEstabelecimento", query = "SELECT c FROM Categoria c WHERE c.codigoEstabelecimento = :codigoEstabelecimento"),
    @NamedQuery(name = "Categoria.findByDescricaoCategoria", query = "SELECT c FROM Categoria c WHERE c.descricaoCategoria = :descricaoCategoria"),
    @NamedQuery(name = "Categoria.findByCategoriaId", query = "SELECT c FROM Categoria c WHERE c.categoriaId = :categoriaId")})
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "codigo_categoria")
    private String codigoCategoria;
    @Size(max = 255)
    @Column(name = "codigo_estabelecimento")
    private String codigoEstabelecimento;
    @Size(max = 255)
    @Column(name = "descricao_categoria")
    private String descricaoCategoria;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "categoria_id")
    private Integer categoriaId;
    @JoinColumn(name = "estabelecimento_id", referencedColumnName = "estabelecimento_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Estabelecimento estabelecimentoId;
    @OneToMany(mappedBy = "categoriaId", fetch = FetchType.EAGER)
    private List<Productos> productosList;

    public Categoria() {
    }

    public Categoria(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Categoria(Integer categoriaId, String codigoCategoria) {
        this.categoriaId = categoriaId;
        this.codigoCategoria = codigoCategoria;
    }

    public String getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(String codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    public void setCodigoEstabelecimento(String codigoEstabelecimento) {
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public String getDescricaoCategoria() {
        return descricaoCategoria;
    }

    public void setDescricaoCategoria(String descricaoCategoria) {
        this.descricaoCategoria = descricaoCategoria;
    }

    public Integer getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Integer categoriaId) {
        this.categoriaId = categoriaId;
    }

    public Estabelecimento getEstabelecimentoId() {
        return estabelecimentoId;
    }

    public void setEstabelecimentoId(Estabelecimento estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }

    @XmlTransient
    public List<Productos> getProductosList() {
        return productosList;
    }

    public void setProductosList(List<Productos> productosList) {
        this.productosList = productosList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoriaId != null ? categoriaId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categoria)) {
            return false;
        }
        Categoria other = (Categoria) object;
        if ((this.categoriaId == null && other.categoriaId != null) || (this.categoriaId != null && !this.categoriaId.equals(other.categoriaId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Categoria[ categoriaId=" + categoriaId + " ]";
    }
    
}

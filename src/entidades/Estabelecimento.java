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
    @NamedQuery(name = "Estabelecimento.findAll", query = "SELECT e FROM Estabelecimento e"),
    @NamedQuery(name = "Estabelecimento.findByActivo", query = "SELECT e FROM Estabelecimento e WHERE e.activo = :activo"),
    @NamedQuery(name = "Estabelecimento.findByCidadeEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.cidadeEstabelecimento = :cidadeEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByCodigoEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.codigoEstabelecimento = :codigoEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByCodigoPostal", query = "SELECT e FROM Estabelecimento e WHERE e.codigoPostal = :codigoPostal"),
    @NamedQuery(name = "Estabelecimento.findByEmailEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.emailEstabelecimento = :emailEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByIvaEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.ivaEstabelecimento = :ivaEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByNomeEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.nomeEstabelecimento = :nomeEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByTelefoneEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.telefoneEstabelecimento = :telefoneEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByTipoEstabelecimento", query = "SELECT e FROM Estabelecimento e WHERE e.tipoEstabelecimento = :tipoEstabelecimento"),
    @NamedQuery(name = "Estabelecimento.findByEstabelecimentoId", query = "SELECT e FROM Estabelecimento e WHERE e.estabelecimentoId = :estabelecimentoId")})
public class Estabelecimento implements Serializable {

    private static final long serialVersionUID = 1L;
    private Boolean activo;
    @Size(max = 255)
    @Column(name = "cidade_estabelecimento")
    private String cidadeEstabelecimento;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "codigo_estabelecimento")
    private String codigoEstabelecimento;
    @Size(max = 255)
    @Column(name = "codigo_postal")
    private String codigoPostal;
    @Size(max = 255)
    @Column(name = "email_estabelecimento")
    private String emailEstabelecimento;
    @Column(name = "iva_estabelecimento")
    private BigInteger ivaEstabelecimento;
    @Size(max = 255)
    @Column(name = "nome_estabelecimento")
    private String nomeEstabelecimento;
    @Size(max = 255)
    @Column(name = "telefone_estabelecimento")
    private String telefoneEstabelecimento;
    @Size(max = 255)
    @Column(name = "tipo_estabelecimento")
    private String tipoEstabelecimento;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "estabelecimento_id")
    private Integer estabelecimentoId;
    @OneToMany(mappedBy = "estabelecimentoId", fetch = FetchType.EAGER)
    private List<Categoria> categoriaList;
    @OneToMany(mappedBy = "estabelecimentoId", fetch = FetchType.EAGER)
    private List<Utilizadores> utilizadoresList;
    @OneToMany(mappedBy = "estabelecimentoId", fetch = FetchType.EAGER)
    private List<Productos> productosList;

    public Estabelecimento() {
    }

    public Estabelecimento(Integer estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }

    public Estabelecimento(Integer estabelecimentoId, String codigoEstabelecimento) {
        this.estabelecimentoId = estabelecimentoId;
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getCidadeEstabelecimento() {
        return cidadeEstabelecimento;
    }

    public void setCidadeEstabelecimento(String cidadeEstabelecimento) {
        this.cidadeEstabelecimento = cidadeEstabelecimento;
    }

    public String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    public void setCodigoEstabelecimento(String codigoEstabelecimento) {
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getEmailEstabelecimento() {
        return emailEstabelecimento;
    }

    public void setEmailEstabelecimento(String emailEstabelecimento) {
        this.emailEstabelecimento = emailEstabelecimento;
    }

    public BigInteger getIvaEstabelecimento() {
        return ivaEstabelecimento;
    }

    public void setIvaEstabelecimento(BigInteger ivaEstabelecimento) {
        this.ivaEstabelecimento = ivaEstabelecimento;
    }

    public String getNomeEstabelecimento() {
        return nomeEstabelecimento;
    }

    public void setNomeEstabelecimento(String nomeEstabelecimento) {
        this.nomeEstabelecimento = nomeEstabelecimento;
    }

    public String getTelefoneEstabelecimento() {
        return telefoneEstabelecimento;
    }

    public void setTelefoneEstabelecimento(String telefoneEstabelecimento) {
        this.telefoneEstabelecimento = telefoneEstabelecimento;
    }

    public String getTipoEstabelecimento() {
        return tipoEstabelecimento;
    }

    public void setTipoEstabelecimento(String tipoEstabelecimento) {
        this.tipoEstabelecimento = tipoEstabelecimento;
    }

    public Integer getEstabelecimentoId() {
        return estabelecimentoId;
    }

    public void setEstabelecimentoId(Integer estabelecimentoId) {
        this.estabelecimentoId = estabelecimentoId;
    }

    @XmlTransient
    public List<Categoria> getCategoriaList() {
        return categoriaList;
    }

    public void setCategoriaList(List<Categoria> categoriaList) {
        this.categoriaList = categoriaList;
    }

    @XmlTransient
    public List<Utilizadores> getUtilizadoresList() {
        return utilizadoresList;
    }

    public void setUtilizadoresList(List<Utilizadores> utilizadoresList) {
        this.utilizadoresList = utilizadoresList;
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
        hash += (estabelecimentoId != null ? estabelecimentoId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Estabelecimento)) {
            return false;
        }
        Estabelecimento other = (Estabelecimento) object;
        if ((this.estabelecimentoId == null && other.estabelecimentoId != null) || (this.estabelecimentoId != null && !this.estabelecimentoId.equals(other.estabelecimentoId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Estabelecimento[ estabelecimentoId=" + estabelecimentoId + " ]";
    }
    
}

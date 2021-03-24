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
    @NamedQuery(name = "Utilizadores.findAll", query = "SELECT u FROM Utilizadores u"),
    @NamedQuery(name = "Utilizadores.findByActivoUtilizador", query = "SELECT u FROM Utilizadores u WHERE u.activoUtilizador = :activoUtilizador"),
    @NamedQuery(name = "Utilizadores.findByCodigoEstabelecimento", query = "SELECT u FROM Utilizadores u WHERE u.codigoEstabelecimento = :codigoEstabelecimento"),
    @NamedQuery(name = "Utilizadores.findByCodigoUtilizador", query = "SELECT u FROM Utilizadores u WHERE u.codigoUtilizador = :codigoUtilizador"),
    @NamedQuery(name = "Utilizadores.findByFuncaoUtilizador", query = "SELECT u FROM Utilizadores u WHERE u.funcaoUtilizador = :funcaoUtilizador"),
    @NamedQuery(name = "Utilizadores.findByNomeUtlizador", query = "SELECT u FROM Utilizadores u WHERE u.nomeUtlizador = :nomeUtlizador"),
    @NamedQuery(name = "Utilizadores.findBySenhaUtilizador", query = "SELECT u FROM Utilizadores u WHERE u.senhaUtilizador = :senhaUtilizador"),
    @NamedQuery(name = "Utilizadores.findByIdUtilizador", query = "SELECT u FROM Utilizadores u WHERE u.idUtilizador = :idUtilizador")})
public class Utilizadores implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "activo_utilizador")
    private Boolean activoUtilizador;
    @Size(max = 255)
    @Column(name = "codigo_estabelecimento")
    private String codigoEstabelecimento;
    @Size(max = 255)
    @Column(name = "codigo_utilizador")
    private String codigoUtilizador;
    @Size(max = 255)
    @Column(name = "funcao_utilizador")
    private String funcaoUtilizador;
    @Size(max = 255)
    @Column(name = "nome_utlizador")
    private String nomeUtlizador;
    @Size(max = 255)
    @Column(name = "senha_utilizador")
    private String senhaUtilizador;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_utilizador")
    private Integer idUtilizador;
    @OneToMany(mappedBy = "utilizadorId", fetch = FetchType.EAGER)
    private List<Venda> vendaList;
    @JoinColumn(name = "estabelecimento_id", referencedColumnName = "estabelecimento_id")
    @ManyToOne(fetch = FetchType.EAGER)
    private Estabelecimento estabelecimentoId;

    public Utilizadores() {
    }

    public Utilizadores(Integer idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    public Boolean getActivoUtilizador() {
        return activoUtilizador;
    }

    public void setActivoUtilizador(Boolean activoUtilizador) {
        this.activoUtilizador = activoUtilizador;
    }

    public String getCodigoEstabelecimento() {
        return codigoEstabelecimento;
    }

    public void setCodigoEstabelecimento(String codigoEstabelecimento) {
        this.codigoEstabelecimento = codigoEstabelecimento;
    }

    public String getCodigoUtilizador() {
        return codigoUtilizador;
    }

    public void setCodigoUtilizador(String codigoUtilizador) {
        this.codigoUtilizador = codigoUtilizador;
    }

    public String getFuncaoUtilizador() {
        return funcaoUtilizador;
    }

    public void setFuncaoUtilizador(String funcaoUtilizador) {
        this.funcaoUtilizador = funcaoUtilizador;
    }

    public String getNomeUtlizador() {
        return nomeUtlizador;
    }

    public void setNomeUtlizador(String nomeUtlizador) {
        this.nomeUtlizador = nomeUtlizador;
    }

    public String getSenhaUtilizador() {
        return senhaUtilizador;
    }

    public void setSenhaUtilizador(String senhaUtilizador) {
        this.senhaUtilizador = senhaUtilizador;
    }

    public Integer getIdUtilizador() {
        return idUtilizador;
    }

    public void setIdUtilizador(Integer idUtilizador) {
        this.idUtilizador = idUtilizador;
    }

    @XmlTransient
    public List<Venda> getVendaList() {
        return vendaList;
    }

    public void setVendaList(List<Venda> vendaList) {
        this.vendaList = vendaList;
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
        hash += (idUtilizador != null ? idUtilizador.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Utilizadores)) {
            return false;
        }
        Utilizadores other = (Utilizadores) object;
        if ((this.idUtilizador == null && other.idUtilizador != null) || (this.idUtilizador != null && !this.idUtilizador.equals(other.idUtilizador))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entidades.Utilizadores[ idUtilizador=" + idUtilizador + " ]";
    }
    
}

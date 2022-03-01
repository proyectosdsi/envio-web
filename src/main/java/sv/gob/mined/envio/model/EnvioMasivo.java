/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.envio.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author MISanchez
 */
@Entity
@Table(name = "ENVIO_MASIVO")
@NamedQueries({
    @NamedQuery(name = "EnvioMasivo.findAll", query = "SELECT e FROM EnvioMasivo e")})
public class EnvioMasivo implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "ID_ENVIO")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqEnvio")
    @SequenceGenerator(name = "seqEnvio", sequenceName = "SEQ_ENVIO_MASIVA", allocationSize = 1, initialValue = 1)
    private BigDecimal idEnvio;
    @Column(name = "CORRE_REMITENTE")
    private String correRemitente;
    @Column(name = "FECHA_ENVIO")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaEnvio;
    @Column(name = "TITULO")
    private String titulo;
    @Column(name = "MENSAJE")
    private String mensaje;
    @Column(name = "ARCHIVO")
    private String archivo;
    
    @OneToMany(mappedBy = "idEnvio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<DetalleEnvio> detalleEnvioList;

    public EnvioMasivo() {
    }

    public EnvioMasivo(BigDecimal idEnvio) {
        this.idEnvio = idEnvio;
    }

    public BigDecimal getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(BigDecimal idEnvio) {
        this.idEnvio = idEnvio;
    }

    public String getCorreRemitente() {
        return correRemitente;
    }

    public void setCorreRemitente(String correRemitente) {
        this.correRemitente = correRemitente;
    }

    public Date getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(Date fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public List<DetalleEnvio> getDetalleEnvioList() {
        if (detalleEnvioList == null) {
            detalleEnvioList = new ArrayList<>();
        }
        return detalleEnvioList;
    }

    public void setDetalleEnvioList(List<DetalleEnvio> detalleEnvioList) {
        this.detalleEnvioList = detalleEnvioList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idEnvio != null ? idEnvio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EnvioMasivo)) {
            return false;
        }
        EnvioMasivo other = (EnvioMasivo) object;
        return !((this.idEnvio == null && other.idEnvio != null) || (this.idEnvio != null && !this.idEnvio.equals(other.idEnvio)));
    }

    @Override
    public String toString() {
        return "sv.gob.mined.envio.model.EnvioMasivo[ idEnvio=" + idEnvio + " ]";
    }

}

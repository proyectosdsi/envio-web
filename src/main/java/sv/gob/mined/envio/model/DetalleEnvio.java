/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.envio.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author MISanchez
 */
@Entity
@Table(name = "DETALLE_ENVIO")
@NamedQueries({
    @NamedQuery(name = "DetalleEnvio.findAll", query = "SELECT d FROM DetalleEnvio d")})
public class DetalleEnvio implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @Column(name = "ID_DETALLE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqDetalleEnvio")
    @SequenceGenerator(name = "seqDetalleEnvio", sequenceName = "SEQ_DETALLE_ENVIO", allocationSize = 1, initialValue = 1)
    private BigDecimal idDetalle;
    @Column(name = "NIP")
    private String nip;
    @Column(name = "NOMBRE_DESTINATARIO")
    private String nombreDestinatario;
    @Column(name = "CORREO_DESTINATARIO")
    private String correoDestinatario;
    @JoinColumn(name = "ID_ENVIO", referencedColumnName = "ID_ENVIO")
    @ManyToOne(fetch = FetchType.EAGER)
    private EnvioMasivo idEnvio;
    @Column(name = "ENVIADO")
    private Short enviado;

    public DetalleEnvio() {
    }

    public DetalleEnvio(BigDecimal idDetalle) {
        this.idDetalle = idDetalle;
    }

    public BigDecimal getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(BigDecimal idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getCorreoDestinatario() {
        return correoDestinatario;
    }

    public void setCorreoDestinatario(String correDestinatario) {
        this.correoDestinatario = correDestinatario;
    }

    public EnvioMasivo getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(EnvioMasivo idEnvio) {
        this.idEnvio = idEnvio;
    }

    public Short getEnviado() {
        return enviado;
    }

    public void setEnviado(Short enviado) {
        this.enviado = enviado;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetalle != null ? idDetalle.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleEnvio)) {
            return false;
        }
        DetalleEnvio other = (DetalleEnvio) object;
        return !((this.idDetalle == null && other.idDetalle != null) || (this.idDetalle != null && !this.idDetalle.equals(other.idDetalle)));
    }

    @Override
    public String toString() {
        return "sv.gob.mined.envio.model.DetalleEnvio[ idDetalle=" + idDetalle + " ]";
    }
    
}

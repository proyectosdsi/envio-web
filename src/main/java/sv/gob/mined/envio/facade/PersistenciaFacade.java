/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.envio.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.jboss.ejb3.annotation.TransactionTimeout;
import sv.gob.mined.envio.model.Destinatarios;
import sv.gob.mined.envio.model.DetalleEnvio;
import sv.gob.mined.envio.model.Director;
import sv.gob.mined.envio.model.EnvioMasivo;
import sv.gob.mined.envio.model.Remitentes;

/**
 *
 * @author MISanchez
 */
@Singleton
public class PersistenciaFacade {

    @PersistenceContext(unitName = "sv.gob.mined_envio-web_war_1.0PU")
    private EntityManager em;

    public EnvioMasivo findEnvio(BigDecimal idEnvio) {
        return em.find(EnvioMasivo.class, idEnvio);
    }

    /*public BigDecimal guardarEnvio(String correo, String titulo, String mensaje, String archivo) {
        EnvioMasivo eMasivo = new EnvioMasivo();
        eMasivo.setArchivo(archivo);
        eMasivo.setCorreRemitente(correo);
        eMasivo.setFechaEnvio(new Date());
        eMasivo.setMensaje(mensaje);
        eMasivo.setTitulo(titulo);

        em.persist(eMasivo);

        return eMasivo.getIdEnvio();
    }*/
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    @TransactionTimeout(value = 120, unit = TimeUnit.MINUTES)
    public BigDecimal guardarEnvio(EnvioMasivo eMasivo) {
        em.persist(eMasivo);

        return eMasivo.getIdEnvio();
    }

    /*public void guardarDetalleEnviado(String nip, String nombre, String correo, EnvioMasivo idEnvio, Boolean enviado) {
        DetalleEnvio de = new DetalleEnvio();
        de.setCorreoDestinatario(correo);
        de.setIdEnvio(idEnvio);
        de.setNip(nip);
        de.setNombreDestinatario(nombre);
        de.setEnviado(enviado ? (short) 1 : 0);

        em.persist(de);
    }*/
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<DetalleEnvio> findDetalleEnvio(BigDecimal idEnvio) {
        Query q = em.createQuery("SELECT d FROM DetalleEnvio d WHERE d.enviado=0 and d.idEnvio.idEnvio=:idEnvio ORDER BY d.idDetalle", DetalleEnvio.class);
        q.setParameter("idEnvio", idEnvio);
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void actualizarDetalleEnviado(List<BigDecimal> detalleEnvios) {
        List<BigDecimal> param = new ArrayList<>();
        for (BigDecimal detalleEnvio : detalleEnvios) {
            param.add(detalleEnvio);
            if (param.size() == 999) {
                Query q = em.createQuery("UPDATE DetalleEnvio d set d.enviado=1 WHERE d.idDetalle in :list");
                q.setParameter("list", param);
                q.executeUpdate();

                param.clear();
            }
        }

        if (!param.isEmpty()) {
            Query q = em.createQuery("UPDATE DetalleEnvio d set d.enviado=1 WHERE d.idDetalle in :list");
            q.setParameter("list", param);
            q.executeUpdate();
        }
    }

    public List<Destinatarios> getLstDestinatarioByCodigoDepartamento(String codigoDepartamento) {
        Query q = em.createQuery("SELECT d FROM Destinatarios d WHERE d.codigo.codigoDepartamento=:codEnt", Destinatarios.class);
        q.setParameter("codEnt", codigoDepartamento);
        return q.getResultList();
    }

    public List<Director> getLstDirectores() {
        Query q = em.createQuery("SELECT d FROM Director d", Director.class);
        return q.getResultList();
    }

    public List<Destinatarios> getLstDestinatarioByCodigoDepartamento(String codigoDepartamento, Long idInicio, Long idFin) {
        Query q = em.createQuery("SELECT d FROM Destinatarios d WHERE d.codigo.codigoDepartamento=:codEnt and (d.id>=:inicio and d.id<=:fin)", Destinatarios.class);
        q.setParameter("codEnt", codigoDepartamento);
        q.setParameter("inicio", idInicio);
        q.setParameter("fin", idFin);
        return q.getResultList();
    }

    public List<Remitentes> getLstRemitentes(Boolean asc) {
        Query q = em.createQuery("SELECT r FROM Remitentes r ORDER BY r.clave " + (asc ? "ASC" : "DESC"), Remitentes.class);
        return q.getResultList();
    }
}

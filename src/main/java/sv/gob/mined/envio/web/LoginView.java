/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.envio.web;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import sv.gob.mined.envio.facade.EMailFacade;
import sv.gob.mined.utils.jsf.JsfUtil;

/**
 *
 * @author misanchez
 */
@ManagedBean
@ViewScoped
public class LoginView {

    private Boolean correoValido = false;
    private String correoRemitente;
    private String idDominioCorreo = "2";
    private String dominio;
    private String password;

    private String remitente;
    private String port;
    private String server;
    private Session mailSession;
    private Transport transport;

    @Inject
    private EMailFacade eMailFacade;

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getSessionMap().clear();
    }

    public Boolean getCorreoValido() {
        return correoValido;
    }

    public void setCorreoValido(Boolean correoValido) {
        this.correoValido = correoValido;
    }

    public String getCorreoRemitente() {
        return correoRemitente;
    }

    public void setCorreoRemitente(String correoRemitente) {
        this.correoRemitente = correoRemitente;
    }

    public String getIdDominioCorreo() {
        return idDominioCorreo;
    }

    public void setIdDominioCorreo(String idDominioCorreo) {
        this.idDominioCorreo = idDominioCorreo;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String validarCrendecialesDelCorreo() {
        String url = "";
        try {
            if (correoRemitente != null && password != null) {
                if (idDominioCorreo.equals("1")) {
                    remitente = correoRemitente.concat("@").concat("mined.gob.sv");
                    port = "587";
                    server = "svr2k13mail01.mined.gob.sv";
                    mailSession = eMailFacade.getMailSessionMined(mailSession, dominio, password, remitente);
                } else {
                    remitente = correoRemitente.concat("@").concat("admin.mined.edu.sv");
                    port = "587";
                    server = "smtp.office365.com";
                    mailSession = eMailFacade.getMailSessionOffice(mailSession, remitente, password);
                }

                transport = mailSession.getTransport("smtp");
                transport.connect(server, Integer.parseInt(port), remitente, password);

                correoValido = true;

                transport.close();

                FacesContext context = FacesContext.getCurrentInstance();
                context.getExternalContext().getSessionMap().put("sessionMail", mailSession);
                context.getExternalContext().getSessionMap().put("server", server);
                context.getExternalContext().getSessionMap().put("port", port);
                context.getExternalContext().getSessionMap().put("remitente", remitente);
                context.getExternalContext().getSessionMap().put("password", password);

                url = "mensaje?faces-redirect=true";
            }
        } catch (NoSuchProviderException ex) {
            JsfUtil.mensajeError("Error en el usuario o  clave de acceso.");
            correoValido = false;
        } catch (MessagingException ex) {
            JsfUtil.mensajeError("Error en el usuario o  clave de acceso.");
            correoValido = false;
        }

        return url;
    }
}

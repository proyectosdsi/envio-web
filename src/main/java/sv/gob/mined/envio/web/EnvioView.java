/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.envio.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.ServletContext;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import sv.gob.mined.envio.facade.PersistenciaFacade;
import sv.gob.mined.envio.facade.ProcesoFacade;
import sv.gob.mined.envio.model.EnvioMasivo;
import sv.gob.mined.utils.jsf.JsfUtil;

/**
 *
 * @author MISanchez
 */
@ManagedBean
@SessionScoped
public class EnvioView {

    private Boolean showUploadFile = true;

    private BigDecimal idEnvio;

    private String remitente;

    private String titulo;
    private String mensaje;
    private String pathArchivo;
    private String port;
    private String server;
    private String password;

    private UploadedFile file;

    private Transport transport;
    private Session mailSession;

    @Inject
    private ProcesoFacade procesoFacade;
    
    @Inject
    private PersistenciaFacade persistenciaFacade;

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("parametros");

    public EnvioView() {
    }

    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getExternalContext().getSessionMap().containsKey("sessionMail")) {
            try {
                mailSession = (Session) context.getExternalContext().getSessionMap().get("sessionMail");
                server = (String) context.getExternalContext().getSessionMap().get("server");
                port = (String) context.getExternalContext().getSessionMap().get("port");
                remitente = (String) context.getExternalContext().getSessionMap().get("remitente");
                password = (String) context.getExternalContext().getSessionMap().get("password");

                transport = mailSession.getTransport("smtp");
                transport.connect(server, Integer.parseInt(port), remitente, password);

            } catch (NoSuchProviderException ex) {
                Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MessagingException ex) {
                Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        if (!transport.isConnected()) {
            try {
                transport.close();
            } catch (MessagingException ex) {
                Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="getter-setter">
    public BigDecimal getIdEnvio() {
        return idEnvio;
    }

    public void setIdEnvio(BigDecimal idEnvio) {
        this.idEnvio = idEnvio;
    }

    public String getRemitente() {
        return remitente;
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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public Boolean getShowUploadFile() {
        return showUploadFile;
    }
    // </editor-fold>

    public void handleFileUpload(FileUploadEvent event) throws IOException {
        file = event.getFile();

        try {

            if (System.getProperty("os.name").toUpperCase().contains("WINDOWS")) {
                pathArchivo = RESOURCE_BUNDLE.getString("path_archivo_windows") + File.separator + file.getFileName();
            } else {
                pathArchivo = RESOURCE_BUNDLE.getString("path_archivo_linux") + File.separator + file.getFileName();
            }
            Path folder = Paths.get(pathArchivo);
            Path arc;

            if (folder.toFile().exists()) {
                arc = folder;
            } else {
                arc = Files.createFile(folder);
            }

            try (InputStream input = file.getInputStream()) {
                if (validarArchivo(input)) {
                    pathArchivo = folder.toString();
                    Files.copy(file.getInputStream(), arc, StandardCopyOption.REPLACE_EXISTING);
                    showUploadFile = false;
                } else {
                    JsfUtil.mensajeError("El archivo cargado no contiene el formato requerido");
                    showUploadFile = true;
                }

                input.close();
            } catch (IOException ex) {
                Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
                JsfUtil.mensajeError("Ah ocurrido un error en la carga del archivo, por favor dar aviso al adminstrado de la página");
            }
        } catch (IOException ex) {
            Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.mensajeError("Ah ocurrido un error en la carga del archivo, por favor dar aviso al adminstrado de la página");
        }
    }

    public void validarFormulario() {
        String error = "";

        if (titulo == null || titulo.trim().isEmpty()) {
            error += "Debe de ingresar un Titulo del Mensaje.<br/>";
        }
        if (mensaje == null || mensaje.trim().isEmpty()) {
            error += "Debe de ingresar el Mensaje a enviar.<br/>";
        }

        /*if (mensaje.length() > 4000 && mensaje.contains("data:image")) {
            error += "La imagen es muy grande por favor, reduzca el peso de la imagen.<br/>";
        }*/
        if (file == null) {
            error += "Debe de seleccionar un archivo con la lista de correos a enviar.<br/>";
        }
        if (error.isEmpty()) {
            PrimeFaces.current().executeScript("onClick('btnSend');");
        } else {
            JsfUtil.mensajeError("<br/>" + error);
        }
    }

    public void enviarCorreos() {
        procesoFacade.enviarCorreos(pathArchivo, titulo, mensaje, mailSession, transport, remitente, password, server, port);
        JsfUtil.mensajeInformacion("El proceso de envio de correos se realizara en background.");
        limpiarFormato();
    }

    private void limpiarFormato() {
        titulo = "";
        mensaje = "";
        mensaje = "";
        pathArchivo = "";
        reemplazarArchivo();
    }

    public void enviarProcesoPendiente() {
        EnvioMasivo em = persistenciaFacade.findEnvio(idEnvio);
        procesoFacade.envioPendiente(remitente, password, em.getTitulo(), em.getMensaje(), persistenciaFacade.findDetalleEnvio(idEnvio), transport, mailSession, server, port);
        JsfUtil.mensajeInformacion("El proceso de envio de correos se realizara en background.");
    }

    private Boolean validarArchivo(InputStream input) throws IOException {
        Workbook wb = WorkbookFactory.create(input);
        Sheet sheet = wb.getSheetAt(0);
        Row row = sheet.getRow(0);
        Cell cellCorreo = row.getCell(0);

        try {
            return cellCorreo.getStringCellValue().toUpperCase().equals("CORREO");
        } catch (Exception e) {
            return false;
        }

    }

    public void reemplazarArchivo() {
        file = null;
        showUploadFile = true;
    }

    public void logout() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            context.getExternalContext().getSessionMap().clear();
            ExternalContext externalContext = context.getExternalContext();
            externalContext.redirect(((ServletContext) externalContext.getContext()).getContextPath() + "/index.mined");
            System.gc();
        } catch (IOException ex) {
            Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

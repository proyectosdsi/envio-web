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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.jboss.ejb3.annotation.TransactionTimeout;
import sv.gob.mined.envio.model.DetalleEnvio;
import sv.gob.mined.envio.web.EnvioView;
import sv.gob.mined.utils.jsf.JsfUtil;

/**
 *
 * @author MISanchez
 */
@Stateless
@LocalBean
public class EnvioFacade {

    @EJB
    public RegistrosFacade registrosFacade;

    @Asynchronous
    public void enviarCorreos(String pathArchivo, String correoRemitente, String idDominioCorreo, String titulo, String mensaje,
            Session mailSession, Transport transport, String remitente, String password, BigDecimal idEnvio) {

        //envio(correoRemitente, password, titulo, mensaje, registrosFacade.findDetalleEnvio(idEnvio), transport, mailSession);
    }

    @Asynchronous
    public void enviarCorreosPendientes(BigDecimal idEnvio, Session mailSession, Transport transport, String password) {
//        EnvioMasivo envioMasivo = registrosFacade.findEnvio(idEnvio);
//
//        List<BigDecimal> lstDetalle;
//        try {
//            lstDetalle = envio(envioMasivo.getCorreRemitente(), password, envioMasivo.getTitulo(), envioMasivo.getMensaje(),
//                    registrosFacade.findDetalleEnvio(idEnvio), transport, mailSession);
//            registrosFacade.actualizarDetalleEnviado(lstDetalle);
//            
//            System.out.println("final");
//        } catch (Exception e) {
//            System.out.println("error superior");
//            e.printStackTrace();
//        }
    }
    
    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 120)
    private List<BigDecimal> envio(String remitente, String password, String titulo, String mensaje, List<DetalleEnvio> lstDetalle, Transport transport, Session mailSession) {
        Integer cont = 1;
        List<BigDecimal> correosEnviados = new ArrayList<>();
        try {
            if (transport.isConnected()) {

            } else {
                transport.connect();
            }

            try {
                Address from = new InternetAddress(remitente);

                for (DetalleEnvio detalleEnvio : lstDetalle) {
                    try {

                        String msjTemp = mensaje.replace(":DOCENTE:", detalleEnvio.getNombreDestinatario().
                                concat(" - ").
                                concat(detalleEnvio.getNip() == null ? "" : detalleEnvio.getNip()));

                        MimeMessage message = new MimeMessage(mailSession);

                        message.setFrom(from);

                        InternetAddress[] address = {new InternetAddress(detalleEnvio.getCorreoDestinatario())};
                        message.setRecipients(Message.RecipientType.TO, address);
                        message.setRecipients(Message.RecipientType.BCC, "miguel.sanchez@admin.mined.edu.sv");

                        BodyPart messageBodyPart1 = new MimeBodyPart();

                        messageBodyPart1.setContent(msjTemp, "text/html; charset=utf-8");

                        Multipart multipart = new MimeMultipart();
                        multipart.addBodyPart(messageBodyPart1);

                        message.setContent(multipart);
                        message.setSubject(titulo, "UTF-8");

                        message.saveChanges();

                        transport.sendMessage(message, message.getAllRecipients());
                        //Thread.sleep(new Long("800"));
                        System.out.println(cont + " enviado: " + detalleEnvio.getCorreoDestinatario());

                        correosEnviados.add(detalleEnvio.getIdDetalle());
                        cont++;
                    } catch (AddressException ex) {
                        System.out.println("Error 1");
                        if (transport.isConnected()) {
                            transport.close();

                            transport = mailSession.getTransport("smtp");
                            transport.connect("smtp.office365.com", Integer.parseInt("587"), remitente, password);
                        }
                        Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MessagingException ex) {
                        System.out.println("Error 2");
                        if (transport.isConnected()) {
                            transport.close();

                            transport = mailSession.getTransport("smtp");
                            transport.connect("smtp.office365.com", Integer.parseInt("587"), remitente, password);
                        } else {
                            transport = mailSession.getTransport("smtp");
                            transport.connect("smtp.office365.com", Integer.parseInt("587"), remitente, password);
                        }
                        Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                JsfUtil.mensajeInformacion("Ha finalizado el proceso de envio de correos.");
            } catch (AddressException e) {
                e.printStackTrace();
                JsfUtil.mensajeError("Ah ocurrido un error en el envio de correos.");
            }
            transport.close();
        } catch (NoSuchProviderException ex) {
            Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EnvioView.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            
            //registrosFacade.actualizarDetalleEnviado(correosEnviados);
            
            return correosEnviados;
        }
    }
}

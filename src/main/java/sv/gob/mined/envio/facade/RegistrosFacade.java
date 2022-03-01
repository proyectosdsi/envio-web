/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.gob.mined.envio.facade;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import sv.gob.mined.envio.model.DetalleEnvio;

/**
 *
 * @author MISanchez
 */
@Stateless
@LocalBean
public class RegistrosFacade {

    

    

    public void correoEnviado(DetalleEnvio detalleEnvio) {
        detalleEnvio.setEnviado((short) 1);
        //em.merge(detalleEnvio);
    }

    

    

}

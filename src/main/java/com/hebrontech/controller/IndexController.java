
package com.hebrontech.controller;

import com.hebrontech.ejb.UsuarioFacadeLocal;
import com.hebrontech.model.Usuario;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author Briceno
 */

@Named
@ViewScoped
public class IndexController implements Serializable {
    
    @EJB
    private UsuarioFacadeLocal EJBUsuario;
    private Usuario usuario;
    
    @PostConstruct
    public void init(){
        usuario = new Usuario();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public String iniciarSesion(){
        Usuario us;
        String redireccion = null;
        try {
            us = EJBUsuario.iniciarSesion(usuario);
            if (us != null) {
                redireccion = "/protegido/principal?faces-redirect=true";
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso", "Credenciales incorrectas"));
            }
        }catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Aviso", "Se ha presentado un error"));
        }
        return redireccion;
    }
    
}

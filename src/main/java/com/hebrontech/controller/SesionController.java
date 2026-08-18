package com.hebrontech.controller;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Briceno
 */

@Named
@SessionScoped
public class SesionController implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer codigoUsuario;
    private String usuario;
    private String tipo;

    public void autenticar(Integer codigoUsuario, String usuario, String tipo) {
        this.codigoUsuario = codigoUsuario;
        this.usuario = usuario;
        this.tipo = tipo;
    }

    public boolean isAutenticado() {
        return codigoUsuario != null;
    }

    public String cerrarSesion() {
        ExternalContext externalContext =
                FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session =
                (HttpSession) externalContext.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "/index?faces-redirect=true";
    }

    public Integer getCodigoUsuario() {
        return codigoUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getTipo() {
        return tipo;
    }
}

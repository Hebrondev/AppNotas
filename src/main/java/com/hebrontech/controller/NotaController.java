package com.hebrontech.controller;

import com.hebrontech.ejb.CategoriaFacadeLocal;
import com.hebrontech.ejb.NotaFacadeLocal;
import com.hebrontech.model.Categoria;
import com.hebrontech.model.Nota;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ViewScoped
public class NotaController implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private NotaFacadeLocal notaEJB;

    @EJB
    private CategoriaFacadeLocal categoriaEJB;

    @Inject
    private SesionController sesionController;

    private Nota nota;
    private Integer codigoCategoria;
    private List<Nota> notas;
    private List<Categoria> categorias;

    @PostConstruct
    public void init() {
        limpiar();
        cargar();
    }

    public void guardar() {
        try {
            int codigoPersona = codigoPersonaAutenticada();

            if (nota.getCodigo() == 0) {
                notaEJB.createForPersona(codigoPersona, codigoCategoria,
                        nota.getEncabezado(), nota.getCuerpo());
            } else if (!notaEJB.editOwned(nota.getCodigo(), codigoPersona,
                    codigoCategoria, nota.getEncabezado(), nota.getCuerpo())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "La nota no existe o no pertenece al usuario");
                return;
            }

            mensaje(FacesMessage.SEVERITY_INFO, "Nota guardada");
            limpiar();
            cargar();
        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No fue posible guardar la nota");
        }
    }

    public void seleccionar(Nota seleccionada) {
        Nota propia = notaEJB.findOwned(seleccionada.getCodigo(),
                codigoPersonaAutenticada());

        if (propia == null) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "La nota no existe o no pertenece al usuario");
            return;
        }

        nota = propia;
        codigoCategoria = propia.getCategoria().getCodigo();
    }

    public void eliminar(Nota seleccionada) {
        try {
            if (!notaEJB.removeOwned(seleccionada.getCodigo(),
                    codigoPersonaAutenticada())) {
                mensaje(FacesMessage.SEVERITY_ERROR,
                        "La nota no existe o no pertenece al usuario");
                return;
            }

            mensaje(FacesMessage.SEVERITY_INFO, "Nota eliminada");
            limpiar();
            cargar();
        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No fue posible eliminar la nota");
        }
    }

    public void cancelar() {
        limpiar();
    }

    public Nota getNota() {
        return nota;
    }

    public Integer getCodigoCategoria() {
        return codigoCategoria;
    }

    public void setCodigoCategoria(Integer codigoCategoria) {
        this.codigoCategoria = codigoCategoria;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    private void cargar() {
        categorias = categoriaEJB.findActivas();

        if (sesionController.isAutenticado()) {
            notas = notaEJB.findByPersona(codigoPersonaAutenticada());
        } else {
            notas = new ArrayList<>();
        }
    }

    private void limpiar() {
        nota = new Nota();
        codigoCategoria = null;
    }

    private int codigoPersonaAutenticada() {
        Integer codigo = sesionController.getCodigoUsuario();

        if (codigo == null) {
            throw new IllegalStateException("Sesión no autenticada");
        }

        return codigo;
    }

    private void mensaje(FacesMessage.Severity severity, String detalle) {
        FacesContext.getCurrentInstance().addMessage(
                null, new FacesMessage(severity, "Aviso", detalle));
    }
}


package com.hebrontech.controller;

import com.hebrontech.ejb.CategoriaFacadeLocal;
import com.hebrontech.model.Categoria;
import java.io.Serializable;
import java.util.List;
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

//Con estas anotaciones el framework JSF reconoce esta Clase
@Named
@ViewScoped
public class CategoriaController implements Serializable{
    
    //Inyectamos el EJB
    @EJB
    private CategoriaFacadeLocal categoriaEJB;
    private Categoria categoria;
    private List<Categoria> categorias;

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }
    
    
    @PostConstruct
    public void init(){
        limpiar();
        cargar();
    }

    public void guardar(){
        try {
            if (categoria.getCodigo() == 0) {
                categoriaEJB.create(categoria);
            } else {
                categoriaEJB.edit(categoria);
            }

            mensaje(FacesMessage.SEVERITY_INFO, "Categoría guardada");
            limpiar();
            cargar();
        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No fue posible guardar la categoría");
        }
    }

    public void seleccionar(Categoria seleccionada) {
        categoria = categoriaEJB.find(seleccionada.getCodigo());
    }

    public void cambiarEstado(Categoria seleccionada) {
        try {
            Categoria actual = categoriaEJB.find(seleccionada.getCodigo());
            actual.setEstado(!actual.isEstado());
            categoriaEJB.edit(actual);
            limpiar();
            cargar();
            mensaje(FacesMessage.SEVERITY_INFO,
                    "Estado de categoría actualizado");
        } catch (Exception e) {
            mensaje(FacesMessage.SEVERITY_ERROR,
                    "No fue posible cambiar el estado");
        }
    }

    public void cancelar() {
        limpiar();
    }

    private void cargar() {
        categorias = categoriaEJB.findAll();
    }

    private void limpiar() {
        categoria = new Categoria();
    }

    private void mensaje(FacesMessage.Severity severity, String detalle) {
        FacesContext.getCurrentInstance().addMessage(
                null, new FacesMessage(severity, "Aviso", detalle));
    }
}

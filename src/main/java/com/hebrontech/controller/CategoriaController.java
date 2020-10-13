
package com.hebrontech.controller;

import com.hebrontech.ejb.CategoriaFacadeLocal;
import com.hebrontech.model.Categoria;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    
    @PostConstruct
    public void init(){
        categoria = new Categoria();
        
    }
    
    public void registrar(){
        try {
            categoriaEJB.create(categoria);
            
        } catch (Exception e) {
            //Mensaje prime grwol
        }
    }
    
}

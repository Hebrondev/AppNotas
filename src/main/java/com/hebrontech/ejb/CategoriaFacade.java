
package com.hebrontech.ejb;

import com.hebrontech.model.Categoria;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Briceno
 */
@Stateless
public class CategoriaFacade extends AbstractFacade<Categoria> implements CategoriaFacadeLocal {

    @PersistenceContext(unitName = "notaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategoriaFacade() {
        super(Categoria.class);
    }

    @Override
    public List<Categoria> findActivas() {
        return em.createQuery(
                "SELECT c FROM Categoria c WHERE c.estado = true "
                + "ORDER BY c.nombre",
                Categoria.class)
                .getResultList();
    }
    
}

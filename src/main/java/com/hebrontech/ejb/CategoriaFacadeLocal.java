
package com.hebrontech.ejb;

import com.hebrontech.model.Categoria;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Briceno
 */
@Local
public interface CategoriaFacadeLocal {

    void create(Categoria categoria);

    void edit(Categoria categoria);

    void remove(Categoria categoria);

    Categoria find(Object id);

    List<Categoria> findAll();

    List<Categoria> findRange(int[] range);

    int count();
    
}

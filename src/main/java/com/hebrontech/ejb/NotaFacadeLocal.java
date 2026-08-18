package com.hebrontech.ejb;

import com.hebrontech.model.Nota;
import java.util.List;
import javax.ejb.Local;

@Local
public interface NotaFacadeLocal {

    List<Nota> findByPersona(int codigoPersona);

    Nota findOwned(int codigoNota, int codigoPersona);

    void createForPersona(int codigoPersona, int codigoCategoria,
            String encabezado, String cuerpo);

    boolean editOwned(int codigoNota, int codigoPersona,
            int codigoCategoria, String encabezado, String cuerpo);

    boolean removeOwned(int codigoNota, int codigoPersona);
}

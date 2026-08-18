
package com.hebrontech.ejb;

import com.hebrontech.model.Usuario;
import com.hebrontech.security.ClaveUtil;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Briceno
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> implements UsuarioFacadeLocal {

    @PersistenceContext(unitName = "notaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    @Override
    public Usuario iniciarSesion(Usuario us){
        Usuario usuario = null;
        String consulta;
        try {
            consulta = "FROM Usuario u WHERE u.usuario = ?1 and u.estado = ?2";
            Query query = em.createQuery(consulta);
            query.setParameter(1, us.getUsuario());
            query.setParameter(2, (short) 1);
            
            List<Usuario> lista = query.getResultList();
            for (Usuario candidato : lista) {
                String claveAlmacenada = candidato.getClave();
                boolean hash = ClaveUtil.esHash(claveAlmacenada);
                boolean coincide = hash
                        ? ClaveUtil.verificar(us.getClave(), claveAlmacenada)
                        : verificarLegacy(us.getClave(), claveAlmacenada);

                if (coincide) {
                    if (!hash) {
                        candidato.setClave(ClaveUtil.hash(us.getClave()));
                    }
                    usuario = candidato;
                    break;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        return usuario;
    }

    private boolean verificarLegacy(String ingresada, String almacenada) {
        if (ingresada == null || almacenada == null) {
            return false;
        }

        byte[] ingresadaBytes = ingresada.getBytes(StandardCharsets.UTF_8);
        byte[] almacenadaBytes = almacenada.getBytes(StandardCharsets.UTF_8);

        try {
            return MessageDigest.isEqual(ingresadaBytes, almacenadaBytes);
        } finally {
            Arrays.fill(ingresadaBytes, (byte) 0);
            Arrays.fill(almacenadaBytes, (byte) 0);
        }
    }
    
}

package com.hebrontech.ejb;

import com.hebrontech.model.Categoria;
import com.hebrontech.model.Nota;
import com.hebrontech.model.Persona;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class NotaFacade extends AbstractFacade<Nota>
        implements NotaFacadeLocal {

    @PersistenceContext(unitName = "notaPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public NotaFacade() {
        super(Nota.class);
    }

    @Override
    public List<Nota> findByPersona(int codigoPersona) {
        return em.createQuery(
                "SELECT n FROM Nota n "
                + "WHERE n.persona.codigo = :codigoPersona "
                + "ORDER BY n.fecha DESC, n.codigo DESC",
                Nota.class)
                .setParameter("codigoPersona", codigoPersona)
                .getResultList();
    }

    @Override
    public Nota findOwned(int codigoNota, int codigoPersona) {
        List<Nota> resultados = em.createQuery(
                "SELECT n FROM Nota n "
                + "WHERE n.codigo = :codigoNota "
                + "AND n.persona.codigo = :codigoPersona",
                Nota.class)
                .setParameter("codigoNota", codigoNota)
                .setParameter("codigoPersona", codigoPersona)
                .setMaxResults(1)
                .getResultList();

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public void createForPersona(int codigoPersona, int codigoCategoria,
            String encabezado, String cuerpo) {
        Persona persona = em.find(Persona.class, codigoPersona);
        Categoria categoria = findCategoriaActiva(codigoCategoria);

        if (persona == null) {
            throw new IllegalArgumentException("Usuario no válido");
        }

        Nota nota = new Nota();
        nota.setPersona(persona);
        nota.setCategoria(categoria);
        nota.setEncabezado(encabezado);
        nota.setCuerpo(cuerpo);
        nota.setFecha(new Date());
        em.persist(nota);
    }

    @Override
    public boolean editOwned(int codigoNota, int codigoPersona,
            int codigoCategoria, String encabezado, String cuerpo) {
        Nota nota = findOwned(codigoNota, codigoPersona);

        if (nota == null) {
            return false;
        }

        nota.setCategoria(findCategoriaActiva(codigoCategoria));
        nota.setEncabezado(encabezado);
        nota.setCuerpo(cuerpo);
        return true;
    }

    @Override
    public boolean removeOwned(int codigoNota, int codigoPersona) {
        Nota nota = findOwned(codigoNota, codigoPersona);

        if (nota == null) {
            return false;
        }

        em.remove(nota);
        return true;
    }

    private Categoria findCategoriaActiva(int codigoCategoria) {
        Categoria categoria = em.find(Categoria.class, codigoCategoria);

        if (categoria == null || !categoria.isEstado()) {
            throw new IllegalArgumentException("Categoría no válida");
        }

        return categoria;
    }
}

package org.webp;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UnitEjb {

    @PersistenceContext
    private EntityManager em;

    public Unit getUnit(long id) {

        return em.find(Unit.class, id);
    }

}
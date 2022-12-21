package org.webp;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class DepartmentEjb {

    @PersistenceContext
    private EntityManager em;

    public Long createDepartment(String name) {

        Department department = new Department();
        department.setName(name);

        em.persist(department);
        return department.getId();
    }

    public Long createUnit(long parentId, String name) {

        Department department = em.find(Department.class,parentId);
        if(department == null){
            throw new IllegalArgumentException("Department not found with "+parentId+" id does not exist");
        }
        Unit unit = new Unit();
        unit.setName(name);
        unit.setParent(department);
        em.persist(unit);

        department.getUnits().add(unit);

        return unit.getId();
    }

    public List<Department> getAllDepartments(boolean withUnit) {

        TypedQuery<Department> query = em.createQuery("select d from Department d", Department.class);
        List<Department> departments = query.getResultList();

        if(withUnit) {
            departments.forEach(department -> department.getUnits().size());
        }

        return departments;
    }

    public Unit getUnit(long id) {

        return em.find(Unit.class, id);
    }

    public Department getDepartment(long id,boolean withUnit) {

        Department department = em.find(Department.class, id);
        if (withUnit && department != null) {
            department.getUnits().size();
        }

        return department;
    }

}
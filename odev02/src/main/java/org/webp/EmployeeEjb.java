package org.webp;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Stateless
public class EmployeeEjb {

    @PersistenceContext
    private EntityManager em;

    public long createEmployee(long unitId,String name,String lastName,int birthYear,int salary){

        Unit unit = em.find(Unit.class,unitId);

        if(unit == null){
            throw new IllegalArgumentException("Unit not found. Unit "+unitId+" does not exist");
        }

        Employee employee = new Employee();
        employee.setUnit(unit);
        employee.setName(name);
        employee.setLastName(lastName);
        employee.setBirthYear(birthYear);
        employee.setSalary(salary);

        em.persist(employee);

        return employee.getId();
    }

    public List<Employee> getEmployees(){

        TypedQuery<Employee> query = em.createQuery("select e from Employee e", Employee.class);

        return query.getResultList();
    }

    public Employee getEmployee(long id){

        return em.find(Employee.class, id);

    }





}
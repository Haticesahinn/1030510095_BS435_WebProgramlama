package org.webp;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InsertTest {

    private EntityManagerFactory factory;
    private EntityManager em;

    @BeforeEach
    public void init() {
        //her bir test calismadan once BeforeEach calistirilir
        factory = Persistence.createEntityManagerFactory("Hibernate");
        em = factory.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        //her bir test calistiktan sonra BeforeEach calistirilir

        em.close();
        factory.close();
    }

    private boolean persistInATransaction(Object... obj) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            for(Object o:obj) {
                em.persist(o);
            }
            tx.commit();
        } catch (Exception e) {
            System.out.println("FAILED TRANSACTION: " + e.toString());
            tx.rollback();
            return false;
        }

        return true;
    }
    @Test
    public void testQuiz(){

        Employee employee = new Employee();
        employee.setName("Hatice");
        employee.setBirthYear(2000);
        employee.setLastName("Şahin");
        employee.setSalary(5500);

        boolean persisted = persistInATransaction(employee);
        //missing Unit class
        assertFalse(persisted);
    }

    @Test
    public void testEmployeeWithUnit(){

        Department department = new Department();
        department.setName("Finansal Yönetim");

        Unit unit = new Unit();
        unit.setName("Muhasebe");

        department.getUnits().add(unit);
        unit.setParent(department);

        Employee employee = new Employee();
        employee.setName("Hatice");
        employee.setBirthYear(2000);
        employee.setLastName("Sahin");
        employee.setSalary(5500);

        employee.setUnit(unit);

        assertTrue(persistInATransaction(department,unit,employee));
    }
    @Test
    public void testTooLongName(){

        String name = new String(new char[150]);

        Department department = new Department();
        department.setName(name);

        assertFalse(persistInATransaction(department));

        department.setId(null);
        department.setName("Arge");

        assertTrue(persistInATransaction(department));
    }
    @Test
    public void testUniqueName(){

        String name = "Arge";

        Department department = new Department();
        department.setName(name);

        assertTrue(persistInATransaction(department));

        Department anotherDepartment = new Department();
        anotherDepartment.setName(name);

        assertFalse(persistInATransaction(anotherDepartment));
    }


    private Unit addUnit(Department department, String unitName){
        Unit unit = new Unit();
        unit.setName(unitName);

        department.getUnits().add(unit);
        unit.setParent(department);

        return unit;
    }

    private Employee createEmployee(Unit unit, String name){

        Employee employee = new Employee();
        employee.setName(name);
        employee.setBirthYear(2000);
        employee.setLastName("Sahin");
        employee.setSalary(5500);

        employee.setUnit(unit);

        return employee;
    }

    @Test
    public void testQueries(){

        Department financialManagement = new Department();
        financialManagement.setName("Financial Management");

        Unit budget = addUnit(financialManagement, "budget");
        Unit accounting = addUnit(financialManagement, "accounting");
        Unit purchasing = addUnit(financialManagement, "purchasing");


        assertTrue(persistInATransaction(financialManagement, budget, accounting, purchasing));


        Employee a = createEmployee(budget,"ahmet");
        Employee b = createEmployee(budget,"betul");
        Employee c = createEmployee(accounting,"ceyda");
        Employee d = createEmployee(purchasing,"deniz");

        assertTrue(persistInATransaction(a,b,c,d));


        TypedQuery<Employee> queryBudget = em.createQuery(
                "select e from Employee e where e.unit.name='budget'",Employee.class);
        List<Employee> employeeBudge = queryBudget.getResultList();
        assertEquals(2, employeeBudge.size());
        assertTrue(employeeBudge.stream().anyMatch(e -> e.getName().equals("ahmet")));
        assertTrue(employeeBudge.stream().anyMatch(e -> e.getName().equals("betul")));

        TypedQuery<Employee> queryFinancialManagement = em.createQuery(
                "select e from Employee e where e.unit.parent.name='Financial Management'",Employee.class);
        List<Employee> all = queryFinancialManagement.getResultList();
        assertEquals(4, all.size());
    }


}

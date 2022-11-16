import entities.Address;
import entities.Employee;
import entities.Project;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private BufferedReader reader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }


    @Override
    public void run() {
        while (true) {
            System.out.println("Input exercise number(2-13):");

            try {
                int exerciseNumber = Integer.parseInt(reader.readLine());
                switch (exerciseNumber) {
                    case 2 -> changeCasing();
                    case 3 -> containsEmployee();
                    case 4 -> employeesWithSalaryOver50000();
                    case 5 -> employeesFromDepartment();
                    case 6 -> addingNewAddressAndUpdatingEmployee();
                    case 7 -> addressesWithEmployeeCount();
                    case 8 -> getEmployeeWithProject();
                    case 9 -> findLatest10Projects();
                    case 10 -> increaseSalaries();
                    case 11 -> findEmployeesByFirstName();
                    case 12 -> employeesWithMaximumSalaries();
                    case 13 -> removeTowns();
                    default -> System.out.println("Invalid exercise number! You should try again!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println();
            char ch = ' ';
            while (ch != 'Y') {
                System.out.println("Would you like to continue (Y/N)?");
                try {
                    ch = reader.readLine().toUpperCase().charAt(0);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (ch == 'N') {
                    System.out.println("GOODBYE! =^.^=");
                    break;
                }
            }
            if (ch == 'N') {
                break;
            }
        }
    }

    private void removeTowns() throws IOException {
        System.out.println("Input town name:");
        String townName = reader.readLine();
        Town town = entityManager.createQuery("SELECT t FROM Town t " +
                        "WHERE t.name = :t_name", Town.class)
                .setParameter("t_name", townName)
                .getSingleResult();

        int affectedRows = removeAddressesByTownId(town.getId());
        entityManager.getTransaction().begin();
        entityManager.remove(town);
        entityManager.getTransaction().commit();

        String str = affectedRows < 2 ? "address" : "addresses";
        System.out.printf("%d %s in %s deleted\n", affectedRows, str, town.getName());
    }

    private int removeAddressesByTownId(int townId) {
        List<Address> addresses = entityManager.createQuery("SELECT a FROM Address a " +
                        "WHERE a.town.id = :t_id", Address.class)
                .setParameter("t_id", townId)
                .getResultList();
        entityManager.getTransaction().begin();
        addresses.forEach(address -> {
            address.getEmployees().forEach(e -> e.setAddress(null));
            entityManager.remove(address);
        });
        entityManager.getTransaction().commit();
        return addresses.size();
    }

    private void employeesWithMaximumSalaries() {
        entityManager.createQuery("SELECT d, MAX(e.salary) AS max_salary FROM Department d " +
                        "JOIN Employee e ON d.id = e.department.id " +
                        "GROUP BY d.name " +
                        "HAVING MAX(e.salary) NOT BETWEEN 30000 AND 70000", Object[].class)
                .getResultList()
                .forEach(o -> System.out.println(o[0].toString() + " " + o[1]));
        }


    private void findEmployeesByFirstName() throws IOException {
        System.out.println("Enter first name start:");
        String start = reader.readLine();
        entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.firstName LIKE CONCAT(:name_start, '%') ", Employee.class)
                .setParameter("name_start", start)
                .getResultStream()
                .forEach(e -> System.out.printf("%s %s - %s - (%.2f)\n",
                        e.getFirstName(), e.getLastName(), e.getJobTitle(), e.getSalary()));
    }

    private void increaseSalaries() {
        entityManager.getTransaction().begin();
        entityManager.createQuery("UPDATE Employee e " +
                        "SET e.salary = e.salary * 1.12 " +
                        "WHERE e.department.id IN :ids ")
                .setParameter("ids", Set.of(1, 2, 4, 11))
                .executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.department.id IN :ids", Employee.class)
                .setParameter("ids", Set.of(1, 2, 4, 11))
                .getResultStream()
                .forEach(e -> System.out.printf("%s %s ($%.2f)\n", e.getFirstName(), e.getLastName(), e.getSalary()));
    }

    private void findLatest10Projects() {
        List<Project> projects = entityManager.createQuery("SELECT p FROM Project p " +
                        "ORDER BY p.startDate DESC ", Project.class)
                .setMaxResults(10)
                .getResultList();
        projects.stream().sorted(Comparator.comparing(Project::getName)).forEach(p -> System.out.printf("Project name: %s\nProject Description: %s\nProject Start Date: %s\nProject End Date: %s\n",
                p.getName(), p.getDescription(), p.getStartDate(), p.getEndDate()));
    }

    private void getEmployeeWithProject() throws IOException {
        System.out.println("Input employee id:");
        int id = Integer.parseInt(reader.readLine());
        Employee employee = entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.id = :e_id", Employee.class)
                .setParameter("e_id", id)
                .getSingleResult();
        System.out.printf("%s %s - %s\n", employee.getFirstName(), employee.getLastName(), employee.getJobTitle());
        employee.getProjects().stream().map(Project::getName).sorted().forEach(System.out::println);
    }

    private void addressesWithEmployeeCount() {
        List<Address> resultList = entityManager.createQuery("SELECT a FROM Address a " +
                        "ORDER BY a.employees.size DESC ", Address.class)
                .setMaxResults(10)
                .getResultList();
        resultList.forEach(el -> System.out.printf("%s, %s - %d employees\n",
                el.getText(),
                el.getTown() == null ? "Unknown" : el.getTown().getName(),
                el.getEmployees().size()));
    }

    private void addingNewAddressAndUpdatingEmployee() throws IOException {
        System.out.println("Input employee last name:");
        String lastName = reader.readLine();
        Employee employee = entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.lastName = :l_name", Employee.class)
                .setParameter("l_name", lastName)
                .getSingleResult();

        Address address = createAddress("Vitoshka 15");

        entityManager.getTransaction().begin();
        employee.setAddress(address);
        entityManager.getTransaction().commit();
    }

    private Address createAddress(String addressName) {
        Address address = new Address();
        address.setText(addressName);
        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();
        return address;
    }

    private void employeesFromDepartment() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.department.name = :d_name " +
                        "ORDER BY e.salary, e.id", Employee.class)
                .setParameter("d_name", "Research and Development")
                .getResultStream()
                .forEach(empl -> System.out.printf("%s %s from %s - $%.2f\n", empl.getFirstName(), empl.getLastName(), empl.getDepartment().getName(), empl.getSalary()));
    }

    private void employeesWithSalaryOver50000() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.salary > :min_salary", Employee.class)
                .setParameter("min_salary", BigDecimal.valueOf(50000L))
                .getResultStream()
                .map(Employee::getFirstName)
                .forEach(System.out::println);
    }

    private void containsEmployee() throws IOException {
        System.out.println("Input employee's full name:");
        String[] names = reader.readLine().split("\\s+");
        String firstName = names[0];
        String lastName = names[1];
        Long result = entityManager.createQuery("SELECT count(e) FROM Employee e " +
                        "WHERE e.firstName = :f_name AND e.lastName = :l_name", Long.class)
                .setParameter("f_name", firstName)
                .setParameter("l_name", lastName)
                .getSingleResult();
        System.out.println(result == 0 ? "No" : "Yes");
    }

    private void changeCasing() {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("UPDATE Town t " +
                "SET t.name = upper(t.name) " +
                "WHERE length(t.name) <= 5 ");
        System.out.println(query.executeUpdate());
        entityManager.getTransaction().commit();
    }

}

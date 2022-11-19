package hospital_db_04;

import hospital_db_04.entity.Diagnose;
import hospital_db_04.entity.Medicament;
import hospital_db_04.entity.Patient;
import hospital_db_04.entity.Visitation;
import hospital_db_04.exception.InvalidChoiceException;
import hospital_db_04.exception.InvalidPatientException;

import javax.persistence.EntityManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HospitalEngine implements Runnable {
    private final EntityManager entityManager;
    private final BufferedReader reader;

    public HospitalEngine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        while (true) {
            System.out.println("Possible actions:");
            System.out.println("1 -> register patient.");
            System.out.println("2 -> create new visit.");
            System.out.println("3 -> set new diagnose.");
            System.out.println("4 -> prescribe a medicine.");
            System.out.println("5 -> EXIT");
            System.out.println("Please insert action (1-5):");
            int choice = 0;
            try {
                choice = Integer.parseInt(reader.readLine());
            } catch (IOException e) {
                throw new InvalidChoiceException("Invalid choice!");
            }

            switch (choice) {
                case 1 -> {
                    try {
                        registerPatient();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 2 -> {
                    try {
                        addVisit();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 3 -> {
                    try {
                        setDiagnose();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 4 -> {
                    try {
                        prescribeMedicament();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> {
                    System.out.println("Your choice is not valid! Select 1-5!");
                }
            }
        }
    }

    private void prescribeMedicament() throws IOException {
        Patient patient = getPatientById();
        System.out.println("Enter medicament name:");
        String medicamentName = reader.readLine();
        Medicament medicament = getMedicamentByName(medicamentName);
        if (medicament == null) {
            System.out.printf("Medicament %s in not added in the database. New medicament will be added.\n", medicamentName);
            medicament = new Medicament();
            medicament.setName(medicamentName);
            entityManager.persist(medicament);
        }
        patient.addMedicament(medicament);
        entityManager.persist(patient);
        entityManager.getTransaction().commit();
    }

    private void setDiagnose() throws IOException {
        Patient patient = getPatientById();
        System.out.println("Enter diagnose name:");
        String diagnoseName = reader.readLine();
        Diagnose diagnose = getDiagnoseByName(diagnoseName);
        if (diagnose == null) {
            System.out.printf("Diagnose %s in not added in the database. New diagnose will be added.\n", diagnoseName);
            diagnose = new Diagnose();
            diagnose.setName(diagnoseName);
            entityManager.persist(diagnose);
        }
        System.out.println("Would you like to add comment (Y/N)?");
        String choice = reader.readLine().toUpperCase();
        if (choice.equals("Y")) {
            System.out.println("Enter your comment:");
            String comment = reader.readLine();
            diagnose.setComments(comment);
        }
        patient.addDiagnose(diagnose);
        entityManager.persist(patient);
        entityManager.getTransaction().commit();
    }

    private void addVisit() throws IOException {
        Patient patient = getPatientById();
        System.out.println("Please, enter visitation date in format dd-mm-yyyy:");
        String input = reader.readLine();
        Visitation visitation = new Visitation();
        LocalDate visitationDate = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        visitation.setDate(visitationDate);
        System.out.println("Would you like to add comment (Y/N)?");
        String choice = reader.readLine().toUpperCase();
        if (choice.equals("Y")) {
            System.out.println("Enter your comment:");
            String comment = reader.readLine();
            visitation.setComments(comment);
        }
        patient.addVisitation(visitation);
        entityManager.persist(visitation);
        entityManager.persist(patient);
        entityManager.getTransaction().commit();
    }

    private void registerPatient() throws IOException {
        System.out.println("Please enter patient data in format [first name, last name, date of birth (dd-mm-yyyy), has medical insurance (true/false)]");
        String[] tokens = reader.readLine().split(",\\s+");
        //firstName, String lastName, LocalDate dateOfBirth, boolean hasMedicalInsurance
        Patient patient = new Patient(tokens[0], tokens[1], LocalDate.parse(tokens[2], DateTimeFormatter.ofPattern("dd-MM-yyyy")), tokens[3].equals("true"));
        entityManager.getTransaction().begin();
        entityManager.persist(patient);
        entityManager.getTransaction().commit();
        System.out.printf("Successfully added patient %s %s\n", tokens[0], tokens[1]);
    }

    private Patient getPatientById() throws IOException {
        System.out.println("Please enter visited patient id:");
        long id = Long.parseLong(reader.readLine());
        entityManager.getTransaction().begin();
        Patient patient = entityManager.createQuery("SELECT p FROM Patient p WHERE p.id = :p_id", Patient.class)
                .setParameter("p_id", id)
                .getSingleResult();
        if (patient == null) {
            throw new InvalidPatientException("There is no patient with id " + id);
        }
        return patient;
    }

    private Diagnose getDiagnoseByName(String name) {
       return entityManager.createQuery("SELECT d FROM Diagnose d WHERE d.name = :d_name", Diagnose.class)
                .setParameter("d_name", name)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

    }
    private Medicament getMedicamentByName(String name) {
        return entityManager
                .createQuery("SELECT m FROM Medicament m WHERE m.name = :m_name", Medicament.class)
                .setParameter("m_name", name)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }
}

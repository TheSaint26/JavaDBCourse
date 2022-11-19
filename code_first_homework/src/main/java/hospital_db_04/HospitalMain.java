package hospital_db_04;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class HospitalMain {
    public static void main(String[] args) {
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("code_first_home")
                .createEntityManager();
        HospitalEngine engine = new HospitalEngine(entityManager);
        engine.run();
    }
}

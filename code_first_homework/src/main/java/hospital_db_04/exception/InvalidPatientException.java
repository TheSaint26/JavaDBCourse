package hospital_db_04.exception;

public class InvalidPatientException extends RuntimeException {
    public InvalidPatientException(String msg) {
        super(msg);
    }
}

package practice.chat.domain.user.exception;

public class EmailCertificationMismatchException extends IllegalArgumentException{
    public EmailCertificationMismatchException() {
    }

    public EmailCertificationMismatchException(String s) {
        super(s);
    }
}

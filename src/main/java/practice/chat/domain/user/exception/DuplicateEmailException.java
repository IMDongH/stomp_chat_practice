package practice.chat.domain.user.exception;

public class DuplicateEmailException extends IllegalArgumentException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException() {
    }
}

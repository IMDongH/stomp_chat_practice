package practice.chat.domain.user.exception;

public class InvalidEmailException extends IllegalArgumentException{
    public InvalidEmailException() {
    }

    public InvalidEmailException(String s) {
        super(s);
    }
}

package practice.chat.domain.user.exception;

public class DuplicatePhoneException extends IllegalArgumentException{
    public DuplicatePhoneException() {
    }

    public DuplicatePhoneException(String s) {
        super(s);
    }
}

package practice.chat.domain.user.exception;

public class MemberNotFoundException extends IllegalArgumentException{

    public MemberNotFoundException() {
    }

    public MemberNotFoundException(String message) {
        super(message);
    }
}

package practice.chat.domain.user.exception;

public class UnauthenticatedMemberException extends RuntimeException{

    public UnauthenticatedMemberException(String message) {
        super(message);
    }
}
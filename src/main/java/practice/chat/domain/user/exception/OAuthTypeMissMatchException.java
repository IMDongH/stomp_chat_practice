package practice.chat.domain.user.exception;

public class OAuthTypeMissMatchException extends RuntimeException{
    public OAuthTypeMissMatchException() {
    }

    public OAuthTypeMissMatchException(String message) {
        super(message);
    }
}

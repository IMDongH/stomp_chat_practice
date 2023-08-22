package practice.chat.domain.user.exception;

public class InvalidRefreshTokenException extends IllegalArgumentException{
    public InvalidRefreshTokenException() {
    }

    public InvalidRefreshTokenException(String s) {
        super(s);
    }
}

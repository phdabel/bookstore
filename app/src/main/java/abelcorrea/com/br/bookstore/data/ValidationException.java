package abelcorrea.com.br.bookstore.data;

public class ValidationException extends IllegalArgumentException {

    public ValidationException(String message) {
        super(message);
    }
}

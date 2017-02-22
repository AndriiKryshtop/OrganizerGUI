package ua.sumdu.j2se.kryshtop.tasks.model.exceptions;

public class InvalidTaskIndexException extends ArrayIndexOutOfBoundsException {
    public InvalidTaskIndexException() {
        super("Wrong index");
    }
}

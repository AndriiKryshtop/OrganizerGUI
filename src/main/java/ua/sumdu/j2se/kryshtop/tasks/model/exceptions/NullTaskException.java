package ua.sumdu.j2se.kryshtop.tasks.model.exceptions;

public class NullTaskException extends NullPointerException {
    public NullTaskException() {
        super("Task == null");
    }
}

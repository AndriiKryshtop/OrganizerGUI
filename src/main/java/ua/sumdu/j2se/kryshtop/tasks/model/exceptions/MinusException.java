package ua.sumdu.j2se.kryshtop.tasks.model.exceptions;


public class MinusException extends Exception{
    public MinusException() {
        super("You have entered a wrong start, end or interval");
    }
}

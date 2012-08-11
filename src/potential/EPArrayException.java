package potential;

public class EPArrayException extends Exception {
    public EPArrayException() { super(); }
    public EPArrayException(String msg) { super(msg); }
}

class InvalidFileSyntaxEPArrayException extends EPArrayException {
    public InvalidFileSyntaxEPArrayException() { super("Invalid EP file syntax"); }
}

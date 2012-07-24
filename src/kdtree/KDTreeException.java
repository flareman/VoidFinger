package kdtree;

public class KDTreeException extends Exception {
    public KDTreeException() { super(); }
    public KDTreeException(String msg) { super(msg); }
}

class KDTreeInvalidCreationArgumentException extends KDTreeException {
    public KDTreeInvalidCreationArgumentException() {
        super("Wrong argument during kd-tree creation");
    }
}

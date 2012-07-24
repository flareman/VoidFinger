package kdtree;

public class KDTreeCellException extends Exception {
    public KDTreeCellException() { super(); }
    public KDTreeCellException(String msg) { super(msg); }
}

class KDTreeCellWrongCreationArgumentException extends KDTreeCellException {
    public KDTreeCellWrongCreationArgumentException() {
        super("Wrong argument during kd-tree cell creation");
    }
}

class KDTreeCellWrongPointCoordinatesException extends KDTreeCellException {
    public KDTreeCellWrongPointCoordinatesException() {
        super("The points of the kd-tree must have the same number of dimensions as the tree");
    }
}
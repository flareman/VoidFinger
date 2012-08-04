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

class KDTreeCellInvalidCellTypeException extends KDTreeCellException {
    public KDTreeCellInvalidCellTypeException() {
        super("Method called does not apply to given kd-tree cell type");
    }
}

class KDTreeCellWrongPointCoordinatesException extends KDTreeCellException {
    public KDTreeCellWrongPointCoordinatesException() {
        super("The points of the kd-tree must have the same number of dimensions as the tree");
    }
}

class KDTreeCellInvalidMethodArgumentException extends KDTreeCellException {
    public KDTreeCellInvalidMethodArgumentException() {
        super("Method called with invalid argument for kd-tree cell");
    }
}

class CoordinateComparatorException extends KDTreeCellException {
    public CoordinateComparatorException() {
        super("Negative dimension value passed to comparator constructor");
    }
}

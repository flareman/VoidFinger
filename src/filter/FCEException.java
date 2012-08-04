package filter;

public class FCEException extends Exception {
    public FCEException() { super(); }
    public FCEException(String msg) { super(msg); }
}

class FCEInvalidMethodArgumentException extends FCEException {
    public FCEInvalidMethodArgumentException() {
        super("Filter clustering enging method called with invalid argument");
    }
}

class CenterDistanceComparatorException extends FCEException {
    public CenterDistanceComparatorException() {
        super("Point to compare cluster centers to cannot be null");
    }
}

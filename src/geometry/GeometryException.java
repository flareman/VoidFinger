package geometry;

public class GeometryException extends Exception {
    public GeometryException() { super(); }
    public GeometryException(String str) { super("GeometryException: "+str); }
}

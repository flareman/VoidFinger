package geometry;

public class Plane {
    static public final int POINT_ONPLANE = 0;
    static public final int POINT_INFRONT = 1;
    static public final int POINT_BEHIND = 2;

    private Point origin;
    private Vector normal;
    
    public Plane(Point p, Vector n) throws GeometryException {
        if (p == null || n == null) throw new GeometryException("Plane point and normal vector can't be null");
        if (p.getDimensions() != n.getDimensions())
            throw new GeometryException("Plane normal vector and origin point must have same dimensionality");
        this.origin = p;
        this.normal = n;
    }
    
    public Point getOrigin() { return this.origin; }
    public Vector getNormal() { return this.normal; }
    public Integer getDimensions() { return this.origin.getDimensions(); }
    
    public int compareToPlane(Point p) throws GeometryException {
        Float d = this.normal.getProjection(new Vector(this.origin, p));
        if (d == 0) return POINT_ONPLANE;
        else if (d > 0) return POINT_INFRONT;
        else return POINT_BEHIND;
    }
    
    public Boolean intersect(Vector v) throws GeometryException {
        return (this.normal.getProjection(v) == 0)?false:true;
    }
    
    public Float distanceFromPoint(Point p) throws GeometryException {
        return this.normal.getProjection(new Vector(this.origin, p));
    }
}

package geometry;

public class BoundingBox {
    static public final int BB_INTERSECTS = 0;
    static public final int BB_INFRONT = 1;
    static public final int BB_BEHIND = 2;
    private Point min, max;
    
    public BoundingBox(Point a, Point b) throws GeometryException {
        if (a == null || b == null) throw new GeometryException("Cornerpoints for bounding boxes cannot be null");
        if (a.getDimensions() != b.getDimensions())
            throw new GeometryException("Cornerpoints for bounding boxes must have same dimensionality");
        for (int i = 0; i < a.getDimensions(); i++)
            if (a.getCoordinate(i) > b.getCoordinate(i))
                    throw new GeometryException("Minimum bounding box point is not minimum");
        this.min = a;
        this.max = b;
    }
    
    public Boolean pointInBox(Point p) throws GeometryException {
        for (int i = 0; i < this.getDimensions(); i++)
            if (p.getCoordinate(i) < this.min.getCoordinate(i) || p.getCoordinate(i) > this.max.getCoordinate(i)) return false;
        return true;
    }
    
    public Point getMinPoint() { return this.min; }
    public Point getMaxPoint() { return this.max; }
    public Integer getDimensions() { return this.min.getDimensions(); }

    public Point getCenter() {
        Float[] center = new Float[this.getDimensions()];
        Point result = null;
        try {
            for (int i = 0; i < this.getDimensions(); i++)
                center[i] = (this.min.getCoordinate(i) + this.max.getCoordinate(i))/2;
            result = new Point(center);
        } catch (GeometryException ge) {}
        return result;
    }
    
    public BoundingBox transposedBox(Vector v) throws GeometryException {
        return new BoundingBox(this.min.transposedPoint(v.getCoords()), this.max.transposedPoint(v.getCoords()));
    }
    
    public Boolean intersectWithRay(Vector r, Point p, Boolean infinite) throws GeometryException {
        if (r.getDimensions() != this.getDimensions() || p.getDimensions() != this.getDimensions())
            throw new GeometryException("Ray vector and origin dimensionality must match that of bounding box");
        
        if (infinite) return (this.intersectWithRay(r, p, false) || this.intersectWithRay(r.inverseVector(), p, false));
        
        Float[] rfCoords = r.unitVector().getCoords();
        for (int i = 0; i < r.getDimensions(); i++)
            rfCoords[i] = 1.0f / rfCoords[i];

        Float tmin = 0.0f;
        Float tmax = 0.0f;
        for (int i = 0; i < this.getDimensions(); i++) {
            Float t1 = (this.min.getCoordinate(i) - p.getCoordinate(i))*rfCoords[i];
            Float t2 = (this.max.getCoordinate(i) - p.getCoordinate(i))*rfCoords[i];
            Float imin = Math.min(t1, t2);
            Float imax = Math.max(t1, t2);
            if (i == 0) {
                tmin = imin;
                tmax = imax;
            } else {
                tmin = Math.max(tmin, imin);
                tmax = Math.min(tmax, imax);
            }
        }

        if (tmax < 0) return false;
        if (tmin > tmax) return false;
        return true;
    }
    
    public int intersectWithPlane(Plane pl) throws GeometryException {
        Float[] pCoords = this.min.getCoords();
        Float[] nCoords = this.max.getCoords();
        Float[] normalCoords = pl.getNormal().getCoords();
        
        for (int i = 0; i < this.getDimensions(); i++)
            if (normalCoords[i] >= 0) {
                Float temp = nCoords[i];
                nCoords[i] = pCoords[i];
                pCoords[i] = temp;
            }

        int result = BB_INFRONT;

        if (pl.distanceFromPoint(new Point(pCoords)) < 0) result = BB_BEHIND;
        else if (pl.distanceFromPoint(new Point(nCoords)) < 0) result = BB_INTERSECTS;
        return result;
    }
    
}

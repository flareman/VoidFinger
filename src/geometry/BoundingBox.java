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
        if (r.getDimensions() != p.getDimensions() || r.getDimensions() < 3)
            throw new GeometryException("Ray vector and origin dimensionality must match and be at least 3");
        if (infinite) return (this.intersectWithRay(r, p, false) || this.intersectWithRay(r.inverseVector(), p, false));

        Float t1, t2, tmp, tnear, tfar, invDir, invMag, dirx, diry, dirz;
	Float dirLen = r.getMeasure();
	if (dirLen == 0.0)
	    return this.pointInBox(p);
	invMag = 1.0f/dirLen;
	dirx = r.getCoordinate(0)*invMag;
	diry = r.getCoordinate(1)*invMag;
	dirz = r.getCoordinate(2)*invMag;

	tnear = -Float.MAX_VALUE;
	tfar = Float.MAX_VALUE;

	if (dirx == 0.0)
            if (p.getCoordinate(0) < this.min.getCoordinate(0) || p.getCoordinate(0) > this.max.getCoordinate(0))
                return false;
	else {
	    invDir = 1.0f/dirx;
	    t1 = (this.min.getCoordinate(0)-p.getCoordinate(0))*invDir;
	    t2 = (this.max.getCoordinate(0)-p.getCoordinate(0))*invDir;
            tfar = Math.max(t1, t2);
            tnear = Math.min(t1, t2);
	    if (tfar < 0.0f)
                return false;
	}

	if (diry == 0.0)
	    if (p.getCoordinate(1) < this.min.getCoordinate(1) || p.getCoordinate(1) > this.max.getCoordinate(1))
                return false;
	else {
	    invDir = 1.0f/diry;
	    t1 = (this.min.getCoordinate(1)-p.getCoordinate(1))*invDir;
	    t2 = (this.max.getCoordinate(1)-p.getCoordinate(1))*invDir;
            tmp = Math.min(t1, t2);
            t1 = tmp;
            t2 = Math.max(t1, t2);
	    if (t1 > tnear) tnear = t1;
	    if (t2 < tfar) tfar  = t2;

	    if( (tfar < 0.0) || (tnear > tfar))
			return false;
	}

	if (dirz == 0.0) 
	    if (p.getCoordinate(2) < this.min.getCoordinate(2) || p.getCoordinate(2) > this.max.getCoordinate(2))
                return false;
	else {
	    invDir = 1.0f/dirz;
	    t1 = (this.min.getCoordinate(2)-p.getCoordinate(2))*invDir;
	    t2 = (this.max.getCoordinate(2)-p.getCoordinate(2))*invDir;
            tmp = Math.min(t1, t2);
            t1 = tmp;
            t2 = Math.max(t1, t2);
	    if (t1 > tnear) tnear = t1;
	    if (t2 < tfar) tfar  = t2;

	    if ((tfar < 0.0) || (tnear > tfar))
                return false;
	}

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

        if (pl.distanceFromPoint(new Point(pCoords)) <= 0) result = BB_BEHIND;
        else if (pl.distanceFromPoint(new Point(nCoords)) <= 0) result = BB_INTERSECTS;
        return result;
    }
    
}

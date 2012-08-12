package geometry;

public class Vector {
    private Point endpoint;
    
    public Vector(Float d) {
        this.endpoint = new Point(d);
    }

    public Vector(Float x, Float y) {
        this.endpoint = new Point(x, y);
    }

    public Vector(Float x, Float y, Float z) {
        this.endpoint = new Point(x, y, z);
    }

    public Vector(Point start, Point end) throws GeometryException {
        Vector v1 = new Vector(start);
        Vector v2 = new Vector(end);
        v2.subtract(v1);
        this.endpoint = v2.getPoint();
    }

    public Vector(Point start, Point end, int retain) throws GeometryException {
        Vector v1 = new Vector(start);
        Vector v2 = new Vector(end);
        v2.subtract(v1);
        this.endpoint = v2.getPoint(retain);
    }

    public Vector(Point p) {
        this.endpoint = new Point(p);
    }

    public Vector(Point p, int retain) {
        this.endpoint = new Point(p);
    }

    public Vector(Float[] coords) throws GeometryException {
        this.endpoint = new Point(coords);
    }
    
    public Float[] getCoords() {
        return this.endpoint.getCoords();
    }
    
    public Float getCoordinate(Integer dimension) throws GeometryException {
        return this.endpoint.getCoordinate(dimension);
    }
    
    public Point getPoint() { return this.endpoint; }
    public Point getPoint(int retain) throws GeometryException {
        return this.endpoint.reducedDimensionsPoint(retain);
    }
    public Integer getDimensions() { return this.endpoint.getDimensions(); }

    public Vector inverseVector() {
        return new Vector(this.endpoint.symmetricPoint());
    }

    public void add(Vector v) throws GeometryException {
        this.endpoint = this.endpoint.transposedPoint(v.getCoords());
    }
    
    public void subtract(Vector v) throws GeometryException {
        this.endpoint = this.endpoint.transposedPoint(v.inverseVector().getCoords());
    }
    
    public Float getMeasure() {
        Float result = 0.0f;
        try {
            result = this.endpoint.euclideanDistanceFrom(Point.VOLUME_ZERO);
        } catch (GeometryException ge) {}
        return result;
    }
    
    public void scale(Float factor) {
        this.endpoint.scaledPoint(factor);
    }
    
    public Vector unitVector() {
        Vector result = null;
        try {
            result = new Vector(this.endpoint.scaledPoint(1/this.getMeasure()).getCoords());
        } catch (GeometryException ge) {}
        return result;
    }
    
    public Float getProjection(Vector v) throws GeometryException {
        if (v.getDimensions() < 3 && this.getDimensions() > 3 || v.getDimensions() > 3 && this.getDimensions() < 3 ||
                (v.getDimensions() < 3 && this.getDimensions() < 3 && this.getDimensions() != v.getDimensions()))
            throw new GeometryException("Vector projections require vectors of same dimensionality");
        Float dp = 0.0f;
        for (int i = 0; i < ((this.getDimensions() > 3)?3:this.getDimensions()); i++)
            dp += v.getCoordinate(i)*this.getCoordinate(i);
        return dp/this.getMeasure();
    }
    
    public Float distanceFromPoint(Point p) throws GeometryException {
        Vector n = this.unitVector();
        Vector pv = new Vector(p).inverseVector();
        n.scale(this.getProjection(pv));
        pv.subtract(n);
        return pv.getMeasure();
    }
}

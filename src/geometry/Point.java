package geometry;

import java.util.Arrays;

public class Point {
    static public Float EPSILON = 0.00000001f;
    static public Point LINE_ZERO = new Point(0.0f);
    static public Point PLANE_ZERO = new Point(0.0f, 0.0f);
    static public Point VOLUME_ZERO = new Point(0.0f, 0.0f, 0.0f);
    private Float[] coords;
    private Integer dimensions;

    static public Point zeroPoint(Integer dimensions) throws GeometryException {
        if (dimensions <= 0) throw new GeometryException("Zero coordinate points must be constructed for positive count of dimensions");
        Float[] temp = new Float[dimensions];
        Arrays.fill(temp, 0.0f);
        return new Point(temp);
    }
    
    public Point(Float[] coords) throws GeometryException {
        if (coords == null || coords.length == 0) throw new GeometryException("Invalid point arguments passed to constructor");
        this.coords = Arrays.copyOf(coords, coords.length);
        this.dimensions = coords.length;
    }
    
    public Point(Point p) {
        this.dimensions = p.getDimensions();
        this.coords = Arrays.copyOf(p.getCoords(), this.dimensions);
    }

    public Point(Float d) {
        this.coords = new Float[1];
        this.dimensions = 1;
        this.coords[0] = d;
    }

    public Point(Float x, Float y) {
        this.coords = new Float[2];
        this.dimensions = 2;
        this.coords[0] = x;
        this.coords[1] = y;
    }

    public Point(Float x, Float y, Float z) {
        this.coords = new Float[3];
        this.dimensions = 3;
        this.coords[0] = x;
        this.coords[1] = y;
        this.coords[2] = z;
    }

    public Float minkowskiDistanceFrom(Point p, Integer norm) throws GeometryException {
        if (norm <= 0) throw new GeometryException("p must be positive in p-norm distance");
        if (p == null) throw new GeometryException("Point to count distance to was null");
        if (p.getDimensions() != this.dimensions) throw new GeometryException("Point dimensions not matching");
        Float sum = 0.0f;
        for (int i = 0; i < this.dimensions; i++)
            sum += new Float(Math.pow(Math.abs(p.getCoords()[i].doubleValue() - this.coords[i].doubleValue()), norm));
        return new Float(Math.pow(sum, 1.0f/norm));
    }
    
    public Float euclideanDistanceFrom(Point p) throws GeometryException {
        if (p == null) throw new GeometryException("Point to count distance to was null");
        if (p.getDimensions() != this.dimensions) throw new GeometryException("Point dimensions not matching");
        Float sum = 0.0f;
        for (int i = 0; i < ((this.dimensions > 3)?3:this.dimensions); i++)
            sum += (p.getCoordinate(i) - this.coords[i])*(p.getCoordinate(i) - this.coords[i]);
        Float dist = new Float(Math.sqrt(sum));
        return dist;
    }

    public Float[] getCoords() {
        return Arrays.copyOf(this.coords, this.coords.length);
    }
    
    public Float getCoordinate(Integer dimension) throws GeometryException {
        if (dimension < 0 || dimension >= this.dimensions) throw new GeometryException("Point does not have requested dimension");
        return this.coords[dimension];
    }
    
    public Integer getDimensions() { return this.dimensions; }
    
    public Point midpointFromPoint(Point p) throws GeometryException {
        if ((p.getDimensions() < 3 && this.getDimensions() >= 3) || 
                (p.getDimensions() >= 3 && this.getDimensions() < 3))
            throw new GeometryException("Midpoint can be calculated only for same-dimensionality points");
        Float[] temp = new Float[(this.coords.length > 3)?3:this.coords.length];
        for (int i = 0; i < temp.length; i++)
            temp[i] = (this.coords[i] + p.getCoordinate(i)) / 2;
        return new Point(temp);
    }
    
    public Point transposedPoint(Float[] transpositionMatrix) throws GeometryException {
        if (transpositionMatrix.length != this.dimensions) throw new GeometryException("Point is "+this.dimensions+"-dimensional, matrix is "+transpositionMatrix.length+"-dimensional");
        Float[] newCoords = Arrays.copyOf(this.coords, this.dimensions);
        for (int i = 0; i < this.dimensions; i++)
            newCoords[i] += transpositionMatrix[i];
        return new Point(newCoords);
    }
    
    public Point transposedPoint(Point p) throws GeometryException {
        if (p.getDimensions() != this.dimensions) throw new GeometryException("Both points must have same dimensionality");
        Float[] newCoords = Arrays.copyOf(this.coords, this.dimensions);
        for (int i = 0; i < this.dimensions; i++)
            newCoords[i] += p.getCoordinate(i);
        return new Point(newCoords);
    }

    public Point symmetricPoint() {
        Float[] newCoords = Arrays.copyOf(this.coords, this.dimensions);
        for (int i = 0; i < this.dimensions; i++)
            newCoords[i] *= -1.0f;
        Point result = null;
        try {
            result = new Point(newCoords);
        } catch (GeometryException ge) {}
        return result;
    }
    
    public Point scaledPoint(Float factor) {
        Float[] newCoords = Arrays.copyOf(this.coords, this.dimensions);
        for (int i = 0; i < this.dimensions; i++)
            newCoords[i] *= factor;
        Point result = null;
        try {
            result = new Point(newCoords);
        } catch (GeometryException ge) {}
        return result;
    }

    public Point scaledPoint(Float[] scaleMatrix) throws GeometryException {
        if (scaleMatrix.length != this.dimensions) throw new GeometryException("Scaling matrix must have same dimensionality as the point to be scaled");
        Float[] newCoords = Arrays.copyOf(this.coords, this.dimensions);
        for (int i = 0; i < this.dimensions; i++)
            newCoords[i] *= scaleMatrix[i];
        return new Point(newCoords);
    }
    
    public Point reducedDimensionsPoint(int retain) throws GeometryException {
        if (retain < 0 || retain > this.dimensions)
            throw new GeometryException("Cannot retain more dimensions than those originally available");
        Float[] newCoords = Arrays.copyOf(this.coords, retain);
        return new Point(newCoords);
    }
}


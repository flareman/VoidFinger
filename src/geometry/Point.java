package geometry;

import java.util.Arrays;

public class Point {
    static public Point AXIS_ZERO = new Point(0.0f, 0.0f, 0.0f);
    private Float[] coords;
    
    public Point(Float[] coords) throws GeometryException {
        if (coords.length != 3) throw new GeometryException("Points are three-dimensional");
        else this.coords = Arrays.copyOf(coords, coords.length);
    }
    
    public Point(Point p) {
        this.coords = Arrays.copyOf(p.getCoords(), p.getCoords().length);
    }

    public Point(Float x, Float y, Float z) {
        this.coords = new Float[3];
        this.coords[0] = x;
        this.coords[1] = x;
        this.coords[2] = x;
    }
    
    public Float distanceFrom(Point p) throws GeometryException {
        if (p == null) throw new GeometryException("Point to count distance to was null");
        Float sum = 0.0f;
        for (int i = 0; i < 3; i++)
            sum += new Float(Math.pow(p.getCoords()[i].doubleValue() - this.coords[i].doubleValue(), 2));
        return new Float(Math.sqrt(sum));
    }
    
    public Float[] getCoords() {
        return Arrays.copyOf(this.coords, this.coords.length);
    }
    
    public Point transposedPoint(Float x, Float y, Float z) {
        return new Point(this.coords[0]+x, this.coords[1]+x, this.coords[2]+x);
    }
    
    public Point transposedPoint(Float[] transpositionMatrix) throws GeometryException {
        if (transpositionMatrix.length != this.coords.length) throw new GeometryException("Points are three-dimensional");
        Float[] newCoords = Arrays.copyOf(this.coords, this.coords.length);
        for (int i = 0; i < newCoords.length; i++)
            newCoords[i] += transpositionMatrix[i];
        return new Point(newCoords);
    }
    
    public Point symmetricPoint() {
        Float[] newCoords = Arrays.copyOf(this.coords, this.coords.length);
        for (int i = 0; i < newCoords.length; i++)
            newCoords[i] *= -1.0f;
        Point result = null;
        try {
            result = new Point(newCoords);
        } catch (GeometryException ge) {}
        return result;
    }
    
    public Point scaledPoint(Float factor) {
        Float[] newCoords = Arrays.copyOf(this.coords, this.coords.length);
        for (int i = 0; i < newCoords.length; i++)
            newCoords[i] *= factor;
        Point result = null;
        try {
            result = new Point(newCoords);
        } catch (GeometryException ge) {}
        return result;
    }
}

package visibilityGraph;

import geometry.GeometryException;
import geometry.Point;

public class GraphNode {
    private Point point;
    
    public GraphNode(Point dims) { point = dims; }
    public int getDimensions() { return this.point.getDimensions(); }
    public Float getCoordinate(int coord) throws GeometryException { return point.getCoordinate(coord); }
    public Point getPoint() { return this.point; }
}

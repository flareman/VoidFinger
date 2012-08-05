package visibilityGraph;

public class GraphException extends Exception {
    public GraphException(String str) { super(str); }
}

class EmptyNodeSetGraphException extends GraphException {
    public EmptyNodeSetGraphException() { super("The node set provided for graph construction is empty."); }
}

class ZeroProjectedClustersGraphException extends GraphException {
    public ZeroProjectedClustersGraphException() { super("No octree leaf nodes intersect with ray."); }
}

class InvalidCreationArgumentDimensionsGraphException extends GraphException {
    public InvalidCreationArgumentDimensionsGraphException() { super("Both point list and octree passed as arguments to the graph constructor must be of same dimensionality"); }
}

class InvalidEdgeCreationArgumentsGraphException extends GraphException {
    public InvalidEdgeCreationArgumentsGraphException(String str) { super("Invalid argument passed to edge constructor: "+str); }
}
package visibilityGraph;

public class GraphEdgeException extends Exception{
    public GraphEdgeException(String str) { super(str); }
}

class InvalidNodeException extends GraphEdgeException{
    public InvalidNodeException(){super("The nodes provided as connected to the edge are invalid.");}
}

class NegativeWeightException extends GraphEdgeException{
    public NegativeWeightException(){super("Invalid Weight. The weight provided during construction must not be negative.");}
}

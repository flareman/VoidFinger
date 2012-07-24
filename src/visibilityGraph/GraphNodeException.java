package visibilityGraph;


public class GraphNodeException extends Exception{
    public GraphNodeException(String str) { super(str); }
}

class InvalidNumberOfDimensionsException extends GraphNodeException{
    public InvalidNumberOfDimensionsException(){super("Invalid size for dimensions array provided during node construction");}
}

class InvalidDimensionRequestException extends GraphNodeException{
    public InvalidDimensionRequestException() {super("Invalid Request. The dimension number requested is out of bounds");}
}

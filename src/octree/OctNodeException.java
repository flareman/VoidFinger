package octree;

public class OctNodeException extends Exception {
    public OctNodeException(String str) { super(str); }
}

class InvalidOctNodeTypeException extends OctNodeException {
    public InvalidOctNodeTypeException() { super("OctNode type does not support the requested operation."); }
}

class InvalidElementIDOctNodeException extends OctNodeException {
    public InvalidElementIDOctNodeException() { super("Invalid octree element ID requested."); }
}

class InvalidArgumentOctNodeException extends OctNodeException {
    public InvalidArgumentOctNodeException() { super("Invalid argument passed to OctNode member method."); }
}

class UnrecognizedOctNodeTypeException extends OctNodeException {
    public UnrecognizedOctNodeTypeException() { super("Unrecognized OctNode type."); }
}

class InvalidOctNodeCreationParameterException extends OctNodeException {
    public InvalidOctNodeCreationParameterException() { super("Invalid parameter passed during the creation of an OctNode."); }
}

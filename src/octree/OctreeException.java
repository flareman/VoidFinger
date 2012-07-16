package octree;

public class OctreeException extends Exception {
    public OctreeException(String msg) { super(msg); }
}

class InvalidSOGFileSyntaxOctreeException extends OctreeException {
    public InvalidSOGFileSyntaxOctreeException() { super("Invalid syntax of .SOG file contents."); }
}

class InvalidCreationParametersOctreeException extends OctreeException {
    public InvalidCreationParametersOctreeException() { super("Invalid parameters of octree creation."); }
}

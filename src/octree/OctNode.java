package octree;

import geometry.BoundingBox;
import geometry.GeometryException;
import geometry.Point;
import java.util.ArrayList;
import java.util.Arrays;

public class OctNode {
    // Static class contants for value passing
    public static final int OCTNODE_INTERMEDIATE = 0;
    public static final int OCTNODE_EMPTY = 1;
    public static final int OCTNODE_LEAF = 2;
    
    // Private data members
    private Integer depth = 0;
    private int type = OCTNODE_EMPTY;
    private Boolean sign = false;
    private Boolean[] signs = new Boolean[8];
    private ArrayList<OctNode> children = new ArrayList<OctNode>();
    private Float length;
    private Point point, origin;

    // OctNode constructors
    public OctNode(Integer depth, OctNode children[], Float[] origin, Float length) throws OctNodeException {
        this.depth = depth;
        this.type = OCTNODE_INTERMEDIATE;
        if (children.length != 8) throw new InvalidOctNodeCreationParameterException();
        this.children.addAll(Arrays.asList(children));
        this.length = length;
        this.point = null;
        try {
            this.origin = new Point(Arrays.copyOf(origin, origin.length));
        } catch (GeometryException ge) { throw new InvalidOctNodeCreationParameterException(); }
    }

    public OctNode(Integer depth, Boolean sign, Float[] origin, Float length) throws OctNodeException {
        this.depth = depth;
        this.type = OCTNODE_EMPTY;
        this.sign = sign;
        this.length = length;
        this.point = null;
        try {
            this.origin = new Point(Arrays.copyOf(origin, origin.length));
        } catch (GeometryException ge) { throw new InvalidOctNodeCreationParameterException(); }
    }

    public OctNode(Integer depth, Boolean signs[], Float coords[], Float[] origin, Float length) throws OctNodeException {
        this.depth = depth;
        this.type = OCTNODE_LEAF;
        this.signs = Arrays.copyOf(signs, signs.length);
        this.length = length;
        try {
            this.origin = new Point(Arrays.copyOf(origin, origin.length));
            this.point = new Point(Arrays.copyOf(coords, coords.length));
            if (this.origin.getDimensions() != this.point.getDimensions()) throw new GeometryException();
        } catch (GeometryException ge) { throw new InvalidOctNodeCreationParameterException(); }
    }
    
    public OctNode(Integer depth, byte signs, Float coords[], Float[] origin, Float length) throws OctNodeException {
        this.depth = depth;
        this.type = OCTNODE_LEAF;
        byte temp = signs;
        for (int i = 0; i < 8; i++) {
            this.signs[i] = (temp % 2 == 0)?true:false;
            temp /= 2;
        }
        this.length = length;
        try {
            this.origin = new Point(Arrays.copyOf(origin, origin.length));
            this.point = new Point(Arrays.copyOf(coords, coords.length));
            if (this.origin.getDimensions() != this.point.getDimensions()) throw new GeometryException();
        } catch (GeometryException ge) { throw new InvalidOctNodeCreationParameterException(); }
    }

    // Getters
    public Boolean getSign() throws OctNodeException {
        switch (type) {
            case OCTNODE_EMPTY:
                return this.sign;
            case OCTNODE_INTERMEDIATE:
                throw new InvalidOctNodeTypeException();
            case OCTNODE_LEAF:
                int i = 0;
                for (Boolean b: this.signs)
                    if (b == true)
                        if (++i > 4)
                            return true;
                return false;
            default:
                throw new UnrecognizedOctNodeTypeException();
        }
    }

    public Boolean getSignForElementWithID(int id) throws OctNodeException {
        if (this.type != OCTNODE_LEAF) throw new InvalidOctNodeTypeException();
        if (id < 0 || id > 7) throw new InvalidElementIDOctNodeException();
        return this.signs[id];
    }
    
    public ArrayList<OctNode> getChildren() throws OctNodeException {
        if (this.type != OCTNODE_INTERMEDIATE) throw new InvalidOctNodeTypeException();
        return this.children;
    }
    
    public OctNode getChildWithID(int id) throws OctNodeException {
        if (this.type != OCTNODE_INTERMEDIATE) throw new InvalidOctNodeTypeException();
        if (id < 0 || id > 7) throw new InvalidElementIDOctNodeException();
        return this.children.get(id);
    }
    
    public Point getPoint() throws OctNodeException {
        if (this.type != OCTNODE_LEAF) throw new InvalidOctNodeTypeException();
        return this.point;
    }
    
    public Integer getDimensions() { return this.origin.getDimensions(); }
    public Point getOrigin() { return this.origin; }
    public Point getOriginAntipode() {
        Point result = null;
        Float[] temp = new Float[this.getDimensions()];
        Arrays.fill(temp, this.length);
        try {
            result = this.origin.transposedPoint(temp);
        } catch (GeometryException ge) {}
        return result;
    }
    
    public BoundingBox getBoundingBox() {
        BoundingBox bb = null;
        try {
            bb = new BoundingBox(this.origin, this.getOriginAntipode());
        } catch (GeometryException ge) {}
        return bb;
    }
    
    public int getNodeType() { return this.type; }

    public Float getLength() { return this.length; }
    public Integer getNodeDepth() { return this.depth; }
    
    public Integer getMaxDepth() throws OctNodeException {
        switch (type) {
            case OCTNODE_INTERMEDIATE:
                Integer result = this.depth;
                for (OctNode node: this.children) {
                    Integer temp = node.getMaxDepth();
                    if (temp > result) result = temp;
                }
                return result;
            case OCTNODE_LEAF:
            case OCTNODE_EMPTY:
                return this.depth;
            default:
                throw new UnrecognizedOctNodeTypeException();
        }
    }

    public ArrayList<Point> getAllVertices() throws OctNodeException {
        ArrayList<Point> result = new ArrayList<Point>();
        switch (type) {
            case OCTNODE_INTERMEDIATE:
                for (OctNode node: this.children)
                    result.addAll(node.getAllVertices());
                return result;
            case OCTNODE_LEAF:
                result.add(this.point);
                return result;
            case OCTNODE_EMPTY:
                return result;
            default:
                throw new UnrecognizedOctNodeTypeException();
        }
        
    }
}

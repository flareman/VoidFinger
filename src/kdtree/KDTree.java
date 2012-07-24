package kdtree;

import octree.OctNodeException;
import octree.Octree;

public class KDTree {
    private Integer dimensions;
    private KDTreeCell root;
    
    public KDTree(Integer dimensions, Float[] lengths) throws KDTreeException {
        if (dimensions <= 0)
            throw new KDTreeInvalidCreationArgumentException();
        if (lengths.length != dimensions)
            throw new KDTreeInvalidCreationArgumentException();
        for (Float i: lengths)
            if (i < 0)
                throw new KDTreeInvalidCreationArgumentException();
        try {
            this.dimensions = dimensions;
            this.root = new KDTreeCell(dimensions, lengths);
        } catch (KDTreeCellException kdtce) {
            this.root = null;
        }
    }
    
    public KDTree(Octree octree) throws KDTreeException {
        this.dimensions = octree.getDimensions();
        Float[] lengths = new Float[3];
        lengths[0] = octree.getLength();
        lengths[1] = lengths[0];
        lengths[2] = lengths[0];
        try {
            this.root = new KDTreeCell(dimensions, lengths);
            for (Float[] coords: octree.getAllVertices())
                this.root.addPoint(coords);
        } catch (KDTreeCellException kdtce) {
            this.root = null;
        } catch (OctNodeException kdtce) {
            this.root = null;
        }
    }

    public void addPoint(Float[] coords) throws KDTreeCellException { this.root.addPoint(coords); }
}

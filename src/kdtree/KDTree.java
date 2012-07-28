package kdtree;

import java.util.ArrayList;
import java.util.Collections;
import octree.OctNodeException;
import octree.Octree;

public class KDTree {
    private Integer dimensions;
    private KDTreeCell root;
    
    // This implementation of the class is feature-complete for constructing a
    // kd-tree from octree data only; however, we have included a generic constructor
    // as well, for future development. This constructor creates a simple kd-tree
    // containing only an empty leaf node
    public KDTree(Integer dimensions) throws KDTreeException {
        if (dimensions <= 0)
            throw new KDTreeInvalidCreationArgumentException();
        try {
            this.dimensions = dimensions;
            this.root = new KDTreeCell(dimensions, 0, null);
            System.out.println("Created an empty kd-tree");
        } catch (KDTreeCellException kdtce) {
            this.root = null;
        }
    }
    
    public KDTree(Octree octree) throws KDTreeException {
        this.dimensions = octree.getDimensions();
        try {
            // The points of the octree are duplicated in as many ArrayLists as there
            // are dimensions; every list is sorted by its corresponding point dimension.
            // This step has a complexity of O(knlogn) for n points and k dimensions, and
            // simplifies the remaining procedure of kd-tree construction massively, when
            // compared with the naive approach of selecting the median point for splitting
            // at each recursion step, while guaranteeing the creation of a quality, balanced
            // tree.
            ArrayList<ArrayList<Float[]>> pointArrays = new ArrayList<ArrayList<Float[]>>();
            ArrayList<Float[]> points = octree.getAllVertices();
            Collections.sort(points, new CoordinateComparator(0));
            pointArrays.add(points);
            for (int i = 1; i < dimensions; i++) {
                ArrayList<Float[]> temp = new ArrayList<Float[]>();
                temp.addAll(points);
                Collections.sort(temp, new CoordinateComparator(i));
                pointArrays.add(temp);
            }
            this.root = new KDTreeCell(dimensions, 0, pointArrays);
            System.out.println("Created a kd-tree with depth: " + this.root.getMaxDepth());
        } catch (CoordinateComparatorException cce) {
            this.root =  null;
            cce.printStackTrace();
        } catch (KDTreeCellException kdtce) {
            this.root = null;
            kdtce.printStackTrace();
        } catch (OctNodeException one) {
            this.root = null;
            one.printStackTrace();
        }
    }
    
    public Integer getMaxDepth() { return this.root.getMaxDepth(); }
}

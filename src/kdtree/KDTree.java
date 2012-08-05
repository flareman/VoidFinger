package kdtree;

import geometry.Point;
import java.util.ArrayList;
import java.util.Collections;
import octree.Octree;

public class KDTree {
    private Integer dimensions;
    private KDTreeCell root;
    private ArrayList<Point> points = new ArrayList<Point>();
    private Float length, threshold;
    
    // This implementation of the class is feature-complete for constructing a
    // kd-tree from octree data only; however, we have included a generic constructor
    // as well, for future development. This constructor creates a simple kd-tree
    // containing only an empty leaf node
    public KDTree(Integer dimensions) throws KDTreeException {
        if (dimensions <= 0)
            throw new KDTreeInvalidCreationArgumentException();
        try {
            this.dimensions = dimensions;
            this.length = 0.0f;
            this.threshold = 0.0f;
            this.root = new KDTreeCell(dimensions, 0, null, Point.VOLUME_ZERO, new Point(100.0f, 100.0f, 100.0f));
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
            ArrayList<ArrayList<Point>> pointArrays = new ArrayList<ArrayList<Point>>();
            this.points.addAll(octree.getAllVertices());
            Collections.sort(this.points, new CoordinateComparator(0));
            pointArrays.add(this.points);
            for (int i = 1; i < dimensions; i++) {
                ArrayList<Point> temp = new ArrayList<Point>();
                temp.addAll(this.points);
                Collections.sort(temp, new CoordinateComparator(i));
                pointArrays.add(temp);
            }
            Point min = octree.getOrigin();
            Point max = octree.getOriginAntipode();
            this.root = new KDTreeCell(dimensions, 0, pointArrays, min, max);
            this.length = octree.getLength();
            this.threshold = octree.getMinNodeLength();
            System.out.println("Created a kd-tree with depth " + this.getMaxDepth()+" and "+this.root.getPointCount()+" points.");
        } catch (CoordinateComparatorException cce) {
            this.root =  null;
            cce.printStackTrace();
        } catch (KDTreeCellException kdtce) {
            this.root = null;
            kdtce.printStackTrace();
        }
    }
    
    public Integer getMaxDepth() { return this.root.getMaxDepth(); }
    public ArrayList<Point> getAllPoints() { return this.points; }
    public KDTreeCell getRoot() { return this.root; }
    public Float getLength() { return this.length; }
    public Float getThreshold() { return this.threshold; }
}

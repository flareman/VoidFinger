package kdtree;

import geometry.GeometryException;
import geometry.Point;
import java.util.Comparator;

// Comparator class implementation for comparing and sorting Float[] objects by specific element ID
// This is used for sorting the vertex ArrayLists at the first step of kd-tree creation, and
// also for binary-finding the split point in the subsequent recursions
class CoordinateComparator implements Comparator<Point> {
    private Integer dimension;
    
    public CoordinateComparator(Integer dimension) throws CoordinateComparatorException {
        if (dimension < 0) throw new CoordinateComparatorException();
        else this.dimension = dimension;
    }
    
    @Override
    public int compare(Point left, Point right) {
        try {
            return (left.getCoordinate(this.dimension) > right.getCoordinate(this.dimension))?1:((left.getCoordinate(this.dimension) < right.getCoordinate(this.dimension))?-1:0);
        } catch (GeometryException ge) { return 0; }
    }
}

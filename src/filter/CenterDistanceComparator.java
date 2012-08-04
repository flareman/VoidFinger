package filter;

import geometry.GeometryException;
import geometry.Point;
import java.util.Comparator;

class CenterDistanceComparator implements Comparator<FCECenter> {
    private Point cellCenter;
    
    public CenterDistanceComparator(Point c) throws CenterDistanceComparatorException {
        if (c == null) throw new CenterDistanceComparatorException();
        this.cellCenter = c;
    }
    
    public int compare(FCECenter left, FCECenter right) {
        try {
            return (left.getCenter().euclideanDistanceFrom(this.cellCenter) > right.getCenter().euclideanDistanceFrom(this.cellCenter))?1:((left.getCenter().euclideanDistanceFrom(this.cellCenter) < right.getCenter().euclideanDistanceFrom(this.cellCenter))?-1:0);
        } catch (GeometryException ge) { return 0; }
    }
}

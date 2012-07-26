package kdtree;

import java.util.Comparator;

// Comparator class implementation for comparing and sorting Float[] objects by specific element ID
// This is used for sorting the vertex ArrayLists at the first step of kd-tree creation, and
// also for binary-finding the split point in the subsequent recursions
class CoordinateComparator implements Comparator<Float[]> {
    private Integer dimension;
    
    public CoordinateComparator(Integer dimension) throws CoordinateComparatorException {
        if (dimension < 0) throw new CoordinateComparatorException();
        else this.dimension = dimension;
    }
    
    public int compare(Float[] left, Float[] right) {
        return (left[this.dimension] > right[this.dimension])?1:((left[this.dimension] < right[this.dimension])?-1:0);
    }
}

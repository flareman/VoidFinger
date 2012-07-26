package kdtree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class KDTreeCell {
    private Integer splitDimension;
    private Integer dimensions;
    private Integer depth;
    private KDTreeCell[] children = null;
    private Float[] point = null;

    public KDTreeCell(Integer dimensions, Integer depth,
            ArrayList<ArrayList<Float[]>> points) throws KDTreeCellException {
        if (dimensions <= 0)
            throw new KDTreeCellWrongCreationArgumentException();
        if (depth < 0)
            throw new KDTreeCellWrongCreationArgumentException();
        if (points.size() != dimensions)
            throw new KDTreeCellWrongCreationArgumentException();
        this.dimensions = dimensions;
        this.splitDimension = depth % dimensions;
        this.depth = depth;
        if (points != null) {
            if (points.get(0).size() > 2) {
                ArrayList<ArrayList<Float[]>> leftPoints = new ArrayList<ArrayList<Float[]>>();
                ArrayList<ArrayList<Float[]>> rightPoints = new ArrayList<ArrayList<Float[]>>();
                ArrayList<Float[]> master = points.get(this.splitDimension);
                Integer medianID = master.size()/2;
                while (medianID >= 0 &&
                        master.get(medianID)[this.splitDimension] == master.get(medianID-1)[this.splitDimension] )
                    medianID--;
                Float[] median = master.get(medianID);
                this.point = new Float[this.dimensions];
                this.point = Arrays.copyOf(median, median.length);
                master.remove(median);
                for (int i = 0; i < this.dimensions; i++) {
                    ArrayList<Float[]> left = new ArrayList<Float[]>();
                    ArrayList<Float[]> right = new ArrayList<Float[]>();
                    if (i == this.splitDimension) {
                        left.addAll(master.subList(0, medianID));
                        right.addAll(master.subList(medianID, master.size()));
                    } else {
                        ArrayList<Float[]> slave = points.get(i);
                        for (Iterator<Float[]> it = slave.iterator(); it.hasNext();) {
                            Float[] v = it.next();
                            Integer j = -1;
                            Float[] foundMedian = null;
                            while (++j < this.dimensions) {
                                if (v[j] != median[j]) continue;
                                else if (j == this.dimensions-1)
                                    foundMedian = v;
                            }
                            if (foundMedian != null) {
                                it.remove();
                                continue;
                            } else if (v[this.splitDimension] < median[this.splitDimension])
                                left.add(v);
                            else right.add(v);
                        }
                    }
                    leftPoints.add(left);
                    rightPoints.add(right);
                }
                this.children = new KDTreeCell[2];
                this.children[0] = new KDTreeCell(dimensions, depth+1, leftPoints);
                this.children[1] = new KDTreeCell(dimensions, depth+1, rightPoints);
            } else switch (points.get(0).size()) {
                case 2:
                    ArrayList<ArrayList<Float[]>> leftPoints = new ArrayList<ArrayList<Float[]>>();
                    ArrayList<ArrayList<Float[]>> rightPoints = new ArrayList<ArrayList<Float[]>>();
                    for (int i = 0; i < this.dimensions; i++) {
                        ArrayList<Float[]> temp = new ArrayList<Float[]>();
                        temp.add(points.get(0).get(0));
                        leftPoints.add(temp);
                        rightPoints.add(temp);
                    }
                    this.children = new KDTreeCell[2];
                    this.children[0] = new KDTreeCell(dimensions, depth+1, leftPoints);
                    this.children[1] = new KDTreeCell(dimensions, depth+1, rightPoints);
                    break;
                case 1:
                    this.point = new Float[this.dimensions];
                    Float[] leafPoint = points.get(0).get(0);
                    this.point = Arrays.copyOf(leafPoint, leafPoint.length);
                    this.children = null;
                    break;
                case 0:
                    this.children = null;
                    this.point = null;
                    break;
            }
        } else {
            this.children = null;
            this.point = null;
        }
    }

    public Integer getMaxDepth() {
        if (this.children == null) return this.depth;
        else return Math.max(this.children[0].getMaxDepth(), this.children[1].getMaxDepth());
    }
    
    public Integer getDepth() { return this.depth; }
}

package filter;

import geometry.BoundingBox;
import geometry.GeometryException;
import geometry.Plane;
import geometry.Point;
import geometry.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import kdtree.KDTree;
import kdtree.KDTreeCell;
import kdtree.KDTreeCellException;

public class FilterClusterEngine {
    private KDTree kdtree;
    private Integer count = 0;
    private Integer repetitions = 0;
    private ArrayList<FCECenter> centers = new ArrayList<FCECenter>();
    
    public FilterClusterEngine(KDTree tree, Integer count) throws FCEException {
        if (tree == null)
            throw new FCEException("The clustering engine needs a non-null kd-tree to work on");
        if (count <= 0)
            throw new FCEException("The clusters must be positive");
        this.kdtree = tree;
        this.count = count;
    }
    
    private void generateRandomCenters() {
        this.centers.clear();
        ArrayList<Integer> newCenterIDs = new ArrayList<Integer>();
        ArrayList<Point> points = this.kdtree.getAllPoints();
        int i = this.count;
        while (i > 0) {
            Integer temp = new Double(Math.random()*points.size()).intValue();
            if (newCenterIDs.contains(temp)) continue;
            else newCenterIDs.add(temp);
            i--;
        }
        this.count = 0;
        try {
            for (Integer id: newCenterIDs)
                this.centers.add(new FCECenter(points.get(id)));
        } catch (FCEException fcee) {}
    }
    
    private void filter(KDTreeCell cell, ArrayList<FCECenter> candidates) {
        try {
            ArrayList<FCECenter> input = new ArrayList<FCECenter>();
            input.addAll(candidates);
            if (cell.isLeafNode() && !cell.isEmpty()) {
                Collections.sort(input, new CenterDistanceComparator(cell.getPoint()));
                input.get(0).addToCount(1);
                input.get(0).addToSum(cell.getPoint());
            } else {
                Collections.sort(input, new CenterDistanceComparator(cell.getCenter()));
                FCECenter closest = input.get(0);
                Iterator<FCECenter> it = input.iterator();
                it.next();
                while (it.hasNext()) {
                    FCECenter z = it.next();
                    Point midpoint = closest.getCenter().midpointFromPoint(z.getCenter());
                    Vector normal = new Vector(midpoint, closest.getCenter());
                    if (cell.getBoundingBox().intersectWithPlane(new Plane(midpoint, normal)) == BoundingBox.BB_INFRONT)
                        it.remove();
                }
                if (input.size() > 1) {
                    ArrayList<FCECenter> newCandidates = new ArrayList<FCECenter>();
                    newCandidates.addAll(input);
                    this.filter(cell.getChild(0), newCandidates);
                    this.filter(cell.getChild(1), input);
                } else {
                    closest.addToCount(cell.getPointCount());
                    closest.addToSum(cell.getSum());
                }
            }
        } catch (CenterDistanceComparatorException cdce) {
        } catch (GeometryException ge) {
        } catch (KDTreeCellException ge) {
        } catch (FCEException fcee) {}
    }
    
    private Boolean checkForConvergence(Integer cutoff) throws FCEException {
        if (cutoff < 0) throw new FCEInvalidMethodArgumentException();
        if (this.repetitions >= cutoff) return true;
        else return false;
    }
    
    private void updateCenters() {
        for (FCECenter c: this.centers)
            c.updateCenter();
    }
    
    public void performClustering(Integer cutoff) throws FCEException {
        this.generateRandomCenters();
        while (!this.checkForConvergence(cutoff)) {
            this.filter(this.kdtree.getRoot(), this.centers);
            this.repetitions++;
            this.updateCenters();
        }
    }
    
    public ArrayList<Point> getClusterCenters() {
        ArrayList<Point> result = new ArrayList<Point>();
        for (FCECenter c: this.centers)
            result.add(c.getCenter());
        return result;
    }
}

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
import potential.EPArray;

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
        } catch (KDTreeCellException kdtce) {
        } catch (FCEException fcee) {}
    }
    
    private Boolean checkForConvergence(Integer cutoff) throws FCEException {
        if (cutoff <= 0) throw new FCEInvalidMethodArgumentException();
        if (cutoff > 0 && this.repetitions >= cutoff) return true;
        for (FCECenter c: this.centers)
            if (c.hasConverged(this.kdtree.getThreshold()/100.0f))
                return true;
        return false;
    }
    
    private Boolean checkForConvergence() {
        Integer convergedCenters = 0;
        for (FCECenter c: this.centers) {
            if (c.hasConverged(this.kdtree.getThreshold()))
                convergedCenters++;
            if (convergedCenters >= this.centers.size() - 1) return true;
        }
        return false;
    }

    private void updateCenters() {
        for (FCECenter c: this.centers)
            c.updateCenter();
    }
    
    public int performClustering(Integer cutoff) throws FCEException {
        this.generateRandomCenters();
        while (!this.checkForConvergence(cutoff)) {
            this.filter(this.kdtree.getRoot(), this.centers);
            this.repetitions++;
            this.updateCenters();
        }
        return this.repetitions;
    }
    
    public int performClustering() {
        this.generateRandomCenters();
        while (!this.checkForConvergence()) {
            this.filter(this.kdtree.getRoot(), this.centers);
            this.repetitions++;
            this.updateCenters();
        }
        return this.repetitions;
    }

    public ArrayList<Point> getClusterCenters(EPArray potentials) {
        ArrayList<Point> result = new ArrayList<Point>();
        for (FCECenter c: this.centers)
            try {
                Float[] coords = new Float[4];
                coords[0] = c.getCenter().getCoordinate(0);
                coords[1] = c.getCenter().getCoordinate(1);
                coords[2] = c.getCenter().getCoordinate(2);
                coords[3] = potentials.getPotentialForCoordinates(c.getCenter());
                result.add(new Point(coords));
            } catch (GeometryException ge) {}
        return result;
    }
}

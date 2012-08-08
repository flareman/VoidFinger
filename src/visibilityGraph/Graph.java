package visibilityGraph;

import geometry.GeometryException;
import geometry.Point;
import geometry.Vector;
import java.util.ArrayList;
import java.util.Collections;
import octree.OctNode;
import octree.OctNodeException;
import octree.Octree;
import octree.OctreeException;

public class Graph {
    private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    public ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
    private Octree surface;
    private int numOfThreads;
    private ArrayList<Float> costs = new ArrayList<Float>();
    private final Object mutex = new Object();
    
    public Graph(ArrayList<Point> nds, Octree tree,int threads) throws GraphException {
        if (nds.isEmpty())
            throw new EmptyNodeSetGraphException();
        if (nds.get(0).getDimensions() != tree.getDimensions())
            throw new InvalidCreationArgumentDimensionsGraphException();
        for (Point p: nds) {
            nodes.add(new GraphNode(p));
        }
        surface = tree;
        numOfThreads = threads;
    }
    
    public int getNodeCount(){return this.nodes.size();}
    public int getDimensions() { return this.surface.getDimensions(); }
    public GraphNode getNode(int x) { return nodes.get(x); }
    public GraphEdge getEdge(int x){ return edges.get(x); }
    
    private void recurseGetOctreeLeafs(Point origin, Vector ray, ArrayList<Point> visible, OctNode root) throws GeometryException {
        if (root.getBoundingBox().intersectWithRay(ray, origin, false))
            try {
                if (root.getNodeType() == OctNode.OCTNODE_LEAF)
                    visible.add(root.getPoint());
                if (root.getNodeType() == OctNode.OCTNODE_INTERMEDIATE) {
                    ArrayList<OctNode> children = root.getChildren();
                    for (OctNode n: children)
                        recurseGetOctreeLeafs(origin, ray, visible, n);
                }
            } catch (OctNodeException one) {}
    }
    
    private ArrayList<Point> getOctreeLeafs(Point origin,Vector ray) throws GeometryException {
        ArrayList<Point> result = new ArrayList<Point>();
        recurseGetOctreeLeafs(origin, ray, result, this.surface.getRoot());
        return result;
    }
    
    public GraphEdge createEdgeForVisible(int i, int j) {
        ArrayList<Float> projections = new ArrayList<Float>();
        try {
            Vector ray = new Vector(this.nodes.get(i).getPoint(), this.nodes.get(j).getPoint());
            ArrayList<Point> visibleList = getOctreeLeafs(this.nodes.get(i).getPoint(), ray);
            for (Point p: visibleList) {
                Vector v = new Vector(p);
                projections.add(ray.getProjection(v));
            }
        } catch (GeometryException ge) {}
        if (projections.isEmpty()) return null;
        Collections.sort(projections);
        Boolean visible = false;
        int clusterCount = 1;
        float D = this.surface.getMinNodeLength();
        float distance;
        for (int k = 1; k < projections.size() && clusterCount < 3; k++) {
            distance = projections.get(k) - projections.get(k-1);
            if (distance > 1.5f*D)
                clusterCount++;
        }
        try {
            switch (clusterCount) {
                case 1: visible = true; break;
                case 2:
                    visible = (this.surface.getSignForPointInSpace(this.nodes.get(i).getPoint().midpointFromPoint(this.nodes.get(j).getPoint())));
                    break;
                case 3: visible = false; break;
                default: break;
            }

            if (visible)
                return new GraphEdge(i, j, this.nodes.get(i).getPoint().minkowskiDistanceFrom(this.nodes.get(j).getPoint(), 2));
            else return null;
        } catch (GeometryException ge) {
        } catch (GraphException gre) {
        } catch (OctreeException oe) {
        }
        return null;
    }
    
    public void buildVisibilityGraph() {
        VgraphCreationThread[] workers = new VgraphCreationThread[numOfThreads];
        for (int i = 0; i < this.numOfThreads; i++) {
            workers[i] = new VgraphCreationThread(numOfThreads, i, this);
            workers[i].start();
        }
        for (int i = 0; i < this.numOfThreads; i++)
            try {
                workers[i].join();
            } catch (InterruptedException ie) {}
    }
    
    private ArrayList<Integer> getNeighbors(int node) {
        ArrayList<Integer> neighbors = new ArrayList<Integer>();
        for (GraphEdge e: this.edges) {
            if (e.getNodes()[0] == node) {
                neighbors.add(e.getNodes()[1]);
                continue;
            }
            if (e.getNodes()[1] == node)
                neighbors.add(e.getNodes()[0]);
        }
        return neighbors;
    }
    
    public Float calculateInnerDistanceForNodes(int start, int end) {
        Float[] tentative = new Float[this.nodes.size()];
        tentative[start] = 0.0f;
        for (int i = 0; i < nodes.size(); i++)
            if (i != start)
                tentative[i] = Float.POSITIVE_INFINITY;
        int current = start;
        ArrayList<Integer> visited = new ArrayList<Integer>();
        ArrayList<Integer> neighbors;
        while (!visited.contains(end)) {
            neighbors = getNeighbors(current);
            for (Integer i : neighbors) {
                Float weight = 0.0f;
                for (GraphEdge e: this.edges) {
                    Integer[] nds = e.getNodes();
                    if ((nds[0] == current && nds[1] == i) ||
                            (nds[1] == current && nds[0] == i)) {
                        weight = e.getWeight();
                        break;
                    }
                    else continue;
                }
                tentative[i] = tentative[current] + weight;
            }
            visited.add(current);
            Float minDist = Float.POSITIVE_INFINITY;
            int next = -1;
            for (int i = 0; i < tentative.length; i++)
                if ((!visited.contains(i)) && (tentative[i] < minDist)) {
                    minDist = tentative[i];
                    next = i;
                }
            if (next == -1) break;
            if (minDist == Float.POSITIVE_INFINITY) break;
            current = next;
        }
        return tentative[end];
    }
    
    public ArrayList<Float> getInnerDistances() {
        DijkstraThread[] workers = new DijkstraThread[numOfThreads];
        for (int i = 0; i < this.numOfThreads; i++) {
            workers[i] = new DijkstraThread(numOfThreads, i, this);
            workers[i].start();
        }
        for (int i = 0; i < this.numOfThreads; i++)
            try {
                workers[i].join();
            } catch (InterruptedException ie) {}
        return this.costs;
    }
    
    public void addEdges(ArrayList<GraphEdge> e) throws GraphException {
        if (e == null)
            throw new InvalidMethodArgumentGraphException();
        synchronized(this.mutex) {
            this.edges.addAll(e);
        }
    }

    public void addCosts(ArrayList<Float> c) throws GraphException {
        if (c == null)
            throw new InvalidMethodArgumentGraphException();
        synchronized(this.mutex) {
            this.costs.addAll(c);
        }
    }
}

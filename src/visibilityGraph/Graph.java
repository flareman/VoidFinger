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
    public ArrayList<ArrayList<GraphEdge>> edges = new ArrayList<ArrayList<GraphEdge>>();
    private Octree surface;
    private int numOfThreads;
    private ArrayList<Float> costs = new ArrayList<Float>();
    private final Object dijkstraMutex = new Object();
    private final Object edgeMutex = new Object();
    public int infinity = 0;
    
    public Graph(ArrayList<Point> nds, Octree tree, int threads) throws GraphException {
        if (nds.isEmpty())
            throw new EmptyNodeSetGraphException();
        if (nds.get(0).getDimensions() < tree.getDimensions())
            throw new InvalidCreationArgumentDimensionsGraphException();
        for (Point p: nds) {
            this.nodes.add(new GraphNode(p));
            edges.add(new ArrayList<GraphEdge>());
        }
        this.surface = tree;
        this.numOfThreads = threads;
    }
    
    public int getNodeCount() {return this.nodes.size();}
    public int getDimensions() { return this.surface.getDimensions(); }
    public GraphNode getNode(int n) { return nodes.get(n); }
    
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
    
    public void addEdgeForVisible(int i, int j) {
        ArrayList<Float> projections = new ArrayList<Float>();
        try {
            Vector ray = new Vector(this.nodes.get(i).getPoint(), this.nodes.get(j).getPoint());
            ArrayList<Point> visibleList = getOctreeLeafs(this.nodes.get(i).getPoint(), ray);
            for (Point p: visibleList) {
                Vector v = new Vector(p);
                projections.add(ray.getProjection(v));
            }
        } catch (GeometryException ge) {}
        if (projections.isEmpty()) return;
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

            if (visible) {
                synchronized(this.edgeMutex) {
                    Float dist = this.nodes.get(i).getPoint().minkowskiDistanceFrom(this.nodes.get(j).getPoint(), 2);
                    this.edges.get(i).add(new GraphEdge(j, dist));
                    this.edges.get(j).add(new GraphEdge(i, dist));
                }
            }
        } catch (GeometryException ge) {
        } catch (GraphException gre) {
        } catch (OctreeException oe) {
        }
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
    
    private ArrayList<GraphEdge> getAdjacencies(int node) throws GraphException {
        if (node < 0 || node > this.nodes.size() - 1)
            throw new InvalidMethodArgumentGraphException();
        ArrayList<GraphEdge> result = new ArrayList<GraphEdge>();
        result.addAll(this.edges.get(node));
        return result;
    }
    
    public Float calculateInnerDistanceForNodes(int start, int end) {
        PriorityQueue pq = new PriorityQueue(start, this.nodes.size());
        QueueNode res;
        while (true) {
            res = pq.remove();
            if (res.getTentativeDistance().isInfinite()) {
                synchronized(this.dijkstraMutex) {infinity++;}
                return Float.POSITIVE_INFINITY;
            }
            if (res.getNodeID().equals(end)) return res.getTentativeDistance();
            try {
                for (GraphEdge ge: this.getAdjacencies(res.getNodeID())) {
                    if (pq.containsNodeWithID(ge.getEndpoint()))
                        if (Float.compare(pq.getDistanceForNodeWithID(ge.getEndpoint()), res.getTentativeDistance() + ge.getWeight()) > 0)
                            pq.updateDistanceForNodeWithID(ge.getEndpoint(), res.getTentativeDistance()+ge.getWeight());
                }
            } catch (GraphException e) {}
        }
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
    
    public void addCosts(ArrayList<Float> c) throws GraphException {
        if (c == null)
            throw new InvalidMethodArgumentGraphException();
        synchronized(this.dijkstraMutex) {
            this.costs.addAll(c);
        }
    }
}

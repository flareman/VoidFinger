package visibilityGraph;

import java.util.ArrayList;

public class WorkerThread extends Thread {
    protected int threadID;
    protected Graph vGraph;
    protected int numOfThreads;
    
    public WorkerThread (int threads, int ID, Graph graph) {
        threadID = ID;
        vGraph = graph;
        numOfThreads = threads;
    }
    
}

class VgraphCreationThread extends WorkerThread {
    public VgraphCreationThread(int threads, int ID, Graph graph) { super(threads, ID, graph); }
    
    @Override
    public void run() {
        ArrayList<GraphEdge> localEdges = new ArrayList<GraphEdge>();
        for (int i = this.threadID; i < this.vGraph.getNodeCount(); i += this.numOfThreads) {
            for (int j = i+1; j < this.vGraph.getNodeCount(); j++) {
                GraphEdge edge = this.vGraph.createEdgeForVisible(i, j);
                if (edge != null)
                    localEdges.add(edge);
            }
            try {
                this.vGraph.addEdges(localEdges);
            } catch (GraphException gre) {}
        }
    }
}

class DijkstraThread extends WorkerThread {
    public DijkstraThread(int threads, int ID, Graph graph) { super(threads, ID, graph); }
    
    @Override
    public void run() {
        ArrayList<Float> localCosts = new ArrayList<Float>();
        for (int i = this.threadID; i < this.vGraph.getNodeCount(); i += this.numOfThreads) {
            for (int j = i+1; j < this.vGraph.getNodeCount(); j++) {
                Float f = this.vGraph.calculateInnerDistanceForNodes(i, j);
                if (!f.isInfinite()) localCosts.add(f);
            }
        }
        try {
            this.vGraph.addCosts(localCosts);
        } catch (GraphException gre) {}
    }
}

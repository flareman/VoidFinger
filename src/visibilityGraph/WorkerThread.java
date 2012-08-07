package visibilityGraph;

import java.util.ArrayList;

public class WorkerThread extends Thread {
    protected int threadID;
    protected Graph vGraph;
    protected int numOfThreads;
    final protected Object mutex = new Object();
    
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
                if(edge != null)
                    localEdges.add(edge);
            }
            synchronized(this.mutex) {
                this.vGraph.edges.addAll(localEdges);
            }
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
                localCosts.add(this.vGraph.calculateInnerDistanceForNodes(i, j));
            }
        }
        synchronized(this.mutex) {
            this.vGraph.costs.addAll(localCosts);
        }
    }
}

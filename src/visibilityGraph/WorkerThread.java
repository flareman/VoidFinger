package visibilityGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class WorkerThread extends Thread{
    int threadID;
    Graph vGraph;
    int numOfThreads;
    
    public WorkerThread(int threads,int ID, Graph graph){
        threadID=ID;
        vGraph = graph;
        numOfThreads = threads;
    }
    
}

class VgraphCreationThread extends WorkerThread{
    
    public VgraphCreationThread(int threads,int ID, Graph graph){
        super(threads,ID,graph);
    }
      
    @Override
    public void run(){
        ArrayList<GraphEdge> localEdges = new ArrayList<GraphEdge>();
        for(int i=threadID;i<vGraph.getNodeCount();i+=numOfThreads){
            for(int j=i+1;j<vGraph.getNodeCount();j+=numOfThreads){
                GraphEdge edge = vGraph.createEdgeForVisible(i, j);
                if(edge != null)
                    localEdges.add(edge);
            }
            Object mutex = new Object();
            synchronized(mutex){
                Collections.copy(vGraph.edges, localEdges);
            }
        }
    }
    
    
}

class DijkstraThread extends WorkerThread{
    
    public DijkstraThread(int threads,int ID, Graph graph){
        super(threads,ID,graph);
    }
    
    @Override
    public void run(){
        ArrayList<Float> localCosts = new ArrayList<Float>();
        for(int i=threadID;i<vGraph.getNodeCount();i+=numOfThreads){
            for(int j=i+1;j<vGraph.getNodeCount();j+=numOfThreads){
                localCosts.add(vGraph.calculateInnerDistanceForNodes(i, j));
            }
        }
        Object mutex = new Object();
        synchronized(mutex){
            Collections.copy(vGraph.costs, localCosts);
        }
    }
    
}

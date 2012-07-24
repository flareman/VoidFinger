package visibilityGraph;
import java.util.ArrayList;
import octree.Octree;

public class Graph {
    private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
    private int numOfDims;
    private Octree surface;
    
    public Graph(ArrayList<Float[]> nds, int dims) throws GraphException,GraphNodeException{
        if(dims < 1 )
            throw new InvalidGraphNumberOfDimensionsException();
        if(nds.isEmpty())
            throw new EmptyNodeSetException();
        
        numOfDims = dims;
        for(Float[] v : nds){
            nodes.add(new GraphNode(dims, v));
        }
        
    }
    
    public int getNumOfDims(){
            return this.numOfDims;
    }
    
    public GraphNode getNodeAtPosition(int x){
        return nodes.get(x);
    }
    
    public GraphEdge getEdgeAtPosition(int x){
        return edges.get(x);
    }
    
    public Float calculateEucledianDistance(GraphNode node1, GraphNode node2) throws GraphNodeException{
        Float dist,acc;
        acc = new Float(0);
        for(int i=0;i<numOfDims;i++){
            acc += new Float( Math.pow(Math.abs(node2.getDimension(i, numOfDims) - node1.getDimension(i,numOfDims)),numOfDims));
        }
        dist = new Float (Math.pow(acc,(1/numOfDims)));
        return dist;
    }
    
}

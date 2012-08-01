package visibilityGraph;
import geometry.Point;
import java.util.ArrayList;
import octree.Octree;

public class Graph {
    private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
    private int numOfDims;
    private Octree surface;
    
    public Graph(ArrayList<Point> nds, int dims) throws GraphException,GraphNodeException{
        if(dims < 1 )
            throw new InvalidGraphNumberOfDimensionsException();
        if(nds.isEmpty())
            throw new EmptyNodeSetException();
        
        numOfDims = dims;
        for(Point p : nds){
            nodes.add(new GraphNode(dims, p));
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
    
    
    public void iterateToCreateEdges(){
        for(int i=0;i<nodes.size();i++){
            for(int j=i+1;j<nodes.size();j++){
                //create ray and call octree code here
            }
        }
    }
}

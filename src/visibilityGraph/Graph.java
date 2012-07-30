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
    
    private boolean checkIntersection(Float[] p ,octree.OctNode node){
        return false;
    }
    
    private ArrayList<Float[]> getVisibleNodesForPoint(Float[] point) throws Exception{
        ArrayList<Float[]> visibleNodes = new ArrayList<Float[]>();
        recurseGetVisibleNodes(point,visibleNodes, surface.getRoot());
        return visibleNodes;
    }
    
    private void recurseGetVisibleNodes(Float[] p , ArrayList<Float[]> visibleNodes, octree.OctNode root) throws Exception{
        if(checkIntersection(p,root)){
                if(root.getChildren().isEmpty())// MUST TELL DOC TO ALTER IMPLEMENTATION CAUSE HE IS STUPID - SAME GOES FOR GET COORDS
                    visibleNodes.add(root.getCoordinates());
                else{
                    for(octree.OctNode ch : root.getChildren())
                        recurseGetVisibleNodes(p, visibleNodes, ch);
                }
        }
        
    }
    
    private void createAllEdges() throws Exception{// Have Questions
        for(GraphNode gn : nodes){
            ArrayList<Float[]> visible = getVisibleNodesForPoint(gn.getCoords());
            for(Float[] p : visible){
                for(GraphNode gn2 : nodes){
                    int i;
                    for(i=0;i<numOfDims;i++){
                        if(p[i] != gn2.getCoords()[i])
                            break;
                    }
                    if(i == numOfDims){
                        
                    }                        
                }
            }
        }
    }
}

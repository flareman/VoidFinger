package visibilityGraph;
import geometry.GeometryException;
import geometry.Point;
import geometry.Vector;
import java.util.ArrayList;
import java.util.Collections;
import octree.OctNode;
import octree.OctNodeException;
import octree.Octree;

public class Graph {
    private ArrayList<GraphNode> nodes = new ArrayList<GraphNode>();
    private ArrayList<GraphEdge> edges = new ArrayList<GraphEdge>();
    private int numOfDims;
    private Octree surface;
    
    public Graph(ArrayList<Point> nds, int dims, Octree tree) throws GraphException,GraphNodeException{
        if(dims < 1 )
            throw new InvalidGraphNumberOfDimensionsException();
        if(nds.isEmpty())
            throw new EmptyNodeSetException();
        
        numOfDims = dims;
        for(Point p : nds){
            nodes.add(new GraphNode(dims, p));
        }
        surface = tree;
        
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
    
    private void recurseGetOctreeLeafs(Point origin, Vector ray, ArrayList<Point> visible, OctNode root) throws GeometryException, OctNodeException{
        if(root.getBoundingBox().intersectWithRay(ray, origin, Boolean.FALSE)){
            if(root.getNodeType() == OctNode.OCTNODE_LEAF){
                visible.add(root.getPoint());
            }
            if(root.getNodeType() == OctNode.OCTNODE_INTERMEDIATE){
                ArrayList<OctNode> children = root.getChildren();
                for(OctNode n : children)
                    recurseGetOctreeLeafs(origin,ray,visible,n);
            }
        }
    }
    
    private ArrayList<Point> getOctreeLeafs(Point origin,Vector ray) throws GeometryException, OctNodeException{
        ArrayList<Point> visible = new ArrayList<Point>();
        recurseGetOctreeLeafs(origin,ray,visible,this.surface.getRoot());
        return visible;
    }
    
    public void iterateToCreateEdges() throws GeometryException, OctNodeException{
        for(int i=0;i<nodes.size();i++){
            for(int j=i+1;j<nodes.size();j++){
                //create ray and call octree code here
                Vector ray = new Vector(nodes.get(i).getPoint(),nodes.get(j).getPoint());
                ArrayList<Point> visibleList = getOctreeLeafs(nodes.get(i).getPoint(), ray);
                ArrayList<Float> projections = new ArrayList<Float>();
                if(projections.isEmpty())
                    continue;
                for(Point p : visibleList){
                    Vector v = new Vector(p);
                    projections.add(ray.getProjection(v));
                }
                Collections.sort(projections);
                boolean visible=false;
                int clusterCount = 1;
                float D;
                for(int k=1;k<projections.size();k++){
                    
                }
            }
        }
    }
}

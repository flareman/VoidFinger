package visibilityGraph;


public class GraphEdge {
    private Integer[] nodes = new Integer[2];
    private Float weight;
    
    public GraphEdge(Integer node1, Integer node2, Float weight) throws GraphEdgeException {
        if(node1 == null || node2 == null)
            throw new InvalidNodeException();
        if(weight < 0)
            throw new NegativeWeightException();
        nodes[0] = node1;
        nodes[1] = node2;
        this.weight = weight;
    }
    
    public Float getWeight(){
        return this.weight;
    }
    
    public Integer[] getNodes(){
        return this.nodes;
    }
}

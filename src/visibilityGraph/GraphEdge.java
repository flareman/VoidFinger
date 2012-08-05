package visibilityGraph;

public class GraphEdge {
    private Integer[] nodes = new Integer[2];
    private Float weight;
    
    public GraphEdge(int node1, int node2, float weight) throws GraphException {
        if (node1 < 0 || node2 < 0)
            throw new InvalidEdgeCreationArgumentsGraphException("node IDs must be non-negative");
        if (weight < 0)
            throw new InvalidEdgeCreationArgumentsGraphException("edge weight must be non-negative");
        nodes[0] = node1;
        nodes[1] = node2;
        this.weight = weight;
    }
    
    public Float getWeight() { return this.weight; }
    public Integer[] getNodes() { return this.nodes; }
}

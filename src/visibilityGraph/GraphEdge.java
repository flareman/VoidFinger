package visibilityGraph;

public class GraphEdge {
    private Integer endpoint;
    private Float weight;
    
    public GraphEdge(int end, Float wgt) throws GraphException {
        if (wgt < 0 || end < 0)
            throw new InvalidEdgeCreationArgumentsGraphException("edge weight must be non-negative");
        this.endpoint = end;
        this.weight = wgt;
    }
    
    public Float getWeight() { return this.weight; }
    public Integer getEndpoint() { return this.endpoint; }
}

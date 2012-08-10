package visibilityGraph;

public class QueueNode extends java.lang.Object implements Comparable<QueueNode> {
    private Integer nodeID;
    private Float tentativeDistance;
    
    public QueueNode(Float tent, Integer id) {
        super();
        if (tent == null || id == null) throw new NullPointerException();
        if (tent < 0.0f || id < 0) throw new IllegalArgumentException();
        this.tentativeDistance = tent;
        this.nodeID = id;
    }
    
    public Integer getNodeID() { return this.nodeID; }
    public Float getTentativeDistance() { return this.tentativeDistance; }
    public int compareTo(QueueNode b) { return this.tentativeDistance.compareTo(b.tentativeDistance); }
    public int hashCode() { return ((this.tentativeDistance.hashCode()+this.nodeID.hashCode())/2)+14; }
    
    public boolean equals(Object o) {
        if (o instanceof QueueNode == false) throw new ClassCastException();
        if (!this.tentativeDistance.equals(((QueueNode)o).tentativeDistance) ||
                !this.nodeID.equals(((QueueNode)o).nodeID))
            return false; else return true;
    }
    
}

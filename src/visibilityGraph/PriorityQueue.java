package visibilityGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class PriorityQueue extends java.lang.Object implements Set<QueueNode> {
    private ArrayList<QueueNode> innerList = new ArrayList<>();
    private ArrayList<Integer> nodeIDs = new ArrayList<>();
    
    public PriorityQueue(int startID, int capacity) {
        super();
        this.innerList.add(new QueueNode(0.0f, startID));
        int add = 1;
        for (int i = 0; i < capacity; i ++)
            if (i != startID) {
                this.innerList.add(new QueueNode(Float.POSITIVE_INFINITY, i));
                this.nodeIDs.add(i+add);
            } else {
                this.nodeIDs.add(0);
                add = 0;
            }
    }

    public PriorityQueue() { super(); }
    public QueueNode peek() { if (!this.innerList.isEmpty()) return this.innerList.get(0); return null;}
    public QueueNode get() { return this.peek(); }
    public QueueNode remove() { return this.pop(); }
    @Override public void clear() { this.innerList.clear(); this.nodeIDs.clear(); }
    @Override public int size() { return this.innerList.size(); }
    @Override public boolean isEmpty() { return this.innerList.isEmpty(); }
    @Override public boolean add(QueueNode qn) { return this.push(qn); }
    @Override public <T> T[] toArray(T[] array) { return this.innerList.toArray(array); }
    @Override public Object[] toArray() { return this.innerList.toArray(); }
    @Override public Iterator<QueueNode> iterator() { return this.innerList.iterator(); }
    @Override public boolean remove(Object qn) { throw new UnsupportedOperationException(); }
    @Override public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    @Override public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }

    private int compareNodes(int i, int j) { return this.innerList.get(i).compareTo(this.innerList.get(j)); }

    private void switchNodes(int i, int j) {
        this.nodeIDs.set(this.innerList.get(i).getNodeID(), j);
        this.nodeIDs.set(this.innerList.get(j).getNodeID(), i);
        QueueNode left = this.innerList.get(i);
        this.innerList.set(i, this.innerList.get(j));
        this.innerList.set(j, left);
    }
    
    private void update(int i) {
        if (i < 0) throw new IllegalArgumentException();
        
        int p = i, pn, p1, p2;
        while (true) {
            if (p == 0) break;
            p2 = (p-1)/2;
            if (this.compareNodes(p, p2) < 0) { this.switchNodes(p, p2); p = p2; }
            else break;
        }
        
        if (p < i) return;
        
        while (true) {
            pn = p;
            p1 = 2*p+1;
            p2 = 2*p+2;
            if (this.innerList.size() > p1 && this.compareNodes(p, p1) > 0) p = p1;
            if (this.innerList.size() > p2 && this.compareNodes(p, p2) > 0) p = p2;
            if (p == pn) break;
            this.switchNodes(p, pn);
        }
    }

    @Override
    public boolean contains(Object qn) {
        if (qn instanceof QueueNode == false) throw new ClassCastException();
        if (this.nodeIDs.get(((QueueNode)qn).getNodeID()) == -1) return false;
        return true;
    }
    
    @Override
    public boolean containsAll(Collection<?> coll) {
        for (Iterator<?> it = coll.iterator(); it.hasNext();) {
            if (!this.contains(it.next())) return false;
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends QueueNode> coll) {
        if (coll == null) throw new NullPointerException();
        boolean modified = false;
        for (QueueNode qn: coll)
            if (this.push(qn)) modified = true;
        return modified;
    }

    public boolean push(QueueNode qn) {
        if (qn == null) throw new NullPointerException();
        if (this.innerList.contains(qn)) return false;
        if (this.nodeIDs.get(qn.getNodeID()) != -1) throw new IllegalArgumentException("Node with id "+qn.getNodeID()+" is already contained in the priority queue");
        
        int p = this.innerList.size(), p2;
        this.innerList.add(qn);
        this.nodeIDs.add(qn.getNodeID());
        while (true) {
            if (p == 0) break;
            p2 = (p-1)/2;
            if (this.compareNodes(p, p2) < 0) { this.switchNodes(p, p2); p = p2; }
            else break;
        }
        
        return true;
    }

    public QueueNode pop() {
        if (this.isEmpty()) return null;
        QueueNode result = this.peek();
        int p = 0, p1, p2, pn;
        this.innerList.set(0, this.innerList.get(this.innerList.size()-1));
        this.innerList.remove(this.innerList.size()-1);
        this.nodeIDs.set(result.getNodeID(), -1);
        if (this.isEmpty()) return result;
        this.nodeIDs.set(this.innerList.get(0).getNodeID(), 0);
        while (true) {
            pn = p;
            p1 = 2*p+1;
            p2 = 2*p+2;
            if (this.innerList.size() > p1 && this.compareNodes(p, p1) > 0) p = p1;
            if (this.innerList.size() > p2 && this.compareNodes(p, p2) > 0) p = p2;
            if (p == pn) break;
            this.switchNodes(p, pn);
        }
        return result;
    }
    
    public Float getDistanceForNodeWithID(int id) {
        if (id < 0 || id > this.nodeIDs.size()-1) throw new IllegalArgumentException();
        return this.innerList.get(this.nodeIDs.get(id)).getTentativeDistance();
    }

    public boolean containsNodeWithID(int id) {
        if (id < 0 || id > this.nodeIDs.size()-1) throw new IllegalArgumentException();
        if (this.nodeIDs.get(id) == -1) return false;
        return true;
    }

    public boolean updateDistanceForNodeWithID(int id, Float dist) {
        if (id < 0 || id > this.nodeIDs.size()-1) throw new IllegalArgumentException();
        if (this.nodeIDs.get(id) == -1) return false;
        QueueNode nqn = new QueueNode(dist, id);
        this.innerList.set(this.nodeIDs.get(id), nqn);
        this.update(this.nodeIDs.get(id));
        return true;
    }
}
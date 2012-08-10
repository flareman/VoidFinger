package visibilityGraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class PriorityQueue extends java.lang.Object implements Set<QueueNode> {
    private ArrayList<QueueNode> innerList = new ArrayList<QueueNode>();
    
    public PriorityQueue() { super(); }
    public void clear() { this.innerList.clear(); }
    public int size() { return this.innerList.size(); }
    public boolean isEmpty() { return this.innerList.isEmpty(); }
    public boolean contains(Object qn) { return this.innerList.contains(qn); }
    public boolean containsAll(Collection<?> coll) { return this.innerList.containsAll(coll); }
    public boolean add(QueueNode qn) { return this.push(qn); }
    public QueueNode remove() { return this.pop(); }
    public QueueNode get() { return this.peek(); }
    public QueueNode peek() { if (!this.innerList.isEmpty()) return this.innerList.get(0); return null;}
    public <T> T[] toArray(T[] array) { return this.innerList.toArray(array); }
    public Object[] toArray() { return this.innerList.toArray(); }
    public Iterator<QueueNode> iterator() { return this.innerList.iterator(); }
    public boolean remove(Object qn) { throw new UnsupportedOperationException(); }
    public boolean removeAll(Collection<?> coll) { throw new UnsupportedOperationException(); }
    public boolean retainAll(Collection<?> coll) { throw new UnsupportedOperationException(); }

    private int compareNodes(int i, int j) { return this.innerList.get(i).compareTo(this.innerList.get(j)); }

    private int containsNode(Integer id) {
        if (id == null) throw new NullPointerException();
        if (id < 0) throw new IllegalArgumentException();
        for (int i = 0; i < this.innerList.size(); i++)
            if (this.innerList.get(i).getNodeID().equals(id)) return i;
        return -1;
    }
    
    private void switchNodes(int i, int j) {
        QueueNode temp = this.innerList.get(i);
        this.innerList.set(i, this.innerList.get(j));
        this.innerList.set(j, temp);
    }
    
    
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
        if (this.containsNode(qn.getNodeID()) != -1) throw new IllegalArgumentException("Node with id "+qn.getNodeID()+" is already contained in the priority queue");
        
        int p = this.innerList.size(), p2;
        this.innerList.add(qn);
        while (true) {
            if (p == 0) break;
            p2 = (p-1)/2;
            if (this.compareNodes(p, p2) < 0) { this.switchNodes(p, p2); p = p2; }
            else break;
        }
        
        return true;
    }

    public QueueNode pop() {
        QueueNode result = this.peek();
        int p = 0, p1, p2, pn;
        this.innerList.set(0, this.innerList.get(this.innerList.size()-1));
        this.innerList.remove(this.innerList.size()-1);
        while (true) {
            pn = p;
            p1 = 2*p+1;
            p2 = 2*p+2;
            if (this.innerList.size() > p1 && this.compareNodes(p, p1) < 0) p = p1;
            if (this.innerList.size() > p2 && this.compareNodes(p, p2) < 0) p = p2;
            if (p == pn) break;
            this.switchNodes(p, pn);
        }
        return result;
    }
    
    public boolean updateNodeDistance(Float dist, int id) {
        QueueNode nqn = new QueueNode(dist, id);
        if (this.contains(nqn)) return false;
        int nodeLocation = this.containsNode(id);
        if (nodeLocation == -1) throw new IllegalArgumentException();
        this.innerList.set(nodeLocation, nqn);
        this.update(nodeLocation);
        return true;
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
}
package octree;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Octree {
    private Integer depth;
    private OctNode root = null;
    
    private Octree(Integer depth, OctNode root) {
        this.depth = depth;
        this.root = root;
    }
    
    public OctNode getRoot() { return root; }
    
    static public Octree parseFromFile(String filename) throws FileNotFoundException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        // TODO: Actually parse SOG file and return the new octree instead of dummy one
        return new Octree(0, null);
    }
}
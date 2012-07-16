package voidfinger;

import java.io.FileNotFoundException;
import octree.*;

public class VoidFinger {
    private Octree molecule = null;
    
    public VoidFinger(String filename) {
        try {
            this.molecule = Octree.parseFromFile(filename);
        } catch (FileNotFoundException fe) {
            this.molecule = null;
        }
    }

    public static void main(String[] args) {
        VoidFinger instance = new VoidFinger(args[0]);
    }
}

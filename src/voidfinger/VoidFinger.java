package voidfinger;

import java.io.FileNotFoundException;
import java.io.IOException;
import octree.OctNodeException;
import octree.Octree;
import octree.OctreeException;

public class VoidFinger {
    private Octree molecule = null;
    
    public VoidFinger(String filename) {
        try {
            this.molecule = Octree.parseFromFile(filename);
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getLocalizedMessage());
            this.molecule = null;
        } catch (IOException ioe) {
            System.out.println(ioe.getLocalizedMessage());
            this.molecule = null;
        } catch (OctreeException oe) {
            System.out.println(oe.getLocalizedMessage());
            this.molecule = null;
        }
        catch (OctNodeException one) {
            System.out.println(one.getLocalizedMessage());
            this.molecule = null;
        }
    }
    
    public Octree getMolecule() { return this.molecule; }

    public static void main(String[] args) {
        VoidFinger instance = new VoidFinger(args[0]);
        try {
            System.out.println(instance.getMolecule().getDepth());
        } catch (Exception e) { e.printStackTrace(); }
    }
}

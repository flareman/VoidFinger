package voidfinger;

import filter.FCEException;
import filter.FilterClusterEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import kdtree.KDTree;
import kdtree.KDTreeException;
import octree.Octree;
import octree.OctreeException;
import visibilityGraph.Graph;
import visibilityGraph.GraphException;

public class VoidFinger {
    private Octree molecule = null;
    private KDTree kdtree = null;
    private FilterClusterEngine fce = null;
    private Graph graph = null;
    
    public VoidFinger(String filename) {
        try {
            System.out.println("Volumetric Inner Distance Fingerprinting Utility");
            System.out.println("(c) 2012 Spyridon Smparounis, George Papakyriakopoulos");
            System.out.println("National and Kapodistrian University of Athens");
            System.out.println("Department of Informatics and Telecommunications");
            System.out.println();
            this.molecule = Octree.parseFromFile(filename);
            this.kdtree = new KDTree(this.molecule);
            this.fce = new FilterClusterEngine(this.kdtree, 500);
            System.out.print("Filtering kd-tree...");
            this.fce.performClustering();
            this.graph = new Graph(this.fce.getClusterCenters(), this.molecule);
            System.out.print("Building visibilty graph... ");
            this.graph.buildVisibilityGraph();
            System.out.println("done");
            System.out.print("Calculating inner distances... ");
            ArrayList<Float> result = this.graph.getInnerDistances();
            System.out.println("done");
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getLocalizedMessage());
            this.molecule = null;
        } catch (IOException ioe) {
            System.out.println(ioe.getLocalizedMessage());
            this.molecule = null;
        } catch (OctreeException oe) {
            System.out.println(oe.getLocalizedMessage());
            this.molecule = null;
        } catch (KDTreeException kdte) {
            System.out.println(kdte.getLocalizedMessage());
            this.molecule = null;
        } catch (FCEException fcee) {
            System.out.println(fcee.getLocalizedMessage());
            this.molecule = null;
        } catch (GraphException gre) {
            System.out.println(gre.getLocalizedMessage());
            this.molecule = null;
        }
    }
    
    public Octree getMolecule() { return this.molecule; }

    public static void main(String[] args) {
        VoidFinger instance = new VoidFinger(args[0]);
    }
}

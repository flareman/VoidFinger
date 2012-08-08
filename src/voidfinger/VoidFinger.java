package voidfinger;

import filter.FCEException;
import filter.FilterClusterEngine;
import histogram.Histogram;
import histogram.HistogramException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    private Histogram histogram = null;
    
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
            System.out.print("Filtering kd-tree... ");
            int passes = this.fce.performClustering();
            System.out.println("done after "+passes+" passes");
            this.graph = new Graph(this.fce.getClusterCenters(), this.molecule, 4);
            System.out.print("Building visibilty graph... ");
            this.graph.buildVisibilityGraph();
            System.out.println("done");
            System.out.print("Calculating inner distances... ");
            ArrayList<Float> result = this.graph.getInnerDistances();
            System.out.println("done");
            System.out.println(result.size()+" inner distances calculated.");
            System.out.print("Building histogram... ");
            this.histogram = new Histogram(128, Collections.min(result), Collections.max(result));
            this.histogram.addAll(result);
            System.out.println("done");
//             this.histogram.printHistogram();
        } catch (FileNotFoundException fe) {
            System.out.println(fe.getLocalizedMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getLocalizedMessage());
        } catch (OctreeException oe) {
            System.out.println(oe.getLocalizedMessage());
        } catch (KDTreeException kdte) {
            System.out.println(kdte.getLocalizedMessage());
        } catch (FCEException fcee) {
            System.out.println(fcee.getLocalizedMessage());
        } catch (GraphException gre) {
            System.out.println(gre.getLocalizedMessage());
        } catch (HistogramException he) {
            System.out.println(he.getLocalizedMessage());
        }
    }
    
    public Octree getMolecule() { return this.molecule; }

    public static void main(String[] args) {
        VoidFinger instance = new VoidFinger(args[0]);
    }
}

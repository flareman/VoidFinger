package voidfinger;

import filter.FCEException;
import filter.FilterClusterEngine;
import histogram.Histogram;
import histogram.HistogramException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import kdtree.KDTree;
import kdtree.KDTreeException;
import octree.Octree;
import octree.OctreeException;
import potential.EPArray;
import potential.EPArrayException;
import visibilityGraph.Graph;
import visibilityGraph.GraphException;

public class VoidFinger {
    private long time = 0;
    private Octree molecule = null;
    private KDTree kdtree = null;
    private FilterClusterEngine fce = null;
    private Graph graph = null;
    private Histogram histogram = null;
    private EPArray potentials = null;

    public int getElapsedSeconds() { return (int)(this.time/1000000000); }
    
    public VoidFinger(String filename, String potentialsFilename, Integer centers, Integer threads, Integer bins, String output) {
        try {
            this.time = System.nanoTime();
            if (filename == null || filename.equals(""))
                throw new IllegalArgumentException("You must specify an input filename");
            if (potentialsFilename == null || potentialsFilename.equals(""))
                throw new IllegalArgumentException("You must specify an EP input filename");
            if (output == null || output.equals(""))
                throw new IllegalArgumentException("You must specify an output filename");
            if (centers == null || centers < 1)
                throw new IllegalArgumentException("The clustering centers must be at least one");
            if (bins == null || bins < 1)
                throw new IllegalArgumentException("The histogram bins must be at least one");
            if (threads == null || threads < 1)
                throw new IllegalArgumentException("This program cannot have less than one thread");
            System.out.println("Volumetric Inner Distance Fingerprinting Utility");
            System.out.println("(c) 2012 Spyridon Smparounis, George Papakyriakopoulos");
            System.out.println("National and Kapodistrian University of Athens");
            System.out.println("Department of Informatics and Telecommunications");
            System.out.println();
            System.out.println("Arguments:");
            System.out.println("==========");
            System.out.println("Input file:\t"+filename);
            System.out.println("EP file:\t"+potentialsFilename);
            System.out.println("# of centers:\t"+centers);
            System.out.println("# of threads:\t"+threads);
            System.out.println("Histogram bins:\t"+bins);
            System.out.println("Output file:\t"+output);
            System.out.println();
            System.out.print("Parsing molecular octree from file... ");
            this.molecule = Octree.parseFromFile(filename);
            System.out.println("done");
            this.kdtree = new KDTree(this.molecule);
            this.fce = new FilterClusterEngine(this.kdtree, centers);
            System.out.print("Filtering kd-tree... ");
            int passes = this.fce.performClustering();
            System.out.println("done after "+passes+" passes");
            System.out.print("Parsing electrostatic potentials from file... ");
            this.potentials = EPArray.readArrayFromFile(potentialsFilename, this.kdtree.getThreshold());
            System.out.println("done");
            this.graph = new Graph(this.fce.getClusterCenters(this.potentials), this.molecule, threads);
            System.out.print("Building visibility graph... ");
            this.graph.buildVisibilityGraph();
            System.out.println("done");
            System.out.println(this.graph.totalEdges+" total edges created.");
            System.out.print("Calculating inner distances... ");
            ArrayList<Float> result = this.graph.getInnerDistances();
            System.out.println("done");
            System.out.println(result.size()+" inner distances calculated.");
            System.out.print("Building histogram... ");
            this.histogram = Histogram.createFromCollection(bins, result);
            System.out.println("done");
            this.histogram.saveToFile(output);
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getLocalizedMessage());
        } catch (FileNotFoundException fe) {
            System.out.println("File "+filename+" not found; you may have mistyped the filename.");
        } catch (IOException ioe) {
            System.out.println("An error occured when reading/writing to the disk; please, try again.");
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
        } catch (EPArrayException epae) {
            System.out.println(epae.getLocalizedMessage());
        } finally { this.time = System.nanoTime() - this.time; }
        
    }
    
    public static void main(String[] args) {
        System.out.println();
        if (args.length < 6) {
            System.out.println("Invalid argument count.");
            System.out.println("Proper syntax is:");
            System.out.println("java VoidFinger [input] [EP input] [centers] [threads] [bins] [output]");
            return;
        }
        VoidFinger instance = new VoidFinger(args[0], args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), args[5]);
        System.out.println("Total running time: "+instance.getElapsedSeconds()+" sec.");
    }
}

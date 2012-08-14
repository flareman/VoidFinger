package voidfinger;

import filter.FCEException;
import filter.FilterClusterEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import kdtree.KDTree;
import kdtree.KDTreeException;
import kernel.KDEDistance;
import kernel.KernelDensityEstimator;
import octree.Octree;
import octree.OctreeException;
import potential.EPArray;
import potential.EPArrayException;
import visibilityGraph.Graph;
import visibilityGraph.GraphException;

public class VoidFinger {
    private long time = 0;
    private int threads = 1;
    private int steps = 1;
    private int runs = 1;
    private int centers = 100;
    private boolean verbose = false;
    private String filename;
    private Octree octree = null;
    private KDTree kdtree = null;
    private EPArray potentials = null;
    private ArrayList<KernelDensityEstimator> estimators = new ArrayList<KernelDensityEstimator>();
    private int medianID = -1;

    public int getElapsedSeconds() {
        this.time = System.nanoTime() - this.time;
        return (int)(this.time/1000000000);
    }

    public int getRuns() { return this.runs; }
    public int getSelectedKDEID() { return this.medianID; }
    
    public VoidFinger(String filename, Integer centers, Integer threads, Integer steps, Integer runs, boolean verbose) {
        try {
            this.time = System.nanoTime();
            if (filename == null || filename.equals(""))
                throw new IllegalArgumentException("You must specify a PDB ID");
            if (centers == null || centers < 1)
                throw new IllegalArgumentException("The clustering centers must be at least one");
            if (steps == null || steps < 1)
                throw new IllegalArgumentException("The sampling steps must be at least one");
            if (runs == null || runs < 1)
                throw new IllegalArgumentException("The runs must be an odd positive integer");
            if (runs % 2 == 0)
                throw new IllegalArgumentException("The runs must be an odd positive integer");
            if (threads == null || threads < 1)
                throw new IllegalArgumentException("This program run on less than one thread");
            this.threads = threads;
            this.steps = steps;
            this.runs = runs;
            this.verbose = verbose;
            this.filename = filename;
            this.centers = centers;
            System.out.println("VoidFinger: Volumetric Inner Distance Fingerprinting Utility");
            System.out.println("(c) 2012 Spyridon Smparounis, George Papakyriakopoulos");
            System.out.println("National and Kapodistrian University of Athens");
            System.out.println("Department of Informatics and Telecommunications");
            System.out.println();
            System.out.println("PDB ID:\t\t"+filename);
            System.out.println("# of centers:\t"+centers);
            System.out.println("# of threads:\t"+threads);
            System.out.println("Sampling steps:\t"+steps);
            System.out.println("# of runs:\t"+runs);
            System.out.println();
            if (verbose) {
                System.out.print("Parsing molecular octree from file... ");
                this.octree = Octree.parseFromFile(filename+".sog");
                System.out.println("done");
                System.out.print("Building kd-tree... ");
                this.kdtree = new KDTree(this.octree);
                System.out.println("Created a kd-tree with depth " + this.kdtree.getMaxDepth()+" and "+this.kdtree.getPointCount()+" points.");
                System.out.print("Parsing electrostatic potentials from file... ");
                this.potentials = EPArray.readArrayFromFile(filename+".pot.dx", this.kdtree.getThreshold());
                System.out.println("done");
            } else {
                System.out.print("Preparing for runs... ");
                this.octree = Octree.parseFromFile(filename+".sog");
                this.kdtree = new KDTree(this.octree);
                this.potentials = EPArray.readArrayFromFile(filename+".pot.dx", this.kdtree.getThreshold());
                System.out.println("ready");
            }
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
        } catch (EPArrayException epae) {
            System.out.println(epae.getLocalizedMessage());
        }
    }
    
    public void performAnalysis() {
        try {
            if (verbose) {
                FilterClusterEngine fce = new FilterClusterEngine(this.kdtree, this.centers);
                System.out.println("Filtering engine is ready.");
                System.out.print("Filtering kd-tree... ");
                int passes = fce.performClustering();
                System.out.println("done after "+passes+" passes");
                Graph graph = new Graph(fce.getClusterCenters(this.potentials), this.octree, this.threads);
                System.out.print("Building visibility graph... ");
                graph.buildVisibilityGraph();
                System.out.println("done");
                System.out.println(graph.totalEdges+" total edges created.");
                System.out.print("Calculating inner distances... ");
                ArrayList<Float> result = graph.getInnerDistances();
                System.out.println("done");
                System.out.println(result.size()+" inner distances calculated.");
                System.out.print("Building kernel density estimator... ");
                KernelDensityEstimator estimator = KernelDensityEstimator.generateEstimatorFromValues(KernelDensityEstimator.KDE_GAUSSIAN, result);
                this.estimators.add(estimator);
                System.out.println("done");
                System.out.println("KDE added to list.");
            } else {
                FilterClusterEngine fce = new FilterClusterEngine(this.kdtree, this.centers);
                int passes = fce.performClustering();
                Graph graph = new Graph(fce.getClusterCenters(this.potentials), this.octree, this.threads);
                graph.buildVisibilityGraph();
                ArrayList<Float> result = graph.getInnerDistances();
                KernelDensityEstimator estimator = KernelDensityEstimator.generateEstimatorFromValues(KernelDensityEstimator.KDE_GAUSSIAN, result);
                this.estimators.add(estimator);
                System.out.println(passes+"-pass filtering, "+graph.totalEdges+" edges, "+result.size()+" IDs.");
                System.out.println("KDE added to list.");
            }
        } catch (GraphException ge) {
        } catch (FCEException fcee) {}
    }
    
    public void selectMedianKDE() {
        ArrayList<KDEDistance> distances = new ArrayList<KDEDistance>();
        for (int i = 0; i < this.estimators.size(); i++)
            for (int j = i+1; j < this.estimators.size(); j++)
                distances.add(new KDEDistance(i, j, this.estimators.get(i).getDistanceFromKDE(this.estimators.get(j))));
        Collections.sort(distances);
        int[] extremes = distances.get(0).getIDs();
        Float diff = Float.POSITIVE_INFINITY;
        for (int i = 0; i < this.estimators.size(); i++) {
            if (i == extremes[0] || i == extremes[1]) continue;
            Float left = Float.NEGATIVE_INFINITY;
            Float right = Float.NEGATIVE_INFINITY;
            Float temp = diff;
            for (KDEDistance d: distances) {
                if ((d.getAlpha() == i && d.getBeta() == extremes[0]) ||
                        (d.getBeta() == i && d.getAlpha() == extremes[0]))
                    left = d.getDistance();
                if ((d.getAlpha() == i && d.getBeta() == extremes[1]) ||
                        (d.getBeta() == i && d.getAlpha() == extremes[1]))
                    right = d.getDistance();
                if (!left.isInfinite() && !right.isInfinite()) {
                    temp = Math.abs(left-right);
                    break;
                }
            }
            if (temp < diff) { diff = temp; this.medianID = i; }
        }
    }
    
    public void saveKDEToFiles(int id) {
        if (id < 0 || id > this.estimators.size()-1) throw new IllegalArgumentException();
        try {
            this.estimators.get(id).writeEstimatorToFile(this.filename+".kde.txt");
            this.estimators.get(id).writeApproximateCurveToFile(this.filename+".plot.txt", this.steps);
        } catch (IOException ioe) {}
    }
    
    public static void main(String[] args) {
        System.out.println();
        if (args.length < 5) {
            System.out.println("Invalid argument count.");
            System.out.println("Proper syntax is:");
            System.out.println("java VoidFinger [PDB code] [centers] [threads] [sample steps] [passes] <verbose>");
            return;
        }
        boolean verbose = false;
        if (args.length == 6 && args[5].equalsIgnoreCase("verbose")) verbose = true;
        VoidFinger theFinger = new VoidFinger(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), verbose);
        for (int i = 1; i <= theFinger.getRuns(); i++) {
            System.out.println();
            System.out.println("PASS "+i+" OF "+theFinger.getRuns()+":");
            System.out.println("=============");
            theFinger.performAnalysis();
        }
        System.out.println();
        System.out.print("Selecting median KDE... ");
        theFinger.selectMedianKDE();
        System.out.println("done");
        System.out.print("Saving KDE and plot to file... ");
        theFinger.saveKDEToFiles(theFinger.getSelectedKDEID());
        System.out.println("done");
        System.out.println("Total running time: "+theFinger.getElapsedSeconds()+" sec.");
    }
}

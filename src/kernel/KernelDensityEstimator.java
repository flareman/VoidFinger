package kernel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class KernelDensityEstimator {
    public static final int KDE_GAUSSIAN = 0;
    public static final int KDE_EPANECHNIKOV = 1;
    public static final int KDE_TRIANGULAR = 2;
    public static final int KDE_RECTANGULAR = 3;
    private static final int KDE_MAX_TYPE = 3;

    private int kernel = KDE_GAUSSIAN;
    private Integer n = 0;
    private Float h = 0.0f;
    private Boolean optimalBandwidth = false;
    private ArrayList<Float> values = new ArrayList<Float>();
    
    private void optimizeBandwidth() {
        if (this.values.isEmpty()) return;
        Collections.sort(this.values);
        if (!this.optimalBandwidth) return;
        Float a = 0.0f, b = 0.0f;
        for (Float f: this.values) { a += f*f; b += f; }
        a /= this.n; b /= this.n; b *= b;
        Float sdev = new Float(Math.sqrt(a-b));
        Float iqr = this.values.get(this.values.size()*3/4 - this.values.size()/4)/1.349f;
        this.h = 1.059f*Math.min(sdev, iqr)*(new Float(Math.pow(this.n, -.2f)));
    }
    
    private Float kernelValue(Float f) {
        switch (this.kernel) {
            case KDE_GAUSSIAN:
                return new Float(Math.exp(-0.5f*f*f)/Math.sqrt(2*Math.PI));
            case KDE_EPANECHNIKOV:
                if (Math.abs(f) > Math.sqrt(5.0d)) return 0.0f;
                else return new Float((1-(f*f)/5)*3/(4*Math.sqrt(5)));
            case KDE_TRIANGULAR:
                if (Math.abs(f) > 1) return 0.0f;
                else return 1 - Math.abs(f);
            case KDE_RECTANGULAR:
                if (Math.abs(f) > 1) return 0.0f;
                else return 0.5f;
            default: throw new IllegalStateException("Unrecognized kernel function");
        }
    }

    public KernelDensityEstimator(int type) {
        if (type < 0 || type > KDE_MAX_TYPE) throw new IllegalArgumentException();
        this.kernel = type;
        this.optimalBandwidth = true;
    }
    
    public KernelDensityEstimator(int type, float bandwidth) {
        if (type < 0 || type > KDE_MAX_TYPE) throw new IllegalArgumentException();
        if (Float.compare(bandwidth, 0.0f) <= 0) throw new IllegalArgumentException();
        this.kernel = type;
        this.h = bandwidth;
        this.optimalBandwidth = false;
    }

    public static KernelDensityEstimator generateEstimatorFromValues(int type, float bandwidth, Collection<Float>coll) {
        if (coll == null) throw new NullPointerException();
        KernelDensityEstimator result = new KernelDensityEstimator(type, bandwidth);
        result.addAll(coll);
        return result;
    }

    public static KernelDensityEstimator generateEstimatorFromValues(int type, Collection<Float>coll) {
        if (coll == null) throw new NullPointerException();
        KernelDensityEstimator result = new KernelDensityEstimator(type);
        result.addAll(coll);
        return result;
    }

    public boolean addAll(Collection<Float> coll) {
        if (coll == null) throw new NullPointerException();
        if (coll.isEmpty()) return false;
        if (this.values.addAll(coll)) {
            this.n = this.values.size();
            this.optimizeBandwidth();
            return true;
        } else return false;
    }
    
    public boolean add(Float f) {
        if (f == null) throw new NullPointerException();
        if (this.values.add(f)) { this.n++; this.optimizeBandwidth(); return true;}
        else return false;
    }
    
    public Float getValue(Float x) {
        Float sum = 0.0f;
        for (Float f: this.values)
            sum += this.kernelValue((x-f)/this.h);
        return sum/(this.n*this.h);
    }
    
    public void writeEstimatorToFile(String filename) throws IOException {
        if (this.values.isEmpty()) return;
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        out.println(this.kernel);
        if (this.optimalBandwidth) out.println("-1");
        else out.println(this.h);
        out.println(this.n);
        for (Float f: this.values) out.println(f);
        out.close();
    }

    public static KernelDensityEstimator readEstimatorFromFile(String filename) throws IOException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        int kernel = Integer.parseInt(input.readLine());
        float bandwidth = Float.parseFloat(input.readLine());
        boolean optimal = (bandwidth < 0.0f)?true:false;
        int count = Integer.parseInt(input.readLine());
        ArrayList<Float> values = new ArrayList<Float>();
        for (int i = 0; i < count; i++)
            values.add(Float.parseFloat(input.readLine()));
        input.close();
        if (optimal) return generateEstimatorFromValues(kernel, values);
        else return generateEstimatorFromValues(kernel, bandwidth, values);
    }
    
    public void writeApproximateCurveToFile(String filename, int steps) throws IOException {
        if (steps < 1) throw new IllegalArgumentException();
        if (this.values.isEmpty()) return;
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        Float edge;
        switch (this.kernel) {
            case KDE_GAUSSIAN: edge = 3.0f; break;
            case KDE_EPANECHNIKOV: edge = new Float(Math.sqrt(5)); break;
            case KDE_TRIANGULAR: case KDE_RECTANGULAR: edge = 1.0f; break;
            default: throw new IllegalStateException("Unrecognized kernel function");
        }
        Float min = this.values.get(0) - edge;
        Float max = this.values.get(this.values.size()-1) + edge;
        out.println(min);
        out.println(max);
        Float increment = (max - min)/steps;
        for (Float f = min; f < max + increment; f += increment)
            out.println(this.getValue(f));
        out.close();
    }
}

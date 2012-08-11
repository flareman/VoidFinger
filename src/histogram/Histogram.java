package histogram;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class Histogram {
    private Integer[] bins;
    private Float h;
    private Float min, max;
    private Integer count;
    
    public Histogram(Integer n, Float min, Float max) throws HistogramException {
        if (n <= 0)
            throw new InvalidCreationArgumentsHistogramException();
        if (min > max)
            throw new InvalidCreationArgumentsHistogramException();
        this.min = min;
        this.max = max;
        this.count = n;
        this.bins = new Integer[this.count];
        Arrays.fill(this.bins, 0);
        this.h = (max-min)/n;
    }
    
    public Histogram(Integer n, Float min, Float max, Integer[] bins) throws HistogramException {
        if (n <= 0)
            throw new InvalidCreationArgumentsHistogramException();
        if (min > max)
            throw new InvalidCreationArgumentsHistogramException();
        if (bins.length != n)
            throw new InvalidCreationArgumentsHistogramException();
        this.min = min;
        this.max = max;
        this.count = n;
        this.h = (max-min)/n;
        this.bins = Arrays.copyOf(bins, this.count);
    }

    public void addPoint(Float p) {
        if (p < this.min || p > this.max) return;
        if (p == this.max) {
            this.bins[count-1]++;
            return;
        }
        for (int i = 0; i < this.count; i++)
            if (p >= this.min + this.h*i && p < this.min + this.h*(i+1)) {
                this.bins[i]++;
                return;
            }
    }
    
    public void addAll(Collection<Float> coll) {
        for (Float f: coll)
            this.addPoint(f);
    }
    
    public void printHistogram() {
        String output = "";
        output += "Number of bins: "+this.count+"\n";
        output += "Min/max: "+this.min+"/"+this.max+"\n";
        output += "==================================\n";
        int i = 0;
        for (Integer b: bins) 
            output += "Bin #"+(i++)+": "+b+"\n";
        System.out.print(output);
    }
    
    public void saveToFile(String filename) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(filename));
        out.println(this.count);
        out.println(this.min);
        out.println(this.max);
        for (Integer b: bins)
            out.println(b);
        out.close();
    }

    static public Histogram readFromFile(String filename) throws IOException, HistogramException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        Integer c = Integer.parseInt(input.readLine());
        Float m = Float.parseFloat(input.readLine());
        Float x = Float.parseFloat(input.readLine());
        if (c <= 0) throw new InvalidFileSyntaxHistogramException();
        if (m > x) throw new InvalidFileSyntaxHistogramException();
        Integer[] bins = new Integer[c];
        String nextBin;
        int i = 0;
        while ((nextBin = input.readLine()) != null) {
            bins[i] = Integer.parseInt(nextBin);
            if (bins[i++] < 0) throw new InvalidFileSyntaxHistogramException();
        }
        if (i != c - 1) throw new InvalidFileSyntaxHistogramException();
        input.close();
        return new Histogram(c, m, x, bins);
    }

    static public Histogram createFromCollection(Integer n, Collection<Float> coll) throws HistogramException {
        if (n < 1) throw new InvalidCreationArgumentsHistogramException();
        if (coll == null) throw new NullPointerException();
        if (coll.isEmpty()) throw new InvalidCreationArgumentsHistogramException();
        Histogram result = new Histogram(n, Collections.min(coll), Collections.max(coll));
        result.addAll(coll);
        return result;
    }
}

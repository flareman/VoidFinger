package kernel;

public class KDEDistance implements Comparable<KDEDistance> {
    private int a, b;
    private float distance;
    
    public KDEDistance(int alpha, int beta, float dist) {
        if (alpha < 0 || beta < 0 || dist < 0.0f) throw new IllegalArgumentException();
        this.a = alpha;
        this.b = beta;
        this.distance = dist;
    }
    
    @Override
    public int compareTo(KDEDistance e) {
        return Float.compare(this.distance, e.distance);
    }
    
    public Float getDistance() { return new Float(this.distance); }
    public int getAlpha() { return this.a; }
    public int getBeta() { return this.b; }
    public int[] getIDs() {
        int[] result = new int[2];
        result[0] = this.a;
        result[1] = this.b;
        return result;
    }
}

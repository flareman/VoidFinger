package potential;

import geometry.GeometryException;
import geometry.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class EPArray {
    private float[][][] potentials;
    private Integer[] steps = new Integer[3];
    private Float[] lengths = new Float[3];
    private Point origin;
    private Integer interpolationRange;
    
    public EPArray(Point o, Float[] s, Integer[] q, Integer d) {
        if (o == null || s == null || q == null || d == null)
            throw new NullPointerException();
        if (s.length != 3 || o.getDimensions() != 3 || q.length != 3 || d < 0)
            throw new IllegalArgumentException();
        this.origin = new Point(o);
        for (Float f: s) {
            if (f == null) throw new IllegalArgumentException(new NullPointerException());
            if (f <= 0.0f) throw new IllegalArgumentException();
        }
        for (Integer i: q) {
            if (i == null) throw new IllegalArgumentException(new NullPointerException());
            if (i < 1) throw new IllegalArgumentException();
        }
        this.interpolationRange = d;
        this.lengths = Arrays.copyOf(s, 3);
        this.steps = Arrays.copyOf(q, 3);
        this.potentials = new float[q[0]][q[1]][q[2]];
    }
    
    public void setPotentialForPoint(Integer[] coords, Float p) {
        if (coords == null) throw new NullPointerException();
        if (coords.length != 3) throw new IllegalArgumentException();
        this.setPotentialForPoint(coords[0], coords[1], coords[2], p);
    }

    public void setPotentialForPoint(Integer x, Integer y, Integer z, Float p) {
        if (x == null || y == null || z == null || p == null)
            throw new NullPointerException();
        if (x < 0 || x >= this.steps[0] || y < 0 || y >= this.steps[1] ||
                z < 0 || z >= this.steps[2])
            throw new IllegalArgumentException();
        this.potentials[x][y][z] = p;
    }
    
    public Float getPotentialForCoordinates(Point p) {
        if (p == null) throw new NullPointerException();
        if (p.getDimensions() != 3) throw new IllegalArgumentException();
        return this.getPotentialForCoordinates(p.getCoords());
    }

    public Float getPotentialForCoordinates(Float[] c) {
        if (c == null) throw new NullPointerException();
        return this.getPotentialForCoordinates(c[0], c[1], c[2]);
    }

    public Float getPotentialForCoordinates(Float x, Float y, Float z) {
        if (x == null || y == null || z == null)
            throw new NullPointerException();
        try {
            Float rx = x - this.origin.getCoordinate(0);
            Float ry = y - this.origin.getCoordinate(1);
            Float rz = z - this.origin.getCoordinate(2);
            int a = (int)(rx / this.lengths[0]) + ((rx % this.lengths[0] < this.lengths[0] / 2)?0:1);
            int b = (int)(ry / this.lengths[1]) + ((ry % this.lengths[1] < this.lengths[1] / 2)?0:1);
            int c = (int)(rz / this.lengths[2]) + ((rz % this.lengths[2] < this.lengths[2] / 2)?0:1);
            Float sum = 0.0f;
            Integer pointsAdded = 0;
            for (int i = -this.interpolationRange; i <= this.interpolationRange; i++)
                for (int j = -this.interpolationRange; j <= this.interpolationRange; j++)
                    for (int k = -this.interpolationRange; k <= this.interpolationRange; k++)
                        if (i >= 0 && j >= 0 && k >= 0 &&
                                i < this.steps[0] && j < this.steps[1] && k < this.steps[2]) {
                            pointsAdded++;
                            sum += this.potentials[i][j][k];
                        }
            return sum / pointsAdded;
        } catch (GeometryException ge) {}
        throw new IllegalStateException();
    }
    
    public static EPArray readArrayFromFile(String filename, Integer d) throws IOException, EPArrayException {
        BufferedReader input = new BufferedReader(new FileReader(filename));
        String line = "";
        Float orx = 0.0f, ory = 0.0f, orz = 0.0f;
        Float[] sides = new Float[3];
        Integer[] dims = new Integer[3];
        Integer items = 0;
        EPArray result;
        while (input.ready()) {
            line = input.readLine();
            if (line.charAt(0) != '#') break;
        }
        if (line.startsWith("object 1 class gridpositions counts")) {
            String[] tokens = line.split(" ");
            if (tokens.length != 8) throw new InvalidFileSyntaxEPArrayException();
            dims[0] = Integer.parseInt(tokens[5]);
            dims[1] = Integer.parseInt(tokens[6]);
            dims[2] = Integer.parseInt(tokens[7]);
        } else throw new InvalidFileSyntaxEPArrayException();
        line = input.readLine();
        if (line.startsWith("origin")) {
            String[] tokens = line.split(" ");
            if (tokens.length != 4) throw new InvalidFileSyntaxEPArrayException();
            orx = Float.parseFloat(tokens[1]);
            ory = Float.parseFloat(tokens[2]);
            orz = Float.parseFloat(tokens[3]);
        } else throw new InvalidFileSyntaxEPArrayException();
        for (int i = 0; i < 3; i++) {
            line = input.readLine();
            if (line.startsWith("delta")) {
                String[] tokens = line.split(" ");
                if (tokens.length != 4) throw new InvalidFileSyntaxEPArrayException();
                sides[i] = Float.parseFloat(tokens[i+1]);
            } else throw new InvalidFileSyntaxEPArrayException();
        }
        line = input.readLine();
        if (!line.equals("object 2 class gridconnections counts "+dims[0]+" "+dims[1]+" "+dims[2]))
            throw new InvalidFileSyntaxEPArrayException();
        result = new EPArray(new Point(orx, ory, orz), sides, dims, d);
        line = input.readLine();
        if (!line.startsWith("object 3 class array type double rank"))
            throw new InvalidFileSyntaxEPArrayException();
        else {
            String[] tokens = line.split(" ");
            if (tokens.length != 12) throw new InvalidFileSyntaxEPArrayException();
            items = Integer.parseInt(tokens[9]);
        }
        if (items < 1 || items != dims[0]*dims[1]*dims[2]) throw new InvalidFileSyntaxEPArrayException();
        int x = 0, y = 0, z = 0;
        while (items > 0) {
            if (input.ready()) line = input.readLine();
            else throw new InvalidFileSyntaxEPArrayException();
            String[] values = line.split(" ");
            for (String f: values) {
                result.setPotentialForPoint(x, y, z, Float.parseFloat(f));
                if (++z >= dims[2]) {
                    z = 0;
                    if (++y >= dims[1]) {
                        y = 0;
                        if (++x >= dims[0]) {
                            if (items > values.length)
                                throw new InvalidFileSyntaxEPArrayException();
                        }
                    }
                }
            }
            items -= values.length;
        }
        return result;
    }
}

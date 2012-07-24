package kdtree;

import java.util.Arrays;

public class KDTreeCell {
    private Integer splitDimension;
    private Integer dimensions;
    private Float[] lengths = null;
    private Float splitValue;
    private Boolean isLeaf;
    private Boolean isEmpty;
    private KDTreeCell[] children = null;
    private Float[] point = null;
    
    public KDTreeCell(Integer dimensions, Float[] lengths) throws KDTreeCellException {
        this.isEmpty = true;
        this.isLeaf = true;
        this.children = null;
        this.point = null;
        if (dimensions <= 0)
            throw new KDTreeCellWrongCreationArgumentException();
        if (lengths.length != dimensions)
            throw new KDTreeCellWrongCreationArgumentException();
        this.splitDimension = 0;
        Float maxDimension = lengths[0];
        for (int i = 0; i < dimensions; i++) {
            if (lengths[i] < 0)
                throw new KDTreeCellWrongCreationArgumentException();
            if (lengths[i] > maxDimension) {
                maxDimension = lengths[i];
                this.splitDimension = i;
            }
        }
        this.lengths = new Float[lengths.length];
        this.lengths = Arrays.copyOf(lengths, lengths.length);
        this.dimensions = dimensions;
        this.splitValue = maxDimension / 2;
    }
    
    public void addPoint(Float[] coords) throws KDTreeCellException {
        if (this.isLeaf)
            if (this.isEmpty) {
                this.isEmpty = false;
                if (coords.length != this.dimensions)
                    throw new KDTreeCellWrongPointCoordinatesException();
                this.point = new Float[coords.length];
                this.point = Arrays.copyOf(coords, coords.length);
            } else {
                Float[] newLengths = new Float[this.dimensions];
                newLengths = Arrays.copyOf(this.lengths, this.dimensions);
                newLengths[this.splitDimension] /= 2;
                this.children = new KDTreeCell[2];
                this.children[0] = new KDTreeCell(this.dimensions, newLengths);
                this.children[1] = new KDTreeCell(this.dimensions, newLengths);
                if (this.splitValue - this.point[this.splitDimension] <= 0)
                    this.children[0].addPoint(this.point);
                else this.children[1].addPoint(this.point);
                if (this.splitValue - coords[this.splitDimension] <= 0)
                    this.children[0].addPoint(coords);
                else this.children[1].addPoint(coords);
                this.point = null;
                this.isLeaf = false;
            }
        else {
            if (this.splitValue - coords[this.splitDimension] <= 0)
                this.children[0].addPoint(coords);
            else this.children[1].addPoint(coords);
        }
    }
    
    public Integer getMaxDepth() {
        if (this.isLeaf) return (this.isEmpty)?0:1;
        else return this.children[0].getMaxDepth() + this.children[1].getMaxDepth();
    }
}

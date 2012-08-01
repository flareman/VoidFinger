package visibilityGraph;
import geometry.GeometryException;
import geometry.Point;

public class GraphNode {
    private Point point;
    private int numOfDims;
    
    public GraphNode(int dimNum, Point dims) throws InvalidNumberOfDimensionsException{
        numOfDims = dimNum;
        if(dimNum != dims.getDimensions())
            throw new InvalidNumberOfDimensionsException();
        point = dims;
    }
    
    public int getNumOfDims(){
        return this.numOfDims;
    }
    
    public Float getDimension(int dim,int numOfDims) throws InvalidDimensionRequestException,GeometryException{
        if(dim >= numOfDims || dim < 0){
            throw new InvalidDimensionRequestException();
        }
        return point.getCoordinate(dim);
    }
    
    public Point getPoint(){
        return this.point;
    }
}

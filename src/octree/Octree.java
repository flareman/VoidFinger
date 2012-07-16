package octree;

import java.io.*;
import java.util.Arrays;

public class Octree {
    private Integer depth;
    private Float length, origin[];
    private OctNode root = null;
    
    private Octree(Integer depth, Float[] origin, Float length, OctNode root) {
        this.depth = depth;
        this.root = root;
        origin = new Float[3];
        this.origin = Arrays.copyOf(origin, 3);
        this.length = length;
    }
    
    public OctNode getRoot() { return root; }
    
    static public Octree parseFromFile(String filename) throws FileNotFoundException, IOException, OctreeException, OctNodeException {
        // Open filename and read content to byte buffer
        File file = new File(filename);
        FileInputStream fis = new FileInputStream(file);
        byte contents[] = new byte[(int)file.length()];
        if (fis.read(contents) != file.length())
            throw new IOException("Unable to read entire file.");
        
        // Check header for correct descriptor
        if (!(new String(contents, 0, 14)).equals("SOG.Format 1.0"))
            throw new InvalidSOGFileSyntaxOctreeException();

        // Read origin of axes and cube length
        DataInputStream header = new DataInputStream(new ByteArrayInputStream(contents, 14, 114));
        Float origin[] = new Float[3];
        origin[0] = header.readFloat();
        origin[1] = header.readFloat();
        origin[2] = header.readFloat();
        Float length = header.readFloat();
        
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(contents, 128, contents.length-128));
        OctNode newRoot = recurseParse(stream);
        Integer depth = newRoot.getMaxDepth();
        return new Octree(depth, origin, length, newRoot);
    }
    
    static private OctNode recurseParse(DataInputStream stream) {
        // Actual parsing happens here; create an appropriate OctNode and return it, after calling self recursively if needed
        return null;
    }
}

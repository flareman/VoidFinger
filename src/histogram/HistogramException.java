package histogram;

public class HistogramException extends Exception {
    public HistogramException() { super(); }
    public HistogramException(String str) { super(str); }
}

class InvalidCreationArgumentsHistogramException extends HistogramException {
    public InvalidCreationArgumentsHistogramException() {
        super("Both number of bins and total value range must be positive");
    }
}

class InvalidFileSyntaxHistogramException extends HistogramException {
    public InvalidFileSyntaxHistogramException() {
        super("Histogram file syntax is invalid");
    }
}

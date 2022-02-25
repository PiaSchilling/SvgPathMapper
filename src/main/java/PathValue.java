import java.util.ArrayList;
import java.util.List;

public class PathValue {

    public String identifier;
    public String mappedIdentifier;
    public int coordinateCount;
    public List<String> coordinates;

    public PathValue(String identifier, String mappedIdentifier, int coordinateCount,List<String> args) {
        this.identifier = identifier;
        this.mappedIdentifier = mappedIdentifier;
        this.coordinateCount = coordinateCount;
        this.coordinates = args;
    }

    /**
     * to copy the path value except for the argument list
     * @param oldValue the path value to copy the first paramters of
     * @param args the coordinate list
     */
    public PathValue(PathValue oldValue,List<String> args){
        this.identifier = oldValue.identifier;
        this.mappedIdentifier = oldValue.mappedIdentifier;
        this.coordinateCount = oldValue.coordinateCount;
        this.coordinates = args;
    }

    public void setCoordinates(List<String> args){
        this.coordinates = args;
    }

    @Override
    public String toString() {
        return "PathValue{" +
                "identifier='" + identifier + '\'' +
                ", mappedIdentifier='" + mappedIdentifier + '\'' +
                ", coordinateCount=" + coordinateCount +
                ", coordinates=" + coordinates +
                '}';
    }
}

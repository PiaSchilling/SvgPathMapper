import java.util.List;

/**
 * Class represents pathValue objects to hold the mapped path coordinates
 */
public class PathValue {

    public String identifier; //M,m,H,h ...
    public String mappedIdentifier; //MoveTo, moveToRelative, HorizontalLineTo, horizontalLineToRelative, ...
    public int coordinateCount; //how many coordinates the value has (e.g. MoveTo(x,y) has two HorizontalLineTo(x) has one)
    public List<String> coordinates; //list containing the coordinates of the pathValue

    public PathValue(String identifier, String mappedIdentifier, int coordinateCount,List<String> coordinates) {
        this.identifier = identifier;
        this.mappedIdentifier = mappedIdentifier;
        this.coordinateCount = coordinateCount;
        this.coordinates = coordinates;
    }

    /**
     * to copy the path value except for the argument list
     * @param oldValue the path value to copy the first parameters of
     * @param newCoordinates the new coordinate list
     */
    public PathValue(PathValue oldValue,List<String> newCoordinates){
        this.identifier = oldValue.identifier;
        this.mappedIdentifier = oldValue.mappedIdentifier;
        this.coordinateCount = oldValue.coordinateCount;
        this.coordinates = newCoordinates;
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

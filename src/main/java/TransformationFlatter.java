import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class is responsible to handle everything related to the transformation attribute
 */
public class TransformationFlatter {

    private final HashMap<String, Double> matrixMap = new HashMap<>();

    /**
     * checks if the svgFile contains the transform attribute
     *
     * @param fileString the string which contains the whole svg file
     * @return true if transformed false if not, if false no transformation needed
     */
    public boolean checkTransformed(String fileString) {
        return fileString.contains("transform") && fileString.contains("matrix");
    }

    /**
     * extracts the part of the fileString which contains the matrix values
     *
     * @param fileString the string which contains the whole svg file
     * @return a string containing only the matrix values
     */
    private String extractTransformationMatrixString(String fileString) {
        int beginnIndex = fileString.indexOf("matrix");
        int endIndex = fileString.indexOf(")", beginnIndex);

        String matrixString = fileString.substring(beginnIndex, endIndex);

        return matrixString.substring(7);
    }

    /**
     * creates a matrix map from the matrix string
     *
     * @param matrixString a string containing only the matrix values
     * @return a hashmap with a-f as keys and the matrix values as values
     */
    private HashMap<String, Double> mapTransformationMatrix(String matrixString) {
        String[] matrixValueStrings = matrixString.split(",");
        List<Double> matrixValues = Arrays.stream(matrixValueStrings).map(Double::valueOf).collect(Collectors.toList());

        List<String> keyList = new ArrayList<>();
        keyList.add("a");
        keyList.add("b");
        keyList.add("c");
        keyList.add("d");
        keyList.add("e");
        keyList.add("f");

        for (int i = 0; i < matrixValues.size(); i++) {
            matrixMap.put(keyList.get(i), matrixValues.get(i));
        }

        return matrixMap;
    }

    /**
     * calculates the pathValue coordinates new based on the values in the transformation matrix
     * @param fileString the string which contains the whole svg file
     * @param pathValues list of "old"/untransformed pathValue objects
     * @return list of all transformed pathValues
     */
    public List<PathValue> flatTransformation(String fileString, List<PathValue> pathValues) {

            String matrixString = extractTransformationMatrixString(fileString);
            mapTransformationMatrix(matrixString);

            List<PathValue> transformedPathValues = new ArrayList<>();

            for (PathValue p : pathValues) {

                List<String> coordinateStrings = p.coordinates;
                List<Double> coordinates = new ArrayList<>();

                if(!p.identifier.equals("Z") && !p.identifier.equals("z")){
                    coordinates = coordinateStrings.stream().map(Double::valueOf).collect(Collectors.toList());
                }

                switch (p.identifier) {
                    case "M", "m", "L", "l", "T", "t" -> transformedPathValues.add(mltTransformation(p, coordinates));
                    case "H", "h" -> transformedPathValues.add(hTransformation(p, coordinates));
                    case "V", "v" -> transformedPathValues.add(vTransformation(p, coordinates));
                    case "C", "c" -> transformedPathValues.add(cTransformation(p, coordinates));
                    case "S", "s", "Q", "q" -> transformedPathValues.add(sqTransformation(p, coordinates));
                    case "A", "a" -> transformedPathValues.add(aTransformation(p, coordinates));
                    case "Z", "z" -> transformedPathValues.add(p);
                }

            }
            return transformedPathValues;
    }

    /**
     * calculating the coordinates of h and H PathValues new based on the transformation matrix values
     * @param pathValue the pathValue which coordinates should be calculated
     * @param coordinates already extracted list of the pathValues coordinates
     * @return the pathValue with an updated list of newly calculated coordinates
     */
    private PathValue hTransformation(PathValue pathValue, List<Double> coordinates) {
        if (StringUtils.isAllLowerCase(pathValue.identifier)) {
            pathValue.coordinates.set(0,calculateXRelative(coordinates.get(0), 0d).toString());
        } else {
            pathValue.coordinates.set(0,calculateX(coordinates.get(0), 0d).toString());
        }
        return pathValue;
    }

    /**
     * calculating the coordinates of v and V PathValues new based on the transformation matrix values
     * @param pathValue the pathValue which coordinates should be calculated
     * @param coordinates already extracted list of the pathValues coordinates
     * @return the pathValue with an updated list of newly calculated coordinates
     */
    private PathValue vTransformation(PathValue pathValue, List<Double> coordinates) {
        if (StringUtils.isAllLowerCase(pathValue.identifier)) {
            pathValue.coordinates.set(0,calculateYRelative(0d,coordinates.get(0)).toString());
        } else {
            pathValue.coordinates.set(0,calculateY(0d,coordinates.get(0)).toString());
        }
        return pathValue;
    }

    /**
     * calculating the coordinates of m,M,l,L and t,T PathValues new based on the transformation matrix values
     * @param pathValue the pathValue which coordinates should be calculated
     * @param coordinates already extracted list of the pathValues coordinates
     * @return the pathValue with an updated list of newly calculated coordinates
     */
    private PathValue mltTransformation(PathValue pathValue, List<Double> coordinates){
        Double oldX = coordinates.get(0);
        Double oldY = coordinates.get(1);

        List<String> newCoordinates = new ArrayList<>();

        if (StringUtils.isAllLowerCase(pathValue.identifier)) {
            newCoordinates.add(calculateXRelative(oldX,oldY).toString());
            newCoordinates.add(calculateYRelative(oldX,oldY).toString());
        } else {
            newCoordinates.add(calculateX(oldX,oldY).toString());
            newCoordinates.add(calculateY(oldX,oldY).toString());
        }

        pathValue.coordinates = newCoordinates;
        return pathValue;
    }

    /**
     * calculating the coordinates of c and C PathValues new based on the transformation matrix values
     * @param pathValue the pathValue which coordinates should be calculated
     * @param coordinates already extracted list of the pathValues coordinates
     * @return the pathValue with an updated list of newly calculated coordinates
     */
    private PathValue cTransformation(PathValue pathValue, List<Double> coordinates){
        Double oldX1 = coordinates.get(0);
        Double oldY1 = coordinates.get(1);
        Double oldX2 = coordinates.get(2);
        Double oldY2 = coordinates.get(3);
        Double oldX = coordinates.get(4);
        Double oldY = coordinates.get(5);

        List<String> newCoordinates = new ArrayList<>();

        if (StringUtils.isAllLowerCase(pathValue.identifier)) {
            newCoordinates.add(calculateXRelative(oldX1,oldY1).toString());
            newCoordinates.add(calculateYRelative(oldX1,oldY1).toString());
            newCoordinates.add(calculateXRelative(oldX2,oldY2).toString());
            newCoordinates.add(calculateYRelative(oldX2,oldY2).toString());
            newCoordinates.add(calculateXRelative(oldX,oldY).toString());
            newCoordinates.add(calculateYRelative(oldX,oldY).toString());
        } else {
            newCoordinates.add(calculateX(oldX1,oldY1).toString());
            newCoordinates.add(calculateY(oldX1,oldY1).toString());
            newCoordinates.add(calculateX(oldX2,oldY2).toString());
            newCoordinates.add(calculateY(oldX2,oldY2).toString());
            newCoordinates.add(calculateX(oldX,oldY).toString());
            newCoordinates.add(calculateY(oldX,oldY).toString());

        }
        pathValue.coordinates = newCoordinates;
        return pathValue;
    }

    /**
     * calculating the coordinates of s,S and q,Q PathValues new based on the transformation matrix values
     * @param pathValue the pathValue which coordinates should be calculated
     * @param coordinates already extracted list of the pathValues coordinates
     * @return the pathValue with an updated list of newly calculated coordinates
     */
    private PathValue sqTransformation(PathValue pathValue, List<Double> coordinates){
        Double oldX1 = coordinates.get(0);
        Double oldY1 = coordinates.get(1);
        Double oldX = coordinates.get(4);
        Double oldY = coordinates.get(5);

        List<String> newCoordinates = new ArrayList<>();

        if (StringUtils.isAllLowerCase(pathValue.identifier)) {
            newCoordinates.add(calculateXRelative(oldX1,oldY1).toString());
            newCoordinates.add(calculateYRelative(oldX1,oldY1).toString());
            newCoordinates.add(calculateXRelative(oldX,oldY).toString());
            newCoordinates.add(calculateYRelative(oldX,oldY).toString());
        } else {
            newCoordinates.add(calculateX(oldX1,oldY1).toString());
            newCoordinates.add(calculateY(oldX1,oldY1).toString());
            newCoordinates.add(calculateX(oldX,oldY).toString());
            newCoordinates.add(calculateY(oldX,oldY).toString());

        }
        pathValue.coordinates = newCoordinates;
        return pathValue;
    }

    /**
     * calculating the coordinates of a and A PathValues new based on the transformation matrix values
     * @param pathValue the pathValue which coordinates should be calculated
     * @param coordinates already extracted list of the pathValues coordinates
     * @return the pathValue with an updated list of newly calculated coordinates
     */
    private PathValue aTransformation(PathValue pathValue, List<Double> coordinates){
        Double oldX = coordinates.get(5);
        Double oldY = coordinates.get(6);

        String newX;
        String newY;

        if (StringUtils.isAllLowerCase(pathValue.identifier)) {
            newX = calculateXRelative(oldX,oldY).toString();
            newY = calculateYRelative(oldX,oldY).toString();
        } else {
            newX = calculateX(oldX,oldY).toString();
            newY = calculateY(oldX,oldY).toString();
        }

        pathValue.coordinates.set(5,newX);
        pathValue.coordinates.set(6,newY);
        return pathValue;
    }

    /**
     * calculate new x value of non relative coordinates
     * @param xCoordinate x value of the coordinate pair
     * @param yCoordinate y value of the coordinate pair
     * @return the new value of the x
     */
    private Double calculateX(Double xCoordinate, Double yCoordinate) {
        return matrixMap.get("a") * xCoordinate + matrixMap.get("c") * yCoordinate + matrixMap.get("e");
    }

    /**
     * calculate new x value of relative coordinates
     * @param xCoordinate x value of the coordinate pair
     * @param yCoordinate y value of the coordinate pair
     * @return the new value of the x
     */
    private Double calculateXRelative(Double xCoordinate, Double yCoordinate) {
        return matrixMap.get("a") * xCoordinate + matrixMap.get("c") * yCoordinate;
    }

    /**
     * calculate new y value of non relative coordinates
     * @param xCoordinate x value of the coordinate pair
     * @param yCoordinate y value of the coordinate pair
     * @return the new value of the y
     */
    private Double calculateY(Double xCoordinate, Double yCoordinate) {
        return matrixMap.get("b") * xCoordinate + matrixMap.get("d") * yCoordinate + matrixMap.get("f");
    }

    /**
     * calculate new y value of relative coordinates
     * @param xCoordinate x value of the coordinate pair
     * @param yCoordinate y value of the coordinate pair
     * @return the new value of the y
     */
    private Double calculateYRelative(Double xCoordinate, Double yCoordinate) {
        return matrixMap.get("b") * xCoordinate + matrixMap.get("d") * yCoordinate;
    }

}

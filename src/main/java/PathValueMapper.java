import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PathValueMapper {

    private final List<PathValue> pathValues = new ArrayList<>(); //list of all PathValues
    private final List<PathValue> checkedPathValues = new ArrayList<>(); //list of all PathValues after checking (see checkPathValues())
    private String fileString; //string contains the whole svg file content
    private String extractedPathString; //string contains only the needed part of the fileString (after d=)


    public String mapFile(String fileUrl) {
        readFile(fileUrl);
        extractPathString();
        splitString();
        checkPathValues();
        return formatOutput();
    }


    /**
     * reads the file and saves the file content to a string
     * @param svgFileUrl the url to the svg input file
     */
    private void readFile(String svgFileUrl) {

        File file = new File(svgFileUrl);
        StringBuilder fileStringBuilder = new StringBuilder();

        try( Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                fileStringBuilder.append(scanner.nextLine());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(fileNotFoundException.getMessage());
        }
        fileString = fileStringBuilder.toString();
    }

    /**
     * extracts the needed part of the fileString
     * needed is the "<path>" content after the "d=" indicator
     */
    private void extractPathString() {
        int startIndex = 0;
        try {
            startIndex = fileString.indexOf(" d=");
            int endIndex = fileString.indexOf(" transform");

            if (startIndex > -1) {
                extractedPathString = fileString.substring(startIndex, endIndex);
            } else {
                throw new IllegalArgumentException("File contains no d= ");
            }
        } catch (IndexOutOfBoundsException e) {
            //if there ist no "transform" look for the next " after d=
            int endIndex = fileString.indexOf("\"", startIndex + 4);
            extractedPathString = fileString.substring(startIndex, endIndex);
        }

    }

    /**
     * splits the string into the single path coordinates and calls mapPathValues() for every pathCoordinate
     */
    private void splitString() {
        String[] splitPathCoordinates = extractedPathString.split("(?=[a-zA-Z])");
        for (String s : splitPathCoordinates) {
            if (!s.isEmpty() && !s.isBlank() && !s.startsWith("d") && !s.startsWith("\"")) {
                mapPathValues(s);
            }
        }
    }

    /**
     * creates PathValue objects from pathCoordinateStrings
     *
     * @param pathCoordinateString pathCoordinateString which should be mapped into a pathValue-Object
     */
    private void mapPathValues(String pathCoordinateString) {
        char identifier = pathCoordinateString.charAt(0); //leading letter M,m,H,h...
        String coordinatesOnlyString = pathCoordinateString.substring(1); //remove identifier

        String[] splitCoordinatesMinus = coordinatesOnlyString.split("(?=[-])"); //separate between minus values

        //separate between every comma
        List<String> splitCoordinatesComma = new ArrayList<>();
        for (String s : splitCoordinatesMinus) {
            String[] splitValues = s.split("[,]");
            splitCoordinatesComma.addAll(Arrays.asList(splitValues));
        }

        List<String> coordinates = splitLeadingNullCoordinates(splitCoordinatesComma);

        //create pathValue objects
        switch (identifier) {
            case 'M' -> pathValues.add(new PathValue("M", "moveTo", 2, coordinates));
            case 'm' -> pathValues.add(new PathValue("m", "moveToRelative", 2, coordinates));
            case 'L' -> pathValues.add(new PathValue("L", "lineTo", 2, coordinates));
            case 'l' -> pathValues.add(new PathValue("l", "lineToRelative", 2, coordinates));
            case 'H' -> pathValues.add(new PathValue("H", "horizontalLineTo", 1, coordinates));
            case 'h' -> pathValues.add(new PathValue("h", "horizontalLineToRelative", 1, coordinates));
            case 'V' -> pathValues.add(new PathValue("V", "verticalLineTo", 1, coordinates));
            case 'v' -> pathValues.add(new PathValue("v", "verticalLineToRelative", 1, coordinates));
            case 'Z', 'z' -> pathValues.add(new PathValue("Z", "close", 0, coordinates));
            case 'C' -> pathValues.add(new PathValue("C", "curveTo", 6, coordinates));
            case 'c' -> pathValues.add(new PathValue("c", "curveToRelative", 6, coordinates));
            case 'S' -> pathValues.add(new PathValue("S", "reflectiveCurveTo", 4, coordinates));
            case 's' -> pathValues.add(new PathValue("s", "reflectiveCurveToRelative", 4, coordinates));
            case 'Q' -> pathValues.add(new PathValue("Q", "quadTo", 4, coordinates));
            case 'q' -> pathValues.add(new PathValue("q", "quadToRelative", 4, coordinates));
            case 'T' -> pathValues.add(new PathValue("T", "reflectiveQuadTo", 2, coordinates));
            case 't' -> pathValues.add(new PathValue("t", "reflectiveQuadToRelative", 2, coordinates));
            case 'A' -> pathValues.add(new PathValue("A", "arcTo", 7, coordinates));
            case 'a' -> pathValues.add(new PathValue("a", "arcToRelative", 7, coordinates));
        }
    }

    /**
     * svg files dont separate leading null values with commas
     * (e.g. c.077.88.11,1.639.11,2.3 need to be converted into c.077,.88,.11,1.639,.11,2.3 to be able to spilt it up correctly)
     * @param splitCoordinatesComma list of coordinates which might contains multiple coordinates in one string (e.g. like so [.077.88.11 | 1.639.11 | 2.3] but needs to be  [.077 | .88 | .11 | 1.639 | .11 | 2.3]
     * @return the list of correct split coordinates
     */
    private List<String> splitLeadingNullCoordinates(List<String> splitCoordinatesComma){

        //separate between leading 0 values
        List<String> splitCoordinatesNull = new ArrayList<>(); //contains the split leading 0 values
        List<String> splitCoordinatesNullCopy = new ArrayList<>(splitCoordinatesComma); //needed to loop and manipulate the list at the same time

        for (String s : splitCoordinatesComma) {

            int dotCount = StringUtils.countMatches(s,".");

            if (dotCount > 1) { //if there is more than one "." there are multiple coordinates in this string -> needs to be split up

                StringBuilder addedCommaBuilder = new StringBuilder(); //holds the coordinate string with added commas where needed
                int firstDotIndex = s.indexOf('.');

                //build the coordinate string new and add commas between leading 0 values so it can be spilt up later
                for (int i = firstDotIndex; i < s.length(); i++) {
                    if (firstDotIndex != 0) {
                        addedCommaBuilder.append(s, 0, firstDotIndex);
                        firstDotIndex = 0;
                    }
                    addedCommaBuilder.append(s.charAt(i));
                    if (i != s.length() - 1 && s.charAt(i + 1) == '.') {
                        addedCommaBuilder.append(",");
                    }
                }

                if (!addedCommaBuilder.toString().isEmpty()) {
                    splitCoordinatesNull.clear();
                    String[] split = addedCommaBuilder.toString().split(",");
                    splitCoordinatesNull.addAll(Arrays.asList(split));

                    int index = splitCoordinatesNullCopy.indexOf(s);
                    splitCoordinatesNullCopy.remove(index);
                    splitCoordinatesNullCopy.addAll(index, splitCoordinatesNull);
                    addedCommaBuilder.delete(0, addedCommaBuilder.length());
                }
            }
        }
        return splitCoordinatesNullCopy;
    }

    /**
     * pathValue objects might have too many coordinates in its coordinates list
     * (e.g. moveTo(2,3,4,1) but moveTo only has two coordinates -> needs to be split up into two pathValue Objects like so moveTo(2,3) moveTo(4,1)
     */
    private void checkPathValues() {

        for (PathValue p : pathValues) {
            if (!p.identifier.equals("Z") && p.coordinateCount != p.coordinates.size()) { //if the coordinates amount in the list differs form the coordinatesCount value it needs to be split
                try {
                    for (int i = 0; i < p.coordinates.size(); ) {
                        List<String> coordinatesOfOnePathValue = p.coordinates.subList(i, i + p.coordinateCount);
                        checkedPathValues.add(new PathValue(p, coordinatesOfOnePathValue));
                        i += p.coordinateCount;
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else {
                checkedPathValues.add(p); //if coordinates amount in the list is same as coordinatesAmount the pathValue objects is correct and can be added without modification
            }
        }
    }

    /**
     * creates the output string from the pathValue objects by concatenating the attributes in a specific pattern
     * @return the final output string representing the mapped pathValue objects
     */
    private String formatOutput() {
        StringBuilder builder = new StringBuilder();
        for (PathValue p : checkedPathValues) {
            builder.append(" ".repeat(15));
            builder.append(p.mappedIdentifier);
            builder.append("(");

            if (p.identifier.equals("A") || p.identifier.equals("a")) { //map the 0 and 1 to true or false when the identifier is an A or a
                if (p.coordinates.get(3).equals("0")) {
                    p.coordinates.set(3, "false");
                } else {
                    p.coordinates.set(3, "true");
                }

                if (p.coordinates.get(4).equals("0")) {
                    p.coordinates.set(4, "false");
                } else {
                    p.coordinates.set(4, "true");
                }
            }

            if (p.coordinateCount != 0) {
                for (String c : p.coordinates) { //append coordinates
                    builder.append(c);
                    if (!c.equals("true") && !c.equals("false")) { //only append an f if the coordinate contains not the value true or false
                        builder.append("f");
                    }
                    builder.append(",");
                }

                builder.deleteCharAt(builder.length() - 1); //delete the comma after the last coordinate
            }

            builder.append(")");
            builder.append("\n");
        }
        return builder.toString();
    }
}

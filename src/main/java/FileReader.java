import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class FileReader {

    /*PathValue M = new PathValue("M","moveTo",2);
    PathValue m = new PathValue("m","moveToRelative",2);

    PathValue H = new PathValue("H","horizontalLineTo",1);
    PathValue h = new PathValue("h","horizontalLineToRelative",1);

    PathValue V = new PathValue("V", "verticalLineTo",1);
    PathValue v = new PathValue("v","verticalLineToRelative",1);*/

    public static void main(String[] args) {
        FileReader fileReader = new FileReader();

     /*  String fileString = fileReader.readFile(args[0]);
       String trimmedString = fileReader.trimString(fileString);
       fileReader.splitString(trimmedString);

        fileReader.checkPathValues();*/

        String fileString = fileReader.readFile(args[0]);
        fileReader.trimString();
        fileReader.splitString();
        fileReader.checkPathValues();
        fileReader.formatOutput();

    }

    private List<PathValue> pathValues = new ArrayList<>();
    private List<PathValue> checkedPathValues = new ArrayList<>();
    private String fileString;
    private String trimmedString;
    private String[] splitStringValues;

    /**
     * reads the file and saves the file content to a string
     *
     * @param filePath the path to the file
     * @return the string containing the file content
     */
    private String readFile(String filePath) {
        File file = new File(filePath);
        StringBuffer fileBuffer = new StringBuffer();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                fileBuffer.append(scanner.nextLine());
            }

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        this.fileString = fileBuffer.toString();
        return fileBuffer.toString();
    }

    /**
     * splits the fileString after the d=
     *
     * @return the trimmed string only containing the information after the d=
     */
    private void trimString() {
        int startIndex = 0;
        try{
            startIndex = fileString.indexOf(" d=");
            int endIndex = fileString.indexOf(" transform");

            if (startIndex > -1) {
                trimmedString = fileString.substring(startIndex, endIndex);
            } else {
                throw new IllegalArgumentException("File contains no d= ");
            }
        }catch (IndexOutOfBoundsException e){
            int endIndex = fileString.indexOf("\"",startIndex+4);
            trimmedString = fileString.substring(startIndex, endIndex);
        }

    }

    /**
     * splits before every word character
     */
    private void splitString() {
        splitStringValues = trimmedString.split("(?=[a-zA-Z])");
        for (String s : splitStringValues) {
            if (!s.isEmpty() && !s.isBlank() && !s.startsWith("d") && !s.startsWith("\"")) {
                System.out.println(s);
                mapPathValues(s);
            }
        }
    }

    /**
     * creates PathValue objects form strings
     *
     * @param pathValue string to map into a pathValue
     * @return
     */
    private List<PathValue> mapPathValues(String pathValue) {
        char identifier = pathValue.charAt(0); //leading letter

        String paramString = pathValue.substring(1); //remove identifier
        String[] splitArgs = paramString.split("(?=[-])"); //seperate minues values

        //split on every comma
        List<String> finalSplitArgs = new ArrayList<>();
        for (String s : splitArgs) {
            String[] temp = s.split("[,]");
            finalSplitArgs.addAll(Arrays.asList(temp));
        }

        List<String> leadingNullSplitValues = new ArrayList<>(); //contains the split leading 0 values
        List<String> finalSplitArgsCopy = new ArrayList<>(finalSplitArgs); // copy of the original array

        for (String s : finalSplitArgs){
            List<Character> characters = s.chars().mapToObj(c -> (char) c).collect(Collectors.toList());

            long dotCount = characters.stream().filter(c -> c == '.').count();

            if(dotCount > 1){ //there are multiple coordinates in this string -> needs to be split up
                StringBuffer commasAdded = new StringBuffer();
                int firstDotIndex = s.indexOf('.');

                for (int i = firstDotIndex; i < s.length(); i++) {
                    if(firstDotIndex != 0){
                        commasAdded.append(s, 0, firstDotIndex);
                        firstDotIndex = 0;
                    }
                    commasAdded.append(s.charAt(i));
                    if(i != s.length()-1 && s.charAt(i+1) == '.'){
                        commasAdded.append(",");
                    }
                }

                if(!commasAdded.toString().isEmpty()){
                    leadingNullSplitValues.clear();
                    String[] split = commasAdded.toString().split(",");
                    leadingNullSplitValues.addAll(Arrays.asList(split));

                    int index = finalSplitArgsCopy.indexOf(s);
                    finalSplitArgsCopy.remove(index);
                    finalSplitArgsCopy.addAll(index,leadingNullSplitValues);
                    commasAdded.delete(0,commasAdded.length());
                }
            }
        }



        System.out.println("Splitted " + Arrays.toString(finalSplitArgs.toArray()));
        //List<String> args = Arrays.asList(splitArgs);

        switch (identifier) {
            case 'M' -> pathValues.add(new PathValue("M", "moveTo", 2, finalSplitArgsCopy));
            case 'm' -> pathValues.add(new PathValue("m", "moveToRelative", 2, finalSplitArgsCopy));
            case 'L' -> pathValues.add(new PathValue("L", "lineTo", 2, finalSplitArgsCopy));
            case 'l' -> pathValues.add(new PathValue("l", "lineToRelative", 2, finalSplitArgsCopy));
            case 'H' -> pathValues.add(new PathValue("H", "horizontalLineTo", 1, finalSplitArgsCopy));
            case 'h' -> pathValues.add(new PathValue("h", "horizontalLineToRelative", 1, finalSplitArgsCopy));
            case 'V' -> pathValues.add(new PathValue("V", "verticalLineTo", 1, finalSplitArgsCopy));
            case 'v' -> pathValues.add(new PathValue("v", "verticalLineToRelative", 1, finalSplitArgsCopy));
            case 'Z', 'z' -> pathValues.add(new PathValue("Z", "close", 0, finalSplitArgsCopy));
            case 'C' -> pathValues.add(new PathValue("C", "curveTo", 6, finalSplitArgsCopy));
            case 'c' -> pathValues.add(new PathValue("c", "curveToRelative", 6, finalSplitArgsCopy));
            case 'S' -> pathValues.add(new PathValue("S", "reflectiveCurveTo", 4, finalSplitArgsCopy));
            case 's' -> pathValues.add(new PathValue("s", "reflectiveCurveToRelative", 4, finalSplitArgsCopy));
            case 'Q' -> pathValues.add(new PathValue("Q", "quadTo", 4, finalSplitArgsCopy));
            case 'q' -> pathValues.add(new PathValue("q", "quadToRelative", 4, finalSplitArgsCopy));
            case 'T' -> pathValues.add(new PathValue("T", "reflectiveQuadTo", 2, finalSplitArgsCopy));
            case 't' -> pathValues.add(new PathValue("t", "reflectiveQuadToRelative", 2, finalSplitArgsCopy));
            case 'A' -> pathValues.add(new PathValue("A", "arcTo", 7, finalSplitArgsCopy));
            case 'a' -> pathValues.add(new PathValue("a", "arcToRelative", 7, finalSplitArgsCopy));
        }

        System.out.println(pathValues.toString());

        return pathValues;
    }

    /**
     * it might be the case that a path value hast too many arguments
     * if this is the case the object needs to be split into two objects of the same type
     */
    private List<PathValue> checkPathValues() {
        System.out.println("check - - - -- - - - - - -- -");
        List<PathValue> fixedValues = new ArrayList<>();

        for (PathValue p : pathValues) {
            if (!p.identifier.equals("Z") && p.coordinateCount != p.coordinates.size()) { //if needed arg length differs from real length it needs to be split
                try {
                    for (int i = 0; i < p.coordinates.size(); ) {
                        List<String> temp = p.coordinates.subList(i, i + p.coordinateCount);
                        checkedPathValues.add(new PathValue(p, temp));
                        i += p.coordinateCount;
                    }
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

            } else {
                checkedPathValues.add(p);
            }
        }

        return fixedValues;
    }

    /**
     * converts the pathValue objects into the correct output format
     */
    private void formatOutput() {
        StringBuffer buffer = new StringBuffer();
        for (PathValue p : checkedPathValues) {
            buffer.append(p.mappedIdentifier);
            buffer.append("(");

            if (p.identifier.equals("A") || p.identifier.equals("a")) { //map the 0 and 1 to true bzw false
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
                    buffer.append(c);
                    if (!c.equals("true") && !c.equals("false")) { //only append an f if the coordinate contains not the value true or false
                        buffer.append("f");
                    }
                    buffer.append(",");
                }

                buffer.deleteCharAt(buffer.length() - 1); //delete the comma after the last coordinate
            }

            buffer.append(")");
            buffer.append("\n");
        }

        System.out.println(buffer);
    }
}

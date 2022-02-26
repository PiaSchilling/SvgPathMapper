import java.io.*;
import java.io.FileReader;

public class CodefileWriter {

    /**
     * wirtes the result of the mapping to a kotlin file, the kotlin file is put in the same directory as the input svg
     *
     * @param iconName        the name the material icon should have (for naming the file and setting variable names in the kotlin code)
     * @param pathValueString the string which contains the actual content (mapped path values, result of PathMapper class)
     * @param svgFileUrl      string representation of the fileUrl of the svg input file (will be used to generate the output path for the resulting kotlin file)
     */
    public void writeFile(String iconName, String pathValueString, String svgFileUrl) {

        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/java/template"))) {

            StringBuilder oldContent = new StringBuilder();
            String line = reader.readLine();

            while (line != null) {
                oldContent.append(line).append(System.lineSeparator());
                line = reader.readLine();
            }

            String newContentIconName = oldContent.toString().replace("Placeholder", iconName);
            String newContentVarName = newContentIconName.replace("_placeholder", "_" + iconName.toLowerCase());
            String newContentPathValues = newContentVarName.replace("pathValues", pathValueString);

            File file = new File(getOutputDirectory(svgFileUrl, iconName));
            FileWriter writer = new FileWriter(file);
            writer.write(newContentPathValues);

            System.out.println(newContentPathValues);

            writer.close();
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }

    }

    /**
     * generates the fileUrl for the resulting kotlin file
     *
     * @param svgFileUrl the url of the input svg file
     * @param iconName   the name the material icon should have (for naming the file)
     * @return the complete output url for the kotlin file including the filename
     */
    private String getOutputDirectory(String svgFileUrl, String iconName) {
        int splitIndex = svgFileUrl.lastIndexOf("/");
        String directoryUrl = svgFileUrl.substring(0, splitIndex);
        return directoryUrl + "/" + iconName + ".kt";
    }
}

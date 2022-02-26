import java.io.*;
import java.io.FileReader;

public class CodefileWriter {

    /**
     * replaces the placeholder strings in the template and wirtes the result to a new file named like the icon
     * @param iconName the name of the icon which is represented by the svg file
     * @param pathValueString the mapped path values
     * @throws IOException when the template file is not found
     */
    public void writeFile(String iconName, String pathValueString, String outputDirectory) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/java/template"));

        String oldContent = "";

        String line = reader.readLine();

        while (line != null)
        {
            oldContent = oldContent + line + System.lineSeparator();
            line = reader.readLine();
        }

        String newContentIconName = oldContent.replace("Placeholder",iconName);
        String newContentVarName = newContentIconName.replace("_placeholder","_"+ iconName.toLowerCase());
        String newContentPathValues = newContentVarName.replace("pathValues",pathValueString);

        File file = new File(getOutputDirectory(outputDirectory,iconName));
        file.createNewFile();

        java.io.FileWriter fileWriter = new java.io.FileWriter(file);
        fileWriter.write(newContentPathValues);

        System.out.println(newContentPathValues);

        fileWriter.close();
        reader.close();
    }

    private String getOutputDirectory(String url, String iconName){
        int splitIndex = url.lastIndexOf("/");
        String directoryUrl = url.substring(0,splitIndex);
        String fileUrl = directoryUrl + "/" + iconName + ".kt";

        System.out.println(fileUrl + " - - - - - - - - - - - - - - -- - - -- - - - ");
        return fileUrl;
    }
}

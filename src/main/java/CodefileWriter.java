import java.io.*;
import java.io.FileReader;

public class CodefileWriter {

    public void writeFile(String iconName, String pathValueString) throws IOException {
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

        File file = new File("src/main/java/" + iconName);
        file.createNewFile();

        java.io.FileWriter fileWriter = new java.io.FileWriter(file);
        fileWriter.write(newContentPathValues);

        fileWriter.close();
        reader.close();
    }

}

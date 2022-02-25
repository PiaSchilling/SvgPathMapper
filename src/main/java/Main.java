import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            PathValueMapper mapper = new PathValueMapper();
            CodefileWriter writer = new CodefileWriter();

            String pathValues = mapper.mapFile(args[0]);
            writer.writeFile(args[1],pathValues);

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }catch (IndexOutOfBoundsException e){
            System.out.println("No programm arguments set. Please set the following information:");
            System.out.println("FileUrl_SvgFile IconName");
        }
    }
}

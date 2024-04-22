
public class CSVGeneratorMain {
    public static void main(String[] args) {
        if(FilePathValidator.checkSourceDirectoryExistsAndValid(args)) {
            if(FilePathValidator.checkDestinationDirectoryExistsAndValid(args)) {
                //call the constructor with 2 paths
                //to write the output to the file specified in the path
                new CSVGenerator(args[0], args[1]).generateCSVFile();
            }
            else if(!isRunningFromJAR()){
                //call the constructor to write the output to the default file
                //NOT INTENDED for executing the jar file, for the purposes of testing in the local environment only.
                new CSVGenerator(args[0], null).generateCSVFile();
            }
            else {
                System.out.println("Missing program argument");
            }
        }
        else {
            System.out.println("Running without arguments");
        }
    }
    private static boolean isRunningFromJAR() {
        String classLocation = CSVGeneratorMain.class.getResource("CSVGeneratorMain.class").toString();
        return classLocation.startsWith("jar:");
    }
}

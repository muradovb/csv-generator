import org.apache.commons.io.FilenameUtils;

import java.io.File;

public interface FilePathValidator {
    static boolean checkSourceDirectoryExistsAndValid(String [] argument) {
        if(argument.length >= 1) {
            File file = new File(argument[0]);
            String extension = FilenameUtils.getExtension(argument[0]);
            try {
                file.getCanonicalPath();
                return file.exists() && extension.equals("jsonl"); //returns true if the file exists and the extension is jsonl
            } catch (Exception e) {
                return false; //file path is invalid
            }
        }
        return false;
    }

    static boolean checkDestinationDirectoryExistsAndValid(String [] argument) {
        if(argument.length == 2) {
            File file = new File(argument[1]);
            String extension = FilenameUtils.getExtension(argument[1]);
            try {
                file.getCanonicalPath();
                return file.exists() && extension.equals("csv"); //returns true if the file exists and the extension is csv
            } catch (Exception e) {
                return false; //file path is invalid
            }
        }
        return false;
    }



}

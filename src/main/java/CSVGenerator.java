import com.opencsv.CSVWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


public class CSVGenerator {

    private String sourceFilePath;
    private String destinationFilePath;

    public CSVGenerator(String sourceFilePath, String destinationFilePath) {
        this.sourceFilePath = sourceFilePath;
        this.destinationFilePath = destinationFilePath;
    }

    public void generateCSVFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(sourceFilePath))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {
                lineNum++;
                JSONObject jsonObject = new JSONObject(line);
                String type = jsonObject.getString("type");
                if (type.equals("PushEvent")) {
                    JSONArray commitsArray = jsonObject.getJSONObject("payload").getJSONArray("commits");
                    StringBuilder finalWriteValue = new StringBuilder(jsonObject.getJSONObject("actor").getString("login"));
                    if (commitsArray.length() > 0) {
                        //get the first commit object
                        JSONObject commitObject = commitsArray.getJSONObject(0);
                        String commitMessage = removePunctuationAndToLowercase(commitObject.optString("message"));
                        String nGram = generateNgram(commitMessage);
                        if(nGram.length() != 0) {
                            finalWriteValue.append(", " + nGram);
                            System.out.println("** Final value to be written to the destination file: " + finalWriteValue + "**");
                            writeToOutputFile(finalWriteValue.toString());
                            System.out.println("** End of line number: "+ lineNum+ " **");
                            System.out.println("\n");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateNgram(String message) {
        System.out.println("** Generating 3-gram for: " + message + " **");
        String [] words = message.split("\\s+");
        StringBuilder allTrigrams = new StringBuilder();
        int tracker = 0;
        for (int i = 0; i < words.length - 2; i++) {
            if (tracker < 5) {
                String trigram = words[i] + " " + words[i + 1] + " " + words[i + 2] + ", ";
                tracker++;
                allTrigrams.append(trigram);
            }
        }
        System.out.println("** Generated 3-gram length: " + tracker + " **");
        if(tracker == 0) {
            System.out.println();
        }
        return allTrigrams.toString();
    }

    private void writeToOutputFile(String line) {
        //if destination doesn't exist, create a new file with default name under resources
        if(destinationFilePath == null) {
            destinationFilePath = createCSVFile();
        }
        String[] splitLineArr = line.split(", ");

        //write to the file
        try (CSVWriter writer = new CSVWriter(new FileWriter(destinationFilePath, true))) {
            writer.writeNext(splitLineArr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String removePunctuationAndToLowercase(String input) {
        String removedPunctuation = input.replaceAll("\\p{Punct}", "");
        String lowercase = removedPunctuation.toLowerCase();

        return lowercase;
    }

    private String createCSVFile() {
        try {
            //get the resources directory path
            URL resourcesURL = CSVGenerator.class.getResource("/");
            URI resourcesURI = resourcesURL.toURI();
            Path resourcesPath = Paths.get(resourcesURI);

            //specify the file name
            String fileName = "default_output.csv";

            //create the file path
            Path filePath = resourcesPath.resolve(fileName);

            //create the file object
            File file = filePath.toFile();

            //check if the file already exists
            if (file.exists()) {
                System.out.println("File already exists. Overriding the file.");
                file.delete();
            }

            //create a new file
            if (file.createNewFile()) {
                System.out.println("File created successfully.");

                //return the file path
                return filePath.toAbsolutePath().toString();
            } else {
                System.out.println("Failed to create a file");
                return null;
            }

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }



}

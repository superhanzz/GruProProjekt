package CapableSimulator;

import FunctionLibrary.CapableFunc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class FileReadingTest {

    @BeforeEach
    public void setup() {

    }

    @RepeatedTest(1)
    public void testReadFile() {

        /*List<Map<String, InputFileStruct>> allInputs = new ArrayList<>();
        Map<String, List<File>> allFiles = CapableFunc.getAllInputFiles("scr/Data");

        for (String folder :  allFiles.keySet()) {
            allFiles.get(folder).forEach((file) -> {
                allInputs.add(CapableFunc.parseInputsFromFile(file));
            });
        }*/


        //File file = new File("src/Data/week-2/t2-4a.txt");

        //Map<String, InputFileStruct> map = CapableFunc.parseInputsFromFile(file);



       Map<String, File> weekInputDataMap = new HashMap<>();

        File dataFolder = new File("src/Data");
        File[] listOfFiles = dataFolder.listFiles();
        System.out.println("File list length: " + listOfFiles.length);

        for  (File file : listOfFiles) {
            System.out.println("Folder: " + file.getName() + ". Has files: ");
            for (File f : file.listFiles()) {
                System.out.println("\t" + f.getName());
                //f.getPath()
                weekInputDataMap.put(file.getName(), f);
            }
            System.out.println();
        }

        weekInputDataMap.forEach((key, value) -> {System.out.println(key);});
    }

    @AfterEach
    public void tearDown() {

    }
}

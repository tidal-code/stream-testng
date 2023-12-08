package dev.tidalcode.testng.utils;

import com.tidal.stream.exceptions.RuntimeTestException;
import com.tidal.utils.filehandlers.Finder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileFinder {

    public static synchronized List<String> findFile(String fileName, Path baseFolderPath) {
        try {
            return Files.walk(baseFolderPath).map(Path::toString).filter(f -> f.contains(fileName)).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeTestException(e.getCause());
        }
    }

    public static void deleteFile(String fileName, Path baseFolderPath){
        File file=Finder.findFile(fileName, baseFolderPath);
        file.delete();
    }
}

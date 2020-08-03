package com.hertfordshire.restfulapi.service.storageForJasper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileSystemStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemStorageService.class.getSimpleName());

    private Path rootLocation = null;


    @Value("${report.location}")
    private String reportLocation;

    public FileSystemStorageService() {
    }

    @Override
    public void init() {
        rootLocation = Paths.get(reportLocation);
    }

    @Override
    public void deleteAll() {
        try {
            FileSystemUtils.deleteRecursively(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not delete files and folders", e);
        }
    }

    @Override
    public boolean jrxmlFileExists(String file) {
        try {
            Resource resource = new ClassPathResource("/reports/" + file + ".jrxml");
            File newFile = resource.getFile();
            Path reportFile = Paths.get(newFile.getAbsolutePath());
            if (Files.exists(reportFile))
                return true;
        } catch (Exception e) {
            // logger.error("Error while trying to get file URI", e);
            return false;
        }
        return false;
    }

    @Override
    public boolean jasperFileExists(String fileName) {

        try {
            File file = new File(rootLocation + "/" + fileName + ".jasper");
            return file.exists();
        } catch (Exception e) {
            logger.info("not file found");
            return false;
        }

    }

    @Override
    public String loadJrxmlFile(String file) {
        try {
            Resource resource = new ClassPathResource("/reports/" + file + ".jrxml");
            File newFile = resource.getFile();
            return newFile.getAbsolutePath();
        } catch (Exception e) {
            logger.error("Error while trying to get file prefix", e);
            throw new StorageFileNotFoundException("Could not load file", e);
        }
    }


    @Override
    public File loadJasperFile(String fileName) {
        try {
           return new File(rootLocation + "/" + fileName + ".jasper");
        } catch (Exception e) {
            logger.info("not file found");
            return null;
        }
    }
}

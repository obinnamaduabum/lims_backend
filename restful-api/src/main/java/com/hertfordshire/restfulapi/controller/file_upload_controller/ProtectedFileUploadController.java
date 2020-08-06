//package com.hertfordshire.restfulapi.com.hertfordshire.restfulapi.controller.file_upload_controller;
//
//import com.google.gson.Gson;
//import com.hertfordshire.utils.errors.ApiError;
//import com.hertfordshire.utils.errors.CustomBadRequestException;
////import com.hertfordshire.fileupload.dto.CroppedFileUploadDto;
////import com.hertfordshire.fileupload.dto.CustomMultipartFileDto;
////import com.merlinlabs.fileupload.dto.ImageResolutionDto;
////import com.merlinlabs.fileupload.lhenum.DataStorageConstant;
////import com.merlinlabs.fileupload.lhenum.ImageUploadTypeConstant;
////import com.merlinlabs.fileupload.models.FileModel;
////import com.merlinlabs.fileupload.models.ImageResolution;
////import com.merlinlabs.fileupload.service.Image_resolution_service.ImageResolutionService;
////import com.merlinlabs.fileupload.service.InitializeFileUploadService;
////import com.merlinlabs.fileupload.util.FileLocationConstant;
//import com.hertfordshire.model.psql.AdminSettings;
//import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
//import com.hertfordshire.utils.MessageUtil;
//import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.IOUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.activation.FileTypeMap;
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.logging.Logger;
//
//@RestController
//public class ProtectedFileUploadController extends ProtectedBaseApiController {
//
//    private Logger logger = Logger.getLogger(ProtectedFileUploadController.class.getSimpleName());
//
//    @Autowired
//    private InitializeFileUploadService initializeFileUploadService;
//
//    @Autowired
//    private MessageUtil messageUtil;
//
//    @Autowired
//    private Gson gson;
//
//    @Autowired
//    private ImageResolutionService imageResolutionService;
//
//    @Autowired
//    private AdminSettingsService adminSettingsService;
//
//    @PostMapping("/file-upload/create")
//    public ResponseEntity<Object> singleFileUpload(@RequestParam("file") MultipartFile multipartFile) {
//
//        ApiError apiError = null;
//        String dataStorageType = "";
//        String fileLocation = "";
//        String fileCode = "";
//
//        if (multipartFile.isEmpty()) {
//            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK,
//                    messageUtil.getMessage("file.empty", "en"),false, new ArrayList<>(), null);
//            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//        }
//
//        List<ImageResolutionDto> imageResolutionDtos = new ArrayList<>();
//
//
//        int[] imageArray = {200,400};
//
//        for (int anImageArray : imageArray) {
//            ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
//            imageResolutionDto.setWidth(anImageArray);
//            imageResolutionDto.setHeight(anImageArray);
//            imageResolutionDtos.add(imageResolutionDto);
//        }
//
//
//        try {
//
//            AdminSettings adminSettings = adminSettingsService.getAdminSettings();
//            if (adminSettings.isDropBoxActive()) {
//                dataStorageType = DataStorageConstant.DROP_BOX.toString();
//            } else {
//                dataStorageType = DataStorageConstant.DEFAULT_SERVER.toString();
//            }
//
//
//            if (adminSettings.isDataStorageProduction()) {
//                fileLocation = FileLocationConstant.MAIN_FOLDER_LOCATION_SERVER;
//            } else {
//                fileLocation = FileLocationConstant.MAIN_FOLDER_LOCATION;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        fileCode = this.initializeFileUploadService.generateFileUploadSequence();
//
//
//        FileModel fileModel = initializeFileUploadService.saveFile(
//                multipartFile,
//                "file_server",
//                imageResolutionDtos,
//                DataStorageConstant.valueOf(dataStorageType),
//                fileLocation, fileCode);
//
//        if (fileModel != null) {
//
//            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.successful", "en")
//                    ,true, new ArrayList<>(), fileModel);
//
//            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//
//        } else {
//            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en")
//                    ,false, new ArrayList<>(), null);
//
//            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//        }
//    }
//
//    @PostMapping("/file-upload/cropped-image/create")
//    public ResponseEntity<Object> multipleFileUpload(@RequestBody CroppedFileUploadDto croppedFileUploadDto,
//                                                     BindingResult bindingResult) {
//        ApiError apiError = null;
//        String dataStorageType = "";
//        String fileLocation = "";
//        String fileCode = "";
//
//        if (bindingResult.hasErrors()) {
//
//            bindingResult.getAllErrors().forEach(objectError -> {
//                logger.info(objectError.toString());
//            });
//
//            throw new CustomBadRequestException();
//
//        } else {
//
//            List<ImageResolutionDto> imageResolutionDtos = new ArrayList<>();
//
//
//            int[] imageArray = {200, 400};
//
//
//            for (int item : imageArray) {
//                //System.out.println("anImageArray: " + imageArray[i]);
//                ImageResolutionDto imageResolutionDto = new ImageResolutionDto();
//                imageResolutionDto.setWidth(item);
//                imageResolutionDto.setHeight(item);
//                imageResolutionDtos.add(imageResolutionDto);
//            }
//
//
//            try {
//
//                AdminSettings adminSettings = adminSettingsService.getAdminSettings();
//                if (adminSettings.isDropBoxActive()) {
//                    dataStorageType = DataStorageConstant.DROP_BOX.toString();
//                } else {
//                    dataStorageType = DataStorageConstant.DEFAULT_SERVER.toString();
//                }
//
//
//                if (adminSettings.isDataStorageProduction()) {
//
//                    Path path = Paths.get(FileLocationConstant.MAIN_FOLDER_LOCATION_SERVER);
//                    fileLocation = path.toAbsolutePath().toString();
//                } else {
//                    fileLocation = FileLocationConstant.MAIN_FOLDER_LOCATION;
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            ArrayList<CustomMultipartFileDto> multipartFileArrayList = new ArrayList<>();
//
//            CustomMultipartFileDto customMultipartFileDto = new CustomMultipartFileDto();
//            customMultipartFileDto.setImageUploadTypeConstant(ImageUploadTypeConstant.CROPPED_IMAGE);
//            customMultipartFileDto.setBase64Image(croppedFileUploadDto.getCroppedImage());
//
//            CustomMultipartFileDto customMultipartFileDto1 = new CustomMultipartFileDto();
//            customMultipartFileDto1.setImageUploadTypeConstant(ImageUploadTypeConstant.ORIGINAL_IMAGE);
//            customMultipartFileDto1.setBase64Image(croppedFileUploadDto.getOriginalImage());
//
//            multipartFileArrayList.add(customMultipartFileDto);
//            multipartFileArrayList.add(customMultipartFileDto1);
//
//
//
//            fileCode = this.initializeFileUploadService.generateFileUploadSequence();
//
//            FileModel fileModel = initializeFileUploadService.saveCroppedImages(
//                    multipartFileArrayList,
//                    "file_server",
//                    imageResolutionDtos,
//                    DataStorageConstant.valueOf(dataStorageType),
//                    fileLocation,
//                    fileCode);
//
//            if (fileModel != null) {
//
//                this.initializeFileUploadService.deleteTempFiles(fileCode, fileLocation);
//
//                if(croppedFileUploadDto.getPreviousFileFileCode() != null) {
//                    this.initializeFileUploadService.deleteAllFilesFromDropBox(croppedFileUploadDto.getPreviousFileFileCode());
//                }
//
//                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.successful", "en")
//                        ,true, new ArrayList<>(), fileModel);
//
//                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//
//            } else {
//                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en")
//                        , false, new ArrayList<>(), null);
//
//                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//            }
//        }
//    }
//
//    @RequestMapping("/file-upload/{file-code}/{dimension}")
//    public ResponseEntity<byte[]> getSingleFileUpload(@PathVariable("file-code") String fileCode,
//                                                      @PathVariable("dimension") String dimension) {
//
//        String width = "";
//        String height = "";
//
//
//        String[] split = dimension.split("x");
//        width = split[0];
//        height = split[1];
//        String dropBoxImageUrl = "";
//
//        ImageResolution imageResolution;
//
//        if (fileCode != null) {
//
//            if (StringUtils.isNotBlank(fileCode) && StringUtils.isNotBlank(width)) {
//                imageResolution = imageResolutionService.findByFileCodeAndWidth(fileCode, Integer.valueOf(width));
//
//            } else if (StringUtils.isNotBlank(fileCode) && StringUtils.isNotBlank(height)) {
//
//                imageResolution = imageResolutionService.findByFileCodeAndHeight(fileCode, Integer.valueOf(height));
//
//            } else {
//
//                imageResolution = imageResolutionService.findByFileCode(fileCode);
//            }
//
//
//            if (imageResolution != null) {
//
//                File img = new File(imageResolution.getUrl());
//
//                if (imageResolution.getDataStorageConstant().equals(DataStorageConstant.DROP_BOX)) {
//
//                    String[] split1 = imageResolution.getUrl().split("\\?");
//                    String String2 = split1[0];
//                    String[] split2 = String2.split(".com");
//                    dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
//
//                    URL url = null;
//                    try {
//                        url = new URL(dropBoxImageUrl);
//                        BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
//
//                        byte[] imageBytes = IOUtils.toByteArray(bis);
//
//                        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(imageBytes);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//
//                    try {
//                        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(Files.readAllBytes(img.toPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @RequestMapping("/file-upload/{file-code}")
//    public ResponseEntity<byte[]> getSingleFileUpload(@PathVariable("file-code") String fileCode) {
//
//        ImageResolution imageResolution = null;
//
//        if (fileCode != null) {
//
//            imageResolution = imageResolutionService.findByFileCode(fileCode);
//
//            if (imageResolution != null) {
//
//                if (imageResolution.getDataStorageConstant().equals(DataStorageConstant.DROP_BOX)) {
//
//                    String[] split = imageResolution.getUrl().split("\\?");
//                    String String2 = split[0];
//                    String[] split2 = String2.split(".com");
//                    String dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
//
//                    URL url = null;
//                    File file = null;
//
//                    try {
//                        url = new URL(dropBoxImageUrl);
//                        String newFile = url.getFile();
//                        file = new File(newFile);
//
//                        try {
//                            FileUtils.copyURLToFile(url, file);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//                        try {
//                            return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file))).body(Files.readAllBytes(file.toPath()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//
//
//                    String[] split = imageResolution.getUrl().split("\\?");
//                    String String2 = split[0];
//                    String[] split2 = String2.split(".com");
//                    String dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
//
//                    URL url = null;
//                    File file = null;
//                    try {
//
//                        // logger.info(dropBoxImageUrl);
//                        url = new URL(dropBoxImageUrl);
//
//
//                        String newFile = url.getFile();
//                        file = new File(newFile);
//                        try {
//                            return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file))).body(Files.readAllBytes(file.toPath()));
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
//
//                    try {
//                        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(file))).body(Files.readAllBytes(file.toPath()));
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @RequestMapping("/file-upload/url/{file-code}/{dimension}")
//    public ResponseEntity<?> getFileUrl(@PathVariable("file-code") String fileCode,
//                                        @PathVariable("dimension") String dimension) {
//
//        String width = "";
//        String height = "";
//        String[] split = dimension.split("x");
//        width = split[0];
//        height = split[1];
//        String dropBoxImageUrl = "";
//        ImageResolution imageResolution = null;
//        ApiError apiError = null;
//
//        if (fileCode != null) {
//
//            if (StringUtils.isNotBlank(fileCode) && StringUtils.isNotBlank(width)) {
//                imageResolution = imageResolutionService.findByFileCodeAndWidth(fileCode, Integer.valueOf(width));
//
//            } else if (StringUtils.isNotBlank(fileCode) && StringUtils.isNotBlank(height)) {
//
//                imageResolution = imageResolutionService.findByFileCodeAndHeight(fileCode, Integer.valueOf(height));
//
//            } else {
//
//                imageResolution = imageResolutionService.findByFileCode(fileCode);
//            }
//
//
//            if (imageResolution != null) {
//
//                // System.out.println(gson.toJson(imageResolution));
//
//                if (imageResolution.getDataStorageConstant().equals(DataStorageConstant.DROP_BOX)) {
//
//                    String[] split1 = imageResolution.getUrl().split("\\?");
//                    String String2 = split1[0];
//                    String[] split2 = String2.split(".com");
//                    dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
//
//                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.url.successful", "en"),true, new ArrayList<>(), dropBoxImageUrl);
//
//                    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//                }
//            }
//        }
//
//        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.url.failed", "en"),false, new ArrayList<>(), null);
//
//        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
//    }
//
//}
//package com.hertfordshire.restfulapi.com.hertfordshire.restfulapi.controller.file_upload_controller;
//
//import com.google.gson.Gson;
//import com.merlinlabs.access.errors.ApiError;
//import com.merlinlabs.fileupload.lhenum.DataStorageConstant;
//import com.merlinlabs.fileupload.models.ImageResolution;
//import com.merlinlabs.fileupload.service.Image_resolution_service.ImageResolutionService;
//import com.merlinlabs.fileupload.service.InitializeFileUploadService;
//import com.merlinlabs.service.psql.settings.admin.AdminSettingsService;
//import com.merlinlabs.utils.MessageUtil;
//import com.merlinlabs.utils.controllers.PublicBaseApiController;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.activation.FileTypeMap;
//import java.io.File;
//import java.io.IOException;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.nio.file.Files;
//import java.util.ArrayList;
//import java.util.logging.Logger;
//
//@RestController
//public class PublicFileUploadController extends PublicBaseApiController {
//
//    private Logger logger = Logger.getLogger(PublicFileUploadController.class.getSimpleName());
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
//
//    @RequestMapping("/file-upload/url/{file-code}/{dimension}")
//    public ResponseEntity<?> getFileUrl(@PathVariable("file-code") String fileCode,
//                                        @PathVariable("dimension") String dimension) {
//
//        String width = null;
//        String height = null;
//
//        try {
//            String[] split = dimension.split("x");
//            width = split[0];
//            height = split[1];
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        String dropBoxImageUrl = "";
//
//        ImageResolution imageResolution = null;
//
//        ApiError apiError = null;
//
//        if (fileCode != null) {
//
//            if (StringUtils.isNotBlank(fileCode) && width != null) {
//                imageResolution = imageResolutionService.findByFileCodeAndWidth(fileCode, Integer.valueOf(width));
//
//            } else if (StringUtils.isNotBlank(fileCode) && height != null) {
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
//
//    @RequestMapping("/file-upload/url/{file-code}")
//    public ResponseEntity<?> getOriginalFileUrl(@PathVariable("file-code") String fileCode) {
//
//
//        String dropBoxImageUrl = "";
//
//        ImageResolution imageResolution = null;
//
//        ApiError apiError = null;
//
//        if (StringUtils.isNotBlank(fileCode)) {
//
//                imageResolution = imageResolutionService.findByFileCode(fileCode);
//
//            if (imageResolution != null) {
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
////    @RequestMapping("/file-upload/{file-code}/{dimension}")
////    public ResponseEntity<byte[]> getSingleFileUploadWithDimensions(@PathVariable("file-code") String fileCode,
////                                                      @PathVariable("dimension") String dimension) {
////
////        String width = null;
////        String height = null;
////
////        try {
////            String[] split = dimension.split("x");
////            width = split[0];
////            height = split[1];
////        } catch (Exception e){
////            e.printStackTrace();
////        }
////
////        String dropBoxImageUrl = "";
////
////        ImageResolution imageResolution = null;
////
////        if (fileCode != null) {
////
////            if (StringUtils.isNotBlank(fileCode) && width != null) {
////                imageResolution = imageResolutionService.findByFileCodeAndWidth(fileCode, Integer.valueOf(width));
////
////            } else if (StringUtils.isNotBlank(fileCode) && height != null) {
////                imageResolution = imageResolutionService.findByFileCodeAndHeight(fileCode, Integer.valueOf(height));
////            } else {
////                imageResolution = imageResolutionService.findByFileCode(fileCode);
////            }
////
////
////            if (imageResolution != null) {
////
////                File img = new File(imageResolution.getUrl());
////
////                if (imageResolution.getDataStorageConstant().equals(DataStorageConstant.DROP_BOX)) {
////
////                    String[] split1 = imageResolution.getUrl().split("\\?");
////                    String String2 = split1[0];
////                    String[] split2 = String2.split(".com");
////                    dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
////
////                    URL url = null;
////                    try {
////                        url = new URL(dropBoxImageUrl);
////                        BufferedInputStream bis = new BufferedInputStream(url.openConnection().getInputStream());
////
////                        byte[] imageBytes = IOUtils.toByteArray(bis);
////
////                        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(imageBytes);
////                    } catch (MalformedURLException e) {
////                        e.printStackTrace();
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////
////                } else {
////
////                    try {
////                        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(Files.readAllBytes(img.toPath()));
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                }
////            }
////        }
////
////        return null;
////    }
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
//
//    @RequestMapping("/file-upload/blob/{file-code}/{dimension}")
//    public ResponseEntity<byte[]> getFileAsBlob(@PathVariable("file-code") String fileCode,
//                                                @PathVariable("dimension") String dimension) {
//
//        String width = null;
//        String height = null;
//
//        try {
//            String[] split = dimension.split("x");
//            width = split[0];
//            height = split[1];
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//
//        String dropBoxImageUrl = "";
//
//        ImageResolution imageResolution = null;
//
//        ApiError apiError = null;
//
//        if (fileCode != null) {
//
//            if (StringUtils.isNotBlank(fileCode) && width != null) {
//                imageResolution = imageResolutionService.findByFileCodeAndWidth(fileCode, Integer.valueOf(width));
//
//            } else if (StringUtils.isNotBlank(fileCode) && height != null) {
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
//                if (imageResolution.getDataStorageConstant().equals(DataStorageConstant.DROP_BOX)) {
//
//                    String[] split1 = imageResolution.getUrl().split("\\?");
//                    String String2 = split1[0];
//                    String[] split2 = String2.split(".com");
//                    dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
//
//                    URL url = null;
//                    File file = null;
//                    try {
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
//
//}
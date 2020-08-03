package com.hertfordshire.dto;



import com.hertfordshire.utils.lhenum.FileTypeConstant;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class FileUploadDto {

    @NotBlank
    private FileTypeConstant fileTypeConstant;

    private List<ImageResolutionDto> imageResolutionDtoList;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileTypeConstant getFileTypeConstant() {
        return fileTypeConstant;
    }

    public void setFileTypeConstant(FileTypeConstant fileTypeConstant) {
        this.fileTypeConstant = fileTypeConstant;
    }

    public List<ImageResolutionDto> getImageResolutionDtoList() {
        return imageResolutionDtoList;
    }

    public void setImageResolutionDtoList(List<ImageResolutionDto> imageResolutionDtoList) {
        this.imageResolutionDtoList = imageResolutionDtoList;
    }
}

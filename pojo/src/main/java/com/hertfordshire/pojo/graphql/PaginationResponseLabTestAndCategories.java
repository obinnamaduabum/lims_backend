package com.hertfordshire.pojo.graphql;


import com.hertfordshire.pojo.LabTestPojo;
import lombok.Data;

import java.util.List;

@Data
public class PaginationResponseLabTestAndCategories {

    private Long length;
    private Long pageSize;
    private Long pageNumber;
    private List<LabTestPojo> dataList;


    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public List<LabTestPojo> getDataList() {
        return dataList;
    }

    public void setDataList(List<LabTestPojo> dataList) {
        this.dataList = dataList;
    }
}

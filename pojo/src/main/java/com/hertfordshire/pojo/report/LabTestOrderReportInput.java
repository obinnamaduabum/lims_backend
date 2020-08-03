package com.hertfordshire.pojo.report;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.HashMap;
import java.util.Map;

public class LabTestOrderReportInput {

    private String reportTitle;
    private String instituteName;
    private JRBeanCollectionDataSource labTestOrderDataSource;


    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getInstituteName() {
        return instituteName;
    }

    public void setInstituteName(String instituteName) {
        this.instituteName = instituteName;
    }

    public JRBeanCollectionDataSource getLabTestOrderDataSource() {
        return labTestOrderDataSource;
    }

    public void setLabTestOrderDataSource(JRBeanCollectionDataSource labTestOrderDataSource) {
        this.labTestOrderDataSource = labTestOrderDataSource;
    }


    public Map<String, Object> getParameters() {
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("P_INSTITUTE_NAME", getInstituteName());

        return parameters;
    }
    public Map<String, Object> getDataSources() {
        Map<String,Object> dataSources = new HashMap<>();
        dataSources.put("labTestOrderDataSource", labTestOrderDataSource);

        return dataSources;
    }
}

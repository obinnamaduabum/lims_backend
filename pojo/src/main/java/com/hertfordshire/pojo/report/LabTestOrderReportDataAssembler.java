package com.hertfordshire.pojo.report;


import com.hertfordshire.pojo.LabTestOrderPojoReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.List;

public class LabTestOrderReportDataAssembler {

    public static LabTestOrderReportInput assemble(List<LabTestOrderPojoReport> labTestOrderPojoReports) {
        LabTestOrderReportInput labTestOrderReportInput = new LabTestOrderReportInput();
        labTestOrderReportInput.setReportTitle("Receipt Report");

        JRBeanCollectionDataSource studentDataSource = new JRBeanCollectionDataSource(labTestOrderPojoReports, false);
        labTestOrderReportInput.setLabTestOrderDataSource(studentDataSource);

        return labTestOrderReportInput;
    }
}

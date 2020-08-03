package com.hertfordshire.restfulapi.service.jasper;

import net.sf.jasperreports.engine.JasperPrint;

import java.util.List;
import java.util.Map;

public interface JasperReportService {


    JasperPrint generatePDFReportForList(String inputFileName, List<Map<String, Object>> list);

    JasperPrint generatePDFReportForASingleElementInArray(String inputFileName, List<Map<String, Object>> list);
}

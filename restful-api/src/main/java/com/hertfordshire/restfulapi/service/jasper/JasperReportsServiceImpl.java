package com.hertfordshire.restfulapi.service.jasper;

import com.google.gson.Gson;
import com.hertfordshire.restfulapi.service.storageForJasper.StorageService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JasperReportsServiceImpl implements JasperReportService {

    private static final Logger logger = LoggerFactory.getLogger(JasperReportsServiceImpl.class.getSimpleName());

    private Gson gson;

    @Autowired
    private StorageService storageService;


    private JasperReportsServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public JasperPrint generatePDFReportForList(String inputFileName, List<Map<String, Object>> list) {

        JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(list);

        this.storageService.init();

        JasperReport jasperReport = null;
        try {
            // Check if a compiled report exists
            if (storageService.jasperFileExists(inputFileName)) {

                logger.info(this.gson.toJson("already compiled"));

                jasperReport = (JasperReport) JRLoader.loadObject(storageService.loadJasperFile(inputFileName));
            }
            // Compile report from source and save
            else {

//                logger.info(this.gson.toJson(list));
                String jrxml = storageService.loadJrxmlFile(inputFileName);
//                logger.info("loaded Compiling report", jrxml);
                jasperReport = JasperCompileManager.compileReport(jrxml);
                // Save compiled report. Compiled report is loaded next time
                JRSaver.saveObject(jasperReport,
                        storageService.loadJasperFile(inputFileName));
            }
            return JasperFillManager.fillReport(jasperReport, null, dataSource);
        } catch (JRException e) {
            e.printStackTrace();
//            logger.error("Encountered error when loading jasper file", e);
            return null;
        }
    }

    @Override
    public JasperPrint generatePDFReportForASingleElementInArray(String inputFileName, List<Map<String, Object>> list) {

       // logger.info(this.gson.toJson(list));

        JRDataSource dataSource = new JRBeanCollectionDataSource(list);

        this.storageService.init();

        JasperReport jasperReport = null;
        try {
            // Check if a compiled report exists
            if (storageService.jasperFileExists(inputFileName)) {

              //  logger.info(this.gson.toJson("already compiled"));

                jasperReport = (JasperReport) JRLoader.loadObject(storageService.loadJasperFile(inputFileName));
            }
            // Compile report from source and save
            else {

               //  logger.info(this.gson.toJson(list));
                String jrxml = storageService.loadJrxmlFile(inputFileName);
                // logger.info("loaded Compiling report", jrxml);
                jasperReport = JasperCompileManager.compileReport(jrxml);
                // Save compiled report. Compiled report is loaded next time
                JRSaver.saveObject(jasperReport,
                        storageService.loadJasperFile(inputFileName));
            }
            return JasperFillManager.fillReport(jasperReport, null, dataSource);
        } catch (JRException e) {
            e.printStackTrace();
           // logger.error("Encountered error when loading jasper file", e);
            return null;
        }
    }
}

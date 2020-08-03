package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.dto.paymentConfig.PaymentConfigDto;
import com.hertfordshire.service.psql.payment_method_config.PaymentMethodConfigService;
import com.hertfordshire.utils.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;

@Component
public class CreatePaymentMethodInfoTransform {

    private static final Logger logger = Logger.getLogger(CreatePaymentMethodInfoTransform.class.getSimpleName());

    @Autowired
    private PaymentMethodConfigService paymentMethodConfigService;

    private Gson gson;

    public CreatePaymentMethodInfoTransform() {
        this.gson = new Gson();
    }

    public void createPaymentMethods() {

        BufferedReader bufferedReader;

        InputStream inputStream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + JSON_FOLDER + File.separator + PAYMENT_METHOD_CONFIG);

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        PaymentConfigDto[] paymentConfigDtos = gson.fromJson(bufferedReader, PaymentConfigDto[].class);

        logger.info(gson.toJson(paymentConfigDtos));

        if (paymentMethodConfigService.findAll().size() <= 0) {

            logger.info("Adding payment methods");
            for (int i =0; i < paymentConfigDtos.length; i++){

                //logger.info("testing public key :"+ paymentConfigDtos[i].getPaymentConfig().getTesting().getPublicKey());
                paymentMethodConfigService.save(paymentConfigDtos[i]);
            }

            logger.info("Done adding payment methods");

        }else {

            logger.info("payment methods already exist");

        }
    }
}

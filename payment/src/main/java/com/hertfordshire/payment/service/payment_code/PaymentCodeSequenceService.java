package com.hertfordshire.payment.service.payment_code;


import com.hertfordshire.payment.service.SequenceServiceImp;
import org.springframework.stereotype.Component;

@Component
public class PaymentCodeSequenceService extends SequenceServiceImp {
     public PaymentCodeSequenceService() {
        super("payment_code_id");
    }
}

package com.anymind.ecommerce.application.controller;

import com.anymind.ecommerce.application.requestEntity.GetSalesRequestEntity;
import com.anymind.ecommerce.application.requestEntity.PaymentDetailsRequestEntity;
import com.anymind.ecommerce.application.responseEntity.PaymentResponse;
import com.anymind.ecommerce.application.responseEntity.SalesItem;
import com.anymind.ecommerce.domain.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @MutationMapping
    public PaymentResponse makePayment(@Argument PaymentDetailsRequestEntity payment) {
        log.info("Controller, Make Payment Request Received: {}", payment.toString());
        return paymentService.savePayment(payment);
    }

    @QueryMapping
    public List<SalesItem> sales(@Argument GetSalesRequestEntity request) {
        log.info("Controller, Get Sales Request Received: {}", request.toString());
        return paymentService.getSales(request);
    }
}

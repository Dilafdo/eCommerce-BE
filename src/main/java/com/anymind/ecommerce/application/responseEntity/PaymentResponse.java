package com.anymind.ecommerce.application.responseEntity;

import lombok.Data;

@Data
public class PaymentResponse {

    private String finalPrice;
    private Float points;
}

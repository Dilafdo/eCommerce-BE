package com.anymind.ecommerce.application.requestEntity;

import com.anymind.ecommerce.domain.entity.PaymentMethod;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class PaymentDetailsRequestEntity {

    private String price;
    private Float priceModifier;
//    @Pattern(regexp = "(CASH|VISA)", message = "Invalid")
    private PaymentMethod paymentMethod;
    private String dateTime;
}

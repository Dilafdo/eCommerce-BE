package com.anymind.ecommerce.application.responseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SalesItem {

    private String dateTime;
    private String sales;
    private Float points;
}

package com.anymind.ecommerce.application.requestEntity;

import lombok.Data;

@Data
public class GetSalesRequestEntity {

    private String startDateTime;
    private String endDateTime;
}

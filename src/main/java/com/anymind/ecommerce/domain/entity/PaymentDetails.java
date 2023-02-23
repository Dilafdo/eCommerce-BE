package com.anymind.ecommerce.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paymentDetails")
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Float price;
    private Float priceModifier;
    private Float finalPrice;
    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;
    private Float points;
    private LocalDateTime dateTime;

}

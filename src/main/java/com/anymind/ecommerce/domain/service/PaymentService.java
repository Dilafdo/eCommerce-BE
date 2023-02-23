package com.anymind.ecommerce.domain.service;

import com.anymind.ecommerce.application.requestEntity.GetSalesRequestEntity;
import com.anymind.ecommerce.application.requestEntity.PaymentDetailsRequestEntity;
import com.anymind.ecommerce.application.responseEntity.PaymentResponse;
import com.anymind.ecommerce.application.responseEntity.SalesItem;
import com.anymind.ecommerce.domain.entity.PaymentDetails;
import com.anymind.ecommerce.domain.entity.PaymentMethod;
import com.anymind.ecommerce.external.repository.PaymentDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PaymentService {

    private final PaymentDetailsRepository paymentDetailsRepository;

    @Value("${points.proportion.cash}")
    private Float POINTS_CASH;

    @Value("${points.proportion.cashOnDelivery}")
    private Float POINTS_CASH_ON_DELIVERY;

    @Value("${points.proportion.visa}")
    private Float POINTS_VISA;

    @Value("${points.proportion.masterCard}")
    private Float POINTS_MASTER_CARD;

    @Value("${points.proportion.amex}")
    private Float POINTS_AMEX;

    @Value("${points.proportion.jcb}")
    private Float POINTS_JCB;

    public PaymentService(PaymentDetailsRepository paymentDetailsRepository) {
        this.paymentDetailsRepository = paymentDetailsRepository;
    }

    public PaymentResponse savePayment(PaymentDetailsRequestEntity payment) {

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPrice(Float.valueOf(payment.getPrice()));
        paymentDetails.setPriceModifier(payment.getPriceModifier());
        paymentDetails.setPaymentMethod(payment.getPaymentMethod());
        paymentDetails.setDateTime(convertToLocalDateTime(payment.getDateTime()));
        paymentDetails.setFinalPrice(roundToTwoFloatingPoints(paymentDetails.getPrice() * paymentDetails.getPriceModifier()));

        paymentDetails.setPoints(
                roundToTwoFloatingPoints(
                        calculatePoints(paymentDetails.getPaymentMethod(), paymentDetails.getPrice())
                )
        );

        PaymentDetails savedDetails = paymentDetailsRepository.save(paymentDetails);

        PaymentResponse response = new PaymentResponse();
        response.setFinalPrice(String.valueOf(savedDetails.getFinalPrice()));
        response.setPoints(savedDetails.getPoints());

        log.info("Service, Make Payment Response Received: {}", response);

        return response;
    }

    protected LocalDateTime convertToLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String dateTimeWithoutZ = dateTime.substring(0, dateTime.length() - 1);
        return LocalDateTime.parse(dateTimeWithoutZ, formatter);
    }

    protected Float roundToTwoFloatingPoints(Float number) {
        return Math.round(number * 100.0f) / 100.0f;
    }

    protected Float calculatePoints(PaymentMethod paymentMethod, Float price) {

        return switch (paymentMethod) {
            case CASH -> price * POINTS_CASH;
            case CASH_ON_DELIVERY -> price * POINTS_CASH_ON_DELIVERY;
            case VISA -> price * POINTS_VISA;
            case MASTERCARD -> price * POINTS_MASTER_CARD;
            case AMEX -> price * POINTS_AMEX;
            case JCB -> price * POINTS_JCB;
        };
    }

    public List<SalesItem> getSales(GetSalesRequestEntity request) {

        List<PaymentDetails> list = this.paymentDetailsRepository
                .findByDateRange(
                        convertToLocalDateTime(request.getStartDateTime()),
                        convertToLocalDateTime(request.getEndDateTime()));

        Map<LocalDateTime, List<PaymentDetails>> salesByHour = list.stream()
                .collect(Collectors.groupingBy(payment -> payment.getDateTime().truncatedTo(ChronoUnit.HOURS)));

        List<SalesItem> responseList = salesByHour.entrySet().stream()
                .map(entry -> new SalesItem(
                        entry.getKey().toString() + ":00Z",
                        Float.toString(roundToTwoFloatingPoints((float) entry.getValue().stream().mapToDouble(PaymentDetails::getFinalPrice).sum())),
                        roundToTwoFloatingPoints((float) entry.getValue().stream().mapToDouble(PaymentDetails::getPoints).sum())))
                .sorted(Comparator.comparing(SalesItem::getDateTime))
                .toList();

        log.info("Service, Get Sales Response Received: {}", responseList.toString());

        return responseList;
    }
}

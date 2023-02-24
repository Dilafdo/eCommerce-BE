package com.anymind.ecommerce.domain.service;

import com.anymind.ecommerce.application.requestEntity.GetSalesRequestEntity;
import com.anymind.ecommerce.application.requestEntity.PaymentDetailsRequestEntity;
import com.anymind.ecommerce.application.responseEntity.PaymentResponse;
import com.anymind.ecommerce.application.responseEntity.SalesItem;
import com.anymind.ecommerce.domain.entity.PaymentDetails;
import com.anymind.ecommerce.domain.entity.PaymentMethod;
import com.anymind.ecommerce.domain.error.InvalidDateFormatException;
import com.anymind.ecommerce.external.repository.PaymentDetailsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentServiceTest {

    @MockBean
    private PaymentDetailsRepository repository;

    @Autowired
    private PaymentService service;

    @Test
    public void testSavePayment() {
        PaymentDetailsRequestEntity payment = new PaymentDetailsRequestEntity();
        payment.setPrice("100.0");
        payment.setPriceModifier(0.90f);
        payment.setPaymentMethod(PaymentMethod.VISA);
        payment.setDateTime("2022-01-01T12:00:00Z");

        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setPrice(100.0f);
        paymentDetails.setPriceModifier(0.90f);
        paymentDetails.setPaymentMethod(PaymentMethod.VISA);
        paymentDetails.setDateTime(LocalDateTime.parse("2022-01-01T12:00:00"));
        paymentDetails.setFinalPrice(90.0f);
        paymentDetails.setPoints(3.0f);

        PaymentDetails savedDetails = new PaymentDetails();
        savedDetails.setFinalPrice(90.0f);
        savedDetails.setPoints(3.0f);

        Mockito.when(repository.save(any(PaymentDetails.class))).thenReturn(savedDetails);

        PaymentResponse response = service.savePayment(payment);

        PaymentResponse expected = new PaymentResponse();
        expected.setFinalPrice("90.0");
        expected.setPoints(3.0f);

        assertEquals(expected, response);
    }

    @Test
    public void testConvertToLocalDateTime() {
        String dateTime = "2022-01-01T12:00:00Z";
        LocalDateTime localDateTime = service.convertToLocalDateTime(dateTime);

        assertEquals(LocalDateTime.parse("2022-01-01T12:00:00"), localDateTime);
    }

    @Test
    public void testConvertToLocalDateTimeInvalidFormat() {
        String dateTime = "2022-01-01T12:00:00";

        assertThrows(InvalidDateFormatException.class, () -> service.convertToLocalDateTime(dateTime));
    }

    @Test
    public void testRoundToTwoFloatingPoints() {
        float number = 1.23456f;
        float number2 = 150.0000f;
        float roundedNumber = service.roundToTwoFloatingPoints(number);
        float roundedNumber2 = service.roundToTwoFloatingPoints(number2);

        assertEquals(1.23f, roundedNumber, 0.01);
        assertEquals(150.00f, roundedNumber2, 0.01);
    }

    @Test
    public void testCalculatePoints() {

        float price = 100.0f;

        float cashPoints = service.calculatePoints(PaymentMethod.CASH, price);
        float cashOnDeliveryPoints = service.calculatePoints(PaymentMethod.CASH_ON_DELIVERY, price);
        float visaPoints = service.calculatePoints(PaymentMethod.VISA, price);
        float masterCardPoints = service.calculatePoints(PaymentMethod.MASTERCARD, price);
        float amexPoints = service.calculatePoints(PaymentMethod.AMEX, price);
        float jcbPoints = service.calculatePoints(PaymentMethod.JCB, price);

        assertEquals(5.0f, cashPoints, 0.01);
        assertEquals(5.0f, cashOnDeliveryPoints, 0.01);
        assertEquals(3.0f, visaPoints, 0.01);
        assertEquals(3.0f, masterCardPoints, 0.01);
        assertEquals(2.0f, amexPoints, 0.01);
        assertEquals(5.0f, jcbPoints, 0.01);
    }

    @Test
    public void testGetSales() {

        String startDateTime = "2022-01-01T00:00:00Z";
        String endDateTime = "2022-01-01T23:59:59Z";

        PaymentDetails paymentDetails1 = new PaymentDetails();
        paymentDetails1.setDateTime(LocalDateTime.of(2022, 1, 1, 2, 0, 0));
        paymentDetails1.setFinalPrice(100.0f);
        paymentDetails1.setPoints(3.0f);

        PaymentDetails paymentDetails2 = new PaymentDetails();
        paymentDetails2.setDateTime(LocalDateTime.of(2022, 1, 1, 2, 20, 0));
        paymentDetails2.setFinalPrice(150.0f);
        paymentDetails2.setPoints(4.5f);

        PaymentDetails paymentDetails3 = new PaymentDetails();
        paymentDetails3.setDateTime(LocalDateTime.of(2022, 1, 1, 10, 0, 0));
        paymentDetails3.setFinalPrice(200.0f);
        paymentDetails3.setPoints(6.0f);

        PaymentDetails paymentDetails4 = new PaymentDetails();
        paymentDetails4.setDateTime(LocalDateTime.of(2022, 1, 1, 10, 20, 0));
        paymentDetails4.setFinalPrice(250.0f);
        paymentDetails4.setPoints(7.5f);

        PaymentDetails paymentDetails5 = new PaymentDetails();
        paymentDetails5.setDateTime(LocalDateTime.of(2022, 1, 1, 10, 30, 0));
        paymentDetails5.setFinalPrice(300.0f);
        paymentDetails5.setPoints(9.0f);

        List<PaymentDetails> paymentDetailsList = Arrays.asList(paymentDetails1, paymentDetails2, paymentDetails3, paymentDetails4, paymentDetails5);

        Mockito.when(repository.findByDateRange(
                LocalDateTime.of(2022, 1, 1, 0, 0, 0),
                LocalDateTime.of(2022, 1, 1, 23, 59, 59))).thenReturn(paymentDetailsList);

        GetSalesRequestEntity request = new GetSalesRequestEntity();
        request.setStartDateTime(startDateTime);
        request.setEndDateTime(endDateTime);

        List<SalesItem> salesItemList = service.getSales(request);

        assertEquals(2, salesItemList.size());

        SalesItem salesItem1 = new SalesItem("2022-01-01T02:00:00Z", "250.0", 7.50f);
        SalesItem salesItem2 = new SalesItem("2022-01-01T10:00:00Z", "750.0", 22.50f);

        List<SalesItem> expected = Arrays.asList(salesItem1, salesItem2);

        assertEquals(expected, salesItemList);
    }
}

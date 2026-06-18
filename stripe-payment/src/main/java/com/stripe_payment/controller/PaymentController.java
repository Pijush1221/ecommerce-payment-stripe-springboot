package com.stripe_payment.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Value("${stripe.secretkey}")
    private String stripeSecretKey;

    @PostMapping("/create-checkout-session")
    @ResponseBody
    public Map<String, String> createCheckoutSession(
            @RequestParam String productName,
            @RequestParam Long price) {

        Map<String, String> response = new HashMap<>();

        try {
            Stripe.apiKey = stripeSecretKey;
            Long priceInPaisa = price * 100;

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl("http://localhost:8083/success")
                    .setCancelUrl("http://localhost:8083/cancel")
                    .addLineItem(
                            SessionCreateParams.LineItem.builder()
                                    .setQuantity(1L)
                                    .setPriceData(
                                            SessionCreateParams.LineItem.PriceData.builder()
                                                    .setCurrency("bdt")
                                                    .setUnitAmount(priceInPaisa)
                                                    .setProductData(
                                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName(productName)
                                                                    .build()
                                                    )
                                                    .build()
                                    )
                                    .build()
                    )
                    .build();

            Session session = Session.create(params);
            response.put("url", session.getUrl());

        } catch (StripeException e) {
            response.put("error", e.getMessage());
        }

        return response;
    }
}
package com.stripe_payment.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe_payment.dto.ProductRequest;
import com.stripe_payment.dto.StripeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {

    @Value("${stripe.secretKey}")  // ❌ আপনার লেখা ছিল: $stripe.secretKey{}
    private String secretKey;

    public StripeResponse checkoutProducts(ProductRequest productRequest) throws StripeException {
        Stripe.apiKey = secretKey;

        // ProductData তৈরি করুন
        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(productRequest.getName())
                        .build();

        // PriceData তৈরি করুন (এখানে productData যুক্ত করতে হবে)
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(productRequest.getCurrency() == null ? "usd" : productRequest.getCurrency().toLowerCase())
                        .setUnitAmount(productRequest.getAmount())
                        .setProductData(productData)  // ⬅️ এটা missing ছিল
                        .build();

        // LineItem তৈরি করুন
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(productRequest.getQuantity())
                        .setPriceData(priceData)  // ⬅️ priceData যুক্ত করতে হবে
                        .build();

        // Session তৈরি করুন
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8083/success")  // আপনার success URL
                        .setCancelUrl("http://localhost:8083/cancel")    // আপনার cancel URL
                        .addLineItem(lineItem)
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException ex) {
            System.out.println( ex.getMessage());

        }

        return StripeResponse.builder()
                .status( "SUCCESS")
                .message("Payment session created successfully")
                .sessionId(session.getId())
                .sessionUrl(session.getUrl())
                .build();
    }
}
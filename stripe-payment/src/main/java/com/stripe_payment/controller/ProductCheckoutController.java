package com.stripe_payment.controller;

import com.stripe.exception.StripeException;
import com.stripe_payment.dto.ProductRequest;
import com.stripe_payment.dto.StripeResponse;
import com.stripe_payment.service.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product/v1")
public class ProductCheckoutController {

    private StripeService stripeService;

    public ProductCheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody ProductRequest productRequest) throws StripeException {
        StripeResponse stripeResponse = stripeService.checkoutProducts(productRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    @GetMapping("/success")
    public String success() {
        return "Payment Successful! ";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Payment Cancelled! ";
    }
}
package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.PaymentRepository;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService {

    private PaymentRepository paymentRepository;

    // in PaymentService, because we want to use Stripe API (to create PaymentIntent), so we need to initialize Stripe API secret key (to tell Stripe that we're authorized to use its API) --> we pass in the constructor's parameter a variable has Stripe secret key as its value, then assign this value to Stripe.apiKey
    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }
}

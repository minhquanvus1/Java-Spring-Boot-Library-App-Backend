package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.PaymentRepository;
import com.luv2code.springbootlibrary.entity.Payment;
import com.luv2code.springbootlibrary.requestmodels.PaymentInfoRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {

    private PaymentRepository paymentRepository;

    // in PaymentService, because we want to use Stripe API (to create PaymentIntent), so we need to initialize Stripe API secret key (to tell Stripe that we're authorized to use its API) --> we pass in the constructor's parameter a variable has Stripe secret key as its value, then assign this value to Stripe.apiKey
    public PaymentService(PaymentRepository paymentRepository, @Value("${stripe.key.secret}") String secretKey) {
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = secretKey;
    }

    // create a PaymentService function to create PaymentIntent, by: receiving PaymentInfoRequest object from controller (which got it from frontend API call), extract necessary data, and put it in Map object, then call method of Stripe to create PaymentIntent
    public PaymentIntent createPaymentIntent(PaymentInfoRequest paymentInfoRequest) throws StripeException {
        List<String> paymentMethodTypes = new ArrayList<>();
        paymentMethodTypes.add("card");

        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentInfoRequest.getAmount());
        params.put("currency", paymentInfoRequest.getCurrency());
        params.put("payment_method_types", paymentMethodTypes);

        return PaymentIntent.create(params);
    }

    // create a function to set the amount (amount of money that a user has to paid, due to late book return) to 0.00, when he has already paid the fee
    public ResponseEntity<String> stripePayment(String userEmail) throws Exception {
        // check in the payment table in database if this user has the payment needed to be charged (because we can't charge a user if he isn't in the payment table where he has to pay fee)
        Payment payment = paymentRepository.findByUserEmail(userEmail);
        if(payment == null) {
            throw new Exception("Payment information is missing!");
        }
        // after this user has successfully made the payment, then, set his amount (amount of money he has to pay due to late book return) to 0 (becasue he has already paid the fee)
        payment.setAmount(0.00);
        // save this payment with the updated infor to the database
        paymentRepository.save(payment);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

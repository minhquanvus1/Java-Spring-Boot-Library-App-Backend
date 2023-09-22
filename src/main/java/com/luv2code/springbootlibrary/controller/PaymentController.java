package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.requestmodels.PaymentInfoRequest;
import com.luv2code.springbootlibrary.service.PaymentService;
import com.luv2code.springbootlibrary.utils.ExtractJWT;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@CrossOrigin("https://localhost:3000")
@RestController
@RequestMapping("/api/payment/secure")
public class PaymentController {

    private PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // create a Rest endpoint to receive request to create PaymentIntent
    // here, because we want to receive the content of PaymentIntent object returned by createPaymentIntent function, we need to use ResponseEntity<String>, and return an object of ResponseEntity (pass in a paymentStr in string of json)
    @PostMapping("/payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfoRequest paymentInfoRequest) throws Exception {
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfoRequest);
        // convert the PaymentIntent object to json string
        String paymentStr = paymentIntent.toJson();
        // return a ResponseEntity object, with paymentStr (of type String) as response body, and http status OK (200)
        return new ResponseEntity<>(paymentStr, HttpStatus.OK);
    }

    @PutMapping("/payment-complete")
    public ResponseEntity<String> stripePaymentComplete(@RequestHeader(value = "Authorization") String token) throws Exception {
        // check if this user is authenticated or not (because only authenticated user can call this API)
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        if(userEmail == null) {
            throw new Exception("User email is missing!");
        }
        // because the stripePayment(userEmail) method returns a ResponseEntity object, with a response body of type String, so we can use this method as the return in this function
        return paymentService.stripePayment(userEmail);
    }
}

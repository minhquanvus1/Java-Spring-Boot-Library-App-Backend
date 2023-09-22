package com.luv2code.springbootlibrary.requestmodels;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentInfoRequest {

    private int amount;

    private String currency;

    private String receiptEmail;
}

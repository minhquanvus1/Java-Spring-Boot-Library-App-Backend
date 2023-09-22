package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // return a Payment object that relates to this userEmail (how about this user has made a lot of payments?)
    // available at: https://localhost:8443/api/payments/search/findByUserEmail?userEmmail=:userEmail
    Payment findByUserEmail(String userEmail);
}

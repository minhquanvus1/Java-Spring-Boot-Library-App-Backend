package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // return a Payment object that relates to this userEmail (how about this user has made a lot of payments? Answer: a user will never have many payments with amount > 0 at the same time. Because: a Payment object is created when that user CHECKS OUT a book, and currently, he doesn't have any Checkouts books that is late return. So once the user is having a Payment with amount > 0, OR, currently having a Checkout that is overdue, he will not be able to checkout any more book!, no other Payment objects are created)
    // available at: https://localhost:8443/api/payments/search/findByUserEmail?userEmmail=:userEmail
    Payment findByUserEmail(String userEmail);
}

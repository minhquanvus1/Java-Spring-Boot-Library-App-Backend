package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    // return the Book that has been checked out by the user with this userEmail, and the book he checked out has id = bookId
    // available at: http://localhost:8080/api/checkouts/search/findByUserEmailAndBookId{?userEmail&bookId}
    Checkout findByUserEmailAndBookId(String userEmail, Long bookId);
}

package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource
public interface CheckoutRepository extends JpaRepository<Checkout, Long> {

    // return the Book that has been checked out by the user with this userEmail, and the book he checked out has id = bookId
    // available at: http://localhost:8080/api/checkouts/search/findByUserEmailAndBookId{?userEmail&bookId}
    Checkout findByUserEmailAndBookId(String userEmail, Long bookId);

    // return list of Checkout objects (books that has been checked out) that a particular user (with the particular email) has checked out
   // the SAME as: select * from checkout where user_email := email;
    List<Checkout> findBooksByUserEmail(String email);
}

package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying // add @Modidying annotation: because we're deleting records in database --> we're adding a query to MODIFY the database
    @Query("delete from checkout where book_id := book_id")
    // here, we want that: if admin deletes a book, then we want to delete all records in checkout table whose book_id == book_id of this book
    void deleteAllByBookId(@Param("book_id") Long bookId);
}

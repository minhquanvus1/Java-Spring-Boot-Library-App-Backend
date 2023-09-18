package com.luv2code.springbootlibrary.service;


import com.luv2code.springbootlibrary.dao.BookRepository;
import com.luv2code.springbootlibrary.dao.CheckoutRepository;
import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.entity.Checkout;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class BookService {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;

    // construcor dependency injection
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
    }

    // create a book checkout service that takes in userEmail, bookId as parameter, and return the Book that he wants to check out
    public Book checkoutBook (String userEmail, Long bookId) throws Exception {

        // find the Book with this bookId in the book table in database ("Optional": because this bookId does not exist)
        Optional<Book> book = bookRepository.findById(bookId);

        // find this book record in the checkout table in database (return "null" if this book record does not exist in checkout table --> this user has NOT checked out this book yet)
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        // LOGIC for being able to checkout a book: a user can ONLY checkout 1 copy of a book
        // Case 1: user can NOT check out this book: the book DNExist in book table, the user has ALREADY checked out 1 copy of the book, the book has run out of copies to be checked out
        if(!book.isPresent() || validateCheckout != null || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book does not exist, or already checked out by user");
        }
        // Case 2: user can check out 1 copy of this book
        // reduce the number of copies by 1
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        // save infor of this checked out book to the book table in database (here, the # of copies of this book has REDUCED by 1 --> save this infor to database)
        bookRepository.save(book.get());

        // save infor of this Checkout object into the checkout table in database
        Checkout checkout = new Checkout(userEmail, LocalDate.now().toString(), LocalDate.now().plusDays(7).toString(), book.get().getId());
        checkoutRepository.save(checkout);

        // return the book object that user has just checked out
        return book.get();
    }
}

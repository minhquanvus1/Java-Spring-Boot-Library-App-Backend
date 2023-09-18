package com.luv2code.springbootlibrary.controller;

import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.service.BookService;
import com.luv2code.springbootlibrary.utils.ExtractJWT;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/books")
public class BookController {
    private BookService bookService;

    // constructor dependency injection
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PutMapping("/secure/checkout")
    public Book checkoutBook(@RequestParam Long bookId, @RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = "john.doe@test.com";
        return bookService.checkoutBook(userEmail, bookId);

    }

    @GetMapping("/secure/ischeckedout/byuser")
    public Boolean checkoutBookByUser(@RequestParam Long bookId, @RequestHeader(value = "Authorization") String token) {
        // userEmail will be extracted from JWT when the user successfully logged in
        String userEmail = ExtractJWT.payloadJWTExtraction(token);
        return bookService.checkoutBookByUser(userEmail, bookId);
    }

    @GetMapping("/secure/currentloans/count")
    public int currentLoansCount(@RequestHeader(value = "Authorization") String token) {
        // userEmail will be extracted from JWT when the user successfully logged in
        String userEmail = ExtractJWT.payloadJWTExtraction(token);
        return bookService.currentLoansCount(userEmail);
    }
}

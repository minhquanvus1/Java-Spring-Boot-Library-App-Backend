package com.luv2code.springbootlibrary.requestmodels;

import lombok.Data;

import java.util.Optional;

@Data
public class ReviewRequest {

    private double rating;

    private Long bookId;

    // "Optional": because reviewDescription is not required for a particular book (in other words, user is not required to have to write a review for a book)
    private Optional<String> reviewDescription;
}

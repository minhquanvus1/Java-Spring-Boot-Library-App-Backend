package com.luv2code.springbootlibrary.service;


import com.luv2code.springbootlibrary.dao.ReviewRepository;
import com.luv2code.springbootlibrary.entity.Review;
import com.luv2code.springbootlibrary.requestmodels.ReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;

@Service
@Transactional
public class ReviewService {


    private ReviewRepository reviewRepository;

    // constructor dependency injection
    @Autowired
    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }
    public void postReview(String userEmail, ReviewRequest reviewRequest) throws Exception {
        // when user sends a ReviewRequest object to backend, then, Spring Boot backend will check with review table in database, if this user has already made a review for this particular book
        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.getBookId());

        // this user has already made a review for this particular book
        if(validateReview != null) {
            throw new Exception("Review already created");
        }
        // create a Review object
        Review review = new Review();
        // initialize the attributes of Review object
        review.setBookId(reviewRequest.getBookId());
        review.setRating(reviewRequest.getRating());
        review.setUserEmail(userEmail);

        // if user has left a review (reviewDescription), then, because this reviewDescription is of type Optional<String>
        // ---> the Optional<> will copy the string in a different way
        // ---> we need to use "map" function to copy all data from Optional object, into our String
        if(reviewRequest.getReviewDescription().isPresent()) {
            review.setReviewDescription(reviewRequest.getReviewDescription().map(Object :: toString).orElse(null));
        }
        review.setDate(Date.valueOf(LocalDate.now()));
        // save Review object to review table in database
        reviewRepository.save(review);
    }

    // check if a user has made a review for a particular book
    public boolean userReviewListed(String userEmail, Long bookId) {
        Review validateReview = reviewRepository.findByUserEmailAndBookId(userEmail, bookId);
        // this user has already made a review for this book
        return validateReview != null;
//        if(validateReview != null) {
//            return true;
//        } else {
//            return false;
//        }
    }
}

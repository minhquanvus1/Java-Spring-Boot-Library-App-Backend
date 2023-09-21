package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // available at: http://localhost:8080/api/reviews/search/findByBookId{?book_id}
    // the SAME as: select * from review where book_id := book_id;
    Page<Review> findByBookId(@RequestParam("book_id") Long bookId, Pageable pageable);

    Review findByUserEmailAndBookId(String userEmail, Long bookId);

    @Modifying // add @Modidying annotation: because we're deleting records in database --> we're adding a query to MODIFY the database
    @Query("delete from review where book_id = book_id")
        // here, we want that: if admin deletes a book, then we want to delete all records in review table whose book_id == book_id of this book
    void deleteAllByBookId(@Param("book_id") Long book_id);
}

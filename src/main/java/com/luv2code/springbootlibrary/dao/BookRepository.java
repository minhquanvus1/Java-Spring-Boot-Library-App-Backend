package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RepositoryRestResource
public interface BookRepository extends JpaRepository<Book, Long> {
    // available at: http://localhost:8080/api/books/search/findByTitleContaining{?title=}
    // the SAME as: select * from book where title:= title;
    Page<Book> findByTitleContaining(@RequestParam("title") String title, Pageable pageable);

    // available at: http://localhost:8080/api/books/search/findByCategory{?category=}
    // the SAME as: select * from book where category:= category;

    Page<Book> findByCategory(@RequestParam("category") String category, Pageable pageable);

    @Query("select o from Book o where id in :book_ids")
    // select each book record in Book table, that this book has id in this list of book_ids;
    List<Book> findBooksByBookIds(@Param("book_ids") List<Long> bookIds);
}

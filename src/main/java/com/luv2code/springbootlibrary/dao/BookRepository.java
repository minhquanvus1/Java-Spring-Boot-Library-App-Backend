package com.luv2code.springbootlibrary.dao;

import com.luv2code.springbootlibrary.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.RequestParam;

@RepositoryRestResource
public interface BookRepository extends JpaRepository<Book, Long> {
    // available at: http://localhost:8080/api/books/search/findByTitleContaining{?title=}
    // the SAME as: select * from book where title:= title;
    Page<Book> findByTitleContaining(@RequestParam("title") String title, Pageable pageable);

    // available at: http://localhost:8080/api/books/search/findByCategory{?category=}
    // the SAME as: select * from book where category:= category;

    Page<Book> findByCategory(@RequestParam("category") String category, Pageable pageable);
}

package com.luv2code.springbootlibrary.service;

import com.luv2code.springbootlibrary.dao.BookRepository;
import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.requestmodels.AddBookRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AdminService {

    private BookRepository bookRepository;

    // constructor dependency injection
    public AdminService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void postBook(AddBookRequest addBookRequest) {
        Book book = new Book();
        book.setTitle(addBookRequest.getTitle());
        book.setDescription(addBookRequest.getDescription());
        book.setCopies(addBookRequest.getCopies());
        book.setCopiesAvailable(addBookRequest.getCopies());
        book.setImg(addBookRequest.getImg());
        book.setAuthor(addBookRequest.getAuthor());
        book.setCategory(addBookRequest.getCategory());

        bookRepository.save(book);
    }

    // create a function to allow admin to increase quantity of a particular book
    public void increaseBookQuantity(Long bookId) throws Exception {
        // check if this book does exist in the database or not (because we can't increase quantity of a non-exist book)
        Optional<Book> book = bookRepository.findById(bookId);
        if(!book.isPresent()) {
            throw new Exception("Book does not exist");
        }
        // increase quantity of a book == increase its total copies, and increase its total copiesAvailable
        book.get().setCopies(book.get().getCopies() + 1);
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);

        // remember to save this book (with updated infor) back to the database
        bookRepository.save(book.get());
    }

    // create a function to allow admin to decrease quantity of a book
    public void decreaseBookQuantity(Long bookId) throws Exception {
        // check if this book actually exists in the database (because we can't decrease quantity of a book that does not exist in our databse)
        Optional<Book> book = bookRepository.findById(bookId);
        // if this book does not exist, or its current total copies == 0 (het hang), or its current total copiesAvailable == 0 (sach nay da cho muon het roi)
        if(!book.isPresent() || book.get().getCopies() <= 0 || book.get().getCopiesAvailable() <= 0) {
            throw new Exception("Book does not exist, or quantity locked");
        }
        book.get().setCopies(book.get().getCopies() - 1);
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() - 1);
        // save this book with the updated data back to the database
        bookRepository.save(book.get());
    }
}

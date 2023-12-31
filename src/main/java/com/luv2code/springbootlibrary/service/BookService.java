package com.luv2code.springbootlibrary.service;


import com.luv2code.springbootlibrary.dao.BookRepository;
import com.luv2code.springbootlibrary.dao.CheckoutRepository;
import com.luv2code.springbootlibrary.dao.HistoryRepository;
import com.luv2code.springbootlibrary.dao.PaymentRepository;
import com.luv2code.springbootlibrary.entity.Book;
import com.luv2code.springbootlibrary.entity.Checkout;
import com.luv2code.springbootlibrary.entity.History;
import com.luv2code.springbootlibrary.entity.Payment;
import com.luv2code.springbootlibrary.responsemodels.ShelfCurrentLoansResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional // all methods in this class are TRANSACTIONAL (either successfuly, or fail (and roll back the whole method to the previous state right before the method, if any thing in the method(transaction) fails))
public class BookService {

    private BookRepository bookRepository;
    private CheckoutRepository checkoutRepository;

    private HistoryRepository historyRepository;

    private PaymentRepository paymentRepository;

    // construcor dependency injection
    public BookService(BookRepository bookRepository, CheckoutRepository checkoutRepository, HistoryRepository historyRepository, PaymentRepository paymentRepository) {
        this.bookRepository = bookRepository;
        this.checkoutRepository = checkoutRepository;
        this.historyRepository = historyRepository;
        this.paymentRepository = paymentRepository;
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
        // return a list of Checkout objects, which are the books that this user is borrowing/checking out (has not returned yet, because if the user has returned this book, we will delete the record relating to his email address in the checkout table)
        List<Checkout> currentBooksCheckedOut = checkoutRepository.findBooksByUserEmail(userEmail);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        boolean booksNeedReturned = false;

        // use for loop to iterate through each Checkout object in this list of Checkout objects, to see which book is over due (if this book is over due, then break out of the for loop)
        for(Checkout checkout : currentBooksCheckedOut) {
            // expected return date
            Date d1 = sdf.parse(checkout.getReturnDate());
            // at the present (today)
            Date d2 = sdf.parse(LocalDate.now().toString());

            TimeUnit time = TimeUnit.DAYS;
            double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);

            // if expected return date < today  --> this book has OVER DUE
            if(differenceInTime < 0) {
                booksNeedReturned = true; // this book NEEDS TO BE RETURNED
                break; // break out of the innermost loop (here, which is the for loop)
            }
        }
        // return a Payment object relating to this user
        Payment userPayment = paymentRepository.findByUserEmail(userEmail);

        // if this user's payment is already in payment table, and the amount > 0, OR this user's payment is already in payment table, and this book needs to be returned is true --> then, announce that this user has to pay fee before being able to proceed the below code to checkout another book
        // so, there are 2 situations where a user can't checkout a book: 1. this user has not paid the fee for his last over-due book (userPayment != null), and this fee/amount must be > 0 (if amount == 0, this means this user has already paid the fee, so he can checkout normally). 2. This user has already paid the fee/amount for his last over-due book (userEmail != 0, so amount can be == 0), but this book-the book he's borrowing, is overdue (expected return date < today)
        if((userPayment != null && userPayment.getAmount() > 0) || (userPayment != null && booksNeedReturned)) {
            throw new Exception("Outstanding fees");
        }
        // if this user's payment is not in the payment table yet (this is the 1st time this user has an over-due book)
        if(userPayment == null) {
            Payment payment = new Payment();
            payment.setAmount(0.00);
            payment.setUserEmail(userEmail);
            // create a new Payment object for this user, and save it to payment table in database
            paymentRepository.save(payment);
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

    // function to check if a user has already checked out this book or not
    public Boolean checkoutBookByUser(String userEmail, Long bookId) {
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        if(validateCheckout != null) {
            // if this user has checked out this book
            return true;
        } else {
            // if this user has not checked out this book yet
            return false;
        }
    }
    // count # of checkouts that a user with this userEmail has checked out (by finding the size() of the returned list of checkouts that a user with this userEmail has checked out)
    public int currentLoansCount(String userEmail) {
        return checkoutRepository.findBooksByUserEmail(userEmail).size();
    }

    // return the List of ShelfCurrentLoansResponse (Books that this user is in the process of checking out/borrowing, with the daysLeft for returning of each book)
    public List<ShelfCurrentLoansResponse> currentLoans(String userEmail) throws Exception {
        // create an empty list, which is also the list that this function will return at the end
        List<ShelfCurrentLoansResponse> shelfCurrentLoansResponses = new ArrayList<>();

        // return a list of Checkout objects representing Books that this user has checked out
        List<Checkout> checkoutList = checkoutRepository.findBooksByUserEmail(userEmail);

        // because the ShelfCurrentLoansResponse object requires the 1st parameter as the Book object (not Checkout object) --> we need to convert a Checkout object into Book object, by using the id
        // an arrayList to store all bookId of all Checkouts books that this user has checked out
        List<Long> bookIdList = new ArrayList<>();

        // extract all the bookId of each Checkout object, and add into bookIdList arrayList
        // ---> after this for loop, we get: the list of book ids representing books that this user is currently checking out/borrowing
        for(Checkout i : checkoutList) {
            bookIdList.add(i.getBookId());
        }
        // return list of books (Book objects) that has id in this list of bookIdList
        // ---> we get: the list of Books (Book objects, not Checkout objects anymore) that this user is currently checking out/borrowing
        List<Book> books = bookRepository.findBooksByBookIds(bookIdList);

        // is used to: compare dates, to see how many days left for this book to be returned, or how late the book is
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // so at this time, we already get the list of Book objects that this user is borrowing (each Book object in this list is the 1st parameter of the ShelfCurrentLoansResponse object)
        // we only need the (int) daysLeft for each Book object.
        // However, the returnDate attribute ONLY belongs to the Checkout object (not of Book object)
        // ---> We need to: loop through the list of Book objects --> find the only 1 matching Checkout object for this Book object --> if this matching Checkout object exists --> getReturnDate --> combine with LocalDate.now() to find the difference in milliseconds (using getTime()) --> then use TimeUnit.time to convert milliseconds to Days (in "long" data type, which may contain so thap phan) --> type cast to (int), because we need whole number for daysLeft
        for(Book book: books) {
            Optional<Checkout> checkout = checkoutList.stream().filter(x -> x.getBookId() == book.getId()).findFirst();
            if(checkout.isPresent()) {
                Date d1 = sdf.parse(checkout.get().getReturnDate());
                Date d2 = sdf.parse(LocalDate.now().toString());

                TimeUnit time = TimeUnit.DAYS;
                long difference_In_Time = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);

                // for each Book object, initialize a new ShelfCurrentLoansResponse object, pass in that Book, and the difference_In_Time
                // --> then, add this object into the list of ShelfCurrentLoansResponse objects, created at the beginning
                shelfCurrentLoansResponses.add(new ShelfCurrentLoansResponse(book, (int) difference_In_Time));
            }
        }
        // return the list of ShelfCurrentLoansResponse objects (list of books (with daysLeft for each book), that this user is checking out/borrowing)
        return shelfCurrentLoansResponses;
    }

    // function (a book service) that allows user to return a book that he has checked out/borrowed
    public void returnBook(String userEmail, Long bookId) throws Exception {
        // the "findById" method: return a Book object that has this id, then, this Book object will be wrapped/stored in an Optional object
        Optional<Book> book = bookRepository.findById(bookId);
        // find out if this user has really checked out this book
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);

        // check if the book with this bookId actually exists, or if this user has actually checked out this book
        if(!book.isPresent() || validateCheckout == null) {
            throw new Exception("Book does not exist, or not checked out by user");
        }

        // use: Optional.get(): to get the actual object stored in the Optional object (in this case, that is the Book object)
        // increase the copies available of the returned book by 1
        book.get().setCopiesAvailable(book.get().getCopiesAvailable() + 1);
        // save the Book (with the updated attribute back to the book table in database)
        bookRepository.save(book.get());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        TimeUnit time = TimeUnit.DAYS;

        Date d1 = sdf.parse(validateCheckout.getReturnDate());
        Date d2 = sdf.parse(LocalDate.now().toString());

        double differenceInTime = time.convert(d1.getTime() - d2.getTime(), TimeUnit.MILLISECONDS);

        if(differenceInTime < 0) {
            Payment payment = paymentRepository.findByUserEmail(userEmail);
            payment.setAmount(payment.getAmount() + (differenceInTime * (-1)));
            paymentRepository.save(payment);
        }
        // delete the record for this book in the checkout table in database
        checkoutRepository.deleteById(validateCheckout.getId());

        // we want that: History section will store the list of books that a user has returned (after borrowing/checking out for a while)
        // ---> Right after the returning process, we initialize a History object (containing infor about the returned book), and save this History object into history table in database
        History history = new History(
                userEmail,
                validateCheckout.getCheckoutDate(),
                //validateCheckout.getReturnDate(), // this is not suitable, because user may return a book earlier than return due date. But we want to get the ACTUAL returned date of the book --> so it must be Localdate.now() which is at the present
                LocalDate.now().toString(),
                book.get().getTitle(),
                book.get().getAuthor(),
                book.get().getDescription(),
                book.get().getImg()
        );
        historyRepository.save(history); // store this History object in history table in database
    }

    public void renewLoan(String userEmail, Long bookId) throws Exception {

        Optional<Book> book = bookRepository.findById(bookId);
        Checkout validateCheckout = checkoutRepository.findByUserEmailAndBookId(userEmail, bookId);
        if(!book.isPresent()|| validateCheckout == null) {
            throw new Exception("Book does not exist, or not checked out by user");
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = sdf.parse(validateCheckout.getReturnDate()); // return date
        Date d2 = sdf.parse(LocalDate.now().toString()); // today
        // if return date > today, or return date == today (not pass/exceed the return due date yet) --> allow user to renew the loan (extend 7 more days from today)
        if(d1.compareTo(d2) > 0 || d1.compareTo(d2) == 0) {
            validateCheckout.setReturnDate(LocalDate.now().plusDays(7).toString()); // convert to String, because the returnDate in Checkout object is of type String (which we will pass as parameter to the setReturnDate() method)
            checkoutRepository.save(validateCheckout); // save this new checkout object (with the new return date) to the checkout table in database
        }
    }
}

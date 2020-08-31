package fr.d2factory.libraryapp.library;


import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.member.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/*

* @Author Moulay Driss KANZITI El BELRHITI
* @Mail driss.kanziti@gmail.com
*
* */

public class LibraryImpl implements Library {

    private static volatile LibraryImpl instance = null;
    BookRepository bookRepository;
    LibraryRegistry libraryRegistry;

    Logger logger = LoggerFactory.getLogger(LibraryImpl.class);

    private LibraryImpl(BookRepository bookRepository, LibraryRegistry libraryRegistry) {
        this.bookRepository = bookRepository;
        this.libraryRegistry = libraryRegistry;
    }

    public static LibraryImpl getInstance(){
        if(instance == null){
            synchronized (LibraryImpl.class){
                instance = new LibraryImpl(BookRepository.getInstance(), LibraryRegistry.getInstance());
            }
        }
        return instance;
    }

    @Override
    public Book borrowBook(long isbnCode, Member member, LocalDate borrowedAt)throws HasLateBooksException {

        Book book = bookRepository.findBook(isbnCode); // check if the book is available

        if (!isMemberHasLateBook(member)) { // check the status of the member
            bookRepository.saveBookBorrow(book, borrowedAt);
            libraryRegistry.addBorrowedBookToRegistry(book, member);
        }
        logger.info(member.getMemberName() + " has borrowed the book : "+book.getTitle()+ " in "+borrowedAt.toString());
        return book;
    }

    @Override
    public void returnBook(Book book, Member member) {
        LocalDate returnedAt = LocalDate.now();
        LocalDate borrowedAt = bookRepository.findBorrowedBookDate(book);
        int numberOfDays = getPeriodInDays(borrowedAt, returnedAt);
        try {
            member.payBook(numberOfDays);
        }catch(WalletInsufficientCreditException e){
            logger.info(e.getMessage());
        }
        bookRepository.saveBookReturn(book);
        libraryRegistry.removeReturnedBookFromRegistry(book, member);

        logger.info(member.getMemberName() + " returned the book : "+book.getTitle()+ " in "+returnedAt.toString());
        logger.info("The book is kept for : "+numberOfDays+" day(s)");
    }

    public boolean isMemberHasLateBook(Member member){
        LocalDate today = LocalDate.now();
        int periodBeforeBeLate = member.getNumberOfDaysAllowedToKeepBooks();
        for (Book book : libraryRegistry.getMemberListBorrowedBooks(member)) {
            LocalDate bookBorrowDate = BookRepository.getInstance().getBorrowedBooks().get(book);
            if (today.isAfter(bookBorrowDate.plusDays(periodBeforeBeLate))) {
                throw new HasLateBooksException(member);
            }
        }
        return false;
    }

    public int getPeriodInDays(LocalDate borrowedAt, LocalDate returnedAt) {
        return (int) ChronoUnit.DAYS.between(borrowedAt, returnedAt);
    }


}

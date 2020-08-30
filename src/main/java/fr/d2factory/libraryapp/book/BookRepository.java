package fr.d2factory.libraryapp.book;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The book repository emulates a database via 2 HashMaps
 */
public class BookRepository {
    private static volatile BookRepository instance = null;
    private static final Map<ISBN, Book> availableBooks = new HashMap<>();
    private static final Map<Book, LocalDate> borrowedBooks = new HashMap<>();

    // Singleton pattern to keep one instance of this repository class
    private BookRepository() {}
    public static BookRepository getInstance(){
        if(instance == null){
            synchronized (BookRepository.class){
                instance = new BookRepository();
            }
        }
        return instance;
    }

    public void addBook(ISBN isbn, Book book) {
        availableBooks.put(isbn, book);
    }

    public void addBooks(List<Book> books){
        books.forEach(book -> {
            this.addBook(book.isbn, book);
        });
    }

    public Book findBook(long isbnCode) {
        return availableBooks.get(new ISBN(isbnCode));
    }

    public void saveBookBorrow(Book book, LocalDate borrowedAt){
        borrowedBooks.put(book, borrowedAt);
        availableBooks.remove(book.getIsbn());
    }

    public LocalDate findBorrowedBookDate(Book book) {
        return borrowedBooks.get(book);
    }

    public void saveBookReturn(Book book){
        availableBooks.put(book.getIsbn(), book);
        borrowedBooks.remove(book);
    }

    public Map<ISBN, Book> getAvailableBooks() {
        return availableBooks;
    }

    public Map<Book, LocalDate> getBorrowedBooks() {
        return borrowedBooks;
    }
}

package fr.d2factory.libraryapp.library;

public class BookNotAvailableException extends RuntimeException {
    public BookNotAvailableException(long isbnCode) {
        super("Sorry the book["+isbnCode+"] is not available !");
    }
}

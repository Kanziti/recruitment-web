package fr.d2factory.libraryapp.library;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.book.ISBN;
import fr.d2factory.libraryapp.member.Member;
import fr.d2factory.libraryapp.member.Resident;
import fr.d2factory.libraryapp.member.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Do not forget to consult the README.md :)
 */
public class LibraryTest {
    private Library library ;
    private BookRepository bookRepository;
    private static List<Book> books;

    @BeforeEach
    void setup() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        File booksJson = new File("src/test/resources/books.json");
        books = mapper.readValue(booksJson, new TypeReference<List<Book>>() {
        });

        library = LibraryImpl.getInstance();
        LibraryRegistry libraryRegistry = LibraryRegistry.getInstance();
        bookRepository = BookRepository.getInstance();

        bookRepository.addBooks(books);

    }

    @Test
    void create_book(){
        Book book = new Book("Emma","Jane Austen",new ISBN(123456789L));
        Assertions.assertEquals("Emma", book.getTitle());
    }

    @Test
    void add_book_to_library(){
        ISBN isbn = new ISBN(123456789L);
        Book book = new Book("Emma","Jane Austen", isbn);

        int numberOfExistingBooks = bookRepository.getAvailableBooks().size();
        bookRepository.addBook(isbn, book);
        int actualNumberOfExistingBooks = bookRepository.getAvailableBooks().size();
        Assertions.assertEquals(numberOfExistingBooks + 1, actualNumberOfExistingBooks);
    }

    @Test
    void a_book_exist_in_library(){
        Book expectedBook = books.get(0);
        Book foundedBook = bookRepository.findBook(expectedBook.getIsbn().getIsbnCode());
        Assertions.assertEquals(expectedBook, foundedBook);
    }

    @Test
    void create_members(){
        Member studentInFirstYear = new Student(1L, "Nicolas", 20f, true);
        Member studentNotInFirstYear = new Student(2L, "Marine", 10f, false);
        Member resident = new Resident(3L, "Pierre", 30f);

        Assertions.assertEquals("Nicolas", studentInFirstYear.getMemberName());
        Assertions.assertEquals("Marine", studentNotInFirstYear.getMemberName());
        Assertions.assertEquals("Pierre", resident.getMemberName());
    }


    @Test
    void member_can_borrow_a_book_if_book_is_available(){
        Member student0 = new Student(10L,"Rachelle",50f,true);
        //try to borrow unavailable book (isbn : 93325648793) -> Should throw BookNotAvailable Exception
        Assertions.assertThrows(BookNotAvailableException.class,
                ()->library.borrowBook(93325648793L,student0, LocalDate.parse("2020-08-31")));

        //try to borrow an available book (isbn : 3326456467846) -> member can borrow it
        Book availableBook = bookRepository.findBook(3326456467846L);
        Assertions.assertEquals(availableBook, library.borrowBook(3326456467846L,student0, LocalDate.parse("2020-08-31")));
    }

    @Test
    void borrowed_book_is_no_longer_available(){
        Member student1 = new Student(1L,"Pierre",50f,true);

        /* student1 borrow the book with ISBN 3326456467846 */
        Book book1 = library.borrowBook(3326456467846L,student1, LocalDate.parse("2020-05-18"));

        /* the book with ISBN 3326456467846 is no longer available */
        Member resident1 = new Resident(2L, "Nicolas", 20f);

        /* when resident1 try to borrow this book we should have BookNotAvailableException*/
        Assertions.assertThrows(BookNotAvailableException.class, ()->
                library.borrowBook(3326456467846L,resident1, LocalDate.parse("2020-05-18")));
    }

    @Test
    void members_can_return_borrowed_book(){
        Member student2 = new Student(2L, "Marine", 10f, false);
        Book book = library.borrowBook(465789453149L, student2, LocalDate.parse("2020-08-21"));

        library.returnBook(book, student2);
    }

    @Test
    void members_cannot_pay_if_they_have_not_enough_wallet_credit(){
        //Create Member resident with 1 eu in wallet -> 10 days
        Member resident = new Resident(3L, "Pierre", 1f);

        // Pay 11 days (11*0.10= 1.1eu > 1.0 in this resident wallet --> should throw Exception
        Assertions.assertThrows(WalletInsufficientCreditException.class, ()-> resident.payBook(11));
    }

    @Test
    void residents_are_taxed_10cents_for_each_day_they_keep_a_book(){
        Member resident = new Resident(3L, "Pierre", 30f);
        resident.payBook(20); // 20 days * 0.10eu = 2.0eu
        Assertions.assertEquals(28f, resident.getWallet());
    }

    @Test
    void students_pay_10_cents_the_first_30days(){
        Member studentNotInFirstYear = new Student(2L, "Marine", 10f, false);
        studentNotInFirstYear.payBook(30); // 30 days * 0.10eu = 3.0eu : 10-3.0 = 7
        Assertions.assertEquals(7f,studentNotInFirstYear.getWallet());
    }

    @Test
    void students_in_1st_year_are_not_taxed_for_the_first_15days(){
        Member studentInFirstYear = new Student(1L, "Nicolas", 20f, true);
        studentInFirstYear.payBook(30); // first 15 days free + 15 days * 0.10eu = 1.50eu : 20-1.50 = 18.5
        Assertions.assertEquals(18.5f,studentInFirstYear.getWallet());
    }
    
    @Test
    void residents_pay_20cents_for_each_day_they_keep_a_book_after_the_initial_60days(){
        Member resident = new Resident(3L, "Pierre", 30f);
        resident.payBook(61); // 60 days * 0.10eu + 1 day * 0.2eu = 6.2eu : 30-6.2 = 23.8
        Assertions.assertEquals(23.8f,resident.getWallet());
    }

    @Test
    void members_cannot_borrow_book_if_they_have_late_books(){
        // Student case
        Member student1 = new Student(3L,"Paul",10f,true);

        // student1 has borrowed a book for 31 days ago -> is late !
        Book book1 = library.borrowBook(3326456467846L,student1, LocalDate.now().minusDays(31));

        // When student1 try to borrow  another book -> we should have an HasLateBooksException
        Assertions.assertThrows(HasLateBooksException.class, ()-> library.borrowBook(968787565445L,student1, LocalDate.parse("2020-08-31")));

        // Resident case
        Member resident1 = new Resident(2L,"Michel",20f);

        // resident1 has borrowed a book for 61 days ago -> is late !
        Book book3 = library.borrowBook(465789453149L,resident1, LocalDate.now().minusDays(61));

        // When resident1 try to borrow  another book -> we should have an HasLateBooksException
        Assertions.assertThrows(HasLateBooksException.class, ()-> library.borrowBook(968787565445L,resident1, LocalDate.parse("2020-08-31")));
    }

}

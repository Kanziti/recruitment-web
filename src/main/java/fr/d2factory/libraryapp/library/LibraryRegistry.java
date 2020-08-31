package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.book.Book;
import fr.d2factory.libraryapp.member.Member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LibraryRegistry {

    private static volatile LibraryRegistry instance = null;

    // Map to emulate registry of members who have borrowed books
    Map<Member, List<Book>> borrowRegistry = new HashMap<>();

    // Singleton pattern to keep one instance of this registry class
    private LibraryRegistry() {}
    public static LibraryRegistry getInstance(){
        if(instance == null){
            synchronized (LibraryRegistry.class){
                instance = new LibraryRegistry();
            }
        }
        return instance;
    }

    public List<Book> getMemberListBorrowedBooks(Member member){
        if(borrowRegistry.containsKey(member)){
            return borrowRegistry.get(member);
        }else{
            return new ArrayList<>();
        }
    }

    public void addBorrowedBookToRegistry(Book book, Member member){
        List<Book> books = this.getMemberListBorrowedBooks(member);
        books.add(book);
        borrowRegistry.put(member,books);
    }

    public void removeReturnedBookFromRegistry(Book book, Member member){
        List<Book> books = this.getMemberListBorrowedBooks(member);
        books.remove(book);
        borrowRegistry.put(member,books);
    }

}

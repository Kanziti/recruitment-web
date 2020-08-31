package fr.d2factory.libraryapp.library;

import fr.d2factory.libraryapp.member.Member;

/**
 * This exception is thrown when a member who owns late books tries to borrow another book
 */
public class HasLateBooksException extends RuntimeException {

    public HasLateBooksException(Member member) {
        super(member.getMemberName() + " owns late books, can't borrow other books");
    }
}

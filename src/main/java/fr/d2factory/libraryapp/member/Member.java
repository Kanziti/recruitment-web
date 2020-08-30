package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.Library;

import java.util.Objects;

/**
 * A member is a person who can borrow and return books to a {@link Library}
 * A member can be either a student or a resident
 */
public abstract class Member {
    /**
     * simple Member identifiers
     */
    private long id;
    private String memberName;
    private final int numberOfDaysAllowedToKeepBooks;

    /**
     * An initial sum of money the member has
     */
    private float wallet;

    public Member(long id, String memberName, float wallet, int numberOfDaysAllowedToKeepBooks) {
        this.id = id;
        this.memberName = memberName;
        this.wallet = wallet;
        this.numberOfDaysAllowedToKeepBooks = numberOfDaysAllowedToKeepBooks;
    }

    /**
     * The member should pay their books when they are returned to the library
     *
     * @param numberOfDays the number of days they kept the book
     */
    public abstract void payBook(int numberOfDays);

    /**
     * Getters and Setters
     */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public float getWallet() {
        return wallet;
    }

    public void setWallet(float wallet) {
        this.wallet = wallet;
    }

    public int getNumberOfDaysAllowedToKeepBooks() {
        return numberOfDaysAllowedToKeepBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id == member.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
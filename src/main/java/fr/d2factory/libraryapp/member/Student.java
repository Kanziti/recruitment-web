package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.library.WalletInsufficientCreditException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Student extends Member {

    Logger logger = LoggerFactory.getLogger(Student.class);

    /* A student, regardless of what year they are in, keeps a book
    for more than 30 days they are considered to be "late"*/
    private static final int numberOfDaysAllowedToKeepBooks = 30;

    // Students are charged 10 cents a day (0.10 eu)
    private static final float pricePerDay = 0.10f;

    // Students who are in their first year have 15 days of free period for each book
    private static final int numberOfDaysFreeForFirstYear = 15;

    private boolean isInFirstYear;

    public Student(long id, String memberName, float wallet, boolean isInFirstYear) {
        super(id, memberName, wallet, numberOfDaysAllowedToKeepBooks);
        this.isInFirstYear = isInFirstYear;
    }

    @Override
    public void payBook(int numberOfDays) {
        // Check the period of borrow
        boolean isInLatePeriod = (numberOfDays > numberOfDaysAllowedToKeepBooks);
        float initialWallet = getWallet();
        float amountDue = 0f;
        int freeDays = isInFirstYear ? numberOfDaysFreeForFirstYear : 0;
        if(isInLatePeriod){
            amountDue = pricePerDay * (numberOfDaysAllowedToKeepBooks - freeDays);
        }else if(numberOfDays <= freeDays){
            amountDue = 0f;
        }else{
            amountDue = (numberOfDays - freeDays) * pricePerDay;
        }

        if(amountDue <= initialWallet){
            this.setWallet(getWallet() - amountDue);
        }else{
            throw new WalletInsufficientCreditException(amountDue);
        }
        logger.info(this.getMemberName()+ " payed a sum of "+ amountDue + " for "+numberOfDays+" day(s)");

    }

    // Getters and Setters


    public boolean isInFirstYear() {
        return isInFirstYear;
    }

    public void setInFirstYear(boolean inFirstYear) {
        isInFirstYear = inFirstYear;
    }

    public static int getNumberOfDaysFreeForFirstYear() {
        return numberOfDaysFreeForFirstYear;
    }

    public static float getPricePerDay() {
        return pricePerDay;
    }
}

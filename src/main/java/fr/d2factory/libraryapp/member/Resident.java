package fr.d2factory.libraryapp.member;

import fr.d2factory.libraryapp.book.BookRepository;
import fr.d2factory.libraryapp.library.WalletInsufficientCreditException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Resident extends Member {

    Logger logger = LoggerFactory.getLogger(Resident.class);

    /* Residents are allowed to borrow books for a period of 60 days */
    private static final int numberOfDaysAllowedToKeepBooks = 60;

    /* Residents are charged 10 cents a day (0.10 eu) for each day they keep the book */
    private static final float priceForNormalPeriod = 0.10f;

    /* If a resident keeps a book for more than 60 days they are obliged
    to pay 20 cents (0.20 eu) for each day after the initial 60 days and
    they are considered to be "late". */
    private static final float priceForLatePeriod = 0.20f;

    public Resident(long id, String memberName, float wallet) {
        super(id, memberName, wallet, numberOfDaysAllowedToKeepBooks);
    }

    @Override
    public void payBook(int numberOfDays) {
        // Check the period of borrow
        boolean isInLatePeriod = (numberOfDays > numberOfDaysAllowedToKeepBooks);
        float initialWallet = getWallet();
        float amountDue = 0f;
        if(isInLatePeriod){
            amountDue = (priceForNormalPeriod * numberOfDaysAllowedToKeepBooks)
                    + ((numberOfDays - numberOfDaysAllowedToKeepBooks) * priceForLatePeriod);
        }else{
            amountDue = priceForNormalPeriod * numberOfDays;
        }

        if(amountDue <= initialWallet){
            this.setWallet(getWallet() - amountDue);
        }else{
            throw new WalletInsufficientCreditException(amountDue);
        }

        logger.info(this.getMemberName()+ " payed a sum of "+ amountDue + " for "+numberOfDays+" day(s)");
    }

    public static float getPriceForNormalPeriod() {
        return priceForNormalPeriod;
    }

    public static float getPriceForLatePeriod() {
        return priceForLatePeriod;
    }
}

package fr.d2factory.libraryapp.library;

public class WalletInsufficientCreditException extends RuntimeException {
    public WalletInsufficientCreditException(float amountDue) {
        super("The amount due ( "+amountDue+" ) is over your wallet credit");
    }
}

package ru.walletservice.exception.walletException;

public class WalletInsufficientFundsException extends RuntimeException {
    public WalletInsufficientFundsException(String message) {
        super(message);
    }
}

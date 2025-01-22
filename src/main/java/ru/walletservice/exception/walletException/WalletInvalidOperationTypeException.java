package ru.walletservice.exception.walletException;

public class WalletInvalidOperationTypeException extends RuntimeException {
    public WalletInvalidOperationTypeException(String message) {
        super(message);
    }
}

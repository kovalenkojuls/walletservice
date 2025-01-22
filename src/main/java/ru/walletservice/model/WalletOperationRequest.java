package ru.walletservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class WalletOperationRequest {
    private UUID walletId;
    private OperationType operationType;
    private double amount;

    public enum OperationType {
        DEPOSIT,
        WITHDRAW
    }
}

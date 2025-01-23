package ru.walletservice.service;

import jakarta.persistence.LockTimeoutException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walletservice.exception.walletException.WalletInsufficientFundsException;
import ru.walletservice.exception.walletException.WalletInvalidOperationTypeException;
import ru.walletservice.exception.walletException.WalletNotFoundException;
import ru.walletservice.model.Wallet;
import ru.walletservice.model.WalletOperationRequest;
import ru.walletservice.repository.WalletRepository;

import java.util.ConcurrentModificationException;
import java.util.UUID;

/**
 * Сервис для управления кошельками.
 */
@Service
public class WalletService {

    private final WalletRepository walletRepository;

    /**
     * Конструктор сервиса.
     * @param walletRepository Репозиторий для работы с кошельками.
     */
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    /**
     * Выполняет операцию над кошельком (пополнение или снятие).
     *
     * @param request Запрос на операцию с кошельком.
     * @return Новый баланс кошелька после операции.
     * @throws ConcurrentModificationException Если возникли проблемы с получением пессимистической блокировки.
     */
    public double operateOnWallet(WalletOperationRequest request) {
        try {
            return performOperateOnWallet(request);
        }  catch (LockTimeoutException | LockAcquisitionException ex) {
            throw new ConcurrentModificationException("Wallet was updated by another user. Please try again.");
        }
    }

    /**
     * Внутренний метод, непосредственно выполняющий операцию над кошельком (пополнение или снятие).
     *
     * @param request Запрос на операцию с кошельком, содержащий ID кошелька, тип операции и сумму.
     * @return Новый баланс кошелька после выполнения операции.
     * @throws WalletNotFoundException Если кошелек с указанным ID не найден.
     * @throws WalletInsufficientFundsException Если на кошельке недостаточно средств для выполнения операции снятия.
     * @throws WalletInvalidOperationTypeException Если указан неверный тип операции.
     */
    @Transactional
    private double performOperateOnWallet(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findByWalletIdAndLock(request.getWalletId());
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found.");
        }

        double newAmount = wallet.getBalance();
        switch (request.getOperationType()) {
            case DEPOSIT:
                newAmount += request.getAmount();
                break;
            case WITHDRAW:
                if (newAmount < request.getAmount()) {
                    throw new WalletInsufficientFundsException("Insufficient funds.");
                }
                newAmount -= request.getAmount();
                break;
            default:
                throw new WalletInvalidOperationTypeException("Invalid operation type.");
        }

        wallet.setBalance(newAmount);
        walletRepository.save(wallet);

        return newAmount;
    }

    /**
     * Возвращает баланс кошелька.
     *
     * @param walletId ID кошелька.
     * @return Баланс кошелька.
     * @throws WalletNotFoundException Если кошелек не найден.
     */
    public double getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId);
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found");
        }
        return wallet.getBalance();
    }
}

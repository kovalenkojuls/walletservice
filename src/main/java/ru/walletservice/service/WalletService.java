package ru.walletservice.service;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walletservice.exception.walletException.WalletInsufficientFundsException;
import ru.walletservice.exception.walletException.WalletInvalidOperationTypeException;
import ru.walletservice.exception.walletException.WalletNotFoundException;
import ru.walletservice.model.Wallet;
import ru.walletservice.model.WalletOperationRequest;
import ru.walletservice.repository.WalletRepository;

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
     * @throws WalletNotFoundException          Если кошелек не найден.
     * @throws WalletInsufficientFundsException Если в кошельке недостаточно средств для снятия.
     * @throws WalletInvalidOperationTypeException Если указан неверный тип операции.
     */
    @Transactional
    @CachePut(value = "balances", key = "#request.walletId")
    public double operateOnWallet(WalletOperationRequest request) {
        Wallet wallet = walletRepository.findByWalletId(request.getWalletId());
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found");
        }

        double newAmount = wallet.getBalance();

        switch (request.getOperationType()) {
            case DEPOSIT:
                newAmount += request.getAmount();
                break;
            case WITHDRAW:
                if (newAmount < request.getAmount()) {
                    throw new WalletInsufficientFundsException("Insufficient funds");
                }
                newAmount -= request.getAmount();
                break;
            default:
                throw new WalletInvalidOperationTypeException("Invalid operation type");
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
    @Cacheable(value = "balances", key = "#walletId")
    public double getBalance(UUID walletId) {
        Wallet wallet = walletRepository.findByWalletId(walletId);
        if (wallet == null) {
            throw new WalletNotFoundException("Wallet not found");
        }
        return wallet.getBalance();
    }
}

package ru.walletservice.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.walletservice.exception.walletException.WalletInsufficientFundsException;
import ru.walletservice.exception.walletException.WalletNotFoundException;
import ru.walletservice.model.Wallet;
import ru.walletservice.model.WalletOperationRequest;
import ru.walletservice.repository.WalletRepository;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    @Test
    @DisplayName("Успешное получение баланса кошелька по uuid")
    void testGetBalanceSuccess() throws Exception {
        UUID walletId = UUID.randomUUID();
        double expectedBalance = 100.0;
        Wallet wallet = new Wallet(walletId, expectedBalance);
        when(walletRepository.findByWalletId(walletId)).thenReturn(wallet);

        double actualBalance = walletService.getBalance(walletId);

        assertEquals(expectedBalance, actualBalance);
    }

    @Test
    @DisplayName("Ошибка получения баланска кошелька: кошелёк не найден")
    void testGetBalanceWalletNotFound() {
        UUID walletId = UUID.randomUUID();
        when(walletRepository.findByWalletId(walletId)).thenReturn(null);

        assertThrows(WalletNotFoundException.class, () -> walletService.getBalance(walletId));
    }

    @Test
    @DisplayName("Успешное пополнение кошелька")
    void testOperateOnWalletDepositSuccess() {
        UUID walletId = UUID.randomUUID();
        double initialBalance = 100.0;
        double depositAmount = 50.0;
        Wallet wallet = new Wallet(walletId, initialBalance);

        when(walletRepository.findByWalletIdAndLock(walletId)).thenReturn(wallet);

        double newBalance = walletService.operateOnWallet(
                new WalletOperationRequest(walletId, WalletOperationRequest.OperationType.DEPOSIT, depositAmount));

        assertEquals(initialBalance + depositAmount, newBalance);
        verify(walletRepository).save(wallet);
    }

    @Test
    @DisplayName("Успешное снятие средств с кошелька")
    void testOperateOnWalletWithdrawSuccess() {
        UUID walletId = UUID.randomUUID();
        double initialBalance = 100.0;
        double withdrawAmount = 50.0;
        Wallet wallet = new Wallet(walletId, initialBalance);

        when(walletRepository.findByWalletIdAndLock(walletId)).thenReturn(wallet);

        double newBalance = walletService.operateOnWallet(
                new WalletOperationRequest(walletId, WalletOperationRequest.OperationType.WITHDRAW, withdrawAmount));

        assertEquals(initialBalance - withdrawAmount, newBalance);
        verify(walletRepository).save(wallet);
    }

    @Test
    @DisplayName("Ошибка при попытке операции с несуществующим кошельком")
    void testOperateOnWalletWalletNotFound() {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findByWalletIdAndLock(walletId)).thenReturn(null);

        assertThrows(WalletNotFoundException.class, () ->
                walletService.operateOnWallet(
                        new WalletOperationRequest(walletId, WalletOperationRequest.OperationType.DEPOSIT, 50.0)));
    }

    @Test
    @DisplayName("Ошибка при попытке снятия средств с недостаточным балансом")
    void testOperateOnWalletInsufficientFunds() {
        UUID walletId = UUID.randomUUID();
        double initialBalance = 100.0;
        double withdrawAmount = 150.0;
        Wallet wallet = new Wallet(walletId, initialBalance);

        when(walletRepository.findByWalletIdAndLock(walletId)).thenReturn(wallet);

        assertThrows(WalletInsufficientFundsException.class, () ->
                walletService.operateOnWallet(
                        new WalletOperationRequest(walletId, WalletOperationRequest.OperationType.WITHDRAW, withdrawAmount)));
    }
}

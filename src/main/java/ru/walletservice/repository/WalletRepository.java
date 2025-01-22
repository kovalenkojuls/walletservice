package ru.walletservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.walletservice.model.Wallet;

import java.util.UUID;

public interface WalletRepository extends JpaRepository<Wallet, UUID> {
    Wallet findByWalletId(UUID walletId);
}

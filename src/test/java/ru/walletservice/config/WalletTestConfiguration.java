package ru.walletservice.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import ru.walletservice.service.WalletService;

@TestConfiguration
public class WalletTestConfiguration {
    @Bean
    @Primary
    WalletService walletService() {
        return Mockito.mock(WalletService.class);
    }
}

package ru.walletservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.walletservice.config.WalletTestConfiguration;
import ru.walletservice.exception.walletException.WalletInsufficientFundsException;
import ru.walletservice.exception.walletException.WalletInvalidOperationTypeException;
import ru.walletservice.exception.walletException.WalletNotFoundException;
import ru.walletservice.model.WalletOperationRequest;
import ru.walletservice.service.WalletService;

import java.util.UUID;
import java.util.stream.Stream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@Import(WalletTestConfiguration.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletService walletService;

    @Test
    @DisplayName("Успешное получение баланса кошелька по uuid")
    void testGetBalanceSuccess() throws Exception {
        UUID walletId = UUID.randomUUID();
        double balance = 100.50;
        when(walletService.getBalance(walletId)).thenReturn(balance);

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(balance))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("Ошибка получения баланска кошелька: кошелёк не найден")
    void testGetBalanceNotFound() throws Exception {
        UUID walletId = UUID.randomUUID();
        when(walletService.getBalance(any(UUID.class))).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{walletId}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Wallet not found"))
                .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @ParameterizedTest
    @MethodSource("provideWalletOperationSuccessData")
    @DisplayName("Успешные выполнения операций над кошельком")
    void testOperateOnWalletSuccess(
            UUID walletId,
            WalletOperationRequest.OperationType operationType,
            double amount,
            double expectedAmount) throws Exception {

        WalletOperationRequest request = new WalletOperationRequest(
                walletId, WalletOperationRequest.OperationType.DEPOSIT, amount);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        when(walletService.operateOnWallet(ArgumentMatchers.refEq(request))).thenReturn(expectedAmount);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.amount").value(expectedAmount))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @ParameterizedTest
    @MethodSource("provideWalletOperationErrorData")
    @DisplayName("Ошибки выполнения операций над кошельком")
    void testOperateOnWalletError(
            UUID walletId,
            WalletOperationRequest.OperationType operationType,
            RuntimeException exception,
            String errorMsg,
            int httpCode) throws Exception {

        WalletOperationRequest request = new WalletOperationRequest(walletId, operationType, 1000);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(request);

        when(walletService.operateOnWallet(ArgumentMatchers.refEq(request))).thenThrow(exception);

        mockMvc.perform(post("/api/v1/wallets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().is(httpCode))
                .andExpect(jsonPath("$.error").value(errorMsg))
                .andExpect(jsonPath("$.status").value(httpCode))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/wallets"));
    }

    private static Stream<Arguments> provideWalletOperationSuccessData() {
        return Stream.of(
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.DEPOSIT,
                        100.50,
                        200.50
                ),
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.DEPOSIT,
                        294.20,
                        529.63
                ),
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.WITHDRAW,
                        200.43,
                        1198.17
                ),
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.WITHDRAW,
                        1000.0,
                        2.0
                )
        );
    }

    private static Stream<Arguments> provideWalletOperationErrorData() {
        return Stream.of(
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.DEPOSIT,
                        new WalletNotFoundException("Wallet not found"),
                        "Wallet not found",
                        404
                ),
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.WITHDRAW,
                        new WalletInvalidOperationTypeException("Invalid operation type"),
                        "Invalid operation type",
                        400
                ),
                Arguments.of(UUID.randomUUID(),
                        WalletOperationRequest.OperationType.WITHDRAW,
                        new WalletInsufficientFundsException("Insufficient funds"),
                        "Insufficient funds",
                        400
                )
        );
    }
}


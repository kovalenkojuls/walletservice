package ru.walletservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.walletservice.exception.GlobalExceptionHandler;
import ru.walletservice.model.WalletOperationRequest;
import ru.walletservice.service.WalletService;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Wallet API", description = "API для управления кошельками.")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/wallets")
    @Operation(summary = "Выполнить операцию с кошельком",
            description = "Позволяет пополнить или снять средства с кошелька.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Операция выполнена успешно",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<BalanceResponse> operateOnWallet(@RequestBody WalletOperationRequest request) {
        double newAmount = walletService.operateOnWallet(request);
        BalanceResponse operateOnWalletResponse = new BalanceResponse(LocalDateTime.now(), HttpStatus.OK.value(), newAmount);
        return new ResponseEntity<>(operateOnWalletResponse, HttpStatus.OK);
    }

    @GetMapping("/wallets/{walletId}")
    @Operation(summary = "Получить баланс кошелька",
            description = "Возвращает текущий баланс кошелька.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Баланс получен успешно",
                    content = @Content(schema = @Schema(implementation = BalanceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Кошелек не найден",
                    content = @Content(schema = @Schema(implementation = GlobalExceptionHandler.ErrorResponse.class)))
    })
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable UUID walletId) {
        double balance = walletService.getBalance(walletId);
        BalanceResponse response = new BalanceResponse(LocalDateTime.now(), HttpStatus.OK.value(), balance);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    public record BalanceResponse(LocalDateTime timestamp, int status, double amount) {}

}
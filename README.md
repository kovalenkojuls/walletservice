# Wallet Service

RESTful сервис для управления балансом кошелька.

## Функциональность

Сервис предоставляет следующие функции:

* *Получение баланса кошелька:*
    * Метод: `GET /api/v1/wallets/{WALLET_UUID}`
  
* *Пополнение баланса кошелька:*
    * Метод: `POST /api/v1/wallets`
    * Тело запроса (JSON): `{"walletId": "UUID", "operationType": "DEPOSIT", "amount": 1000}`


* *Снятие средств с кошелька:*
    * Метод: `POST /api/v1/wallets`
    * Тело запроса (JSON): `{"walletId": "UUID", "operationType": "DEPOSIT", "amount": 1000}`

## Технологии

* Java 17
* Spring Boot 3
* PostgreSQL 16
* Liquibase
* Docker
* Docker Compose






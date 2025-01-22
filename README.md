# Wallet Service
RESTful сервис для управления балансом кошелька.

## Запуск WalletService с помощью Docker Compose
Для запуска WalletService выполните следующие шаги:

1. Клонируйте репозиторий: `git clone https://github.com/kovalenkojuls/walletservice.git`
2. Перейдите в директорию проекта: `cd walletservice`
3. Установить переменные сред в walletservice/docker/docker-compose.yml. Для запуска в демонтрационном варианте ничего менять не нужно.
4. Запустите Docker Compose: `docker-compose -f docker/docker-compose.yml up -d`
5. Проверьте запущенные контейнеры: `docker ps`

Если переменные сред не менялись, приложение доступно по:
```
http://localhost:8080/api/v1/wallets #api
http://localhost:8081/ #pgadmin
http://localhost:8080/swagger-ui/index.html #swagger
```

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



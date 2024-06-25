# BBVA-FS-W4-Back-T1

# Alkywall - Virtual Wallet Backend

## Overview
Alkywall is a virtual wallet backend developed in Java, designed to provide basic banking functionalities to its users. With Alkywall, customers can perform transactions, link both physical and virtual cards, store money in a digital environment, and make online payments.

## Features

- **Transactions**: Securely transfer money between accounts.
- **Card Management**: Associate and manage both physical and virtual cards.
- **Digital Wallet**: Store and manage funds in a secure digital environment.
- **Online Payments**: Facilitate online purchases and payments.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Maven for dependency management

### Installation
**Clone the repository:**

    ```bash
    git clone https://github.com/alkemyTech/BBVA-FS-W4-Back-T1.git
    cd BBVA-FS-W4-Back-T1
    ```

## Usage

### API Endpoints

#### Transactions
- **Send Money in ARS**
    - `POST /transactions/sendArs`

  **Request Body:**
  ```json
    {
    "destinationIdAccount":2,
    "amount":100,
    "description": "Envio de dinero en pesos"
    }
  ```

- **Send Money in USD**
    - `POST /transactions/sendUsd`

  **Request Body:**
  ```json
    {
    "destinationIdAccount":1,
    "amount":50,
    "description": "Envio de dinero en dolares"
    }
  ```

#### Authentication
- **Register a User**
    - `POST /auth/register`

  **Request Body:**
  ```json
    {
    "firstName": "Nombre usuario",
    "lastName": "Apellido usuario",
    "email": "usuario@email.com",
    "password": "1234"
    }
  ```

- **Register a User with ADMIN Role**
    - `POST /auth/register-admin`

  **Request Body:**
  ```json
    {
    "firstName": "Nombre usuario",
    "lastName": "Apellido usuario",
    "email": "usuario@email.com",
    "password": "1234"
    }
  ```

- **Login to the API**
    - `POST /auth/login`

  **Request Body:**
  ```json
    {
    "email": "usuario@email.com",
    "password": "1234"
    }
  ```

#### Users (Requires ADMIN Role)
- **Get All Users**
    - `GET /users`


- **Get User by ID**
    - `GET /users/{id}`

## Accounts

- **Get Accounts by User ID**
  - `GET /accounts/{userId}`

  **Request Parameters:**
  - `page` (default: 0)
  - `size` (default: 10)

  **Response:**
    ```json
    {
      "accounts": [
        {
          "idAccount": 1,
          "accountType": "CAJA_AHORRO",
          "currency": "ARS",
          "bank": "BANCO_NACION",
          "cbu": "1234567890123456789012",
          "alias": "mi.cuenta",
          "transactionLimit": 10000.00,
          "balance": 5000.00
        }
      ],
      "nextPage": "/accounts/{userId}?page=1",
      "prevPage": null,
      "countPages": 10
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `page` or `size` parameters are invalid.
  - *404 Not Found:*
    - If the user with the specified `userId` does not exist.
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.

- **Get Inactive Accounts by User ID**
  - `GET /accounts/{userId}/inactive`

  **Request Parameters:**
  - `page` (default: 0)
  - `size` (default: 10)

  **Response:**
    ```json
    {
      "accounts": [
        {
          "idAccount": 2,
          "accountType": "CAJA_AHORRO",
          "currency": "ARS",
          "bank": "BANCO_PROVINCIA",
          "cbu": "9876543210987654321098",
          "alias": "otra.cuenta",
          "transactionLimit": 5000.00,
          "balance": 0.00
        }
      ],
      "nextPage": "/accounts/{userId}/inactive?page=1",
      "prevPage": null,
      "countPages": 5
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `page` or `size` parameters are invalid.
  - *404 Not Found:*
    - If the user with the specified `userId` does not exist.
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.


- **Create a New Account**
  - `POST /accounts`

  **Request Body:**
    ```json
    {
      "accountType": "CAJA_AHORRO",
      "currency": "ARS"
    }
    ```

  **Response:**
    ```json
    {
      "idAccount": 3,
      "accountType": "CAJA_AHORRO",
      "currency": "ARS",
      "bank": "BANCO_NACION",
      "cbu": "2345678901234567890123",
      "alias": "nueva.cuenta",
      "transactionLimit": 10000.00,
      "balance": 0.00
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the request body is missing required fields or contains invalid data.
    - If the `accountType` or `currency` is not supported.
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.

- **Update Account Transaction Limit**
  - `PUT /accounts/{idAccount}`

  **Request Body:**
    ```json
    {
      "transactionLimit": 15000.00
    }
    ```

  **Response:**
    ```json
    {
      "idAccount": 1,
      "accountType": "CAJA_AHORRO",
      "currency": "ARS",
      "bank": "BANCO_NACION",
      "cbu": "1234567890123456789012",
      "alias": "mi.cuenta",
      "transactionLimit": 15000.00,
      "balance": 5000.00
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the request body is missing the `transactionLimit` field or contains an invalid value.
  - *404 Not Found:*
    - If the account with the specified `idAccount` does not exist.
  - *401 Unauthorized:*
    - If the user is not authenticated or does not have permission to update the account.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.

- **Get Account Balance**
  - `GET /accounts/balance`

  **Response:**
    ```json
    {
      "accountArs": [
        {
          "idAccount": 1,
          "accountType": "CAJA_AHORRO",
          "currency": "ARS",
          "bank": "BANCO_NACION",
          "cbu": "1234567890123456789012",
          "alias": "mi.cuenta",
          "transactionLimit": 10000.00,
          "balance": 5000.00
        }
      ],
      "accountUsd": {
        "idAccount": 2,
        "accountType": "CAJA_AHORRO",
        "currency": "USD",
        "bank": "BANCO_NACION",
        "cbu": "2345678901234567890123",
        "alias": "mi.cuenta.usd",
        "transactionLimit": 10000.00,
        "balance": 3000.00
      },
      "history": [],
      "fixedTerms": []
    }
    ```

  **Possible Errors:**
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.

- **Search Account by CBU or Alias**
  - `GET /accounts/search`

  **Request Parameters:**
  - `CBU O ALIAS` (String)

  **Response:**
    ```json
    {
      "idAccount": 1,
      "accountType": "CAJA_AHORRO",
      "currency": "ARS",
      "bank": "BANCO_NACION",
      "cbu": "1234567890123456789012",
      "alias": "mi.cuenta",
      "transactionLimit": 10000.00,
      "balance": 5000.00
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `CBU` or `alias` parameter is missing or invalid.
  - *404 Not Found:*
    - If no account matches the provided `CBU` or `alias`.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.

- **Delete Account by ID**
  - `DELETE /accounts/accountId/{id}`

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `id` parameter is missing or invalid.
  - *404 Not Found:*
    - If the account with the specified `id` does not exist.
  - *401 Unauthorized:*
    - If the user is not authenticated or does not have permission to delete the account.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.


## FixedTerm

- **Create a Fixed Term**
  - `POST /fixedTerm`

  **Request Body:**
    ```json
    {
      "amount": 10000.00,
      "closingDate": "2024-12-31"
    }
    ```

  **Response:**
    ```json
    {
      "idDeposit": 1,
      "amount": 10000.00,
      "interest": 5.0,
      "creationDate": "2024-06-24T12:34:56",
      "closingDate": "2024-12-31T12:00:00",
      "interestTotal": 500.00,
      "interestTodayWin": 1.37,
      "amountTotalToReceive": 10500.00
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `amount` is less than the minimum required amount.
    - If the `closingDate` is invalid or in the past.
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.


- **Simulate a Fixed Term**
  - `POST /fixedTerm/simulate`

  **Request Body:**
    ```json
    {
      "amount": 10000.00,
      "closingDate": "2024-12-31"
    }
    ```

  **Response:**
    ```json
    {
      "idDeposit": null,
      "amount": 10000.00,
      "interest": 5.0,
      "creationDate": "2024-06-24T12:34:56",
      "closingDate": "2024-12-31T12:00:00",
      "interestTotal": 500.00,
      "interestTodayWin": 1.37,
      "amountTotalToReceive": 10500.00
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `amount` is less than the minimum required amount.
    - If the `closingDate` is invalid or in the past.
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.


- **Get Fixed Terms for Logged-in User**
  - `GET /fixedTerm`

  **Request Parameters:**
  - `page` (default: 0)
  - `size` (default: 10)

  **Response:**
    ```json
    {
      "fixedTerms": [
        {
          "idDeposit": 1,
          "amount": 10000.00,
          "interest": 5.0,
          "creationDate": "2024-06-24T12:34:56",
          "closingDate": "2024-12-31T12:00:00",
          "interestTotal": 500.00,
          "interestTodayWin": 1.37,
          "amountTotalToReceive": 10500.00
        }
      ],
      "nextPage": "/fixedTerm?page=1",
      "prevPage": null,
      "countPages": 10
    }
    ```

  **Possible Errors:**
  - *400 Bad Request:*
    - If the `page` or `size` parameters are invalid.
  - *401 Unauthorized:*
    - If the user is not authenticated.
  - *500 Internal Server Error:*
    - If there is an error in processing the request.



## Test Data

Test users have been created to facilitate testing of web functionalities.

### Admin Users

| Email              | Password |
|--------------------|----------|
| admin0@example.com | admin0   |
| admin1@example.com | admin1   |
| admin2@example.com | admin2   |
| admin3@example.com | admin3   |
| admin4@example.com | admin4   |
| admin5@example.com | admin5   |
| admin6@example.com | admin6   |
| admin7@example.com | admin7   |
| admin8@example.com | admin8   |
| admin9@example.com | admin9   |

### Regular Users

| Email             | Password |
|-------------------|----------|
| user0@example.com | user0    |
| user1@example.com | user1    |
| user2@example.com | user2    |
| user3@example.com | user3    |
| user4@example.com | user4    |
| user5@example.com | user5    |
| user6@example.com | user6    |
| user7@example.com | user7    |
| user8@example.com | user8    |
| user9@example.com | user9    |

### Seeder Instructions

1. **Run the application**: The seeder will run automatically when you start the application.
2. **Access test users**: Use the email addresses and passwords listed above to log in with different roles and test the functionalities.
3. **Update test data**: If you make changes to the data structure or functionalities, update the seeder and the data in this document.

### How to use test data

To access the test users:
1. Start the application.
2. Use one of the email addresses and passwords from the table above to log in.
3. Verify the functionalities based on the user's role (admin or regular).

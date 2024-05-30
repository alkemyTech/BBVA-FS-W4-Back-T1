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

#### Accounts
- **Get Accounts by User ID**
    - `GET /accounts/{userId}`


- **Create a New Account**
    - `POST /accounts`

  **Request Body:**
  ```json
    {
    "currency": "ARS"
    }
  ```

- **Get Account Balances**
    - `GET /accounts/balance`


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

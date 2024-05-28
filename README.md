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



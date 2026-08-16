  # 📖 Apex Online Banking System - Route & Endpoint Specification

This document details all accessible routes, HTTP methods, authorization requirements, and parameters for the Online Banking System.

---

## 🌐 1. Public Routes (Permit All)

| HTTP Method | Route | Description | Template View |
| :--- | :--- | :--- | :--- |
| `GET` | `/` or `/index` | Landing Home Page | `public/index` |
| `GET` | `/about` | Bank overview & system specs | `public/about` |
| `GET` | `/contact` | Customer support contact details | `public/contact` |
| `GET` | `/login` | Customer & Admin login form | `public/login` |
| `POST` | `/login` | Authenticate credentials via Spring Security | Handled by Security |
| `GET` | `/register` | User registration form | `public/register` |
| `POST` | `/register` | Process new user & bank account creation | Redirect to `/login` |

---

## 👤 2. Customer Routes (Requires `ROLE_CUSTOMER`)

| HTTP Method | Route | Description | Request Params / Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/customer/dashboard` | Main customer dashboard overview | N/A |
| `GET` | `/customer/account-details` | View account number, IFSC, branch | N/A |
| `GET` | `/customer/profile` | View & update user profile | N/A |
| `POST` | `/customer/profile` | Save updated phone / address | `UserProfileDto` |
| `GET` | `/customer/transfer` | Fund transfer form | N/A |
| `POST` | `/customer/transfer` | Process transactional fund transfer | `TransferRequestDto` |
| `GET` | `/customer/deposit-withdraw` | Self-service deposit/withdraw form | N/A |
| `POST` | `/customer/deposit-withdraw` | Execute deposit or cash withdrawal | `DepositWithdrawDto` |
| `GET` | `/customer/transactions` | Transaction history & filter page | `timeframe`, `search` |
| `GET` | `/customer/change-password` | Change user password form | N/A |
| `POST` | `/customer/change-password` | Submit new BCrypt password update | `ChangePasswordDto` |

---

## 🛡️ 3. Admin Routes (Requires `ROLE_ADMIN`)

| HTTP Method | Route | Description | Request Params / Body |
| :--- | :--- | :--- | :--- |
| `GET` | `/admin/dashboard` | Admin high-level system dashboard | N/A |
| `GET` | `/admin/users` | List all registered customers | N/A |
| `POST` | `/admin/users/{id}/toggle-status` | Enable/Disable user login access | Path Variable `id` |
| `GET` | `/admin/accounts` | List all bank accounts | N/A |
| `POST` | `/admin/accounts/{id}/freeze` | Freeze customer account | Path Variable `id` |
| `POST` | `/admin/accounts/{id}/activate` | Activate frozen account | Path Variable `id` |
| `GET` | `/admin/transactions` | View all system transactions | `query` (search TXN ID) |
| `GET` | `/admin/reports` | Bank liquidity & activity reports | N/A |
| `GET` | `/admin/audit-logs` | Security & action audit logs | N/A |

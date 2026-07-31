<div align = center>
  <img width="350" height="350" alt="1000251199-removebg-preview (1)" src="https://github.com/user-attachments/assets/96a53c51-507a-47f2-bb53-34576b1d3751" />

</div>

# Banking System Project

A Java-based banking management system with both a console menu interface and a modern Swing GUI. The system supports role-based access (Admin and Client), persistent file-based storage, and full account management operations.

---

## Project Structure

```
Banking System Project/
├── src/
│   ├── Person.java                   # Base class: name, CNIC, phone number
│   ├── User.java                     # Auth entity: username, password, role
│   ├── Client.java                   # Bank client with linked accounts
│   ├── Account.java                  # Bank account with deposit/withdraw logic
│   ├── Bank.java                     # Core bank: manages clients and accounts
│   ├── AuthenticationManager.java    # Login, registration, role management
│   ├── EnhancedBankingSystemMenu.java # Console-based menu interface (main entry)
│   └── ModernBankingGUI.java         # Swing GUI interface (alternate entry)
├── Account.txt                       # Persisted account data
├── Cilent.txt                        # Persisted client data
├── Person.txt                        # Persisted person data
├── Users.txt                         # Persisted user credentials
└── out/                              # Compiled .class files
```

---

## Class Overview

### `Person`
Represents a person's basic identity.

| Field     | Type   | Description        |
|-----------|--------|--------------------|
| Name      | String | Full name          |
| CNIC      | String | National ID number |
| phoneNo   | String | Contact number     |

---

### `User`
Handles authentication credentials and role assignment.

| Field    | Type   | Description                         |
|----------|--------|-------------------------------------|
| username | String | Login username                      |
| password | String | Login password (plaintext)          |
| role     | String | `"ADMIN"` or `"CLIENT:<clientId>"`  |

---

### `Account`
Represents a bank account linked to a `Client`.

| Field    | Type   | Description                          |
|----------|--------|--------------------------------------|
| number   | String | Auto-generated (e.g., `acc-0`)       |
| amount   | float  | Current balance                      |
| ACholder | Client | The owning client                    |
| count    | int    | Static counter for ID generation     |

**Methods:**
- `deposit(float amount)`: Adds funds; rejects non-positive amounts.
- `withdraw(float amount)`: Deducts funds; rejects overdraft or invalid amounts.

---

### `Client`
Represents a bank customer who can hold multiple accounts.

| Field         | Type              | Description                      |
|---------------|-------------------|----------------------------------|
| Id            | String            | Auto-generated (e.g., `cilent-0`) |
| PersonDetails | Person            | Name, CNIC, phone                |
| AcList        | ArrayList<Account>| List of linked accounts          |

**Methods:**
- `totalAmount()` — Sum of all account balances.
- `withdraw(amount, accNo)` — Delegates to the matching account.
- `deposit(amount, accNo)` — Delegates to the matching account.
- `AddAccount(Account)` — Registers a new account to the client.

---

### `Bank`
The top-level entity that manages all clients and accounts.

**Methods:**

| Method                               | Description                                     |
|--------------------------------------|-------------------------------------------------|
| `addClient(Person)`                  | Creates and registers a new client              |
| `addAccount(id, amount, client)`     | Creates an account and links it to a client     |
| `removeClient(String id)`            | Removes a client and all their accounts         |
| `SearchAccount(String id)`           | Finds an account by account number              |
| `SearchCustomerDetail(String cnic)`  | Finds a client by CNIC                          |
| `totalAmount()`                      | Returns total balance across all accounts       |
| `displayBankDetails()`               | Prints a summary of the bank                    |

---

### `AuthenticationManager`
Handles user login, registration, and persistence to `Users.txt`.

**Methods:**

| Method                                          | Description                                         |
|-------------------------------------------------|-----------------------------------------------------|
| `authenticate(username, password)`              | Returns matched `User` or `null`                    |
| `registerClient(username, password, clientId)`  | Adds a CLIENT role user                             |
| `registerAdmin(username, password)`             | Adds an ADMIN role user                             |
| `changePassword(username, old, new)`            | Updates password if old password matches            |
| `isAdmin(User)`                                 | Returns true if user has ADMIN role                 |
| `getClientId(User)`                             | Extracts client ID from `CLIENT:<id>` role string   |
| `getAllUsers()`                                 | Returns full user list (admin use)                  |

On first run, a default admin is created automatically:
- **Username:** `admin`
- **Password:** `admin123`

---

## Interfaces

### Console Interface (`EnhancedBankingSystemMenu`)
Entry point for terminal-based usage. Prompts for a bank name at startup, then presents login and role-based menus.

**Admin Menu:**
- Manage Clients (add, remove, view)
- Manage Accounts (create, deposit, withdraw)
- Search Operations (by CNIC or account number)
- View Bank Summary
- Register new Admin/Client users

**Client Menu:**
- View own accounts and balances
- Deposit / Withdraw from own accounts
- Change password

### GUI Interface (`ModernBankingGUI`)
A Swing-based graphical interface with a dark theme (slate/blue color palette). Launches as `UET Bank`. Provides the same login and role-based functionality in a windowed UI (450×700px).

---

## Data Persistence

Data is read and written via plain text CSV files on startup and after each modification:

| File         | Format                              | Contents                  |
|--------------|-------------------------------------|---------------------------|
| `Users.txt`  | `Username,Password,Role`            | All user credentials      |
| `Person.txt` | `Name,CNIC,PhoneNo`                 | Person records            |
| `Cilent.txt` | `ClientID,PersonCNIC`               | Client-to-person mapping  |
| `Account.txt`| `AccountNumber,Balance,ClientID`    | Account records           |

Files must be in the working directory (project root) at runtime.

---

## Getting Started

### Prerequisites
- Java 8 or higher
- An IDE such as IntelliJ IDEA (`.iml` and `.idea/` config included)

### Running the Console App
```bash
cd src
javac *.java
java EnhancedBankingSystemMenu
```

### Running the GUI App
```bash
cd src
javac *.java
java ModernBankingGUI
```

---

## Default Credentials

| Username | Password  | Role  |
|----------|-----------|-------|
| admin    | admin123  | ADMIN |

Additional accounts are loaded from `Users.txt` if the file exists.

---

## Known Issues / Notes

- Passwords are stored in **plaintext**: not suitable for production use.
- There is a typo in file/class naming: `Cilent.txt` and the `Client` class use inconsistent spelling.
- Static counters (`Client.count`, `Account.count`) are not persisted; IDs may reset or collide across sessions unless carefully managed during file loading.
- The `Client` ID prefix in code is `"cilent-"` (misspelled): changing this would break compatibility with existing data files.

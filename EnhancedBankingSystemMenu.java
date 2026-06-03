import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class EnhancedBankingSystemMenu {
    private static Bank bank;
    private static Scanner scanner = new Scanner(System.in);
    private static AuthenticationManager authManager;
    private static User currentUser;

    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║       BANK MANAGEMENT SYSTEM          ║");
        System.out.println("╚═══════════════════════════════════════╝\n");

        // Initialize bank and authentication
        initializeSystem();

        // Login loop
        boolean loggedIn = false;
        while (!loggedIn) {
            System.out.println("\n═══════════════════════════════════════");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.println("═══════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    currentUser = login();
                    if (currentUser != null) {
                        loggedIn = true;
                        System.out.println("\n✓ Login successful!");
                        System.out.println("Welcome, " + currentUser.getUsername() + "!");
                    }
                    break;
                case 2:
                    System.out.println("\nThank you for using Banking System!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }

        // Main menu based on role
        if (authManager.isAdmin(currentUser)) {
            adminMenu();
        } else {
            clientMenu();
        }

        scanner.close();
    }

    private static void initializeSystem() {
        System.out.print("Enter Bank Name: ");
        String bankName = scanner.nextLine();
        bank = new Bank(bankName);
        authManager = new AuthenticationManager();

        // Load existing data
        loadDataFromFiles();

        System.out.println("Bank '" + bankName + "' initialized successfully!\n");
    }

    private static User login() {
        scanner.nextLine(); // Clear buffer
        System.out.print("\nUsername: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User user = authManager.authenticate(username, password);
        if (user == null) {
            System.out.println("✗ Invalid credentials!");
        }
        return user;
    }

    // ==================== ADMIN MENU ====================
    private static void adminMenu() {
        boolean exit = false;

        while (!exit) {
            displayAdminMainMenu();
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    manageClients();
                    break;
                case 2:
                    manageAccounts();
                    break;
                case 3:
                    searchOperations();
                    break;
                case 4:
                    viewReports();
                    break;
                case 5:
                    manageUsers();
                    break;
                case 6:
                    changePassword();
                    break;
                case 7:
                    saveAllData();
                    exit = true;
                    System.out.println("\nThank you for using Banking System!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void displayAdminMainMenu() {
        System.out.println("\n═══════════════ ADMIN MENU ═══════════════");
        System.out.println("1. Manage Clients");
        System.out.println("2. Manage Accounts");
        System.out.println("3. Search Operations");
        System.out.println("4. View Reports");
        System.out.println("5. Manage Users");
        System.out.println("6. Change Password");
        System.out.println("7. Save & Exit");
        System.out.println("═══════════════════════════════════════════");
    }

    // ==================== CLIENT MENU ====================
    private static void clientMenu() {
        String clientId = authManager.getClientId(currentUser);
        Client client = findClientById(clientId);

        if (client == null) {
            System.out.println("✗ Client profile not found! Please contact admin.");
            return;
        }

        boolean exit = false;

        while (!exit) {
            displayClientMainMenu(client);
            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewMyAccounts(client);
                    break;
                case 2:
                    clientDeposit(client);
                    break;
                case 3:
                    clientWithdraw(client);
                    break;
                case 4:
                    clientTransfer(client);
                    break;
                case 5:
                    viewMyProfile(client);
                    break;
                case 6:
                    changePassword();
                    break;
                case 7:
                    exit = true;
                    System.out.println("\nThank you for using Banking System!");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void displayClientMainMenu(Client client) {
        System.out.println("\n═══════════════ CLIENT MENU ═══════════════");
        System.out.println("Client: " + client.getPersonDetails().getName());
        System.out.println("Total Balance: $" + client.totalAmount());
        System.out.println("───────────────────────────────────────────");
        System.out.println("1. View My Accounts");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Money");
        System.out.println("5. View My Profile");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
        System.out.println("═══════════════════════════════════════════");
    }

    // ==================== CLIENT OPERATIONS ====================
    private static void viewMyAccounts(Client client) {
        System.out.println("\n─── MY ACCOUNTS ───");
        if (client.getACList().isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Account account : client.getACList()) {
            System.out.println("─────────────────────────────────");
            System.out.println("Account Number: " + account.getNumber());
            System.out.println("Balance: $" + account.getAmount());
        }
        System.out.println("─────────────────────────────────");
        System.out.println("Total Balance: $" + client.totalAmount());
    }

    private static void clientDeposit(Client client) {
        if (client.getACList().isEmpty()) {
            System.out.println("You don't have any accounts. Please contact admin.");
            return;
        }

        System.out.println("\n─── DEPOSIT MONEY ───");
        System.out.println("Your Accounts:");
        for (Account acc : client.getACList()) {
            System.out.println("  " + acc.getNumber() + " - Balance: $" + acc.getAmount());
        }

        System.out.print("\nEnter Account Number: ");
        String accNumber = scanner.next();
        System.out.print("Enter Amount to Deposit: ");
        float amount = scanner.nextFloat();

        client.deposit(amount, accNumber);
    }

    private static void clientWithdraw(Client client) {
        if (client.getACList().isEmpty()) {
            System.out.println("You don't have any accounts. Please contact admin.");
            return;
        }

        System.out.println("\n─── WITHDRAW MONEY ───");
        System.out.println("Your Accounts:");
        for (Account acc : client.getACList()) {
            System.out.println("  " + acc.getNumber() + " - Balance: $" + acc.getAmount());
        }

        System.out.print("\nEnter Account Number: ");
        String accNumber = scanner.next();
        System.out.print("Enter Amount to Withdraw: ");
        float amount = scanner.nextFloat();

        client.withdraw(amount, accNumber);
    }

    private static void clientTransfer(Client client) {
        if (client.getACList().isEmpty()) {
            System.out.println("You don't have any accounts. Please contact admin.");
            return;
        }

        System.out.println("\n─── TRANSFER MONEY ───");
        System.out.println("Your Accounts:");
        for (Account acc : client.getACList()) {
            System.out.println("  " + acc.getNumber() + " - Balance: $" + acc.getAmount());
        }

        System.out.print("\nEnter Source Account Number: ");
        String fromAcc = scanner.next();

        // Verify source account belongs to client
        Account sourceAccount = null;
        for (Account acc : client.getACList()) {
            if (acc.getNumber().equals(fromAcc)) {
                sourceAccount = acc;
                break;
            }
        }

        if (sourceAccount == null) {
            System.out.println("Invalid source account!");
            return;
        }

        System.out.print("Enter Destination Account Number: ");
        String toAcc = scanner.next();
        System.out.print("Enter Amount to Transfer: ");
        float amount = scanner.nextFloat();

        if (sourceAccount.getAmount() < amount) {
            System.out.println("Insufficient funds!");
            return;
        }

        // Find destination account
        Account destAccount = bank.SearchAccount(toAcc);
        if (destAccount == null) {
            System.out.println("Destination account not found!");
            return;
        }

        // Perform transfer
        client.withdraw(amount, fromAcc);
        destAccount.getAcHolder().deposit(amount, toAcc);
        System.out.println("✓ Transfer completed successfully!");
    }

    private static void viewMyProfile(Client client) {
        System.out.println("\n─── MY PROFILE ───");
        System.out.println(client);
    }

    // ==================== ADMIN OPERATIONS ====================
    private static void manageClients() {
        boolean back = false;

        while (!back) {
            System.out.println("\n═══════════════ CLIENT MANAGEMENT ═══════════════");
            System.out.println("1. Add New Client");
            System.out.println("2. Remove Client");
            System.out.println("3. View All Clients");
            System.out.println("4. View Client Details");
            System.out.println("5. Register Client Login");
            System.out.println("6. Back to Main Menu");
            System.out.println("═════════════════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    addClient();
                    break;
                case 2:
                    removeClient();
                    break;
                case 3:
                    viewAllClients();
                    break;
                case 4:
                    viewClientDetails();
                    break;
                case 5:
                    registerClientLogin();
                    break;
                case 6:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void addClient() {
        System.out.println("\n─── ADD NEW CLIENT ───");

        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Client Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter CNIC: ");
        String cnic = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        Person person = new Person(name, cnic, phone);
        Client client = bank.addClient(person);

        if (client != null) {
            System.out.println("✓ Client added successfully!");
            System.out.println("Client ID: " + client.getId());
            System.out.println("\nNote: Use 'Register Client Login' to create login credentials for this client.");
        }
    }

    private static void registerClientLogin() {
        System.out.println("\n─── REGISTER CLIENT LOGIN ───");

        if (bank.getClList().isEmpty()) {
            System.out.println("No clients available. Please add a client first.");
            return;
        }

        System.out.println("Available Clients:");
        for (Client client : bank.getClList()) {
            System.out.println("ID: " + client.getId() + " - " +
                    client.getPersonDetails().getName());
        }

        System.out.print("\nEnter Client ID: ");
        String clientId = scanner.next();

        Client selectedClient = findClientById(clientId);
        if (selectedClient == null) {
            System.out.println("Client not found!");
            return;
        }

        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        authManager.registerClient(username, password, clientId);
    }

    private static void manageUsers() {
        boolean back = false;

        while (!back) {
            System.out.println("\n═══════════════ USER MANAGEMENT ═══════════════");
            System.out.println("1. View All Users");
            System.out.println("2. Register New Admin");
            System.out.println("3. Back to Main Menu");
            System.out.println("═══════════════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    viewAllUsers();
                    break;
                case 2:
                    registerNewAdmin();
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void viewAllUsers() {
        System.out.println("\n─── ALL USERS ───");
        for (User user : authManager.getAllUsers()) {
            System.out.println(user);
        }
    }

    private static void registerNewAdmin() {
        System.out.println("\n─── REGISTER NEW ADMIN ───");
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        authManager.registerAdmin(username, password);
    }

    private static void changePassword() {
        System.out.println("\n─── CHANGE PASSWORD ───");
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter Current Password: ");
        String oldPassword = scanner.nextLine();
        System.out.print("Enter New Password: ");
        String newPassword = scanner.nextLine();
        System.out.print("Confirm New Password: ");
        String confirmPassword = scanner.nextLine();

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("Passwords don't match!");
            return;
        }

        if (authManager.changePassword(currentUser.getUsername(), oldPassword, newPassword)) {
            System.out.println("✓ Password changed successfully!");
            currentUser.setPassword(newPassword);
        } else {
            System.out.println("✗ Current password is incorrect!");
        }
    }

    // ==================== SHARED OPERATIONS ====================
    private static void removeClient() {
        System.out.println("\n─── REMOVE CLIENT ───");
        System.out.print("Enter Client ID to remove: ");
        String clientId = scanner.next();

        bank.removeClient(clientId);
    }

    private static void viewAllClients() {
        System.out.println("\n─── ALL CLIENTS ───");
        bank.displayBankDetails();
    }

    private static void viewClientDetails() {
        System.out.println("\n─── CLIENT DETAILS ───");
        System.out.print("Enter Client ID: ");
        String clientId = scanner.next();

        Client client = findClientById(clientId);
        if (client != null) {
            System.out.println(client);
        } else {
            System.out.println("Client not found!");
        }
    }

    private static void manageAccounts() {
        boolean back = false;

        while (!back) {
            System.out.println("\n═══════════════ ACCOUNT MANAGEMENT ═══════════════");
            System.out.println("1. Create New Account");
            System.out.println("2. View Account Details");
            System.out.println("3. View All Accounts");
            System.out.println("4. Back to Main Menu");
            System.out.println("══════════════════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    viewAccountDetails();
                    break;
                case 3:
                    viewAllAccounts();
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void createAccount() {
        System.out.println("\n─── CREATE NEW ACCOUNT ───");

        if (bank.getClList().isEmpty()) {
            System.out.println("No clients available. Please add a client first.");
            return;
        }

        System.out.println("Available Clients:");
        for (Client client : bank.getClList()) {
            System.out.println("ID: " + client.getId() + " - " +
                    client.getPersonDetails().getName());
        }

        System.out.print("\nEnter Client ID: ");
        String clientId = scanner.next();

        Client selectedClient = findClientById(clientId);
        if (selectedClient == null) {
            System.out.println("Client not found!");
            return;
        }

        System.out.print("Enter Account Number: ");
        String accNumber = scanner.next();

        System.out.print("Enter Initial Deposit: ");
        float amount = scanner.nextFloat();

        bank.addAccount(accNumber, amount, selectedClient);
    }

    private static void viewAccountDetails() {
        System.out.println("\n─── ACCOUNT DETAILS ───");
        System.out.print("Enter Account Number: ");
        String accNumber = scanner.next();

        Account account = bank.SearchAccount(accNumber);
        if (account != null) {
            System.out.println(account);
        } else {
            System.out.println("Account not found!");
        }
    }

    private static void viewAllAccounts() {
        System.out.println("\n─── ALL ACCOUNTS ───");
        if (bank.getAcList().isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Account account : bank.getAcList()) {
            System.out.println("─────────────────────────────────");
            System.out.println(account);
        }
        System.out.println("─────────────────────────────────");
    }

    private static void adminTransactions() {
        boolean back = false;

        while (!back) {
            System.out.println("\n═══════════════ TRANSACTIONS ═══════════════");
            System.out.println("1. Deposit Money");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Transfer Money");
            System.out.println("4. Back to Main Menu");
            System.out.println("════════════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    adminDeposit();
                    break;
                case 2:
                    adminWithdraw();
                    break;
                case 3:
                    adminTransfer();
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void adminDeposit() {
        System.out.println("\n─── DEPOSIT MONEY ───");
        System.out.print("Enter Account Number: ");
        String accNumber = scanner.next();
        System.out.print("Enter Amount to Deposit: ");
        float amount = scanner.nextFloat();

        Account account = bank.SearchAccount(accNumber);
        if (account != null) {
            account.deposit(amount);
        } else {
            System.out.println("Account not found!");
        }
    }

    private static void adminWithdraw() {
        System.out.println("\n─── WITHDRAW MONEY ───");
        System.out.print("Enter Account Number: ");
        String accNumber = scanner.next();
        System.out.print("Enter Amount to Withdraw: ");
        float amount = scanner.nextFloat();

        Account account = bank.SearchAccount(accNumber);
        if (account != null) {
            account.withdraw(amount);
        } else {
            System.out.println("Account not found!");
        }
    }

    private static void adminTransfer() {
        System.out.println("\n─── TRANSFER MONEY ───");
        System.out.print("Enter Source Account Number: ");
        String fromAcc = scanner.next();
        System.out.print("Enter Destination Account Number: ");
        String toAcc = scanner.next();
        System.out.print("Enter Amount to Transfer: ");
        float amount = scanner.nextFloat();

        Account sourceAccount = bank.SearchAccount(fromAcc);
        Account destAccount = bank.SearchAccount(toAcc);

        if (sourceAccount == null) {
            System.out.println("Source account not found!");
            return;
        }

        if (destAccount == null) {
            System.out.println("Destination account not found!");
            return;
        }

        if (sourceAccount.getAmount() < amount) {
            System.out.println("Insufficient funds!");
            return;
        }

        sourceAccount.withdraw(amount);
        destAccount.deposit(amount);
        System.out.println("✓ Transfer completed successfully!");
    }

    private static void searchOperations() {
        boolean back = false;

        while (!back) {
            System.out.println("\n═══════════════ SEARCH OPERATIONS ═══════════════");
            System.out.println("1. Search Account by Account Number");
            System.out.println("2. Search Client by CNIC");
            System.out.println("3. Search Client by ID");
            System.out.println("4. Back to Main Menu");
            System.out.println("═════════════════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    searchAccount();
                    break;
                case 2:
                    searchClientByCNIC();
                    break;
                case 3:
                    searchClientById();
                    break;
                case 4:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void searchAccount() {
        System.out.println("\n─── SEARCH ACCOUNT ───");
        System.out.print("Enter Account Number: ");
        String accNumber = scanner.next();

        Account account = bank.SearchAccount(accNumber);
        if (account != null) {
            System.out.println("✓ Account Found:");
            System.out.println(account);
        } else {
            System.out.println("Account not found!");
        }
    }

    private static void searchClientByCNIC() {
        System.out.println("\n─── SEARCH CLIENT BY CNIC ───");
        scanner.nextLine(); // Clear buffer
        System.out.print("Enter CNIC: ");
        String cnic = scanner.nextLine();

        Client client = bank.SearchCustomerDetail(cnic);
        if (client != null) {
            System.out.println("✓ Client Found:");
            System.out.println(client);
        } else {
            System.out.println("Client not found!");
        }
    }

    private static void searchClientById() {
        System.out.println("\n─── SEARCH CLIENT BY ID ───");
        System.out.print("Enter Client ID: ");
        String clientId = scanner.next();

        Client client = findClientById(clientId);
        if (client != null) {
            System.out.println("✓ Client Found:");
            System.out.println(client);
        } else {
            System.out.println("Client not found!");
        }
    }

    private static void viewReports() {
        boolean back = false;

        while (!back) {
            System.out.println("\n═══════════════ REPORTS ═══════════════");
            System.out.println("1. Bank Summary Report");
            System.out.println("2. Client Summary Report");
            System.out.println("3. Total Bank Balance");
            System.out.println("4. List All Accounts");
            System.out.println("5. Back to Main Menu");
            System.out.println("═══════════════════════════════════════");

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    bank.displayBankDetails();
                    break;
                case 2:
                    clientSummaryReport();
                    break;
                case 3:
                    System.out.println("\nTotal Bank Balance: $" + bank.totalAmount());
                    break;
                case 4:
                    viewAllAccounts();
                    break;
                case 5:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void clientSummaryReport() {
        System.out.println("\n─── CLIENT SUMMARY REPORT ───");
        System.out.print("Enter Client ID: ");
        String clientId = scanner.next();

        Client client = findClientById(clientId);
        if (client != null) {
            System.out.println(client);
            System.out.println("Total Amount: $" + client.totalAmount());
        } else {
            System.out.println("Client not found!");
        }
    }

    // ==================== UTILITY METHODS ====================
    private static Client findClientById(String clientId) {
        for (Client client : bank.getClList()) {
            if (client.getId().equals(clientId)) {
                return client;
            }
        }
        return null;
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.next();
            }
        }
    }

    // ==================== FILE OPERATIONS ====================
    private static void saveAllData() {
        savePeopleToFile();
        saveClientsToFile();
        saveAccountsToFile();
        authManager.saveUsers();
        System.out.println("\n✓ All data saved successfully!");
    }

    private static void savePeopleToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Person.txt"))) {
            writer.println("Name,CNIC,PhoneNo");
            for (Client client : bank.getClList()) {
                Person person = client.getPersonDetails();
                writer.println(person.getName() + "," +
                        person.getCNIC() + "," +
                        person.getPhoneNo());
            }
        } catch (IOException e) {
            System.out.println("Error saving people data: " + e.getMessage());
        }
    }

    private static void saveClientsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Cilent.txt"))) {
            writer.println("ClientID,PersonCNIC");
            for (Client client : bank.getClList()) {
                writer.println(client.getId() + "," +
                        client.getPersonDetails().getCNIC());
            }
        } catch (IOException e) {
            System.out.println("Error saving clients data: " + e.getMessage());
        }
    }

    private static void saveAccountsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Account.txt"))) {
            writer.println("AccountNumber,Balance,ClientID");
            for (Account account : bank.getAcList()) {
                writer.println(account.getNumber() + "," +
                        account.getAmount() + "," +
                        account.getAcHolder().getId());
            }
        } catch (IOException e) {
            System.out.println("Error saving accounts data: " + e.getMessage());
        }
    }

    private static void loadDataFromFiles() {
        try {
            ArrayList<Person> people = loadPeopleFromFile();
            ArrayList<Client> clients = loadClientsFromFile(people);
            loadAccountsFromFile(clients);
        } catch (IOException e) {
            System.out.println("Note: Starting with fresh data (no existing files found)");
        }
    }

    private static ArrayList<Person> loadPeopleFromFile() throws IOException {
        ArrayList<Person> people = new ArrayList<>();
        File file = new File("Person.txt");

        if (!file.exists()) {
            return people;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("Person.txt"))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3) {
                    people.add(new Person(data[0].trim(), data[1].trim(), data[2].trim()));
                }
            }
        }
        return people;
    }

    private static ArrayList<Client> loadClientsFromFile(ArrayList<Person> people) throws IOException {
        ArrayList<Client> clients = new ArrayList<>();
        File file = new File("Cilent.txt");

        if (!file.exists()) {
            return clients;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("Cilent.txt"))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 2) {
                    String clientId = data[0].trim();
                    String personCNIC = data[1].trim();

                    Person person = null;
                    for (Person p : people) {
                        if (p.getCNIC().equals(personCNIC)) {
                            person = p;
                            break;
                        }
                    }

                    if (person != null) {
                        Client client = new Client(clientId, person);
                        clients.add(client);
                        bank.getClList().add(client);
                    }
                }
            }
        }
        return clients;
    }

    private static void loadAccountsFromFile(ArrayList<Client> clients) throws IOException {
        File file = new File("Account.txt");

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("Account.txt"))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3) {
                    try {
                        String accountNumber = data[0].trim();
                        float balance = Float.parseFloat(data[1].trim());
                        String clientId = data[2].trim();

                        Client client = null;
                        for (Client c : clients) {
                            if (c.getId().equals(clientId)) {
                                client = c;
                                break;
                            }
                        }

                        if (client != null) {
                            Account account = new Account(accountNumber, balance, client);
                            client.getACList().add(account);
                            bank.getAcList().add(account);
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid lines
                    }
                }
            }
        }
    }
}
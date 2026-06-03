import java.io.*;
import java.util.ArrayList;

public class AuthenticationManager {
    private static final String USERS_FILE = "Users.txt";
    private ArrayList<User> users;

    public AuthenticationManager() {
        this.users = new ArrayList<>();
        loadUsers();
    }


    // Load users from file
    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            // Create default admin account if file doesn't exist
            createDefaultAdmin();
            saveUsers();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip header
                    continue;
                }

                String[] data = line.split(",");
                if (data.length >= 3) {
                    String username = data[0].trim();
                    String password = data[1].trim();
                    String role = data[2].trim();
                    users.add(new User(username, password, role));
                }
            }
            System.out.println("Loaded " + users.size() + " user accounts");
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
            createDefaultAdmin();
        }
    }

    // Save users to file
    public void saveUsers() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
            writer.println("Username,Password,Role");
            for (User user : users) {
                writer.println(user.getUsername() + "," +
                        user.getPassword() + "," +
                        user.getRole());
            }
            System.out.println("User data saved successfully");
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    // Create default admin account
    private void createDefaultAdmin() {
        users.add(new User("admin", "admin123", "ADMIN"));
        System.out.println("Default admin account created (username: admin, password: admin123)");
    }

    // Authenticate user
    public User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    // Register new client
    public boolean registerClient(String username, String password, String clientId) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists!");
                return false;
            }
        }

        users.add(new User(username, password, "CLIENT:" + clientId));
        saveUsers();
        System.out.println("Client registered successfully!");
        return true;
    }

    // Register new admin
    public boolean registerAdmin(String username, String password) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                System.out.println("Username already exists!");
                return false;
            }
        }

        users.add(new User(username, password, "ADMIN"));
        saveUsers();
        System.out.println("Admin registered successfully!");
        return true;
    }

    // Get client ID from user role
    public String getClientId(User user) {
        if (user.getRole().startsWith("CLIENT:")) {
            return user.getRole().substring(7);
        }
        return null;
    }

    // Check if user is admin
    public boolean isAdmin(User user) {
        return user.getRole().equals("ADMIN");
    }

    // Change password
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        for (User user : users) {
            if (user.getUsername().equals(username) &&
                    user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    // Get all users (for admin)
    public ArrayList<User> getAllUsers() {
        return users;
    }
}
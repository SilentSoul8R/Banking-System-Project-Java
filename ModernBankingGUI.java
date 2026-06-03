import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class ModernBankingGUI extends JFrame {
    private static Bank bank;
    private static AuthenticationManager authManager;
    private User currentUser;

    // Modern Color Scheme
    private final Color PRIMARY_DARK = new Color(15, 23, 42);
    private final Color SECONDARY_DARK = new Color(30, 41, 59);
    private final Color ACCENT_BLUE = new Color(59, 130, 246);
    private final Color ACCENT_PURPLE = new Color(139, 92, 246);
    private final Color ACCENT_ORANGE = new Color(251, 146, 60);
    private final Color TEXT_PRIMARY = new Color(255,255,255);
    private final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private final Color CARD_BG = new Color(30, 41, 59);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ModernBankingGUI());
    }

    public ModernBankingGUI() {
        bank = new Bank("Bank");
        authManager = new AuthenticationManager();
        loadDataFromFiles();

        setTitle("Bank - Modern Banking System");
        setSize(450, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PRIMARY_DARK);

        showLoginScreen();
        setVisible(true);
    }

    // LOGIN SCREEN
    private void showLoginScreen() {
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(PRIMARY_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel logoLabel = new JLabel("🏦");
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("  Bank");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Banking System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setForeground(TEXT_SECONDARY);

        mainPanel.add(Box.createVerticalStrut(60));
        mainPanel.add(logoLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(60));

        JPanel formPanel = createCard();
        formPanel.setMaximumSize(new Dimension(300, 280));
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel loginTitle = new JLabel("Sign In");
        loginTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        loginTitle.setForeground(TEXT_PRIMARY);

        JTextField usernameField = createTextField("Username");
        JPasswordField passwordField = createPasswordField("Password");
        JButton loginButton = createButton("Sign In", ACCENT_BLUE);

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginTitle);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(20));

        mainPanel.add(formPanel);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (username.equals("Username") || password.equals("Password") || username.trim().isEmpty() || password.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter valid credentials", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            currentUser = authManager.authenticate(username, password);
            if (currentUser != null) {
                if (authManager.isAdmin(currentUser)) {
                    showAdminDashboard();
                } else {
                    showClientATM();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // CLIENT ATM
    private void showClientATM() {
        String clientId = authManager.getClientId(currentUser);
        Client client = findClientById(clientId);

        if (client == null) {
            JOptionPane.showMessageDialog(this, "Client profile not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        getContentPane().removeAll();

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SECONDARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel welcomeLabel = new JLabel("Welcome, " + client.getPersonDetails().getName());
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(TEXT_PRIMARY);

        JButton logoutBtn = createSmallButton("Logout");
        logoutBtn.addActionListener(e -> showLoginScreen());

        header.add(welcomeLabel, BorderLayout.WEST);
        header.add(logoutBtn, BorderLayout.EAST);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(PRIMARY_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel balanceCard = createBalanceCard(client);
        mainPanel.add(balanceCard);
        mainPanel.add(Box.createVerticalStrut(25));

        JLabel actionsLabel = new JLabel("Quick Actions");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        actionsLabel.setForeground(TEXT_PRIMARY);
        mainPanel.add(actionsLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        mainPanel.add(createATMButton("View Accounts", e -> showAccounts(client)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createATMButton("Deposit", e -> showDepositDialog(client)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createATMButton("Withdraw", e -> showWithdrawDialog(client)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createATMButton("Transfer", e -> showTransferDialog(client)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createATMButton("Profile", e -> showProfile(client)));
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(createATMButton("Change Password", e -> showClientChangePassword()));
        mainPanel.add(Box.createVerticalStrut(10));

        add(header, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ADMIN DASHBOARD
    private void showAdminDashboard() {
        getContentPane().removeAll();
        setSize(900, 700);
        setLocationRelativeTo(null);

        JPanel sidebar = createSidebar();
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(PRIMARY_DARK);

        JPanel header = createHeader();
        JPanel content = createDashboardContent();

        contentArea.add(header, BorderLayout.NORTH);
        contentArea.add(new JScrollPane(content), BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SECONDARY_DARK);
        sidebar.setPreferredSize(new Dimension(230, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("Bank");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(TEXT_PRIMARY);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 20, 30, 20));
        sidebar.add(logo);

        sidebar.add(createSidebarBtn("Dashboard", true, e -> showAdminDashboard()));
        sidebar.add(createSidebarBtn("Clients", false, e -> showClientsManagement()));
        sidebar.add(createSidebarBtn("Accounts", false, e -> showAccountsManagement()));
        sidebar.add(createSidebarBtn("Search", false, e -> showSearch()));
        sidebar.add(createSidebarBtn("Reports", false, e -> showReports()));
        sidebar.add(createSidebarBtn("Users", false, e -> showUserManagement()));
        sidebar.add(createSidebarBtn("Change Password", false, e -> showAdminChangePassword()));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(createSidebarBtn("Logout", false, e -> {
            setSize(450, 700);
            setLocationRelativeTo(null);
            showLoginScreen();
        }));

        return sidebar;
    }

    private JButton createSidebarBtn(String text, boolean active, ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(active ? TEXT_PRIMARY : TEXT_SECONDARY);
        btn.setBackground(active ? new Color(51, 65, 85) : SECONDARY_DARK);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        btn.setMaximumSize(new Dimension(230, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(action);

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!active) btn.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                if (!active) btn.setBackground(SECONDARY_DARK);
            }
        });

        return btn;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_DARK);
        header.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcomeLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(welcomeLabel);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createDashboardContent() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(PRIMARY_DARK);
        content.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        JPanel statsRow = new JPanel(new GridLayout(1, 3, 15, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(750, 110));

        statsRow.add(createStatCard("Clients", String.valueOf(bank.getClList().size()), "👤", TEXT_PRIMARY));
        statsRow.add(createStatCard("Accounts", String.valueOf(bank.getAcList().size()), "💳", TEXT_PRIMARY));
        statsRow.add(createStatCard("Balance", String.format("$%.0f", bank.totalAmount()), "💵", TEXT_PRIMARY));

        content.add(statsRow);
        content.add(Box.createVerticalStrut(25));

        JLabel actionsLabel = new JLabel("Quick Actions");
        actionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        actionsLabel.setForeground(TEXT_PRIMARY);
        content.add(actionsLabel);
        content.add(Box.createVerticalStrut(15));

        JPanel actionsPanel = new JPanel(new GridLayout(2, 3, 12, 12));
        actionsPanel.setOpaque(false);
        actionsPanel.setMaximumSize(new Dimension(750, 180));

        actionsPanel.add(createActionCard("Add Client", "👤", e -> showAddClientDialog()));
        actionsPanel.add(createActionCard("New Account", "💳", e -> showCreateAccountDialog()));
        actionsPanel.add(createActionCard("Search", "🔍", e -> showSearch()));
        actionsPanel.add(createActionCard("Reports", "📊", e -> showReports()));
        actionsPanel.add(createActionCard("Settings", "⚙️", e -> showUserManagement()));

        content.add(actionsPanel);
        return content;
    }

    private JPanel createStatCard(String title, String value, String icon, Color color) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(TEXT_PRIMARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(valueLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JButton createActionCard(String title, String icon, ActionListener action) {
        JButton card = new JButton();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setFocusPainted(false);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.addActionListener(action);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(CARD_BG);
            }
        });

        return card;
    }

    private JPanel createBalanceCard(Client client) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, ACCENT_BLUE, getWidth(), getHeight(), ACCENT_PURPLE);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setMaximumSize(new Dimension(400, 150));

        JLabel titleLabel = new JLabel("Total Balance");
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(220, 220, 255));

        JLabel balanceLabel = new JLabel(String.format("$%.2f", client.totalAmount()));
        balanceLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        balanceLabel.setForeground(Color.WHITE);

        JLabel accountsLabel = new JLabel(client.getACList().size() + " Accounts");
        accountsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        accountsLabel.setForeground(new Color(220, 220, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(balanceLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(accountsLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    // UI COMPONENTS
    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }

    private JTextField createTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_SECONDARY);
        field.setBackground(SECONDARY_DARK);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setMaximumSize(new Dimension(320, 45));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
            }
        });
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(placeholder);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_SECONDARY);
        field.setBackground(SECONDARY_DARK);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 65, 85), 1),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        field.setMaximumSize(new Dimension(320, 45));
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setEchoChar((char) 0);

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setEchoChar('*');
                    field.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (new String(field.getPassword()).isEmpty()) {
                    field.setEchoChar((char) 0);
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
            }
        });
        return field;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        button.setMaximumSize(new Dimension(320, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private JButton createATMButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(CARD_BG);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        button.setMaximumSize(new Dimension(400, 50));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(51, 65, 85));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_BG);
            }
        });
        return button;
    }

    private JButton createSmallButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_ORANGE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // DIALOGS
    private void showAccounts(Client client) {
        if (client.getACList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("Your Accounts:\n\n");
        for (Account acc : client.getACList()) {
            sb.append(String.format("%s - $%.2f\n", acc.getNumber(), acc.getAmount()));
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Accounts", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showDepositDialog(Client client) {
        if (client.getACList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String accNum = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (accNum == null || accNum.trim().isEmpty()) return;

        // Check if account exists
        Account targetAccount = null;
        for (Account acc : client.getACList()) {
            if (acc.getNumber().equals(accNum.trim())) {
                targetAccount = acc;
                break;
            }
        }

        if (targetAccount == null) {
            JOptionPane.showMessageDialog(this, "Account not found: " + accNum, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amtStr = JOptionPane.showInputDialog(this, "Enter Amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;

        try {
            float amount = Float.parseFloat(amtStr.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Deposit amount must be positive!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            float result = targetAccount.deposit(amount);
            if (result != -1) {
                JOptionPane.showMessageDialog(this, String.format("Deposit successful!\nNew balance: $%.2f", targetAccount.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                saveAllData();
                showClientATM();
            } else {
                JOptionPane.showMessageDialog(this, "Deposit failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showWithdrawDialog(Client client) {
        if (client.getACList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String accNum = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (accNum == null || accNum.trim().isEmpty()) return;

        // Check if account exists
        Account targetAccount = null;
        for (Account acc : client.getACList()) {
            if (acc.getNumber().equals(accNum.trim())) {
                targetAccount = acc;
                break;
            }
        }

        if (targetAccount == null) {
            JOptionPane.showMessageDialog(this, "Account not found: " + accNum, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amtStr = JOptionPane.showInputDialog(this, "Enter Amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;

        try {
            float amount = Float.parseFloat(amtStr.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Withdrawal amount must be positive!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount > targetAccount.getAmount()) {
                JOptionPane.showMessageDialog(this, String.format("Insufficient funds!\nYou tried to withdraw: $%.2f\nAvailable balance: $%.2f", amount, targetAccount.getAmount()), "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                return;
            }

            float result = targetAccount.withdraw(amount);
            if (result != -1) {
                JOptionPane.showMessageDialog(this, String.format("Withdrawal successful!\nNew balance: $%.2f", targetAccount.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                saveAllData();
                showClientATM();
            } else {
                JOptionPane.showMessageDialog(this, "Withdrawal failed!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showTransferDialog(Client client) {
        if (client.getACList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String fromAcc = JOptionPane.showInputDialog(this, "From Account:");
        if (fromAcc == null || fromAcc.trim().isEmpty()) return;

        // Check if source account exists
        Account sourceAccount = null;
        for (Account acc : client.getACList()) {
            if (acc.getNumber().equals(fromAcc.trim())) {
                sourceAccount = acc;
                break;
            }
        }

        if (sourceAccount == null) {
            JOptionPane.showMessageDialog(this, "Source account not found: " + fromAcc, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String toAcc = JOptionPane.showInputDialog(this, "To Account:");
        if (toAcc == null || toAcc.trim().isEmpty()) return;

        String amtStr = JOptionPane.showInputDialog(this, "Amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;

        try {
            float amount = Float.parseFloat(amtStr.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Transfer amount must be positive!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (amount > sourceAccount.getAmount()) {
                JOptionPane.showMessageDialog(this, String.format("Insufficient funds in source account!\nYou tried to transfer: $%.2f\nAvailable balance: $%.2f", amount, sourceAccount.getAmount()), "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account destAccount = bank.SearchAccount(toAcc.trim());
            if (destAccount != null) {
                float withdrawResult = sourceAccount.withdraw(amount);
                if (withdrawResult != -1) {
                    destAccount.deposit(amount);
                    JOptionPane.showMessageDialog(this, String.format("Transfer successful!\nTransferred: $%.2f\nNew balance: $%.2f", amount, sourceAccount.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveAllData();
                    showClientATM();
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer failed during withdrawal!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Destination account not found: " + toAcc, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProfile(Client client) {
        String info = String.format("Name: %s\nCNIC: %s\nPhone: %s\nID: %s\nTotal Balance: $%.2f",
                client.getPersonDetails().getName(),
                client.getPersonDetails().getCNIC(),
                client.getPersonDetails().getPhoneNo(),
                client.getId(),
                client.totalAmount());
        JOptionPane.showMessageDialog(this, info, "Profile", JOptionPane.INFORMATION_MESSAGE);
    }


    // ADMIN OPERATIONS
    private void showAddClientDialog() {
        JTextField nameField = new JTextField();
        JTextField cnicField = new JTextField();
        JTextField phoneField = new JTextField();

        Object[] message = {
                "Name:", nameField,
                "CNIC:", cnicField,
                "Phone:", phoneField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add Client", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String cnic = cnicField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || cnic.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Person person = new Person(name, cnic, phone);
            Client client = bank.addClient(person);

            int register = JOptionPane.showConfirmDialog(this,
                    "Client added! ID: " + client.getId() + "\n\nCreate login credentials?",
                    "Success", JOptionPane.YES_NO_OPTION);

            if (register == JOptionPane.YES_OPTION) {
                String username = JOptionPane.showInputDialog(this, "Username:");
                String password = JOptionPane.showInputDialog(this, "Password:");
                if (username != null && password != null && !username.trim().isEmpty() && !password.trim().isEmpty()) {
                    authManager.registerClient(username, password, client.getId());
                    saveAllData();
                }
            } else {
                saveAllData();
            }
            showAdminDashboard();
        }
    }

    private void showCreateAccountDialog() {
        if (bank.getClList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No clients available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] clientIds = bank.getClList().stream()
                .map(c -> c.getId() + " - " + c.getPersonDetails().getName())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Select Client:", "Create Account",
                JOptionPane.QUESTION_MESSAGE, null, clientIds, clientIds[0]);

        if (selected != null) {
            String clientId = selected.split(" - ")[0];
            Client client = findClientById(clientId);

            String accNum = JOptionPane.showInputDialog(this, "Account Number:");
            if (accNum == null || accNum.trim().isEmpty()) return;

            String amtStr = JOptionPane.showInputDialog(this, "Initial Deposit:");
            if (amtStr == null || amtStr.trim().isEmpty()) return;

            try {
                float amount = Float.parseFloat(amtStr.trim());

                if (amount < 0) {
                    JOptionPane.showMessageDialog(this, "Initial deposit cannot be negative!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Account newAccount = bank.addAccount(accNum.trim(), amount, client);
                if (newAccount != null) {
                    JOptionPane.showMessageDialog(this, String.format("Account created successfully!\nAccount: %s\nBalance: $%.2f", accNum, amount), "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveAllData();
                    showAdminDashboard();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create account!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showClientsManagement() {
        String[] options = {"Add Client", "Remove Client", "View Clients", "Back"};
        int choice = JOptionPane.showOptionDialog(this, "Client Management", "Clients",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: showAddClientDialog(); break;
            case 1: removeClient(); break;
            case 2: viewAllClients(); break;
            case 3: showAdminDashboard(); break;
        }
    }

    private void removeClient() {
        String clientId = JOptionPane.showInputDialog(this, "Enter Client ID to remove:");
        if (clientId != null && !clientId.trim().isEmpty()) {
            Client client = findClientById(clientId.trim());
            if (client != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        String.format("Are you sure you want to remove client:\n%s (ID: %s)?",
                                client.getPersonDetails().getName(), clientId),
                        "Confirm Removal", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    bank.removeClient(clientId.trim());
                    saveAllData();
                    JOptionPane.showMessageDialog(this, "Client removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    showAdminDashboard();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Client not found: " + clientId, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewAllClients() {
        if (bank.getClList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No clients found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("All Clients:\n\n");
        for (Client c : bank.getClList()) {
            sb.append(String.format("%s - %s - $%.2f\n",
                    c.getId(), c.getPersonDetails().getName(), c.totalAmount()));
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Clients", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAccountsManagement() {
        String[] options = {"Create Account", "View Accounts", "Back"};
        int choice = JOptionPane.showOptionDialog(this, "Account Management", "Accounts",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: showCreateAccountDialog(); break;
            case 1: viewAllAccounts(); break;
            case 2: showAdminDashboard(); break;
        }
    }

    private void viewAllAccounts() {
        if (bank.getAcList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No accounts found", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder("All Accounts:\n\n");
        for (Account acc : bank.getAcList()) {
            sb.append(String.format("%s - %s - $%.2f\n",
                    acc.getNumber(), acc.getAcHolder().getPersonDetails().getName(), acc.getAmount()));
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Accounts", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showTransactions() {
        String[] options = {"Deposit", "Withdraw", "Transfer", "Back"};
        int choice = JOptionPane.showOptionDialog(this, "Transactions", "Transactions",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: adminDeposit(); break;
            case 1: adminWithdraw(); break;
            case 2: adminTransfer(); break;
            case 3: showAdminDashboard(); break;
        }
    }

    private void adminDeposit() {
        String accNum = JOptionPane.showInputDialog(this, "Account Number:");
        if (accNum == null || accNum.trim().isEmpty()) return;

        String amtStr = JOptionPane.showInputDialog(this, "Amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;

        try {
            float amount = Float.parseFloat(amtStr.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Deposit amount must be positive!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account acc = bank.SearchAccount(accNum.trim());
            if (acc != null) {
                float result = acc.deposit(amount);
                if (result != -1) {
                    JOptionPane.showMessageDialog(this, String.format("Deposit successful!\nNew balance: $%.2f", acc.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Deposit failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account not found: " + accNum, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminWithdraw() {
        String accNum = JOptionPane.showInputDialog(this, "Account Number:");
        if (accNum == null || accNum.trim().isEmpty()) return;

        String amtStr = JOptionPane.showInputDialog(this, "Amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;

        try {
            float amount = Float.parseFloat(amtStr.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Withdrawal amount must be positive!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account acc = bank.SearchAccount(accNum.trim());
            if (acc != null) {
                if (amount > acc.getAmount()) {
                    JOptionPane.showMessageDialog(this, String.format("Insufficient funds!\nYou tried to withdraw: $%.2f\nAvailable balance: $%.2f", amount, acc.getAmount()), "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                float result = acc.withdraw(amount);
                if (result != -1) {
                    JOptionPane.showMessageDialog(this, String.format("Withdrawal successful!\nNew balance: $%.2f", acc.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Withdrawal failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account not found: " + accNum, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminTransfer() {
        String fromAcc = JOptionPane.showInputDialog(this, "From Account:");
        if (fromAcc == null || fromAcc.trim().isEmpty()) return;

        String toAcc = JOptionPane.showInputDialog(this, "To Account:");
        if (toAcc == null || toAcc.trim().isEmpty()) return;

        String amtStr = JOptionPane.showInputDialog(this, "Amount:");
        if (amtStr == null || amtStr.trim().isEmpty()) return;

        try {
            float amount = Float.parseFloat(amtStr.trim());

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Transfer amount must be positive!\nYou entered: $" + amount, "Invalid Amount", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Account source = bank.SearchAccount(fromAcc.trim());
            Account dest = bank.SearchAccount(toAcc.trim());

            if (source != null && dest != null) {
                if (amount > source.getAmount()) {
                    JOptionPane.showMessageDialog(this, String.format("Insufficient funds in source account!\nYou tried to transfer: $%.2f\nAvailable balance: $%.2f", amount, source.getAmount()), "Insufficient Funds", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                float withdrawResult = source.withdraw(amount);
                if (withdrawResult != -1) {
                    dest.deposit(amount);
                    JOptionPane.showMessageDialog(this, String.format("Transfer successful!\nTransferred: $%.2f\nSource account balance: $%.2f", amount, source.getAmount()), "Success", JOptionPane.INFORMATION_MESSAGE);
                    saveAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Transfer failed during withdrawal!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                if (source == null) {
                    JOptionPane.showMessageDialog(this, "Source account not found: " + fromAcc, "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Destination account not found: " + toAcc, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid amount format!\nPlease enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSearch() {
        String[] options = {"Search Account", "Search by CNIC", "Search by ID", "Back"};
        int choice = JOptionPane.showOptionDialog(this, "Search Operations", "Search",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: searchAccount(); break;
            case 1: searchByCNIC(); break;
            case 2: searchById(); break;
            case 3: showAdminDashboard(); break;
        }
    }

    private void searchAccount() {
        String accNum = JOptionPane.showInputDialog(this, "Enter Account Number:");
        if (accNum != null && !accNum.trim().isEmpty()) {
            Account acc = bank.SearchAccount(accNum.trim());
            if (acc != null) {
                String info = String.format("Account: %s\nHolder: %s\nBalance: $%.2f",
                        acc.getNumber(), acc.getAcHolder().getPersonDetails().getName(), acc.getAmount());
                JOptionPane.showMessageDialog(this, info, "Account Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Account not found: " + accNum, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchByCNIC() {
        String cnic = JOptionPane.showInputDialog(this, "Enter CNIC:");
        if (cnic != null && !cnic.trim().isEmpty()) {
            Client client = bank.SearchCustomerDetail(cnic.trim());
            if (client != null) {
                String info = String.format("Client: %s\nID: %s\nCNIC: %s\nBalance: $%.2f",
                        client.getPersonDetails().getName(), client.getId(),
                        client.getPersonDetails().getCNIC(), client.totalAmount());
                JOptionPane.showMessageDialog(this, info, "Client Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Client not found with CNIC: " + cnic, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchById() {
        String id = JOptionPane.showInputDialog(this, "Enter Client ID:");
        if (id != null && !id.trim().isEmpty()) {
            Client client = findClientById(id.trim());
            if (client != null) {
                String info = String.format("Client: %s\nID: %s\nCNIC: %s\nBalance: $%.2f",
                        client.getPersonDetails().getName(), client.getId(),
                        client.getPersonDetails().getCNIC(), client.totalAmount());
                JOptionPane.showMessageDialog(this, info, "Client Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Client not found with ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showReports() {
        String info = String.format("BANK SUMMARY\n\nTotal Clients: %d\nTotal Accounts: %d\nTotal Balance: $%.2f",
                bank.getClList().size(), bank.getAcList().size(), bank.totalAmount());
        JOptionPane.showMessageDialog(this, info, "Bank Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showUserManagement() {
        String[] options = {"View Users", "Add Admin", "Register Client Login", "Back"};
        int choice = JOptionPane.showOptionDialog(this, "User Management", "Users",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: viewUsers(); break;
            case 1: addAdmin(); break;
            case 2: registerClientLogin(); break;
            case 3: showAdminDashboard(); break;
        }
    }

    private void viewUsers() {
        StringBuilder sb = new StringBuilder("All Users:\n\n");
        for (User user : authManager.getAllUsers()) {
            sb.append(user.toString()).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "Users", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addAdmin() {
        String username = JOptionPane.showInputDialog(this, "Username:");
        if (username == null || username.trim().isEmpty()) return;

        String password = JOptionPane.showInputDialog(this, "Password:");
        if (password == null || password.trim().isEmpty()) return;

        if (authManager.registerAdmin(username.trim(), password)) {
            JOptionPane.showMessageDialog(this, "Admin added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            saveAllData();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add admin!\nUsername may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registerClientLogin() {
        if (bank.getClList().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No clients available", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] clientIds = bank.getClList().stream()
                .map(c -> c.getId() + " - " + c.getPersonDetails().getName())
                .toArray(String[]::new);

        String selected = (String) JOptionPane.showInputDialog(this, "Select Client:", "Register Login",
                JOptionPane.QUESTION_MESSAGE, null, clientIds, clientIds[0]);

        if (selected != null) {
            String clientId = selected.split(" - ")[0];
            String username = JOptionPane.showInputDialog(this, "Username:");
            if (username == null || username.trim().isEmpty()) return;

            String password = JOptionPane.showInputDialog(this, "Password:");
            if (password == null || password.trim().isEmpty()) return;

            if (authManager.registerClient(username.trim(), password, clientId)) {
                JOptionPane.showMessageDialog(this, "Login created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                saveAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create login!\nUsername may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // UTILITY
    private Client findClientById(String clientId) {
        for (Client client : bank.getClList()) {
            if (client.getId().equals(clientId)) {
                return client;
            }
        }
        return null;
    }

    // FILE OPERATIONS
    private void saveAllData() {
        try {
            savePeopleToFile();
            saveClientsToFile();
            saveAccountsToFile();
            authManager.saveUsers();
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    private void savePeopleToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Person.txt"))) {
            writer.println("Name,CNIC,PhoneNo");
            for (Client client : bank.getClList()) {
                Person person = client.getPersonDetails();
                writer.println(person.getName() + "," + person.getCNIC() + "," + person.getPhoneNo());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void saveClientsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Cilent.txt"))) {
            writer.println("ClientID,PersonCNIC");
            for (Client client : bank.getClList()) {
                writer.println(client.getId() + "," + client.getPersonDetails().getCNIC());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void saveAccountsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("Account.txt"))) {
            writer.println("AccountNumber,Balance,ClientID");
            for (Account account : bank.getAcList()) {
                writer.println(account.getNumber() + "," + account.getAmount() + "," +
                        account.getAcHolder().getId());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void loadDataFromFiles() {
        try {
            ArrayList<Person> people = loadPeopleFromFile();
            ArrayList<Client> clients = loadClientsFromFile(people);
            loadAccountsFromFile(clients);
        } catch (Exception e) {
            System.out.println("Starting with fresh data");
        }
    }

    private ArrayList<Person> loadPeopleFromFile() throws IOException {
        ArrayList<Person> people = new ArrayList<>();
        File file = new File("Person.txt");
        if (!file.exists()) return people;

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

    private ArrayList<Client> loadClientsFromFile(ArrayList<Person> people) throws IOException {
        ArrayList<Client> clients = new ArrayList<>();
        File file = new File("Cilent.txt");
        if (!file.exists()) return clients;

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

    private void loadAccountsFromFile(ArrayList<Client> clients) throws IOException {
        File file = new File("Account.txt");
        if (!file.exists()) return;

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

    // PASSWORD CHANGE METHODS
    private void showClientChangePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
                "Current Password:", oldPasswordField,
                "New Password:", newPasswordField,
                "Confirm New Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPassword.length() < 4) {
                JOptionPane.showMessageDialog(this, "New password must be at least 4 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authManager.changePassword(currentUser.getUsername(), oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!\nPlease login again.", "Success", JOptionPane.INFORMATION_MESSAGE);
                currentUser.setPassword(newPassword);
                showLoginScreen();
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAdminChangePassword() {
        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        Object[] message = {
                "Current Password:", oldPasswordField,
                "New Password:", newPasswordField,
                "Confirm New Password:", confirmPasswordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (newPassword.length() < 4) {
                JOptionPane.showMessageDialog(this, "New password must be at least 4 characters long!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (authManager.changePassword(currentUser.getUsername(), oldPassword, newPassword)) {
                JOptionPane.showMessageDialog(this, "Password changed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                currentUser.setPassword(newPassword);
                saveAllData();
            } else {
                JOptionPane.showMessageDialog(this, "Current password is incorrect!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
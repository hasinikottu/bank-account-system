import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;

// ------------------------------------------------------
// BANK ACCOUNT CLASS – Handles account data & operations
// ------------------------------------------------------
class BankAccount {
    private String accountHolder;
    private double balance;
    private ArrayList<String> transactionHistory = new ArrayList<>();

    public BankAccount(String accountHolder, double initialDeposit) {
        this.accountHolder = accountHolder;
        this.balance = initialDeposit;
        addTransaction("Account created with initial balance: ₹" + initialDeposit);
    }

    public void deposit(double amount) {
        balance += amount;
        addTransaction("Deposited ₹" + amount);
    }

    public boolean withdraw(double amount) {
        if (amount <= balance) {
            balance -= amount;
            addTransaction("Withdrew ₹" + amount);
            return true;
        } else {
            addTransaction("Failed withdrawal: ₹" + amount + " (Insufficient funds)");
            return false;
        }
    }

    public void addTransaction(String details) {
        transactionHistory.add(new Date() + " - " + details);
    }

    public ArrayList<String> getTransactionHistory() {
        return transactionHistory;
    }

    public double getBalance() { return balance; }
    public String getAccountHolder() { return accountHolder; }
}

// ------------------------------------------------------
// MAIN GUI CLASS – Handles all visual components & logic
// ------------------------------------------------------
public class BankAccountSystem extends JFrame implements ActionListener {

    // Fields
    private JTextField tfName, tfInitial, tfAmount;
    private JLabel lblBalance, lblWelcome;
    private JButton btnCreate, btnDeposit, btnWithdraw, btnBalance, btnHistory, btnReset;
    private JTable table;
    private DefaultTableModel model;
    private BankAccount account;
    private DecimalFormat df = new DecimalFormat("₹#,##0.00");

    // Constructor
    public BankAccountSystem() {
        setTitle("🏦 Bank Account Management System");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Apply Nimbus Look
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) { System.out.println("Nimbus not available"); }

        // ---------------- TOP PANEL ----------------
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(new Color(240, 248, 255));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Bank Account Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(0, 102, 204));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        topPanel.add(lblTitle, c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 1;
        topPanel.add(new JLabel("Account Holder Name:"), c);
        c.gridx = 1;
        tfName = new JTextField();
        topPanel.add(tfName, c);

        c.gridx = 0; c.gridy = 2;
        topPanel.add(new JLabel("Initial Deposit (₹):"), c);
        c.gridx = 1;
        tfInitial = new JTextField();
        topPanel.add(tfInitial, c);

        c.gridx = 1; c.gridy = 3;
        btnCreate = new JButton("Create Account");
        btnCreate.setBackground(new Color(51, 153, 255));
        btnCreate.setForeground(Color.WHITE);
        topPanel.add(btnCreate, c);

        add(topPanel, BorderLayout.NORTH);

        // ---------------- CENTER PANEL ----------------
        JPanel centerPanel = new JPanel(new GridBagLayout());
        c.insets = new Insets(8, 10, 8, 10);

        c.gridx = 0; c.gridy = 0;
        centerPanel.add(new JLabel("Amount (₹):"), c);
        c.gridx = 1;
        tfAmount = new JTextField(10);
        centerPanel.add(tfAmount, c);

        btnDeposit = new JButton("Deposit");
        btnWithdraw = new JButton("Withdraw");
        btnBalance = new JButton("Check Balance");
        btnHistory = new JButton("View History");
        btnReset = new JButton("Reset");

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnDeposit);
        btnPanel.add(btnWithdraw);
        btnPanel.add(btnBalance);
        btnPanel.add(btnHistory);
        btnPanel.add(btnReset);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        centerPanel.add(btnPanel, c);

        lblBalance = new JLabel("Balance: ₹0.00", SwingConstants.CENTER);
        lblBalance.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBalance.setForeground(new Color(0, 153, 0));
        c.gridy = 2;
        centerPanel.add(lblBalance, c);

        add(centerPanel, BorderLayout.CENTER);

        // ---------------- BOTTOM PANEL ----------------
        JPanel bottomPanel = new JPanel(new BorderLayout());
        lblWelcome = new JLabel("Welcome! Create an account to begin.", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        bottomPanel.add(lblWelcome, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ---------------- TRANSACTION TABLE ----------------
        String[] cols = {"Transaction History"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(650, 150));
        add(scrollPane, BorderLayout.SOUTH);

        // ---------------- ACTION LISTENERS ----------------
        btnCreate.addActionListener(this);
        btnDeposit.addActionListener(this);
        btnWithdraw.addActionListener(this);
        btnBalance.addActionListener(this);
        btnHistory.addActionListener(this);
        btnReset.addActionListener(this);

        setVisible(true);
    }

    // ------------------------------------------------------
    // EVENT HANDLING
    // ------------------------------------------------------
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        try {
            if (src == btnCreate) {
                String name = tfName.getText().trim();
                double initial = Double.parseDouble(tfInitial.getText().trim());
                if (name.isEmpty() || initial < 0) {
                    JOptionPane.showMessageDialog(this, "Enter valid details!");
                    return;
                }
                account = new BankAccount(name, initial);
                lblBalance.setText("Balance: " + df.format(account.getBalance()));
                lblWelcome.setText("Account created for " + name + "!");
                refreshTable();
                JOptionPane.showMessageDialog(this, "Account successfully created!");
            }

            else if (src == btnDeposit) {
                if (account == null) { warn(); return; }
                double amt = Double.parseDouble(tfAmount.getText().trim());
                if (amt <= 0) { JOptionPane.showMessageDialog(this, "Enter valid amount!"); return; }
                account.deposit(amt);
                lblBalance.setText("Balance: " + df.format(account.getBalance()));
                refreshTable();
            }

            else if (src == btnWithdraw) {
                if (account == null) { warn(); return; }
                double amt = Double.parseDouble(tfAmount.getText().trim());
                if (amt <= 0) { JOptionPane.showMessageDialog(this, "Enter valid amount!"); return; }
                boolean ok = account.withdraw(amt);
                if (!ok) JOptionPane.showMessageDialog(this, "Insufficient balance!");
                lblBalance.setText("Balance: " + df.format(account.getBalance()));
                refreshTable();
            }

            else if (src == btnBalance) {
                if (account == null) { warn(); return; }
                JOptionPane.showMessageDialog(this, "Current Balance: " + df.format(account.getBalance()));
            }

            else if (src == btnHistory) {
                if (account == null) { warn(); return; }
                refreshTable();
            }

            else if (src == btnReset) {
                resetAll();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter valid numeric values!");
        }
    }

    // ------------------------------------------------------
    // UTILITY METHODS
    // ------------------------------------------------------
    private void warn() {
        JOptionPane.showMessageDialog(this, "Create an account first!");
    }

    private void resetAll() {
        tfName.setText("");
        tfInitial.setText("");
        tfAmount.setText("");
        lblBalance.setText("Balance: ₹0.00");
        lblWelcome.setText("Welcome! Create an account to begin.");
        model.setRowCount(0);
        account = null;
    }

    private void refreshTable() {
        model.setRowCount(0);
        if (account != null) {
            for (String entry : account.getTransactionHistory()) {
                model.addRow(new Object[]{entry});
            }
        }
    }

    // ------------------------------------------------------
    // MAIN METHOD
    // ------------------------------------------------------
    public static void main(String[] args) {
        new BankAccountSystem();
    }
}


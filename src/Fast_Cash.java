import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.*;

public class Fast_Cash extends JFrame implements ActionListener {

    private JPanel contentPane;
    private JButton btn100, btn500, btn1000, btn2000, btn5000, btn10000, btnBack;
    private String pin;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Fast_Cash frame = new Fast_Cash("1234"); // Example PIN
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Fast_Cash(String pin) {
        this.pin = pin;

        setTitle("Fast Cash");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1550, 1080);
        setLocation(-5, 0);
        setResizable(false);

        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel label = new JLabel("Select Withdrawal Amount");
        label.setForeground(Color.WHITE);
        label.setFont(new Font("System", Font.BOLD, 26));
        label.setBounds(477, 178, 333, 35);
        contentPane.add(label);

        btn100 = createButton("Rs. 100", 413, 259);
        btn500 = createButton("Rs. 500", 694, 259);
        btn1000 = createButton("Rs. 1000", 413, 304);
        btn2000 = createButton("Rs. 2000", 694, 304);
        btn5000 = createButton("Rs. 5000", 413, 349);
        btn10000 = createButton("Rs. 10000", 694, 349);

        btnBack = new JButton("Back");
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Dialog", Font.BOLD, 17));
        btnBack.setBackground(Color.RED);
        btnBack.setBounds(694, 394, 173, 35);
        btnBack.addActionListener(this);
        contentPane.add(btnBack);

        // ATM Image Background
        ImageIcon atm = new ImageIcon(Login_page.class.getResource("/Images/atm2.png"));
        Image img = atm.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
        JLabel background = new JLabel(new ImageIcon(img));
        background.setBounds(10, -12, 1550, 830);
        contentPane.add(background);

        // Registering ActionListeners
        btn100.addActionListener(this);
        btn500.addActionListener(this);
        btn1000.addActionListener(this);
        btn2000.addActionListener(this);
        btn5000.addActionListener(this);
        btn10000.addActionListener(this);
    }

    private JButton createButton(String text, int x, int y) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 128, 128));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Raleway", Font.BOLD, 17));
        button.setBounds(x, y, 173, 35);
        contentPane.add(button);
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnBack) {
        	dispose();
            new Transaction(pin).setVisible(true);
            dispose();
            return;
        }

        try {
            String buttonText = ((JButton) e.getSource()).getText(); // Example: "Rs. 500"
            double amount = Double.parseDouble(buttonText.replaceAll("[^\\d]", ""));

            // Get current date
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            JDBCConn conn = new JDBCConn();
            double balance = 0.0;

            // Fetch current balance
            ResultSet rs = conn.stmt.executeQuery("SELECT TransactionType, Amount FROM Transactions WHERE PIN_No = '" + pin + "'");
            while (rs.next()) {
                String type = rs.getString("TransactionType");
                double amt = rs.getDouble("Amount");
                balance += type.equalsIgnoreCase("Deposit") ? amt : -amt;
            }

            // Check if sufficient balance exists
            if (balance < amount) {
                JOptionPane.showMessageDialog(null, "Insufficient balance.\nCurrent balance: Rs. " + balance);
                return;
            }

            // Insert withdrawal record
            String query = "INSERT INTO Transactions (PIN_No, \"Date\", TransactionType, Amount) VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
            PreparedStatement pstmt = conn.conn.prepareStatement(query);
            pstmt.setString(1, pin);
            pstmt.setString(2, date);
            pstmt.setString(3, "Withdrawal");
            pstmt.setDouble(4, amount);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Rs. " + amount + " withdrawn successfully.");
            dispose();
            new Transaction(pin).setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while processing the transaction.");
        }
    }
}

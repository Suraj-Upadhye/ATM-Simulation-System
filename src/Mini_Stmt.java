import java.awt.event.*;
import java.sql.ResultSet;
import java.awt.*;
import javax.swing.*;

public class Mini_Stmt extends JFrame implements ActionListener {

    private JPanel contentPane;
    private JButton Btn;
    String pin;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Mini_Stmt frame = new Mini_Stmt(""); // Pass a test PIN
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Mini_Stmt(String p) {
        this.pin = p;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocation(20, 20);
        contentPane = new JPanel();
        contentPane.setBackground(new Color(227, 225, 225));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel ATMName = new JLabel("ATM Simulation Mini Statement");
        ATMName.setFont(new Font("System", Font.BOLD, 20));
        ATMName.setBounds(100, 10, 300, 30);
        contentPane.add(ATMName);

        JLabel card = new JLabel("Card Number : ************");
        card.setFont(new Font("System", Font.PLAIN, 16));
        card.setBounds(35, 60, 400, 25);
        contentPane.add(card);

        JLabel dateTime = new JLabel();
        dateTime.setVerticalAlignment(SwingConstants.TOP);
        dateTime.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(dateTime);
        scrollPane.setBounds(21, 100, 443, 300);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        contentPane.add(scrollPane);


        JLabel amountLabel = new JLabel();
        amountLabel.setFont(new Font("System", Font.BOLD, 16));
        amountLabel.setBounds(35, 420, 400, 30);
        contentPane.add(amountLabel);
        

        // Fetch card number from Login_Tab
        try {
            JDBCConn conn = new JDBCConn();
            ResultSet rset = conn.stmt.executeQuery("SELECT * FROM Login_Tab WHERE PIN_No = '" + pin + "'");
            if (rset.next()) {
                String cardNo = rset.getString("CardNo");
                String last4 = cardNo.substring(cardNo.length() - 4);
                card.setText("Card Number : ************" + last4);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fetch mini statement from Transactions table
        try {
            double balance = 0;
            JDBCConn conn = new JDBCConn();
            ResultSet rset = conn.stmt.executeQuery("SELECT * FROM Transactions WHERE PIN_No = '" + pin + "'");

            StringBuilder html = new StringBuilder("<html>");
            html.append(String.format("<b>%-20s %-15s %s</b><br><br>", "Date", "Type", "Amount"));

            while (rset.next()) {
                String date = rset.getString("Date");
                String type = rset.getString("TRANSACTIONTYPE");
                double amount = rset.getDouble("AMOUNT");

                html.append(String.format("%-20s %-15s %.2f<br>", date, type, amount));

                if (type.equalsIgnoreCase("Deposit")) {
                    balance += amount;
                } else {
                    balance -= amount;
                }
            }

            html.append("</html>");
            dateTime.setText(html.toString());
            amountLabel.setText("Your Current Balance is Rs. : " + String.format("%.2f", balance));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Exit button
        Btn = new JButton("Exit");
        Btn.setBackground(new Color(0, 128, 128));
        Btn.setForeground(Color.WHITE);
        Btn.setFont(new Font("Raleway", Font.BOLD, 18));
        Btn.setBounds(60, 490, 100, 33);
        Btn.addActionListener(this);
        contentPane.add(Btn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Transaction home = new Transaction(pin);
        dispose();
        home.setVisible(true);
    }
}

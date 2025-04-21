import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Withdrawl extends JFrame implements ActionListener{

	private JPanel contentPane;
	private JButton Back_btn,Withdraw_btn;
	private JTextField Enter_amt_txt;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Withdrawl frame = new Withdrawl("");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	String pin;
	public Withdrawl(String p) {
		this.pin=p;
		setTitle("Deposit- Additional Details");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1550, 1080);
		setLocation(-5,0);
		setResizable(false);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
				
		JLabel Data_Lab = new JLabel("Maximum Withdrawal is Rs.10000");
		Data_Lab.setForeground(new Color(255, 255, 255));
		Data_Lab.setFont(new Font("System", Font.BOLD, 20));
		Data_Lab.setBounds(469, 180, 333, 35);
		contentPane.add(Data_Lab);

		//Enter amount Label
		JLabel Enter_amt_lab = new JLabel("Please Enter Your Amount:");
		Enter_amt_lab.setForeground(new Color(255, 255, 255));
		Enter_amt_lab.setFont(new Font("System", Font.BOLD, 20));
		Enter_amt_lab.setBounds(494, 225, 281, 35);
		contentPane.add(Enter_amt_lab);
		
		//TextField to enter amount
		Enter_amt_txt = new JTextField();
		Enter_amt_txt.setToolTipText("Enter Withdrawal Amount");
		Enter_amt_txt.setFont(new Font("Raleway", Font.PLAIN, 16));
		Enter_amt_txt.setBackground(new Color(65,125,128));
		Enter_amt_txt.setForeground(new Color(255, 255, 255));
		Enter_amt_txt.setBounds(462, 282, 340, 25);
		contentPane.add(Enter_amt_txt);

		//Deposit Button
		Withdraw_btn = new JButton("WITHDRAW");
		Withdraw_btn.setBackground(new Color(0, 128, 128));
		Withdraw_btn.setForeground(new Color(255, 255, 255));
		Withdraw_btn.setFont(new Font("Raleway", Font.BOLD, 18));
		Withdraw_btn.setBounds(715, 357, 146, 37);
		contentPane.add(Withdraw_btn);
		Withdraw_btn.addActionListener(this);

		//Back button
		Back_btn = new JButton("BACK");
		Back_btn.setForeground(Color.WHITE);
		Back_btn.setFont(new Font("Dialog", Font.BOLD, 18));
		Back_btn.setBackground(new Color(0, 128, 128));
		Back_btn.setBounds(715, 404, 146, 37);
		Back_btn.addActionListener(this);
		contentPane.add(Back_btn);

		//ATM Image
		ImageIcon atm=new ImageIcon(Login_page.class.getResource("/Images/atm2.png"));
		Image img1=atm.getImage().getScaledInstance(1550, 830, Image.SCALE_DEFAULT);
		ImageIcon fortopl=new ImageIcon(img1);
		JLabel Top_logo = new JLabel(fortopl);
		Top_logo.setToolTipText("");
		Top_logo.setBounds(10, -12, 1550, 830);
		contentPane.add(Top_logo);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == Withdraw_btn) {
	        String amtStr = Enter_amt_txt.getText().trim();
	        double balance = 0.0;

	        if (amtStr.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "Please enter the amount you want to withdraw.");
	            return;
	        }

	        if (!amtStr.matches("\\d+(\\.\\d{1,2})?")) {
	            JOptionPane.showMessageDialog(null, "Please enter a valid numeric amount.");
	            return;
	        }

	        double amt = Double.parseDouble(amtStr);

	        try {
	            JDBCConn conn = new JDBCConn();
	            ResultSet rs = conn.stmt.executeQuery("SELECT TransactionType, Amount FROM Transactions WHERE PIN_No = '" + pin + "'");

	            while (rs.next()) {
	                String type = rs.getString("TransactionType");
	                double transactionAmt = rs.getDouble("Amount");

	                if ("Deposit".equalsIgnoreCase(type)) {
	                    balance += transactionAmt;
	                } else {
	                    balance -= transactionAmt;
	                }
	            }

	            if (balance < amt) {
	                JOptionPane.showMessageDialog(null, "Insufficient balance. Current balance: Rs. " + balance);
	            } else {
	                // Insert withdrawal record
	                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	                String date = sdf.format(new Date());

	                String query = "INSERT INTO Transactions (PIN_No, \"Date\", TransactionType, Amount) VALUES (?, TO_DATE(?, 'YYYY-MM-DD'), ?, ?)";
	                PreparedStatement pstmt = conn.conn.prepareStatement(query);
	                pstmt.setString(1, pin);
	                pstmt.setString(2, date);
	                pstmt.setString(3, "Withdrawl");
	                pstmt.setDouble(4, amt);
	                pstmt.executeUpdate();

	                JOptionPane.showMessageDialog(null, "Rs. " + amt + " withdrawn successfully.");
	                dispose();
	                new Transaction(pin).setVisible(true);
	            }

	        } catch (Exception ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(null, "An error occurred during withdrawal.");
	        }

	    } else if (e.getSource() == Back_btn) {
	    	dispose();
	        new Transaction(pin).setVisible(true);
	    }
	}

	
	
}

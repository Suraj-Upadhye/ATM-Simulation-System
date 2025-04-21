import java.sql.*;
public class JDBCConn {
    Connection conn;
    Statement stmt;

    public JDBCConn() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "system", "suraj123");
            stmt = conn.createStatement();
            System.out.println("Connection Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

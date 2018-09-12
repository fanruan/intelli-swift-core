import com.fr.swift.jdbc.Driver;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

/**
 * @author yee
 * @date 2018/8/27
 */
public class JdbcTest {
    private String url = "jdbc:swift:server://127.0.0.1:7000/CUBE";
    private Connection connection;
    private String insertSQL = "insert into test_table values ";
    private StringBuffer buffer = new StringBuffer();

    @Before
    public void setUp() throws Exception {
//        Class.forName("com.fr.swift.jdbc.Driver");
        Driver driver = new Driver();
        connection = driver.connect(url, null);
        buffer.setLength(0);
        buffer.append(insertSQL);
        for (int i = 0; i < 100; i++) {
            buffer.append("(").append(i + 1000).append(", '").append(i + 1000).append("test'),");
        }
        buffer.setLength(buffer.length() - 1);
    }

    @Test
    @Ignore
    public void testQuery() throws SQLException {
        Statement statment = connection.createStatement();
        ResultSet resultSet = statment.executeQuery("select * from `10w`");
        int i = 1;
        while (resultSet.next()) {
            System.out.println(resultSet.getObject(1));
        }
    }

    @Test
    @Ignore
    public void insert() throws SQLException {
        Statement statment = connection.createStatement();
        int row = statment.executeUpdate(buffer.toString());
        assertEquals(row, 100);
    }
}

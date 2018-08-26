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
    private String url = "jdbc:swift://192.168.0.102:7000/CUBE";
    private Connection connection;

    @Before
    public void setUp() throws Exception {
//        Class.forName("com.fr.swift.jdbc.Driver");
        Driver driver = new Driver();
        connection = driver.connect(url, null);
    }

    @Test
    @Ignore
    public void testQuery() throws SQLException {
        Statement statment = connection.createStatement();
        ResultSet resultSet = statment.executeQuery("select id from test_table");
        int i = 1;
        while (resultSet.next()) {
            assertEquals(resultSet.getInt(1), i++);
        }
    }
}

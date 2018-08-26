import com.fr.swift.jdbc.Driver;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    public void testQuery() throws SQLException {
        Statement statment = connection.createStatement();
        ResultSet resultSet = statment.executeQuery("select * from test_table");
        while (resultSet.next()) {
            System.out.println(resultSet.getObject(1));
        }
    }
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class test {
    public void test() {
        try {
            Class.forName("dm.jdbc.driver.DmDriver");
            String urlString = "jdbc:dm://59.255.137.11/:5236/MPMS";
            // 定义连接用户名
            String userName = "MPMS";
            // 定义连接用户口令
            String password = "MPMS_strong";
            Connection conn = DriverManager.getConnection(urlString, userName, password);
            Statement stmt = conn.createStatement();
            String sql = "SELECT ITEM_CODE,DIVISION_CODE,ITEM_NAME,LIMIT_DAYS,EXCHANGE_TIME,VALIDITY_FLAG,NODE_CODE,ID,SYNC_FLAG,CREATE_TIME,IS_BAK,SOURCE_FLAG,DEPT_NAME,REL_ITEM_FLAG,DEPT_CODE,ITEM_SORT,DEPT_CENTER_MAP,IS_DEL,AUDIT_TYPE,UNIT_ID,UNIT_DESCRIPTION,UNIT_NUM,DEPT_CODE_CENTER,DEAL_BASIS,MATERIAL,CONDITIONS,DEAL_FLOW,WORK_TIME_ADDRESS,FEE_STANDARD FROM TZXMSPZH.APPROVE_ITEM_BASE_INFO";
            ResultSet rs = stmt.executeQuery(sql);
            int row=0;
            while (rs.next()) {
                for (int i = 1; i < 29; i++) {
                    System.out.println(rs.getString(i));
                }
                row++;
            }
            System.out.println(row);
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("error");
        }
    }

    public static void main(String[] args) {
        test test1 = new test();
        test1.test();
    }
}
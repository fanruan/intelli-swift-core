package com.fr.swift.source.etl.datamining;

import com.fr.swift.segment.Segment;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftResultSet;
import com.fr.swift.source.etl.datamining.createsegment.CreateSegment;
import com.fr.swift.source.etl.datamining.rcompile.RCompileOperator;
import com.fr.swift.source.etl.datamining.rcompile.RCompileTransferOperator;
import com.fr.swift.source.etl.datamining.rcompile.RConnector;
import junit.framework.TestCase;
import org.rosuda.REngine.Rserve.RConnection;

/**
 * Created by Handsome on 2018/4/10 0010 09:57
 */
public class TestRCompileOperator extends TestCase{
    public void testRCompile() {
        try {
            RConnector connector = new RConnector();
            RConnection conn = connector.getNewConnection(true);
            String[] columns = new String[]{"column1", "column2"};
            Segment[] segments = new Segment[2];
            segments[0] = new CreateSegment().getSegment();
            segments[1] = new CreateSegment().getSegment();
            String tableName = "table1";
            String commands = tableName + "$column1 <- "+ tableName + "$column1 + 1;";
            commands += tableName + "$column2 <- "+ tableName + "$column2 + 100;";
            commands += ";exhibit table";
            int[] columnType = new int[]{4, 4};
            String[][] str = new String[][]{{"1", "18"}, {"1", "19"}, {"1", "20"},
                    {"1", "21"}, {"1", "22"}, {"1", "22"}, {"2", "17"}, {"2", "20"}};
            init(segments, conn, tableName, null, columnType,
                    columns, false, true, false, str);//初始化
            init(segments, conn, tableName, commands, columnType, columns,
                    true, false, false, str);//测试执行R语言命令
            init(segments, conn ,tableName, null, columnType,
                    columns, false, false, true, null);//撤销上一步操作
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void init(Segment[] segments, RConnection conn, String tableName,
                      String commands, int[] columnType, String[] columns, boolean needExecute,
                      boolean init, boolean cancel, String[][] str) throws Exception{
        RCompileOperator operator = new RCompileOperator(commands, needExecute, conn, tableName,
                segments, columnType, columns, cancel, init);
        RCompileTransferOperator transfer = new RCompileTransferOperator(operator.getColumns(),
                operator.getColumnTypes(), operator.getDataList());
        SwiftResultSet rs = transfer.createResultSet(null, null, null);
        int index = 0;
        while(rs.next()) {
            Row row = rs.getRowData();
            for(int i = 0; i < 2; i ++) {
                //assertEquals(row.getValue(i).toString(), str[index][i]);
                System.out.print(row.getValue(i) + "、");
            }
            System.out.println();
            index ++;
            if(index == 8) {
                index = 0;
            }
        }
    }
}

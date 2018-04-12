package com.fr.swift.source.etl.datamining;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.etl.datamining.createsegment.CreateSegment;
import com.fr.swift.source.etl.datamining.rcompile.RExecute;
import junit.framework.TestCase;
import org.rosuda.REngine.Rserve.RConnection;

import java.sql.Types;
import java.util.List;

/**
 * Created by Handsome on 2018/3/30 0030 17:48
 */
public class TestRExecute extends TestCase {

    public void testRExecute() {
        try {
            Segment[] segments = new Segment[2];
            segments[0] = new CreateSegment().getSegment();
            segments[1] = new CreateSegment().getSegment();
            ColumnKey columnKey1 = new ColumnKey("column1");
            ColumnKey columnKey2 = new ColumnKey("column2");
            ColumnKey[] columnKeys = new ColumnKey[]{columnKey1, columnKey2};
            String tableName = "table1";
            String command1 = "table1$column1 <- table1$column1 + 1; exhibit table";
            String command2 = "table1$column1 <- table1$column2 - table1$column1";
            RConnection conn = new RConnection();
            testAssignment(conn, segments, columnKeys, tableName);
            testExecuteCommand(conn, command1, tableName);
            testNoReturnCommand(conn, command2, tableName);
            testCancelPreviousStep(conn, tableName);
            conn.eval("");
            conn.eval("");
            String temp = "library(nutshell);data(field.goals);hist(field.goals$yards)";
            RExecute.executePlot(conn, temp, tableName);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void testCancelPreviousStep(RConnection conn, String tableName) {
        RExecute.cancelPreviousStep(conn, tableName);
        testExecuteCommand(conn, "a <- 1; exhibit table", tableName);
    }

    private void testNoReturnCommand(RConnection conn, String command, String tableName) {
        RExecute.process(conn, command, tableName);
    }

    private void testAssignment(RConnection conn, Segment[] segments, ColumnKey[] columnKeys, String tableName) {
        RExecute.processAssignment(conn, segments, columnKeys, tableName);
    }

    private void testExecuteCommand(RConnection conn, String command, String tableName) {
        List list = RExecute.process(conn, command, tableName);
        int[] columnTypes = (int[]) list.get(1);
        for(int i = 2; i < list.size(); i ++) {
            int type = columnTypes[i - 2];
            switch(type) {
                case Types.DOUBLE: {
                    double[] array = (double[]) list.get(i);
                    for(int j = 0; j < array.length; j ++) {
                        System.out.print(array[j] + "、");
                    }
                    break;
                }
                case Types.INTEGER: {
                    int[] array = (int[]) list.get(i);
                    for(int j = 0; j < array.length; j ++) {
                        System.out.print(array[j] + "、");
                    }
                    break;
                }
                default: {
                    String[] array = (String[]) list.get(i);
                    for(int j = 0; j < array.length; j ++) {
                        System.out.print(array[j] + "、");
                    }
                }
            }
            System.out.println();
        }
    }

}

package com.fr.swift.source.etl.datamining.rcompile;

import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.SwiftMetaData;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;

import java.sql.Types;

/**
 * Created by Handsome on 2018/3/26 0026 11:37
 */
public class RExecute {

    public static void processAssignment(RConnection conn, Segment[] segments, ColumnKey[] columnKeys, String tableName) {
        int rowCount = getTotalRowCount(segments);
        for(int k = 0; k < columnKeys.length; k++) {
            Object[] object = new Object[rowCount];
            int currentRow = 0;
            int columnType = Types.DOUBLE;
            for(int i = 0; i < segments.length; i++) {
                DictionaryEncodedColumn getter = segments[i].getColumn(columnKeys[k]).getDictionaryEncodedColumn();
                SwiftMetaData metaData = segments[i].getMetaData();
                int tempCount = segments[i].getRowCount();
                try {
                    columnType = getColumnType(metaData, columnKeys[k].getName());
                } catch(Exception e) {
                    throw new RuntimeException(e);
                }
                for(int j = 0; j < tempCount; j++) {
                    Object ob = getter.getValue(getter.getIndexByRow(j));
                    object[i + currentRow] = ob;
                }
                currentRow += tempCount;
            }
            String columnName = columnKeys[k].getName();
            assignment(columnName, columnType, conn, object);
            StringBuffer statement = new StringBuffer();
            statement.append(tableName + " <- " + "data.frame(");
            int len = columnKeys.length;
            for(int i = 0; i < len; i++) {
                statement.append(columnKeys[i].getName());
                if(i != len - 1) {
                    statement.append(",");
                }
            }
            statement.append(")");
            try {
                conn.eval(statement.toString());
            } catch(REngineException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static double[][] processRequest(RConnection conn, String commands) {

        return null;
    }

    private static int getColumnType(SwiftMetaData metaData, String columnName) throws Exception{
        int index = 0;
        for(int i = 1; i < metaData.getColumnCount(); i ++) {
            if(metaData.getColumnName(i).equals(columnName)) {
                index = i;
                break;
            }
        }
        if(index > 0) {
            return index;
        } else {
            throw new RuntimeException("not found column:" + columnName);
        }
    }

    private static void assignment(String columnName, int columnType, RConnection conn, Object[] object) {
        try {
            switch(columnType) {
                case Types.DECIMAL:
                case Types.NUMERIC:
                case Types.REAL:
                case Types.DOUBLE:
                case Types.FLOAT: {
                    conn.assign(columnName, parseDoubleArray(object));
                    break;
                }
                case Types.TINYINT:
                case Types.SMALLINT:
                case Types.INTEGER: {
                    conn.assign(columnName, parseIntArray(object));
                    break;
                }
                case Types.BIT: {
                    conn.assign(columnName, parseByteArray(object));
                    break;
                }
                default: {
                    conn.assign(columnName, parseStringArray(object));
                }
            }
        } catch(REngineException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] parseByteArray(Object[] obs) {
        byte[] array = new byte[obs.length];
        for(int i = 0; i < obs.length; i++) {
            array[i] = Byte.parseByte(obs[i].toString());
        }
        return array;
    }

    private static int[] parseIntArray(Object[] obs) {
        int[] array = new int[obs.length];
        for(int i = 0; i < obs.length; i++) {
            array[i] = Integer.parseInt(obs[i].toString());
        }
        return array;
    }

    private static double[] parseDoubleArray(Object[] obs) {
        double[] array = new double[obs.length];
        for(int i = 0; i < obs.length; i++) {
            array[i] = Double.parseDouble(obs[i].toString());
        }
        return array;
    }

    private static String[] parseStringArray(Object[] obs) {
        String[] array = new String[obs.length];
        for(int i = 0; i < obs.length; i++) {
            array[i] = obs[i].toString();
        }
        return array;
    }

    private static int getTotalRowCount(Segment[] segments) {
        int rowCount = 0;
        for(int i = 0; i < segments.length; i++) {
            rowCount += segments[i].getRowCount();
        }
        return rowCount;
    }
}

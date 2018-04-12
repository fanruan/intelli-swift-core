package com.fr.swift.source.etl.datamining.rcompile;

import com.finebi.base.stable.StableManager;
import com.finebi.conf.service.rlink.RLogContext;
import com.fr.swift.log.SwiftLogger;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.segment.column.DictionaryEncodedColumn;
import com.fr.swift.source.SwiftMetaData;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.awt.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Handsome on 2018/3/26 0026 11:37
 */
public class RExecute {

    private static final SwiftLogger LOGGER = SwiftLoggers.getLogger(RExecute.class);

    public final static String TEMPVALUE = "DFHBKD_NLOP_HSYG_DMDJS_THJ";//随便弄一个，为了防止重名；这种应该不会重了吧
    public final static String PREVIOUS = "DFHBKH_NLOP_HTYG_DMDJS_TBJ_HKQOP90J";//上一步表
    public final static String NEXT = "DFHCKH_NLOP_HWYG_DMGJS_TBJ_HKQOP80J";//下一步表
    public final static String EXHIBIT = "exhibit table";
    public final static String CODESPLIT = ";";

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
                    object[j + currentRow] = ob;
                }
                currentRow += tempCount;
            }
            String columnName = columnKeys[k].getName();
            assignment(columnName, columnType, conn, object);
        }
        StringBuffer statement = new StringBuffer();
        statement.append(tableName + " <- " + "data.frame(");
        int len = columnKeys.length;
        for(int i = 0; i < len; i++) {
            statement.append(columnKeys[i].getName());
            if(i != len - 1) {
                statement.append(",");
            }
        }
        statement.append(",stringsAsFactors = FALSE)");
        try {
            conn.eval(statement.toString());
            conn.eval(PREVIOUS + "<- " + tableName);
            conn.eval(NEXT + "<- " + tableName);
        } catch(REngineException e) {
            throw new RuntimeException(e);
        }
    }

    public static List getPreviousDataList(RConnection conn) {
        try {
            REXP rexp = conn.eval(PREVIOUS);
            return convertingResultSet(rexp);
        } catch(RserveException e) {
            LOGGER.error("failed to get previous result set!", e);
        }
        return null;
    }


    public static List process(RConnection conn, String commands, String tableName) {
        String[] str = commands.split(CODESPLIT);
        if(str.length > 0) {
            if(str.length > 1) {
                if(EXHIBIT.equals(str[str.length - 1].trim())) {
                    String[] temp = Arrays.copyOfRange(str, 0, str.length - 1);
                    return processRequest(conn, temp, tableName);
                } else {
                    processTempRequest(conn, str, tableName);
                }
            } else {
                processTempRequest(conn, str, tableName);
            }
        }
        return null;
    }


    private static REXP processCommands(RConnection conn, String[] commands, boolean returnTable, String tableName) {
        RLogFactory logFactory = RLogFactory.getInstance();
        REXP rexp = null;
        String intermediate = "DFHCKH_NLOP_HWYG_DMGJS_TBJ_HKQOX09K";
        if(returnTable) {
            try {
                conn.eval(intermediate + " <- " + PREVIOUS);
                conn.eval(PREVIOUS + " <- " + NEXT);
            } catch (RserveException e) {
                LOGGER.error("This R command" +" executed failed!", e);
            }
        }
        for(int i = 0; i < commands.length; i++) {
            String expression = commands[i].trim();
            if(expression.length() > 0) {
                StringBuffer sb = new StringBuffer();
                sb.append(TEMPVALUE + "<- (" + expression + ")");
                try {
                    rexp = conn.eval(sb.toString());
                    REXP tempRexp = conn.eval("capture.output(" + TEMPVALUE + ")");
                    String[] log = tempRexp.asStrings();
                    for(int k = 0; k < log.length; k ++) {
                        LOGGER.info(log[k]);
                        logFactory.writeLog(log[k]);
                    }
                } catch(Exception e) {
                    LOGGER.error("This R command: " + expression + " executed failed!", e);
                    if(returnTable) {
                        try {
                            conn.eval(PREVIOUS + " <- " + intermediate);
                        } catch (RserveException ee) {
                            LOGGER.error("This R command: " + PREVIOUS + " <- " + intermediate +" executed failed!", ee);
                        }
                    }
                    return null;
                }
            }
        }
        RLogContext context = StableManager.getContext().getObject("rLogContext");
        context.setService(logFactory);
        try {
            if(returnTable) {
                conn.eval(NEXT + " <- " + tableName);
                return conn.eval(NEXT);
            }
        } catch(RserveException e) {
            LOGGER.error("This R command: " + "\"" + NEXT + " <- " + tableName + "\"" +" executed failed!", e);
        }
        return rexp;
    }

    /**
     * 处理一些不需要返回结果的R语句
     */
    private static void processTempRequest(RConnection conn, String[] commands, String tableName) {
        processCommands(conn, commands, false, tableName);
    }


    /**
     * 以list的形式返回一张表的数据，list中的第一项是新表的列名数组，第二项是字段类型，剩下的是各列的数据
     */
    private static List processRequest(RConnection conn, String[] commands, String tableName) {
        REXP rexp = processCommands(conn, commands, true, tableName);
        if(null == rexp) {
            return null;
        }
        return convertingResultSet(rexp);
    }

    public static List cancelPreviousStep(RConnection conn, String tableName) {
        try {
            conn.eval(NEXT + " <- " + PREVIOUS);
            REXP rexp = conn.eval(tableName + " <- " + NEXT);
            return convertingResultSet(rexp);
        } catch(RserveException e) {
            LOGGER.error("Cancelling previous step failed", e);
        }
        return null;
    }

    public static Image executePlot(RConnection conn, String commands, String tableName) {
        try {
            String[] str = commands.split(CODESPLIT);
            conn.setStringEncoding("utf8");
            conn.eval("setwd('c://')");
            REXP xp = conn.eval("jpeg('plot.jpg',quality=90)");
            processTempRequest(conn, str, tableName);
            conn.eval("dev.off()");
            conn.eval("stream=readBin('plot.jpg','raw',2048*2048)");
            conn.eval("unlink('plot.jpg')");//删除临时文件
            xp = conn.eval("stream");
            Image img = Toolkit.getDefaultToolkit().createImage(xp.asBytes());
            return img;
        } catch(Exception e) {
            LOGGER.error("failed to plot!", e);
        }
        return null;
    }

    public static void executeStringFunction(RConnection conn, String function, String[] parameters) {
        String request = "try(" + function + "(";
        for (String parameter : parameters) {
            request += parameter + ",";
        }
        request = request.substring(0, request.length() - 1);
        request += "))";
        REXP xp = null;
        try {
            xp = conn.parseAndEval(request);
        } catch(Exception e) {
            LOGGER.error("failed to execute function: " + function);
        }
        try {
            if (xp.inherits("try-error")) {
                String message = "failed to execute function '" + function + "'; \nrequest: " + request
                        + "; \nError: " + xp.asString();
                LOGGER.error(message);
            }
        } catch(REXPMismatchException e) {

        }
    }

    private static List convertingResultSet(REXP rexp) {
        List dataList = new ArrayList();
        if(rexp.isVector()) {
            try {
                RList rList = rexp.asList();
                String[] columns = rList.keys();
                int[] columnTypes = new int[columns.length];
                dataList.add(columns);
                dataList.add(columnTypes);
                for(int i = 0; i < columns.length; i++) {
                    REXP temp = (REXP)rList.get(columns[i]);
                    if (temp.isInteger()) {
                        dataList.add(temp.asIntegers());
                        columnTypes[i] = Types.INTEGER;
                    }else if(temp.isNumeric()) {
                        dataList.add(temp.asDoubles());
                        columnTypes[i] = Types.DOUBLE;
                    }  else {
                        dataList.add(temp.asStrings());
                        columnTypes[i] = Types.VARCHAR;
                    }
                }
            } catch(REXPMismatchException e) {
                LOGGER.error("Converting R data to Java data failed!", e);
                return null;
            }
        }
        return dataList;
    }

    private static int getColumnType(SwiftMetaData metaData, String columnName) throws Exception{
        int index = 0;
        for(int i = 1; i <= metaData.getColumnCount(); i ++) {
            if(metaData.getColumnName(i).equals(columnName)) {
                index = i;
                break;
            }
        }
        if(index > 0) {
            return metaData.getColumn(index).getType();
        } else {
            throw new RuntimeException("not found column:" + columnName);
        }
    }

    public static boolean executeVoidFunction(RConnection conn, String function, String[] parameters) {
        // create request
        try {
            String request = "try(" + function + "(";
            if (parameters.length != 0) {
                for (String parameter : parameters) {
                    request += parameter + ",";
                }
                // remove last ","
                request = request.substring(0, request.length() - 1);
                request += "))";
            }
            // execute function
            REXP xp = conn.parseAndEval(request);
            if (xp.inherits("try-error")) {
                //close(conn);
                //throw new IOException("failed to execute function '" + function + "'; \nrequest: " + request
                        //+ "; \nError: " + xp.asString());
            }
            return true;
        }
        catch (Exception e) {
            return false;
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

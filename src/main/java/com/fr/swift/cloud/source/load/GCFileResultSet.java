package com.fr.swift.cloud.source.load;

import com.fr.swift.cloud.source.CloudTableType;
import com.fr.swift.cloud.source.table.GCRecord;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/4/26
 *
 * @author Lucifer
 * @description
 */
// TODO: 2019/5/10 by lucifer gc相关待完事
public class GCFileResultSet implements CloudResultSet {

    private LineParser parser;
    private GCRecord gcRecord;
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private SwiftMetaData metaData;
    private List<Row> rowList = new ArrayList<Row>();
    private int currentRow = 0;

    public GCFileResultSet(GCRecord gcRecord, SwiftMetaData metadata) {
        this.metaData = metadata;
        this.gcRecord = gcRecord;
        this.parser = gcRecord.getParser();
        String line;
        try {
            StringBuilder currentBuilder = null;
            int currentFile = 0;
            while (currentFile < gcRecord.getFiles().length) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(gcRecord.getFiles()[currentFile]), "utf8"));
                try {
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("minor GC") || line.contains("major GC")) {
                            currentBuilder = new StringBuilder();
                        }
                        if (currentBuilder == null) {
                            continue;
                        }
                        currentBuilder.append(line);
                        if (line.contains("duration")) {
                            int duration = currentBuilder.indexOf("duration");
                            String time = currentBuilder.substring(0, 25);

                            // TODO: 2019/5/10 by lucifer 改使用
                            LocalDateTime localDateTime = LocalDateTime.parse(time.replace("EDT", " "), dtf);
                            ZoneId zone = ZoneId.systemDefault();
                            Instant instant = localDateTime.atZone(zone).toInstant();
                            Date date = Date.from(instant);
                            rowList.add(new ListBasedRow(date.getTime()
                                    , (currentBuilder.toString().contains("minor GC") ? "minor GC" : "major GC")
                                    , Long.valueOf(currentBuilder.toString().substring(duration + 9, currentBuilder.length() - 2))));
                            currentBuilder = null;
                        }
                    }
                } finally {
                    reader.close();
                }
                currentFile++;
            }
        } catch (Exception e) {
            SwiftLoggers.getLogger().error(e);
        }
    }

    @Override
    public int getFetchSize() {
        return 0;
    }

    @Override
    public SwiftMetaData getMetaData() throws SQLException {
        return metaData;
    }

    @Override
    public boolean hasNext() throws SQLException {
        return rowList.size() > currentRow;
    }

    @Override
    public Row getNextRow() throws SQLException {
        Map<String, Object> row = parser.parseToMap(rowList.get(currentRow++));
        return new ListBasedRow(new ArrayList(row.values()));

    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public CloudTableType getTableType() {
        return CloudTableType.GC;
    }

//    public static void main(String[] args) throws IOException {
//        File[] files = new File[]{
//                new File("D:\\swift-new\\analyseSourceData\\lucifer\\app1\\201904\\fanruan.gc.log.2019-04-22")
//                , new File("D:\\swift-new\\analyseSourceData\\lucifer\\app1\\201904\\fanruan.gc.log.2019-04-23")};
//        String line;
//        try {
//            StringBuilder currentBuilder = null;
//            int currentFile = 0;
//            while (currentFile < files.length) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(files[currentFile]), "utf8"));
//                while ((line = reader.readLine()) != null) {
//                    if (line.contains("minor GC") || line.contains("major GC")) {
//                        currentBuilder = new StringBuilder();
//                    }
//                    if (currentBuilder == null) {
//                        continue;
//                    }
//                    currentBuilder.append(line);
//                    if (line.contains("duration")) {
//                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                        int duration = currentBuilder.indexOf("duration");
//
//                        String time = currentBuilder.substring(0, 25);
//                        Date date = sdf.parse(time.replace("EDT", " "));
//                        System.out.println(date.getTime());
//                        System.out.println("type:" + (currentBuilder.toString().contains("minor GC") ? "minor GC" : "major GC"));
//                        System.out.println("duration:" + currentBuilder.toString().substring(duration + 9, currentBuilder.length() - 2));
//                        currentBuilder = null;
//                        System.out.println("==========");
//                    }
//                }
//                currentFile++;
//            }
//        } catch (Exception e) {
//            SwiftLoggers.getLogger().error(e);
//        }
//        return;
//    }
}

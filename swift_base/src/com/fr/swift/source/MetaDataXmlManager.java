package com.fr.swift.source;

import com.fr.base.FRContext;
import com.fr.file.XMLFileManager;
import com.fr.stable.StringUtils;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLReadable;
import com.fr.stable.xml.XMLableReader;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.util.Crasher;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yee
 * @date 2017/12/27
 *
 * fixme 配置可用10.0持久化了
 */
public class MetaDataXmlManager extends XMLFileManager {

    private static final String META_DATA_FILE_NAME = "sourceMetaData.xml";
    private static final String META_DATA_ROOT_KEY = "SourceMetaData";
    private ConcurrentHashMap<SourceKey, SwiftMetaData> sourceMetaData = new ConcurrentHashMap<SourceKey, SwiftMetaData>();
    private volatile static MetaDataXmlManager manager;

    private MetaDataXmlManager() {
        readXMLFile();
    }

    public static MetaDataXmlManager getManager() {
        if (null == manager) {
            synchronized (MetaDataXmlManager.class) {
                if (null == manager) {
                    manager = new MetaDataXmlManager();
                }
            }
        }
        return manager;
    }

    @Override
    public String fileName() {
        return META_DATA_FILE_NAME;
    }

    @Override
    public void readXML(XMLableReader reader) {
        if (reader.isChildNode()) {
            String tag = reader.getTagName();
            if ("MetaData".equals(tag)) {
                List<SwiftMetaDataColumn> columns = new ArrayList<SwiftMetaDataColumn>();
                SourceKey sourceKey = new SourceKey(reader.getAttrAsString("id", StringUtils.EMPTY));
                String tableName = reader.getAttrAsString("tableName", StringUtils.EMPTY);
                String schema = reader.getAttrAsString("schema", StringUtils.EMPTY);
                readMetaData(reader, columns);
                SwiftMetaDataImpl metaData = new SwiftMetaDataImpl(tableName, StringUtils.EMPTY, schema, columns);
                sourceMetaData.put(sourceKey, metaData);
            }
        }
    }

    private void readMetaData(XMLableReader reader, final List<SwiftMetaDataColumn> columns) {
        reader.readXMLObject(new XMLReadable() {

            @Override
            public void readXML(XMLableReader reader) {
                if ("column".equals(reader.getTagName())) {
                    String columnName = reader.getAttrAsString("columnName", StringUtils.EMPTY);
                    String remark = reader.getAttrAsString("remark", StringUtils.EMPTY);
                    String schema = reader.getAttrAsString("schema", StringUtils.EMPTY);
                    int columnType = reader.getAttrAsInt("columnType", Types.DOUBLE);
                    int precision = reader.getAttrAsInt("precision", 0);
                    int scale = reader.getAttrAsInt("scale", 0);
                    MetaDataColumn column = new MetaDataColumn(columnName, remark, columnType, precision, scale);
                    columns.add(column);
                }
            }
        });
    }

    @Override
    public void writeXML(XMLPrintWriter writer) {
        writer.startTAG(META_DATA_ROOT_KEY);
        Iterator<Map.Entry<SourceKey, SwiftMetaData>> iterator = sourceMetaData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<SourceKey, SwiftMetaData> entry = iterator.next();
            SwiftMetaData data = entry.getValue();
            try {
                writer.startTAG("MetaData");
                writer.attr("tableName", data.getTableName());
                writer.attr("schema", data.getSchemaName());
                writer.attr("id", entry.getKey().getId());
                for (int i = 1, len = data.getColumnCount(); i <= len; i++) {
                    writer.startTAG("column");
                    writer.attr("columnName", data.getColumnName(i));
                    writer.attr("precision", data.getPrecision(i));
                    writer.attr("columnType", data.getColumnType(i));
                    writer.attr("scale", data.getScale(i));
                    writer.attr("schema", data.getSchemaName());
                    writer.attr("remark", data.getColumnRemark(i));
                    writer.end();
                }
                writer.end();
            } catch (SwiftMetaDataException e) {
                Crasher.crash(e);
            }
        }
        writer.end();
    }

    public void putMetaData(SourceKey key, SwiftMetaData metaData) {
        SwiftMetaData data = sourceMetaData.get(key);
        if (null == data) {
            sourceMetaData.put(key, metaData);
            try {
                FRContext.getCurrentEnv().writeResource(this);
            } catch (Exception e) {
                Crasher.crash(e);
            }
        }
    }

    public SwiftMetaData getMetaData(SourceKey key) {
        return sourceMetaData.get(key);
    }
}

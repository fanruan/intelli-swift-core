package com.fr.swift.cloud.load;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fr.swift.base.json.JsonBuilder;
import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.base.meta.SwiftMetaDataBean;
import com.fr.swift.db.SwiftDatabase;
import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;
import com.fr.swift.util.Crasher;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This class created on 2019/5/10
 *
 * @author Lucifer
 * @description
 */
public class CloudVersionProperty {

    /**
     * key:version  field:filenameHead   value:metadata
     */
    private Map<String, Map<String, SwiftMetaData>> metadataMap;

    private CloudVersionProperty() {
        metadataMap = new HashMap<String, Map<String, SwiftMetaData>>();
    }

    private static final CloudVersionProperty INSTANCE = new CloudVersionProperty();

    public static CloudVersionProperty getProperty() {
        return INSTANCE;
    }

    public Map<String, SwiftMetaData> getMetadataMapByVersion(String version) throws Exception {
        if (!metadataMap.containsKey(version)) {
            metadataMap.put(version, loadMetaDataMataByVersion(version));
        }
        return metadataMap.get(version);
    }

    private Map<String, SwiftMetaData> loadMetaDataMataByVersion(String version) throws Exception {
        Properties properties = new Properties();
        InputStream versionInput = SwiftProperty.class.getClassLoader().getResourceAsStream(String.format("cloud.v%s.properties", version));
        try {
            properties.load(versionInput);
            if (properties.isEmpty()) {
                Crasher.crash(String.format("version %s's properties is not exist or empty!", version));
            }
        } catch (Exception e) {
            Crasher.crash(String.format("Load version %s's properties failed!", version), e);
        }
        Set<Object> keySet = properties.keySet();
        Map<String, SwiftMetaData> metaDataMap = new HashMap<>();
        for (Object keyObj : keySet) {
            String key = (String) keyObj;
            if (key.endsWith("filename") || key.endsWith("tablename")) {
                continue;
            }
            String filenameKey = key + ".filename";
            String tablenameKey = key + ".tablename";

            String filename = properties.getProperty(filenameKey);
            String tablename = properties.getProperty(tablenameKey);
            String metadataJson = properties.getProperty(key);

            ObjectMapper objectMapper = new ObjectMapper();
            List<Map> columnJsonList = objectMapper.readValue(metadataJson, List.class);
            List<SwiftMetaDataColumn> metaDataColumnList = new ArrayList<SwiftMetaDataColumn>();
            for (Map columnJson : columnJsonList) {
                metaDataColumnList.add(JsonBuilder.readValue(columnJson, MetaDataColumnBean.class));
            }
            SwiftMetaData metaData = new SwiftMetaDataBean(SwiftDatabase.CUBE, tablename, metaDataColumnList);
            metaDataMap.put(filename, metaData);
        }
        return metaDataMap;
    }

    public static void main(String[] args) throws Exception {
        Map<String, SwiftMetaData> a = CloudVersionProperty.getProperty().getMetadataMapByVersion("1.0");
        Map<String, SwiftMetaData> b = CloudVersionProperty.getProperty().getMetadataMapByVersion("2.0");
    }

}
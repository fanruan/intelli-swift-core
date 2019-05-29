package com.fr.swift.cloud.load;

import com.fr.swift.property.SwiftProperty;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.util.Crasher;
import org.ho.yaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private Map<String, Map<String, SwiftMetaData>> metadatasMap;

    private CloudVersionProperty() {
        metadatasMap = new HashMap<String, Map<String, SwiftMetaData>>();
    }

    private static final CloudVersionProperty INSTANCE = new CloudVersionProperty();

    public static CloudVersionProperty getProperty() {
        return INSTANCE;
    }

    public Map<String, SwiftMetaData> getMetadataMapByVersion(String version) throws Exception {
        if (!metadatasMap.containsKey(version)) {
            Map<String, SwiftMetaData> metaDataMap = loadMetaDataMataByVersion(version);
            if (metaDataMap != null) {
                metadatasMap.put(version, metaDataMap);
            }
        }
        return metadatasMap.get(version);
    }

    private Map<String, SwiftMetaData> loadMetaDataMataByVersion(String version) throws Exception {
        InputStream versionInput = SwiftProperty.class.getClassLoader().getResourceAsStream(String.format("cloud.v%s.yaml", version));
        try {
            Map<String, SwiftMetaData> metaDataMap = Yaml.loadType(versionInput, LinkedHashMap.class);
            if (metaDataMap.isEmpty()) {
                Crasher.crash(String.format("version %s's properties is not exist or empty!", version));
            }
            return metaDataMap;
        } catch (Exception e) {
            Crasher.crash(String.format("Load version %s's properties failed!", version), e);
            return null;
        }
    }
}
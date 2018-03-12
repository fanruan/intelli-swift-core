package com.fr.swift.config;

import com.fr.swift.config.pojo.MetaDataColumnPojo;
import com.fr.swift.config.pojo.SwiftMetaDataPojo;
import com.fr.swift.config.unique.MetaDataColumnUnique;
import com.fr.swift.config.unique.SwiftMetaDataUnique;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataCreater {
    public static IMetaData getMA() {
        List<MetaDataColumnUnique> fieldList = new ArrayList<>();
        fieldList.add(new MetaDataColumnUnique(0, "A1", "a1", 0, 0));
        fieldList.add(new MetaDataColumnUnique(1, "A2", "a2", 1, 1));

        IMetaData metaData = new SwiftMetaDataUnique("1", "A", "a", fieldList);
        return metaData;
    }

    public static IMetaData getMAModify() {
        List<MetaDataColumnUnique> fieldList = new ArrayList<>();
        fieldList.add(new MetaDataColumnUnique(0, "A1", "a1", 0, 0));
        fieldList.add(new MetaDataColumnUnique(1, "A2", "a2", 1, 1));

        IMetaData metaData = new SwiftMetaDataUnique("111", "A", "aaa", fieldList);
        return metaData;
    }
}

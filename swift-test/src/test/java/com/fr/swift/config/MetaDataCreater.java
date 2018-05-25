package com.fr.swift.config;

import com.fr.swift.config.conf.bean.MetaDataColumnBean;
import com.fr.swift.config.conf.bean.SwiftMetaDataBean;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Lucifer
 * @Description:
 * @Date: Created in 2018-3-8
 */
public class MetaDataCreater {
    public static SwiftMetaData getMA() {
        List<SwiftMetaDataColumn> fieldList = new ArrayList<>();
        fieldList.add(new MetaDataColumnBean("A1", "", 0, 0, 0, ""));
        fieldList.add(new MetaDataColumnBean("A2", "", 1, 1, 1, ""));


        SwiftMetaData metaData = new SwiftMetaDataBean("1", "A", "a", fieldList);
        return metaData;
    }

    public static SwiftMetaData getMAModify() {
        List<SwiftMetaDataColumn> fieldList = new ArrayList<>();
        fieldList.add(new MetaDataColumnBean("A1", "a1", 0, 0, 0, ""));
        fieldList.add(new MetaDataColumnBean("A2", "a2", 1, 1, 0, ""));

        SwiftMetaData metaData = new SwiftMetaDataBean("111", "A", "aaa", fieldList);
        return metaData;
    }
}

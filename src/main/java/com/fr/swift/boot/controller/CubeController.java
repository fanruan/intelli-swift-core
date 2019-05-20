package com.fr.swift.boot.controller;

import com.fr.swift.cloud.load.CloudVersionProperty;
import com.fr.swift.cube.io.Types;
import com.fr.swift.cube.io.location.IResourceLocation;
import com.fr.swift.cube.io.location.ResourceLocation;
import com.fr.swift.log.SwiftLoggers;
import com.fr.swift.segment.CompatibleHistorySegment;
import com.fr.swift.segment.Segment;
import com.fr.swift.segment.column.Column;
import com.fr.swift.segment.column.ColumnKey;
import com.fr.swift.source.SwiftMetaData;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class created on 2019/5/15
 *
 * @author Lucifer
 * @description
 */
@RestController
public class CubeController {

    @ResponseBody
    @RequestMapping(value = "/cloud/cube/query", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public Object queryCube(@RequestBody Map<String, String> map) throws Exception {
        String fileName = String.valueOf(map.get("fileName"));
        Types.StoreType storeType = Types.StoreType.valueOf(map.get("storeType"));
        String cubePath = map.get("cubePath");
        //cubes\\0\\execution\\seg101
        IResourceLocation location = new ResourceLocation(cubePath, storeType);

        Map<String, SwiftMetaData> metadataMap = CloudVersionProperty.getProperty().getMetadataMapByVersion("2.0");
        SwiftMetaData metaData = metadataMap.get(fileName);
        Segment segment = new CompatibleHistorySegment(location, metaData);
        List<Column> columnList = new ArrayList<Column>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columnList.add(segment.getColumn(new ColumnKey(metaData.getColumn(i).getName())));
        }
        int rowCount = segment.getRowCount() > 200 ? 200 : segment.getRowCount();
        List<String> columnDatas = new ArrayList<String>();
        for (int i = 0; i < rowCount; i++) {
            StringBuilder columnData = new StringBuilder();
            for (Column column : columnList) {
                columnData.append(column.getDetailColumn().get(i)).append("  ;  ");
            }
            columnDatas.add(columnData.toString());
            SwiftLoggers.getLogger().info(columnData.toString());
        }
        return columnDatas;
    }
}

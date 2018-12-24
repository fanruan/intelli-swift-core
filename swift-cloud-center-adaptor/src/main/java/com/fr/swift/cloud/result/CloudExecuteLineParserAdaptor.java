package com.fr.swift.cloud.result;

import com.fr.swift.cloud.entity.CloudExecute;
import com.fr.swift.exception.meta.SwiftMetaDataException;
import com.fr.swift.source.ListBasedRow;
import com.fr.swift.source.Row;
import com.fr.swift.source.SwiftMetaData;
import com.fr.swift.source.resultset.LineParser;
import com.fr.swift.util.Crasher;
import com.fr.swift.util.JpaAdaptor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yee
 * @date 2018-12-21
 */
public class CloudExecuteLineParserAdaptor implements LineParser.LineParserAdaptor {

    private int consumeIdx;
    private int sqlTimeIdx;

    public CloudExecuteLineParserAdaptor() {
        SwiftMetaData metaData = JpaAdaptor.adapt(CloudExecute.class);
        try {
            consumeIdx = metaData.getColumnIndex("consume") - 1;
            sqlTimeIdx = metaData.getColumnIndex("sqlTime") - 1;
        } catch (SwiftMetaDataException e) {
            Crasher.crash(e);
        }
    }

    @Override
    public Row adapt(Row row) {
        Long consume = row.getValue(consumeIdx);
        Long sqlTime = row.getValue(sqlTimeIdx);
        consume = null == consume ? 0 : consume;
        sqlTime = null == sqlTime ? 0 : sqlTime;
        List data = new ArrayList();
        for (int i = 0; i < row.getSize(); i++) {
            data.add(row.getValue(i));
        }
        data.add(consume - sqlTime);
        return new ListBasedRow(data);
    }
}

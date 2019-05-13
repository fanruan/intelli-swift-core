package com.fr.swift.cloud.source.table;

import com.fr.swift.base.meta.MetaDataColumnBean;
import com.fr.swift.source.SwiftMetaDataColumn;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by lyon on 2019/2/28.
 */
@Deprecated
public class TemplateInfo extends CSVBaseTable {

    public static final String tableName = "template_info";

    public static final SwiftMetaDataColumn time = new MetaDataColumnBean("time", Types.DATE);
    public static final SwiftMetaDataColumn id = new MetaDataColumnBean("id", Types.VARCHAR);
    public static final SwiftMetaDataColumn tId = new MetaDataColumnBean("tId", Types.VARCHAR);
    public static final SwiftMetaDataColumn tName = new MetaDataColumnBean("tName", Types.VARCHAR);

    public static final SwiftMetaDataColumn condition = new MetaDataColumnBean("condition", Types.BIGINT);
    public static final SwiftMetaDataColumn formula = new MetaDataColumnBean("formula", Types.BIGINT);
    public static final SwiftMetaDataColumn sheet = new MetaDataColumnBean("sheet", Types.BIGINT);
    public static final SwiftMetaDataColumn ds = new MetaDataColumnBean("ds", Types.BIGINT);
    public static final SwiftMetaDataColumn complexFormula = new MetaDataColumnBean("complexFormula", Types.BIGINT);
    public static final SwiftMetaDataColumn submission = new MetaDataColumnBean("submission", Types.BIGINT);
    public static final SwiftMetaDataColumn frozen = new MetaDataColumnBean("frozen", Types.BIGINT);
    public static final SwiftMetaDataColumn foldTree = new MetaDataColumnBean("foldTree", Types.BIGINT);
    public static final SwiftMetaDataColumn widget = new MetaDataColumnBean("widget", Types.BIGINT);
    public static final SwiftMetaDataColumn templateSize = new MetaDataColumnBean("templateSize", Types.BIGINT);
    public static final SwiftMetaDataColumn imageSize = new MetaDataColumnBean("imageSize", Types.BIGINT);

    public static final SwiftMetaDataColumn execution0 = new MetaDataColumnBean("execution0", Types.BIGINT);
    public static final SwiftMetaDataColumn execution1 = new MetaDataColumnBean("execution1", Types.BIGINT);
    public static final SwiftMetaDataColumn execution2 = new MetaDataColumnBean("execution2", Types.BIGINT);
    public static final SwiftMetaDataColumn execution3 = new MetaDataColumnBean("execution3", Types.BIGINT);
    public static final SwiftMetaDataColumn execution4 = new MetaDataColumnBean("execution4", Types.BIGINT);

    public static final SwiftMetaDataColumn memory0 = new MetaDataColumnBean("memory0", Types.BIGINT);
    public static final SwiftMetaDataColumn memory1 = new MetaDataColumnBean("memory1", Types.BIGINT);
    public static final SwiftMetaDataColumn memory2 = new MetaDataColumnBean("memory2", Types.BIGINT);
    public static final SwiftMetaDataColumn memory3 = new MetaDataColumnBean("memory3", Types.BIGINT);
    public static final SwiftMetaDataColumn memory4 = new MetaDataColumnBean("memory4", Types.BIGINT);

    public static final SwiftMetaDataColumn sql0 = new MetaDataColumnBean("sql0", Types.BIGINT);
    public static final SwiftMetaDataColumn sql1 = new MetaDataColumnBean("sql1", Types.BIGINT);
    public static final SwiftMetaDataColumn sql2 = new MetaDataColumnBean("sql2", Types.BIGINT);
    public static final SwiftMetaDataColumn sql3 = new MetaDataColumnBean("sql3", Types.BIGINT);
    public static final SwiftMetaDataColumn sql4 = new MetaDataColumnBean("sql4", Types.BIGINT);

    public static final SwiftMetaDataColumn appId = new MetaDataColumnBean("appId", Types.VARCHAR);
    public static final SwiftMetaDataColumn yearMonth = new MetaDataColumnBean("yearMonth", Types.VARCHAR);

    {
        rawColumns.addAll(Arrays.asList(
                time, id, tId, tName, condition, formula, sheet, ds, complexFormula, submission, frozen, foldTree,
                widget, templateSize, imageSize, execution0, execution1, execution2, execution3, execution4,
                memory0, memory1, memory2, memory3, memory4, sql0, sql1, sql2, sql3, sql4
        ));
        rawColumns = Collections.unmodifiableList(rawColumns);
    }

    public TemplateInfo(String appId, String yearMonth) {
        super(appId, yearMonth);
    }

    @Override
    public String getTableName() {
        return tableName;
    }
}

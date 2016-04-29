package com.fr.bi.fe.engine.share;

import com.fr.data.core.db.tableObject.ColumnSize;
import com.fr.data.dao.CommonFieldColumnMapper;
import com.fr.data.dao.FieldColumnMapper;
import com.fr.data.dao.ObjectTableMapper;
import com.fr.data.dao.PrimaryKeyFCMapper;

import java.sql.Types;

/**
 * Created by sheldon on 15-1-20.
 * fine-excel的保存分享node的表格
 */
public class BIExcelTableMapper {
    public static class BI_SHARE_OTHER_REPORT_NODE {
//        private long user_id;         //分享的登录用户id
//        private long mode_id;         //分享的模板id
//        private boolean isShared = true;     //当前模板状态是否分享的
//        private String shared_id;
        public static final String FIELD_SHARED_OTHER_MODE_ID = "mode_id";
        public static final String FIELD_SHARED_OTHER_USER_ID = "user_id";
        public static final String FIELD_SHARED_OTHER_STATE = "isShared";
        public static final String FIELD_SHARED_OTHER_SHARE_ID = "shared_id";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(
                BIShareOtherNode.class,
                new FieldColumnMapper[] {
                        new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                        new CommonFieldColumnMapper(FIELD_SHARED_OTHER_MODE_ID, Types.BIGINT, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_SHARED_OTHER_USER_ID, Types.BIGINT, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_SHARED_OTHER_STATE, Types.BOOLEAN, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_SHARED_OTHER_SHARE_ID, Types.LONGVARCHAR, new ColumnSize(10), false)
                }
        );
    }
}
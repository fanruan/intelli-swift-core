package com.fr.bi.fs;

import com.fr.bi.conf.tablelock.BIConfTableLock;
import com.fr.data.core.db.tableObject.ColumnSize;
import com.fr.data.dao.*;
import com.fr.fs.base.entity.User;

import java.sql.Types;

/**
 * bi相关的表
 *
 * @author frank
 */
public class BITableMapper {

    public static class BI_REPORT_NODE_LOCK {
//        public static final String REPORT_NODE_LOCK_ID = "reportNodeLockId";
        public static final String FIELD_EDITUSERID = "currentEditUserId";
        public static final String FIELD_USERID = "userId";
        public static final String REPORT_ID = "reportId";
        public static final String SESSIONID = "sessionId";
        public static final String LOCKED_TIME = "lockedTime";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(BIReportNodeLock.class, new FieldColumnMapper[]{
                new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                new CommonFieldColumnMapper(FIELD_USERID, Types.BIGINT, new ColumnSize(10), false),
                new CommonFieldColumnMapper(FIELD_EDITUSERID, Types.BIGINT, new ColumnSize(10), false),
                new CommonFieldColumnMapper(REPORT_ID, Types.BIGINT, new ColumnSize(10), false),
                new CommonFieldColumnMapper(LOCKED_TIME, Types.BIGINT, new ColumnSize(10), false),
                new CommonFieldColumnMapper(SESSIONID, Types.VARCHAR, new ColumnSize(50),false),
        }, new String[][]{new String[]{/*REPORT_NODE_LOCK_ID,*/FIELD_USERID,FIELD_EDITUSERID, REPORT_ID,LOCKED_TIME, SESSIONID}});
    }

    public static class BI_CONF_TABLE_LOCK {
        public static final String USER_ID = "userId";
        public static final String TABLE_ID = "tableId";
        public static final String SESSION_ID = "sessionId";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(BIConfTableLock.class, new FieldColumnMapper[]{
                new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                new CommonFieldColumnMapper(USER_ID, Types.BIGINT, new ColumnSize(10), false),
                new CommonFieldColumnMapper(TABLE_ID, Types.VARCHAR, new ColumnSize(50), false),
                new CommonFieldColumnMapper(SESSION_ID, Types.VARCHAR, new ColumnSize(50), false),
        }, new String[][]{new String[]{USER_ID, TABLE_ID, SESSION_ID}});
    }

    public static class BI_BUSINESS_PACK_CONFIG_LOCK {
        public static final String LOCK_NAME = "lockName";
        public static final String LOCK_USERID = "userId";
        public static final String SESSIONID = "sessionId";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(
                BIFineDBConfigLock.class, new FieldColumnMapper[]{
                new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                new CommonFieldColumnMapper(LOCK_USERID, Types.BIGINT, new ColumnSize(10), false),
                new CommonFieldColumnMapper(LOCK_NAME, Types.VARCHAR, new ColumnSize(50), false),
                new CommonFieldColumnMapper(SESSIONID, Types.VARCHAR, new ColumnSize(50), false),

        }, new String[][]{new String[]{LOCK_USERID,LOCK_NAME,SESSIONID}});
    }

    /**
     * 保存的BI设计内容表
     *
     * @author Daniel
     */
    public static class BI_REPORT_NODE {
        public static final String TABLE_NAME = ObjectTableMapper.PREFIX_NAME + BIReportNode.class.getSimpleName();
        public static final String FIELD_USERID = "userid";
        public static final String COLUMN_USERID = "userid";
        public static final String FIELD_PARENTID = "parentid";
        public static final String FIELD_PATH = "path";
        public static final String FIELD_REPORTNAME = "reportname";
        public static final String FIELD_CREATETIME = "createtime";
        public static final String FIELD_DESCRIPTION = "description";
        public static final String FIELD_MODIFYTIME = "modifytime";
        public static final String FIELD_STATUS = "responed";

        public static final RelationFCMapper RELATION_BISHAREDREPORTNODE = new OToMRelationFCMapper("biSharedReportNoedeSet", BISharedReportNode.class, BI_SHARED_REPORT_NODE.FIELD_REPORT_ID);

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(
                BIReportNode.class,
                new FieldColumnMapper[]{
                        new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                        new MToOForeignFCMapper(FIELD_USERID, Types.BIGINT, COLUMN_USERID, new ColumnSize(10), false, User.class, true),
                        new CommonFieldColumnMapper(FIELD_PARENTID, Types.VARCHAR, new ColumnSize(255), false),
                        new CommonFieldColumnMapper(FIELD_PATH, Types.VARCHAR, new ColumnSize(255), false),
                        new CommonFieldColumnMapper(FIELD_REPORTNAME, Types.VARCHAR, new ColumnSize(63), false),
                        new CommonFieldColumnMapper(FIELD_CREATETIME, Types.TIMESTAMP, new ColumnSize(20), false),
                        new CommonFieldColumnMapper(FIELD_MODIFYTIME, Types.TIMESTAMP, new ColumnSize(20), false),
                        new CommonFieldColumnMapper(FIELD_DESCRIPTION, Types.VARCHAR, new ColumnSize(1023), false),

                        RELATION_BISHAREDREPORTNODE,
                        new CommonFieldColumnMapper(FIELD_STATUS, Types.INTEGER, new ColumnSize(10), true)
                }
        );
    }

    public static class BI_CREATED_TEMPLATE_FOLDER {
        public static final String FIELD_FOLDER_PARENTID = "parentId";
        public static final String FIELD_USERID = "userId";
        public static final String FIELD_FOLDER_ID = "folderId";
        public static final String FIELD_FOLDER_NAME = "folderName";
        public static final String FIELD_MODIFYTIME = "modifyTime";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(
                BITemplateFolderNode.class,
                new FieldColumnMapper[]{
                        new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                        new CommonFieldColumnMapper(FIELD_FOLDER_PARENTID, Types.VARCHAR, new ColumnSize(255), false),
                        new CommonFieldColumnMapper(FIELD_FOLDER_ID, Types.VARCHAR, new ColumnSize(255), false),
                        new CommonFieldColumnMapper(FIELD_USERID, Types.BIGINT, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_FOLDER_NAME, Types.VARCHAR, new ColumnSize(255), false),
                        new CommonFieldColumnMapper(FIELD_MODIFYTIME, Types.TIMESTAMP, new ColumnSize(20), false)
                }
        );
    }

    public static class BI_SHARED_REPORT_NODE {
        public static final String FIELD_REPORT_ID = "reportId";
        public static final String FIELD_CREATE_BY = "createBy";
        public static final String FIELD_SHARE_TO = "shareTo";
        public static final String FIELD_CREATE_BY_NAME = "createByName";
        public static final String FIELD_SHARE_TO_NAME = "shareToName";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(
                BISharedReportNode.class,
                new FieldColumnMapper[]{
                        new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                        new CommonFieldColumnMapper(FIELD_REPORT_ID, Types.BIGINT, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_CREATE_BY, Types.BIGINT, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_SHARE_TO, Types.BIGINT, new ColumnSize(10), false),
                        new CommonFieldColumnMapper(FIELD_CREATE_BY_NAME, Types.VARCHAR, new ColumnSize(50), true),
                        new CommonFieldColumnMapper(FIELD_SHARE_TO_NAME, Types.VARCHAR, new ColumnSize(50), true)
                }
        );
    }
}
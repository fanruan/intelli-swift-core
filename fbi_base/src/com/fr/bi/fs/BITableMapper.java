package com.fr.bi.fs;

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
		public static final String FIELD_USERID = "userId";
		public static final String REPORT_ID = "reportId";
		public static final String SESSIONID = "sessionId";
		
		public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(BIReportNodeLock.class, new FieldColumnMapper[] {
				 new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
				 new CommonFieldColumnMapper(FIELD_USERID, Types.BIGINT, new ColumnSize(10), false),
				 new CommonFieldColumnMapper(REPORT_ID, Types.BIGINT, new ColumnSize(10), false),
				 new CommonFieldColumnMapper(SESSIONID, Types.VARCHAR, new ColumnSize(50), false),
		}, new String[][] {new String[]{FIELD_USERID, REPORT_ID}});
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
        public static final String FIELD_STATUS = "status";

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
        public static final String FIELD_SHARE_TO = "createBy";

        public static final ObjectTableMapper TABLE_MAPPER = new ObjectTableMapper(
                BISharedReportNode.class,
                new FieldColumnMapper[]{
                        new PrimaryKeyFCMapper("id", Types.BIGINT, new ColumnSize(10)),
                        new MToOForeignFCMapper(FIELD_REPORT_ID, Types.BIGINT, FIELD_REPORT_ID, new ColumnSize(10), false, BIReportNode.class, true),
                        new MToOForeignFCMapper(FIELD_CREATE_BY, Types.BIGINT, FIELD_CREATE_BY, new ColumnSize(10), false, User.class, true),
                        new MToOForeignFCMapper(FIELD_SHARE_TO, Types.BIGINT, FIELD_SHARE_TO, new ColumnSize(10), false, User.class, true)
                }
        );
    }
}
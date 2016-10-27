package com.finebi.cube.conf.utils;

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.conf.BICubeConfigureCenter;
import com.finebi.cube.conf.field.BusinessField;
import com.finebi.cube.conf.field.BusinessFieldHelper;
import com.finebi.cube.conf.table.BusinessTable;
import com.finebi.cube.conf.table.BusinessTableHelper;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.fr.fs.control.UserControl;

/**
 * Created by Connery on 10/25/2016.
 */
public class BILogHelper {
    private static BILogger logger = BILoggerFactory.getLogger(BILogHelper.class);

    public static String logBusinessTable(BusinessTable table) {
        try {
            table = BusinessTableHelper.getBusinessTable(table.getID());
            long userID = UserControl.getInstance().getSuperManagerID();
            return BIStringUtils.append(
                    "\n" + "       Business Table Alias Name:", BICubeConfigureCenter.getAliasManager().getAliasName(table.getID().getIdentityValue(), userID),
                    "\n" + "       Business Table ID:", table.getID().getIdentity(),
                    "\n" + "       Corresponding  Table Source name:", table.getTableSource().getTableName(),
                    "\n" + "       Corresponding  Table Source ID:", table.getTableSource().getSourceID()
            );
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "";
        }
    }

    public static String logBusinessTableField(BusinessTable table, String prefix) {
        try {
            table = BusinessTableHelper.getBusinessTable(table.getID());
            StringBuffer sb = new StringBuffer();
            int count = 0;
            for (BusinessField field : table.getFields()) {
                sb.append("\n" + prefix + "Field" + ++count + " :");
                sb.append(logBusinessField(field, prefix + "     "));
            }
            return sb.toString();
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "";
        }
    }

    public static String logBusinessField(BusinessField field, String prefix) {
        try {
            long userID = UserControl.getInstance().getSuperManagerID();
            return BIStringUtils.append(
                    "\n" + prefix + "       Business Field Alias Name:", BICubeConfigureCenter.getAliasManager().getAliasName(field.getFieldID().getIdentityValue(), userID),
                    "\n" + prefix + "       Business Field Name:", field.getFieldName(),
                    "\n" + prefix + "       Business Field ID:", field.getFieldID().getIdentity()
            );
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "";
        }

    }

    public static String logTableSource(CubeTableSource table, String prefix) {
        try {
            return BIStringUtils.append(
                    "" + prefix + "       Table Source Name:", table.getTableName(),
                    "" + prefix + "       Table Source ID:", table.getSourceID()
            );
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return "";
        }

    }
}

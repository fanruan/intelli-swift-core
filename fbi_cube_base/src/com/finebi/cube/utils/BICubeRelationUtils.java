package com.finebi.cube.utils;

import com.finebi.cube.structure.BICubeRelation;
import com.finebi.cube.structure.BITableKey;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.base.BIUser;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.exception.BIFieldAbsentException;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.fs.control.UserControl;

/**
 * This class created on 2016/4/11.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeRelationUtils {
    public static BICubeRelation convert(BITableSourceRelation sourceRelation) {
        BIField primaryField = sourceRelation.getPrimaryField();
        BIField foreignField = sourceRelation.getForeignField();
        try {
            /**
             * Connery：环境依赖了
             * 计算模块还会原来传递的Field可能是biField
             * 这里需要的DBField。
             * 测试里面不要传递BIField的，那么就没有问题。
             * TODO 计算部分将BIField转成DBField传递过来。
             */
            if (!(primaryField instanceof BICubeFieldSource)) {
                primaryField = BIConfigureManagerCenter.getDataSourceManager().findDBField(new BIUser(UserControl.getInstance().getSuperManagerID()), primaryField);

            }
            if (!(foreignField instanceof BICubeFieldSource)) {
                foreignField = BIConfigureManagerCenter.getDataSourceManager().findDBField(new BIUser(UserControl.getInstance().getSuperManagerID()), foreignField);
            }
        } catch (BIFieldAbsentException e) {
            e.printStackTrace();
        }
        return new BICubeRelation(
                BIColumnKey.covertColumnKey((BICubeFieldSource) primaryField),
                BIColumnKey.covertColumnKey((BICubeFieldSource) foreignField),
                new BITableKey(sourceRelation.getPrimaryTable()),
                new BITableKey(sourceRelation.getForeignTable())
        );
    }

//    public static BICubeRelation convertTableRelation(BICubeRelation sourceRelation) {
//        return new BICubeRelation(
//                new BIColumnKey(BIRowField.rowNumberField),
//                new BIColumnKey(BIRowField.rowNumberField),
//                sourceRelation.getPrimaryTable(),
//                sourceRelation.getForeignTable()
//        );
//    }
}

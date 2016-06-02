package com.fr.bi.etl.analysis.data;

import com.finebi.cube.api.ICubeDataLoader;
import com.fr.bi.base.annotation.BICoreField;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public class UserBaseTableSource extends AnalysisBaseTableSource implements UserCubeTableSource {
    private GroupValueIndex filter;
    private UserWidget userWidget;
    @BICoreField
    private long userId;
    public UserBaseTableSource(BIWidget widget, int etlType, long userId, List<AnalysisETLSourceField> fieldList, String name, String id) {
        super(widget, etlType,  fieldList, name, id);
        this.userId = userId;
        this.userWidget = new UserWidget(widget, userId);
    }



    /**
     * @return
     */
    @Override
    public long getUserId() {
        return userId;
    }

    /**
     * @return
     */
    @Override
    public Set<String> getSourceUsedMD5() {
        HashSet<String> set = new HashSet<String>();
        set.add(fetchObjectCore().getIDValue());
        return set;
    }

    @Override
    public boolean containsIDParentsWithMD5(String md5) {
        return false;
    }



    @Override
    public int getType() {
        return Constants.TABLE_TYPE.USER_BASE;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        int index = 0, step = 1000, total = 0;
        while (total == (index) * step){
            List<List> values = userWidget.createData(index*step, index*step + step);
            for (int i = 0; i < values.size(); i ++){
                List value = values.get(i);
                for (int j = 0; j < value.size(); j++){
                    travel.actionPerformed(new BIDataValue(i + total, j, value.get(j)));
                }
            }
            total +=values.size();
        }
        return total;
    }


    @Override
    public UserCubeTableSource createUserTableSource(long userId) {
        return this;
    }

    @Override
    public long read4Part(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader, int start, int end) {
        List<List> values = userWidget.createData(start, end);
        for (int i = 0; i < values.size(); i ++){
            List value = values.get(i);
            for (int j = 0; j < value.size(); j++){
                travel.actionPerformed(new BIDataValue(i, j, value.get(j)));
            }
        }
        return values.size();
    }
}
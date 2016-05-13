package com.fr.bi.etl.analysis.data;

import com.fr.bi.conf.report.BIWidget;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public class UserBaseTableSource extends AnalysisBaseTableSource implements UserTableSource{
    private GroupValueIndex filter;
    private long userId;
    public UserBaseTableSource(BIWidget widget, int etlType, long userId, List<AnalysisETLSourceField> fieldList) {
        super(new UserWidget(widget, userId), etlType,  fieldList);
        this.userId = userId;
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
        return ComparatorUtils.equals(md5, fetchObjectCore().getIDValue());
    }



    @Override
    public int getType() {
        return Constants.TABLE_TYPE.USER_BASE;
    }




    @Override
    public UserTableSource createUserTableSource(long userId) {
        return this;
    }

}
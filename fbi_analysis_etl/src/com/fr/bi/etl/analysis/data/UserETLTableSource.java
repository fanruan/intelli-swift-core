package com.fr.bi.etl.analysis.data;

import com.fr.bi.common.inter.Traversal;
import com.fr.bi.conf.data.source.AbstractETLTableSource;
import com.fr.bi.conf.data.source.operator.IETLOperator;
import com.fr.bi.etl.analysis.Constants;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.DBField;
import com.finebi.cube.api.ICubeDataLoader;
import com.fr.general.ComparatorUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 小灰灰 on 2015/12/24.
 */
public class UserETLTableSource extends AbstractETLTableSource<IETLOperator, UserTableSource> implements UserTableSource{
    private long userId;


    public UserETLTableSource(List<IETLOperator> oprators, List<UserTableSource> parents, long userId) {
        super(oprators, parents);
        this.userId = userId;
    }

    @Override
    public int getType() {
        return Constants.TABLE_TYPE.USER_ETL;
    }

    /**
     * 写简单索引
     *
     * @param travel
     * @param field
     * @param loader
     * @return
     */
    @Override
    public long read(Traversal<BIDataValue> travel, DBField[] field, ICubeDataLoader loader) {
        return 0;
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
        Set<String> set = new HashSet<String>();
        for (UserTableSource source : getParents()){
            if (source.getType() == Constants.TABLE_TYPE.USER_BASE){
                set.addAll(source.getSourceUsedMD5());
            }
        }
        return set;
    }

    @Override
    public boolean containsIDParentsWithMD5(String md5) {
        for (UserTableSource source : getParents()){
            if (ComparatorUtils.equals(md5, source.fetchObjectCore().getID().getIdentityValue())){
                return true;
            }
            if (source.containsIDParentsWithMD5(md5)){
                return true;
            }
        }
        return false;
    }
}
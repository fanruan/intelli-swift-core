package com.fr.bi.cal.analyze.cal.store;

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.conf.table.BusinessTable;
import com.fr.bi.field.dimension.calculator.AbstractDimensionCalculator;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.general.ComparatorUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 权限的东东 临时性的东东
 *
 * @author Daniel
 */
public class UserRightColumnKey extends AbstractDimensionCalculator {


    /**
     *
     */
    private static final long serialVersionUID = -7314023318409359365L;
    private final static String ERROR_NAME = "ERROR_NAME";
    private BusinessTable target;
    private GroupValueIndex gvi;
    public UserRightColumnKey(GroupValueIndex gvi, BusinessTable target) {
        super();
        this.gvi = gvi;
        this.target = target;
    }

    /**
     * hash值
     *
     * @return hash值
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((gvi == null) ? 0 : gvi.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserRightColumnKey other = (UserRightColumnKey) obj;
        if (gvi == null) {
            if (other.gvi != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(gvi, other.gvi)) {
            return false;
        }
        if (target == null) {
            if (other.target != null) {
                return false;
            }
        } else if (!ComparatorUtils.equals(target, other.target)) {
            return false;
        }
        return true;
    }

    /**
     * 获取迭代器
     *
     * @param table 表
     * @return 迭代器
     */
    @Override
    public Iterator createValueMapIterator(BusinessTable table, ICubeDataLoader loader) {
        Map<String, GroupValueIndex> vMap = new HashMap<String, GroupValueIndex>();
        vMap.put(ERROR_NAME, gvi);
        return vMap.entrySet().iterator();
    }

    @Override
    public boolean isSupperLargeGroup(ICubeDataLoader loader) {
        return false;
    }


    /**
     * 是否为超级大分组
     *
     * @param targetTable 指标表
     * @param loader      注释
     * @return 是否为超级大分组
     */
    @Override
    public boolean isSupperLargeGroup(BusinessTable targetTable, ICubeDataLoader loader) {
        return false;
    }

}
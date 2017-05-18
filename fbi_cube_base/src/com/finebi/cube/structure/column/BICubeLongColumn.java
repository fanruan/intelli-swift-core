package com.finebi.cube.structure.column;

import com.finebi.cube.data.ICubeResourceDiscovery;
import com.finebi.cube.exception.BICubeIndexException;
import com.finebi.cube.location.ICubeResourceLocation;
import com.finebi.cube.structure.detail.BICubeLongDetailData;
import com.finebi.cube.structure.group.BICubeLongGroupData;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * This class created on 2016/3/28.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeLongColumn extends BICubeColumnEntity<Long> {
    public BICubeLongColumn(ICubeResourceDiscovery discovery, ICubeResourceLocation currentLocation) {
        super(discovery, currentLocation);
    }

    @Override
    protected void initial() {
        detailDataService = new BICubeLongDetailData(discovery, currentLocation);
        groupDataService = new BICubeLongGroupData(discovery, currentLocation);
    }

    /**
     * 根据行号获得对应的原始值。
     *
     * @param rowNumber 数据库中的行号
     * @return 原始值
     */
    public long getOriginalValueByRow(int rowNumber) {
        return ((BICubeLongDetailData)detailDataService).getOriginalValueByRow(rowNumber);
    }

    public long getGroupValue(int position) {
        return ((BICubeLongGroupData)groupDataService).getGroupValueByPosition(position);
    }

    @Override
    public Long getOriginalObjectValueByRow(int rowNumber) {
        return getOriginalValueByRow(rowNumber);
    }

    /**
     * 获取空值表示对象
     *
     * @return
     */
    @Override
    public Long getCubeNullValue() {
        return NIOConstant.LONG.NULL_VALUE;
    }

    /**
     * 获取空值gvi
     * <p>
     * 把空值当成一个正常的分组,像字符串那样进行处理
     *
     * @param position
     * @return
     * @throws BICubeIndexException
     */
    @Override
    public GroupValueIndex getNULLIndex(int position) throws BICubeIndexException {
        try {
            return getIndexByGroupValue(NIOConstant.LONG.NULL_VALUE);
        } catch (Exception e) {
            return GVIFactory.createAllEmptyIndexGVI();
        }
    }
}

package com.finebi.cube.adapter;

import com.finebi.cube.api.*;
import com.finebi.cube.structure.column.BICubeDoubleColumn;
import com.finebi.cube.structure.column.BICubeLongColumn;
import com.finebi.cube.structure.column.CubeColumnReaderService;
import com.fr.bi.stable.constant.DBConstant;
import com.fr.bi.stable.gvi.GVIFactory;
import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.io.newio.NIOConstant;

/**
 * Created by 小灰灰 on 2016/6/24.
 */
public class BICubeColumnDetailGetter implements ICubeColumnDetailGetter {
    private static final long serialVersionUID = -3389205614212915623L;
    CubeColumnReaderService service;

    public BICubeColumnDetailGetter(CubeColumnReaderService service) {
        this.service = service;
    }

    @Override
    public Object getValue(int row) {
        return service.getOriginalObjectValueByRow(row);
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return service.getClassType() == DBConstant.CLASS.LONG ? PrimitiveType.LONG : PrimitiveType.DOUBLE;
    }

    @Override
    public PrimitiveDetailGetter createPrimitiveDetailGetter() {
        PrimitiveType type = getPrimitiveType();
        if (type == PrimitiveType.DOUBLE) {
            return new PrimitiveDoubleGetter() {
                private static final long serialVersionUID = 7141828639662850151L;

                @Override
                public double getValue(int row) {
                    // 直接返回原始值，不管是空值或者不是
                    return ((BICubeDoubleColumn) service).getOriginalValueByRow(row);
                }

                public GroupValueIndex getNullIndex() {
                    try {
                        return service.getIndexByGroupValue(NIOConstant.DOUBLE.NULL_VALUE);
                    } catch (Exception e) {
                    }
                    return GVIFactory.createAllEmptyIndexGVI();
                }
            };
        } else if (type == PrimitiveType.LONG) {
            return new PrimitiveLongGetter() {
                private static final long serialVersionUID = 3658058739271903945L;

                @Override
                public long getValue(int row) {
                    // 直接返回原始值，不管是空值或者不是
                    return ((BICubeLongColumn) service).getOriginalValueByRow(row);
                }

                public GroupValueIndex getNullIndex() {
                    try {
                        return service.getIndexByGroupValue(NIOConstant.LONG.NULL_VALUE);
                    } catch (Exception e) {
                    }
                    return GVIFactory.createAllEmptyIndexGVI();
                }
            };
        }
        return null;
    }
    @Override
    public String getICubeResourceLocationPath(){
        return service.getResourceLocation().getAbsolutePath();
    }

    @Override
    public void clear() {
        service.clear();
    }
}

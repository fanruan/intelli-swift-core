package com.finebi.cube.adapter;

import com.finebi.cube.api.ICubeColumnDetailGetter;
import com.finebi.cube.structure.column.CubeColumnReaderService;

/**
 * Created by 小灰灰 on 2016/6/24.
 */
public class BICubeColumnDetailGetter implements ICubeColumnDetailGetter {
    CubeColumnReaderService service;

    public BICubeColumnDetailGetter(CubeColumnReaderService service) {
        this.service = service;
    }

    @Override
    public Object getValue(int row) {
        return service.getOriginalValueByRow(row);
    }

}

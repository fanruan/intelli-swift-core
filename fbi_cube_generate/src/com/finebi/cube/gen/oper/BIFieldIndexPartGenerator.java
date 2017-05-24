package com.finebi.cube.gen.oper;

import com.finebi.cube.structure.Cube;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.date.BICubeDateSubColumn;
import com.fr.bi.stable.constant.CubeConstant;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;
import com.fr.bi.stable.structure.array.IntList;
import com.fr.bi.stable.structure.array.IntListFactory;
import com.fr.bi.stable.utils.program.BIStringUtils;
import com.google.common.base.Stopwatch;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by 小灰灰 on 2017/5/10.
 */
public class BIFieldIndexPartGenerator<T> extends BIFieldIndexGenerator<T>{
    public BIFieldIndexPartGenerator(Cube cube, CubeTableSource tableSource, ICubeFieldSource hostBICubeFieldSource, BIColumnKey targetColumnKey) {
        super(cube, tableSource, hostBICubeFieldSource, targetColumnKey);
    }

    @Override
    protected void constructMap(Map<T, IntList> map, IntList nullRowNumbers) {
        LOGGER.info(BIStringUtils.append(logFileInfo(), " read detail data ,the row count:", String.valueOf(rowCount)));
        Stopwatch stopwatch = Stopwatch.createStarted();
        OriginValueGetter<T> getter;
        if (columnEntityService instanceof BICubeDateSubColumn){
            getter = new OriginValueGetter<T>() {
                Calendar calendar = Calendar.getInstance();
                BICubeDateSubColumn cubeDateSubColumn = (BICubeDateSubColumn) columnEntityService;
                @Override
                public T getOriginalObjectValueByRow(int row) {
                    return (T) cubeDateSubColumn.getOriginalValueByRow(row, calendar);
                }
            };
        } else {
            getter = new OriginValueGetter<T>() {
                @Override
                public T getOriginalObjectValueByRow(int row) {
                    return columnEntityService.getOriginalObjectValueByRow(row);
                }
            };
        }
        for (int i = 0; i < rowCount; i++) {
            T originalValue = getter.getOriginalObjectValueByRow(i);
            if (originalValue != null) {
                IntList list = map.get(originalValue);
                if (list == null) {
                    list = IntListFactory.createIntList();
                    map.put(originalValue, list);
                }
                list.add(i);
            } else {
                nullRowNumbers.add(i);
            }
            if (CubeConstant.LOG_SEPERATOR_ROW != 0 && i % CubeConstant.LOG_SEPERATOR_ROW == 0) {
                LOGGER.info(BIStringUtils.append(logFileInfo(), " read ", String.valueOf(i), " rows field value and time elapse:", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)), " second"));
            }
        }
        stopwatch.stop();
    }
}

package com.finebi.table.gen;
/**
 * This class created on 2017/5/17.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.api.ICubeDataLoader;
import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.finebi.cube.tools.BIMemoryDataSource;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.data.db.BIDataValue;
import com.fr.bi.stable.data.db.ICubeFieldSource;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.ArrayList;
import java.util.List;

public class RandomTableSource extends BIMemoryDataSource implements CubeTableSource {
    private final static BILogger LOGGER = BILoggerFactory.getLogger(RandomTableSource.class);
    private List<BIRandomFieldSource> fieldGenerator;

    public RandomTableSource(int rowSize, List<BIRandomFieldSource> field) {
        setRowCount(rowSize);
        this.fieldGenerator = new ArrayList<BIRandomFieldSource>(field);
        this.fieldList = new ArrayList<ICubeFieldSource>(field);
    }

    public void setFieldGenerator(List<BIRandomFieldSource> fieldGenerator) {
        this.fieldGenerator = fieldGenerator;
        this.fieldList = new ArrayList<ICubeFieldSource>(fieldGenerator);

    }

    public RandomTableSource(int rowSize) {
        setRowCount(rowSize);
    }

    public List<ICubeFieldSource> fieldList;

    @Override
    public String getModuleName() {
        return null;
    }

    @Override
    public long read(Traversal<BIDataValue> travel, ICubeFieldSource[] field, ICubeDataLoader loader) {
        int columnSize = fieldGenerator.size();
        for (int i = 0; i < rowCount; i++) {
            for (int columnIndex = 0; columnIndex < columnSize; columnIndex++) {
                travel.actionPerformed(new BIDataValue(i, columnIndex, fieldGenerator.get(columnIndex).getValue()));
            }
        }
        return rowCount;
    }
}

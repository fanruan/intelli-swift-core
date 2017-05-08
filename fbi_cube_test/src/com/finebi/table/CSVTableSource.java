package com.finebi.table;
/**
 * This class created on 2017/5/4.
 *
 * @author Connery
 * @since Advanced FineBI Analysis 1.0
 */

import com.finebi.cube.common.log.BILogger;
import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.stable.data.source.AbstractTableSource;
import com.fr.bi.stable.data.source.CubeTableSource;

public class CSVTableSource extends AbstractTableSource implements CubeTableSource{
    private final static BILogger LOGGER = BILoggerFactory.getLogger(CSVTableSource.class);

    @Override
    public String getModuleName() {
        return null;
    }

}

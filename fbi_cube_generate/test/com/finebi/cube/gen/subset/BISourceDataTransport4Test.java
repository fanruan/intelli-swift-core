package com.finebi.cube.gen.subset;

import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.fr.bi.stable.data.source.ITableSource;
import com.finebi.cube.structure.ICube;

import java.util.Set;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class BISourceDataTransport4Test extends BISourceDataTransport {
    @Override
    public Object mainTask() {
        System.out.println("Source Data Transport!");
        BICubeBuildProbeTool.INSTANCE.getFlag().put(tableSource.getSourceID(), 10);

//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public BISourceDataTransport4Test(ICube cube, ITableSource tableSource, Set<ITableSource> allSources) {
        super(cube, tableSource, allSources);
    }
}

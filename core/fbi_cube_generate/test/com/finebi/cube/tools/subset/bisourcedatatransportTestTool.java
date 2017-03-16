package com.finebi.cube.tools.subset;

import com.finebi.cube.gen.oper.BISourceDataTransport;
import com.finebi.cube.message.IMessage;
import com.finebi.cube.structure.Cube;
import com.finebi.cube.tools.BICubeBuildProbeTool;
import com.fr.bi.stable.data.source.CubeTableSource;

import java.util.Map;
import java.util.Set;

/**
 * This class created on 2016/4/13.
 *
 * @author Connery
 * @since 4.0
 */
public class bisourcedatatransportTestTool extends BISourceDataTransport {
    @Override
    public Object mainTask(IMessage lastReceiveMessage) {
        System.out.println("Source Data Transport!");
        BICubeBuildProbeTool.INSTANCE.getFlag().put(tableSource.getSourceID(), 10);

//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    public void release() {

    }

    public bisourcedatatransportTestTool(Cube cube, Cube integrityCube, CubeTableSource tableSource, Set<CubeTableSource> allSources, Set<CubeTableSource> parentTableSource, Map<String, CubeTableSource> tablesNeed2GenerateMap) {
        super(cube, integrityCube, tableSource, allSources, parentTableSource, 1, tablesNeed2GenerateMap);
    }
}

package com.fr.bi.cube.engine.index.loader;

import com.fr.bi.cube.engine.memory.GroupValueInMemoryIndexManager;
import com.fr.bi.cube.engine.switcher.MemoryAndDiskSwitchReader;
import com.fr.bi.cube.engine.switcher.SwitcherStrategy;
import com.fr.bi.data.BIDataColumn;
import junit.framework.TestCase;

/**
 * Created by Connery on 2014/12/2.
 */
public class TableLinkIndexManagerTest extends TestCase {
    private static final String PATH = "c://test";
    private static final int THRESHOLD = 30;

    public void GenerateIndexInUse() {
        TableLinkIndexManagerCollection tableLinkIndexManagerCollection = new TableLinkIndexManagerCollection();
        TableLinkIndexManager tableLinkIndexManager = tableLinkIndexManagerCollection.getTableLinkIndexManager("key");

        int sumTime = (GroupValueInMemoryIndexManager.TIME_LIMIT - 1000) > 0 ? (GroupValueInMemoryIndexManager.TIME_LIMIT.intValue() - 1000) : (GroupValueInMemoryIndexManager.TIME_LIMIT.intValue() - 0);
        TableIndex4Test tableIndex4Test = new TableIndex4Test(sumTime, 100);
        GroupValueIndexLoader4Test groupValueIndexLoader4Test = new GroupValueIndexLoader4Test();

        System.out.println("数据准备时间小于，内存准备时间");

        TableLinkIndexGenerator tableLinkIndexGenerator = new TableLinkIndexGenerator(tableIndex4Test, groupValueIndexLoader4Test);
        tableLinkIndexGenerator.setColumns(new BIDataColumn[1]);
        tableLinkIndexGenerator.initialSerializeLinkedIndex(PATH, PATH, 1, null);
        tableLinkIndexManager.registerTableLinkIndexGenerator(tableLinkIndexGenerator);
        runGenerateIndexTest(tableLinkIndexGenerator, tableLinkIndexManager, sumTime);

        System.out.println("数据准备时间大于，内存准备时间");
        sumTime = GroupValueInMemoryIndexManager.TIME_LIMIT.intValue() + 2000;
        tableLinkIndexManager = tableLinkIndexManagerCollection.getTableLinkIndexManager("key2");
        tableIndex4Test = new TableIndex4Test(sumTime, 100);
        tableLinkIndexGenerator = new TableLinkIndexGenerator(tableIndex4Test, groupValueIndexLoader4Test);
        tableLinkIndexGenerator.setColumns(new BIDataColumn[1]);
        tableLinkIndexGenerator.initialSerializeLinkedIndex(PATH, PATH, 1, null);
        tableLinkIndexManager.registerTableLinkIndexGenerator(tableLinkIndexGenerator);
        runGenerateIndexTest(tableLinkIndexGenerator, tableLinkIndexManager, sumTime);

        System.out.println("数据准备时间等于，内存准备时间");
        sumTime = GroupValueInMemoryIndexManager.TIME_LIMIT.intValue();
        tableLinkIndexManager = tableLinkIndexManagerCollection.getTableLinkIndexManager("key3");
        tableIndex4Test = new TableIndex4Test(sumTime, 100);
        tableLinkIndexGenerator = new TableLinkIndexGenerator(tableIndex4Test, groupValueIndexLoader4Test);
        tableLinkIndexGenerator.setColumns(new BIDataColumn[1]);
        tableLinkIndexGenerator.initialSerializeLinkedIndex(PATH, PATH, 1, null);
        tableLinkIndexManager.registerTableLinkIndexGenerator(tableLinkIndexGenerator);
        runGenerateIndexTest(tableLinkIndexGenerator, tableLinkIndexManager, sumTime);
    }

    private void runGenerateIndexTest(TableLinkIndexGenerator tableLinkIndexGenerator, TableLinkIndexManager tableLinkIndexManager, int sumTime) {

        tableLinkIndexManager.registerTableLinkIndexGenerator(tableLinkIndexGenerator);
        MemoryAndDiskSwitchReader memoryAndDiskSwitchReader = null;
        Long timeRecord = System.currentTimeMillis();
        try {
            memoryAndDiskSwitchReader = tableLinkIndexManager.generateIndexInUse();
        } catch (Exception ex) {

        }
        SwitcherStrategy switcherStrategy = memoryAndDiskSwitchReader.getSwitcherStrategy();
        if (System.currentTimeMillis() - timeRecord < GroupValueInMemoryIndexManager.TIME_LIMIT + THRESHOLD) {
            assertTrue(switcherStrategy.isReadFromDisk());
            assertFalse(switcherStrategy.isReadFromMemory());
        } else {
            assertFalse(switcherStrategy.isReadFromDisk());
            assertTrue(switcherStrategy.isReadFromMemory());
            timeRecord = System.currentTimeMillis();
            while (System.currentTimeMillis() - timeRecord < sumTime) {

            }
            assertTrue(switcherStrategy.isReadFromDisk());
            assertFalse(switcherStrategy.isReadFromMemory());
        }
    }

    public void testCheckPercentOfGroupIndexGeneration() {
        TableLinkIndexManagerCollection tableLinkIndexManagerCollection = new TableLinkIndexManagerCollection();
        final TableLinkIndexManager tableLinkIndexManager = tableLinkIndexManagerCollection.getTableLinkIndexManager("key");
        int sumTime = GroupValueInMemoryIndexManager.TIME_LIMIT.intValue() + 1000;
        TableIndex4Test tableIndex4Test = new TableIndex4Test(sumTime, 100);
        GroupValueIndexLoader4Test groupValueIndexLoader4Test = new GroupValueIndexLoader4Test();

        TableLinkIndexGenerator tableLinkIndexGenerator = new TableLinkIndexGenerator(tableIndex4Test, groupValueIndexLoader4Test);
        tableLinkIndexGenerator.setColumns(new BIDataColumn[1]);
        tableLinkIndexGenerator.initialSerializeLinkedIndex(PATH, PATH, 1, null);
        tableLinkIndexManager.registerTableLinkIndexGenerator(tableLinkIndexGenerator);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tableLinkIndexManager.generateIndexInUse();
                } catch (Exception ex) {

                }
            }
        }).start();

        long timeRecord = System.currentTimeMillis();
        while (System.currentTimeMillis() - timeRecord < sumTime * 2) {
            System.out.println(tableLinkIndexManagerCollection.calculateAveragePercentGeneration());
        }
    }

}
package com.fr.bi.cube.engine.index.loader;

import com.fr.bi.cube.engine.index.GroupValueIndex;
import com.fr.bi.cube.engine.io.read.GroupValueIndexArrayReader;
import junit.framework.TestCase;

/**
 * Created by Connery on 2015/1/15.
 */
public class TableIndexTest extends TestCase {
    public void testReverseIndex() {
        GroupValueIndexArrayReader groupValueIndexArrayReader = new GroupValueIndexArrayReader("C:\\report0114\\webapps\\WebReport\\WEB-INF\\resources\\cubes\\local\\null\\qualification\\linkedindex\\linkedindexlocal_null_qualification_info_id_TO_local_null_info_id_info\\index.row", 2);
        GroupValueIndex gvi = groupValueIndexArrayReader.get(3);
    }
}
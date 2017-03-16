package com.fr.bi.cal.analyze.executor.detail;

import com.fr.bi.cal.report.engine.CBCell;
import junit.framework.TestCase;

/**
 * Created by daniel on 2016/7/13.
 */
public class StreamIteratorTest extends TestCase {



    public void testAddAndGet() {
        final StreamPagedIterator iter = new StreamPagedIterator();
        final CBCell[] cells = new CBCell[1000000];
        for(int i = 0; i < cells.length; i++) {
            cells[i] = new CBCell(i);
        }

        new Thread(){
            public void run() {
                for(int i = 0; i < cells.length; i++) {
                    iter.addCell(cells[i]);
                }
                iter.finish();
            }
        }.start();
        int i = 0;
        while(iter.hasNext()){
            assertEquals(cells[i++], iter.next());
        }

    }


    public void testAll () {

        for(int i = 0; i < 100; i++){
            testAddAndGet();
        }

    }

}

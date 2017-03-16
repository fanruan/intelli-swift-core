package com.fr.bi.test.stable.structure.queue;

import com.fr.bi.base.FinalInt;
import com.fr.bi.common.inter.Traversal;
import com.fr.bi.stable.gvi.traversal.TraversalAction;
import com.fr.bi.stable.structure.queue.QueueThread;
import junit.framework.TestCase;

import java.lang.reflect.Field;

/**
 * Created by daniel on 2016/8/8.
 */
public class QueueThreadTest extends TestCase {


    public void testFirstEmpty () {

        QueueThread<Object> qt = new QueueThread<Object>();
        final FinalInt v = new FinalInt();
        v.value = 0;
        qt.setTraversal(new Traversal<Object>() {
            @Override
            public void actionPerformed(Object data) {
                v.value = 100;
            }
        });
        try {
            Field field = QueueThread.class.getDeclaredField("qThread");
            field.setAccessible(true);
            Object o = field.get(qt);
            synchronized (o) {
                qt.start();

                Thread.sleep(100);
                qt.add(new Object());
            }
            Thread.sleep(400);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        assertEquals(v.value.intValue(), 100);

    }
}

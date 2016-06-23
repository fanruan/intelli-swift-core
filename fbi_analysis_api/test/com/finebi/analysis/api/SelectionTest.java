package com.finebi.analysis.api;

import com.finebi.analysis.api.criteria.CriteriaBuilder;
import com.finebi.analysis.api.criteria.CriteriaQuery;
import com.finebi.analysis.api.criteria.Join;
import com.finebi.analysis.api.criteria.Root;
import com.finebi.analysis.api.criteria.PlainTable;
import junit.framework.TestCase;
import org.easymock.EasyMock;

/**
 * This class created on 2016/6/23.
 *
 * @author Connery
 * @since 4.0
 */
public class SelectionTest extends TestCase {
    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testSelect() {
        try {
            PlainTable target = EasyMock.createMock(PlainTable.class);
            CriteriaBuilder cb = generateCB();
            CriteriaQuery<PlainTable> query = cb.createQuery(PlainTable.class);
            Root<PlainTable> root = query.from(target);
            query.select(target);
            executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testJoin() {
        try {
            PlainTable tar = EasyMock.createMock(PlainTable.class);
            PlainTable src = EasyMock.createMock(PlainTable.class);
            CriteriaBuilder cb = generateCB();
            CriteriaQuery<PlainTable> query = cb.createQuery(PlainTable.class);
            Root<PlainTable> root = query.from(src);
            Join join = root.join(src);
            join.on(cb.equal(tar.getColumn("id"), src.getColumn("id")));
            Object result = executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    /**
     * Detail:
     * Author:Connery
     * Date:2016/6/23
     */
    public void testMutiJoin() {
        try {
            PlainTable one = EasyMock.createMock(PlainTable.class);
            PlainTable two = EasyMock.createMock(PlainTable.class);
            PlainTable three = EasyMock.createMock(PlainTable.class);

            CriteriaBuilder cb = generateCB();
            CriteriaQuery<PlainTable> query = cb.createQuery(PlainTable.class);
            Root<PlainTable> root = query.from(two);
            Join join = root.join(two);
            join.on(cb.equal(one.getColumn("id"), two.getColumn("id"))).join(three);
            query.select(one);
            Object result = executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    private Object executeQuery(CriteriaQuery query) {
        return null;
    }

    private CriteriaBuilder generateCB() {
        CriteriaBuilder cb = EasyMock.createMock(CriteriaBuilder.class);
//        cb.createQuery(PlainTable.class);
//        CriteriaQuery<PlainTable> query=EasyMock.createMock(CriteriaQuery.class);
//        query.from()
        return cb;
    }
}

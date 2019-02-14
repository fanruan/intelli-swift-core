package com.fr.swift.config.hibernate;

import com.fr.swift.config.oper.ConfigWhere;
import com.fr.swift.config.oper.impl.ConfigWhereImpl;
import com.fr.swift.config.oper.impl.OrderImpl;
import org.easymock.EasyMock;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.In;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertFalse;

/**
 * @author yee
 * @date 2019-01-08
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(In.class)
public class HibernateQueryTest {

    private HibernateQuery query;

    @Before
    public void setUp() throws Exception {
        // Generate by Mock Plugin
        Session mockSession = PowerMock.createMock(Session.class);
        // Generate by Mock Plugin
        Query mockQuery = PowerMock.createMock(Query.class);
        EasyMock.expect(mockQuery.getResultList()).andReturn(Arrays.asList(new Object())).anyTimes();
        EasyMock.expect(mockSession.createQuery(EasyMock.anyObject(CriteriaQuery.class))).andReturn(mockQuery).anyTimes();
        // Generate by Mock Plugin
        CriteriaBuilder mockCriteriaBuilder = PowerMock.createMock(CriteriaBuilder.class);
        // Generate by Mock Plugin
        CriteriaQuery mockCriteriaQuery = PowerMock.createMock(CriteriaQuery.class);
        // Generate by Mock Plugin
        Root mockRoot = PowerMock.createMock(Root.class);
        EasyMock.expect(mockRoot.get(EasyMock.notNull(String.class))).andReturn(mockRoot).anyTimes();
        EasyMock.expect(mockCriteriaQuery.from(EasyMock.eq(Object.class))).andReturn(mockRoot).anyTimes();
        EasyMock.expect(mockCriteriaQuery.where(EasyMock.notNull(Predicate.class), EasyMock.notNull(Predicate.class), EasyMock.notNull(Predicate.class))).andReturn(mockCriteriaQuery).anyTimes();
        EasyMock.expect(mockCriteriaQuery.orderBy(EasyMock.notNull(List.class))).andReturn(mockCriteriaQuery).anyTimes();
        EasyMock.expect(mockCriteriaBuilder.createQuery(EasyMock.eq(Object.class))).andReturn(mockCriteriaQuery).anyTimes();
        // Generate by Mock Plugin
        Order mockOrder = PowerMock.createMock(Order.class);

        EasyMock.expect(mockCriteriaBuilder.asc(EasyMock.notNull(Expression.class))).andReturn(mockOrder).anyTimes();
        EasyMock.expect(mockCriteriaBuilder.desc(EasyMock.notNull(Expression.class))).andReturn(mockOrder).anyTimes();
        // Generate by Mock Plugin
        Predicate mockPredicate = PowerMock.createMock(Predicate.class);

        EasyMock.expect(mockCriteriaBuilder.like(EasyMock.notNull(Expression.class), EasyMock.notNull(String.class))).andReturn(mockPredicate).anyTimes();
        EasyMock.expect(mockCriteriaBuilder.equal(EasyMock.notNull(Expression.class), EasyMock.notNull(String.class))).andReturn(mockPredicate).anyTimes();
        // Generate by Mock Plugin
        Expression mockExpression = PowerMock.createMock(Expression.class);
        EasyMock.expect(mockExpression.in(EasyMock.anyObject(Collection.class))).andReturn(mockPredicate).anyTimes();
        // Generate by Mock Plugin
        PowerMock.mockStatic(In.class);
        In mockIn = PowerMock.createMock(In.class);
        EasyMock.expect(mockIn.getExpression()).andReturn(mockExpression).anyTimes();

        EasyMock.expect(mockCriteriaBuilder.in(EasyMock.notNull(Expression.class))).andReturn(mockIn).anyTimes();

        EasyMock.expect(mockSession.getCriteriaBuilder()).andReturn(mockCriteriaBuilder).anyTimes();
        PowerMock.replayAll();
        query = new HibernateQuery(Object.class, mockSession);
    }

    @Test
    public void executeQuery() {
        assertFalse(query.executeQuery().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void where() {
        query.where(ConfigWhereImpl.eq("columnA", "hello"),
                ConfigWhereImpl.in("columnC.name", Arrays.asList("mike", "amy")),
                ConfigWhereImpl.like("columnB", "value", ConfigWhere.MatchMode.ANY));
        assertFalse(query.executeQuery().isEmpty());
        PowerMock.verifyAll();
    }

    @Test
    public void orderBy() {
        query.orderBy(OrderImpl.asc("columnA"), OrderImpl.desc("columnB"));
        assertFalse(query.executeQuery().isEmpty());
        PowerMock.verifyAll();
    }
}
package com.fr.swift.api.info;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.powermock.api.easymock.PowerMock;

import static junit.framework.TestCase.assertTrue;

/**
 * @author yee
 * @date 2019-01-03
 */
public class ApiRequestParserVisitorTest {

    private ApiRequestParserVisitor visitor;

    @Before
    public void setUp() {
        // Generate by Mock Plugin
        // Generate by Mock Plugin
        ApiInvocation mockApiInvocation = PowerMock.createMock(ApiInvocation.class);
        visitor = PowerMock.createMock(ApiRequestParserVisitor.class);
        EasyMock.expect(visitor.visit(EasyMock.notNull(TableRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(TableRequestInfo.class))).andThrow(new RuntimeException()).once();
        EasyMock.expect(visitor.visit(EasyMock.notNull(QueryRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(QueryRequestInfo.class))).andThrow(new RuntimeException()).once();
        EasyMock.expect(visitor.visit(EasyMock.notNull(InsertRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(InsertRequestInfo.class))).andThrow(new RuntimeException()).once();
        EasyMock.expect(visitor.visit(EasyMock.notNull(CreateTableRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(CreateTableRequestInfo.class))).andThrow(new RuntimeException()).once();
        EasyMock.expect(visitor.visit(EasyMock.notNull(DeleteRequestInfo.class))).andReturn(mockApiInvocation).once();
        EasyMock.expect(visitor.visit(EasyMock.isNull(DeleteRequestInfo.class))).andThrow(new RuntimeException()).once();
        PowerMock.replayAll();
    }

    @Test
    public void visitTest() {
        visit(new QueryRequestInfo());
        boolean exp = false;
        try {
            visit((QueryRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        visit(new CreateTableRequestInfo());
        try {
            visit((CreateTableRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        visit(new DeleteRequestInfo());
        try {
            visit((DeleteRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        visit(new InsertRequestInfo());
        try {
            visit((InsertRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        exp = false;
        visit(new TableRequestInfo());
        try {
            visit((TableRequestInfo) null);
        } catch (Exception e) {
            exp = true;
        }
        assertTrue(exp);
        PowerMock.verifyAll();
    }


    void visit(QueryRequestInfo queryRequestInfo) {
        visitor.visit(queryRequestInfo);
    }

    void visit(CreateTableRequestInfo createTableRequestInfo) {
        visitor.visit(createTableRequestInfo);
    }

    void visit(DeleteRequestInfo deleteRequestInfo) {
        visitor.visit(deleteRequestInfo);
    }

    void visit(InsertRequestInfo insertRequestInfo) {
        visitor.visit(insertRequestInfo);
    }

    void visit(TableRequestInfo tableRequestInfo) {
        visitor.visit(tableRequestInfo);
    }
}
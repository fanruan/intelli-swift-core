package com.fr.bi.web.conf.services;

import com.finebi.cube.relation.BITableRelation;
import com.fr.bi.conf.data.source.DBTableSource;
import com.fr.bi.web.conf.utils.BIImportDBTableConnectionRelationTool;
import junit.framework.TestCase;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by naleite on 16/3/1.
 */
public class BIImportDBTableConnectionActionTest extends TestCase {

    Map<String, DBTableSource> newTableSources = new java.util.HashMap<String, DBTableSource>();
    Map<String, DBTableSource> allTableSources = new java.util.HashMap<String, DBTableSource>();
    Map<String, String> allFieldIdMap = new HashMap<String, String>();
    BIImportDBTableConnectionRelationTool stub = new BIImportDBTableTestTool();
    Set<BITableRelation> relations = new HashSet<BITableRelation>();
    BIImportDBTableConnectionExecutor executor = new BIImportDBTableConnectionExecutor();

    DBTableSource tableSourceA;
    DBTableSource tableSourceB;
    DBTableSource tableSourceC;

    @Override
    public void setUp() throws Exception {


        Field toolField = executor.getClass().getDeclaredField("tool");
        toolField.setAccessible(true);
        toolField.set(executor, stub);

        tableSourceA = new DBTableSource("test", "a");
        tableSourceB = new DBTableSource("test", "b");
        tableSourceC = new DBTableSource("test", "c");


    }


    /**
     * @input 空表
     * @oracle 关联表size=0
     * @passed yes
     */
    public void testGetRelationsByTablesZeroNewZeroOld() throws Exception {
        clear();
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.isEmpty());

    }

    /**
     * @input 新表A, 无旧表
     * @oracle 关联表size=0
     * @passed yes
     */
    public void testGetRelationsByTablesOneNewZeroOld() throws Exception {

        clear();
        newTableSources.put("a", tableSourceA);
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.isEmpty());

    }

    /**
     * @input 新表A, B, C 关系为(A->B->C),无旧表
     * @oracle 关联表size=2
     * @passed yes
     * @see BIImportDBTableTestTool#tableRelationA
     */
    public void testGetRelationsByTables3New0Old() throws Exception {

        clear();
        newTableSources.put("a", tableSourceA);
        newTableSources.put("b", tableSourceB);
        newTableSources.put("c", tableSourceC);
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.size() == 2);


    }

    /**
     * @input 旧表A, B, C 关系为(A->B->C),新表添加A(a1)
     * @oracle 关联表size=1
     * @passed yes
     */
    public void testGetRelationsByTables1New3Old() throws Exception {

        clear();
        allTableSources.put("a", tableSourceA);
        allTableSources.put("b", tableSourceB);
        allTableSources.put("c", tableSourceC);
        newTableSources.put("a1", tableSourceA);
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.size() == 1);

    }

    /**
     * @input 旧表A, B, C 关系为(A->B->C),新表添加B(b1)
     * @oracle 关联表size=2
     * @passed yes
     */
    public void testGetRelationsByTables1New3Old2() throws Exception {

        clear();
        allTableSources.put("a", tableSourceA);
        allTableSources.put("b", tableSourceB);
        allTableSources.put("c", tableSourceC);
        newTableSources.put("b1", tableSourceB);
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.size() == 2);


    }

    /**
     * @input 旧表A, B, C 关系为(A->B->C),新表添加C(c1)
     * @oracle 关联表size=1
     * @passed yes
     */
    public void testGetRelationsByTables1New3Old3() throws Exception {

        clear();
        allTableSources.put("a", tableSourceA);
        allTableSources.put("b", tableSourceB);
        allTableSources.put("c", tableSourceC);
        newTableSources.put("c1", tableSourceC);
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.size() == 1);


    }

    /**
     * @input 旧表A, B, C 关系为(A->B->C),新表添加ABC
     * @oracle 关联表size=6
     * @passed yes
     */
    public void testGetRelationsByTables2New3Old() throws Exception {

        clear();
        allTableSources.put("a", tableSourceA);
        allTableSources.put("b", tableSourceB);
        allTableSources.put("c", tableSourceC);
        newTableSources.put("a1", tableSourceA);
        newTableSources.put("b1", tableSourceB);
        newTableSources.put("c1", tableSourceC);
        relations = executor.getRelationsByTables(newTableSources, allTableSources, allFieldIdMap, 1);
        assertTrue(relations.size() == 6);


    }

    private void clear() {
        newTableSources.clear();
        relations.clear();
        allTableSources.clear();
    }

}
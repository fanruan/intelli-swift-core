package com.fr.swift.fine.adaptor.conf.relation;

import com.finebi.conf.internalimp.relation.FineBusinessTableRelationIml;
import com.finebi.conf.structure.bean.field.FineBusinessField;
import com.finebi.conf.structure.relation.FineBusinessTableRelation;
import com.fr.swift.conf.business.ISwiftXmlWriter;
import com.fr.swift.conf.business.relation.SwiftRelationDao;
import com.fr.swift.conf.business.table.SwiftTableDao;
import com.fr.swift.conf.business.table.TableParseXml;
import com.fr.swift.conf.business.table.TableXmlWriter;
import com.fr.swift.fine.adaptor.conf.creater.TestFieldCreator;
import com.fr.swift.fine.adaptor.conf.creater.TestTableCreator;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2018-1-24 10:08:34
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI Analysis 1.0
 */
public class SwiftRelationDaoTest extends TestCase {

    private TableParseXml xmlHandler = new TableParseXml();
    private ISwiftXmlWriter swiftXmlWriter = new TableXmlWriter();
    private String fileName = "table.xml";
    private SwiftTableDao tableDao = new SwiftTableDao(xmlHandler, fileName, swiftXmlWriter);
    private SwiftRelationDao relationDao = new SwiftRelationDao(xmlHandler, fileName, swiftXmlWriter);


    /**
     * 但关联的增、改、删
     */
    @Test
    public void testRelation() {
        tableDao.removeAllConfig();
        relationDao.removeAllConfig();
        tableDao.saveConfig(TestTableCreator.createA());
        tableDao.saveConfig(TestTableCreator.createB());
        tableDao.saveConfig(TestTableCreator.createC());
        tableDao.saveConfig(TestTableCreator.createD());

        List<FineBusinessField> primaryFieldList = new ArrayList<FineBusinessField>();
        List<FineBusinessField> foreignFieldList = new ArrayList<FineBusinessField>();
        primaryFieldList.add(TestFieldCreator.fieldA);
        foreignFieldList.add(TestFieldCreator.fieldB);
        FineBusinessTableRelation fineBusinessTableRelation = new FineBusinessTableRelationIml(primaryFieldList, foreignFieldList, TestTableCreator.createA(), TestTableCreator.createB(), 1);

        List<FineBusinessField> primaryFieldList2 = new ArrayList<FineBusinessField>();
        List<FineBusinessField> foreignFieldList2 = new ArrayList<FineBusinessField>();
        primaryFieldList2.add(TestFieldCreator.fieldB);
        foreignFieldList2.add(TestFieldCreator.fieldC);
        FineBusinessTableRelation fineBusinessTableRelation2 = new FineBusinessTableRelationIml(primaryFieldList2, foreignFieldList2, TestTableCreator.createB(), TestTableCreator.createC(), 1);


        List<FineBusinessTableRelation> relationList = new ArrayList<FineBusinessTableRelation>();
        relationList.add(fineBusinessTableRelation);
        relationList.add(fineBusinessTableRelation2);
        assertEquals(relationDao.getAllConfig().size(), 0);

        relationDao.saveConfig(fineBusinessTableRelation);
        relationDao.saveConfig(fineBusinessTableRelation2);
        List<FineBusinessTableRelation> getRelationList = relationDao.getAllConfig();
        assertEquals(getRelationList.size(), 2);
        assertEquals(getRelationList.get(0).getRelationName(), fineBusinessTableRelation.getRelationName());
        assertEquals(getRelationList.get(1).getRelationName(), fineBusinessTableRelation2.getRelationName());

        relationDao.updateConfig(fineBusinessTableRelation);
        assertEquals(relationDao.getAllConfig().size(), 2);

        relationDao.removeConfig(fineBusinessTableRelation);
        assertEquals(relationDao.getAllConfig().size(), 1);
        assertEquals(relationDao.getAllConfig().get(0).getRelationName(), fineBusinessTableRelation2.getRelationName());

    }

    /**
     * 多关联的增、改、删
     */
    @Test
    public void testRelations() {
        tableDao.removeAllConfig();
        relationDao.removeAllConfig();
        tableDao.saveConfig(TestTableCreator.createA());
        tableDao.saveConfig(TestTableCreator.createB());
        tableDao.saveConfig(TestTableCreator.createC());
        tableDao.saveConfig(TestTableCreator.createD());

        List<FineBusinessField> primaryFieldList = new ArrayList<FineBusinessField>();
        List<FineBusinessField> foreignFieldList = new ArrayList<FineBusinessField>();
        primaryFieldList.add(TestFieldCreator.fieldA);
        foreignFieldList.add(TestFieldCreator.fieldB);
        FineBusinessTableRelation fineBusinessTableRelation = new FineBusinessTableRelationIml(primaryFieldList, foreignFieldList, TestTableCreator.createA(), TestTableCreator.createB(), 1);

        List<FineBusinessField> primaryFieldList2 = new ArrayList<FineBusinessField>();
        List<FineBusinessField> foreignFieldList2 = new ArrayList<FineBusinessField>();
        primaryFieldList2.add(TestFieldCreator.fieldB);
        foreignFieldList2.add(TestFieldCreator.fieldC);
        FineBusinessTableRelation fineBusinessTableRelation2 = new FineBusinessTableRelationIml(primaryFieldList2, foreignFieldList2, TestTableCreator.createB(), TestTableCreator.createC(), 1);

        List<FineBusinessTableRelation> relationList = new ArrayList<FineBusinessTableRelation>();
        relationList.add(fineBusinessTableRelation);
        relationList.add(fineBusinessTableRelation2);
        relationDao.removeAllConfig();
        assertEquals(relationDao.getAllConfig().size(), 0);


        relationDao.saveConfigs(relationList);
        List<FineBusinessTableRelation> getRelationList = relationDao.getAllConfig();
        assertEquals(getRelationList.size(), 2);
        assertEquals(getRelationList.get(0).getRelationName(), fineBusinessTableRelation.getRelationName());
        assertEquals(getRelationList.get(1).getRelationName(), fineBusinessTableRelation2.getRelationName());


        relationDao.updateConfigs(relationList);
        assertEquals(relationDao.getAllConfig().size(), 2);


        relationDao.removeConfigs(relationList);
        assertEquals(relationDao.getAllConfig().size(), 0);
    }

}

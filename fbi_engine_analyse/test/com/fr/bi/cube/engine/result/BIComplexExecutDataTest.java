package com.fr.bi.cube.engine.result;

import com.fr.bi.report.data.dimension.BIDateDimension;
import com.fr.bi.report.data.dimension.BIDimension;
import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by Connery on 14-11-6.
 */
public class BIComplexExecutDataTest extends TestCase {


    /**
     * 单元测试方法
     */
    public void testGetDimensionArray() {

        BIComplexExecutData bi = newBIComplexExecutData();
        BIDimension[] bi2 = bi.getDimensionArray(0);
        assertFalse(bi2 == null);
        //error:只要index不为0，即便数组为空仍然会访问数组。
//        BIDimension[] bi3=   bi.getDimensionArray(1);
    }

    /**
     * 单元测试方法
     */
    public void testGetColumnRowSpan() {
        BIComplexExecutData bi = newBIComplexExecutData();
        int result = bi.getColumnRowSpan(1, 1);
        assertSame(result, 1);
    }

    /**
     * 单元测试方法
     */
    public void tesGetMaxArrayLength() {
        BIComplexExecutData bi = newBIComplexExecutData();
        int result = bi.getMaxArrayLength();
        assertSame(result, 5);
    }

    /**
     * 单元测试方法
     */
    public void testGetDimensionIndexFromRow() {
        BIComplexExecutData bi = newBIComplexExecutData();
        int result = bi.getDimensionIndexFromRow(1, 1);
        assertSame(result, 1);
        int result1 = bi.getDimensionIndexFromRow(1, 2);
        assertSame(result1, 1);
        int result2 = bi.getDimensionIndexFromRow(2, 1);
        assertSame(result2, 2);
    }

    /**
     * 单元测试方法
     */
    public void testGetDimensionRegionFromDimension() {
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
        array.add(content);
        BIDateDimension[] biDateDimensions = new BIDateDimension[5];
        BIComplexExecutData bi = new BIComplexExecutData(array, biDateDimensions);
        int result = bi.getDimensionRegionFromDimension(biDateDimensions);
        assertSame(result, 0);

    }

    /**
     * 单元测试方法
     */
    public void testGetNoneChildRowSpan() {
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
        array.add(content);
        BIDateDimension[] biDateDimensions = new BIDateDimension[5];
        BIComplexExecutData bi = new BIComplexExecutData(array, biDateDimensions);
        int result = bi.getNoneChildRowSpan(1, 1);
        assertSame(result, 0);
        int result1 = bi.getNoneChildRowSpan(2, 1);
        assertSame(result1, -1);
        int result2 = bi.getNoneChildRowSpan(1, 2);
        assertSame(result2, 1);

    }

    /**
     * 单元测试方法
     */
    public void testGetNoneChildSpan() {
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
        array.add(content);
        BIDateDimension[] biDateDimensions = new BIDateDimension[5];
        BIComplexExecutData bi = new BIComplexExecutData(array, biDateDimensions);
        int result = bi.getNoneChildSpan(1, 1);

        int result1 = bi.getNoneChildSpan(2, 1);

        int result2 = bi.getNoneChildSpan(1, 2);
        assertSame(result1, -1);
        assertSame(result, 0);
        assertSame(result2, 1);

    }

    /**
     * 单元测试方法
     */
    public void testGetNormalRowSpan() {
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
        array.add(content);
        BIDateDimension[] biDateDimensions = new BIDateDimension[5];
        BIComplexExecutData bi = new BIComplexExecutData(array, biDateDimensions);
        int result = bi.getNormalRowSpan(1, 1);

        int result1 = bi.getNormalRowSpan(2, 1);

        int result2 = bi.getNormalRowSpan(1, 2);
        assertSame(result1, 1);
        assertSame(result, 1);
        assertSame(result2, 1);

    }


    private BIComplexExecutData newBIComplexExecutData() {
        ArrayList<String> content = new ArrayList<String>();
        ArrayList<ArrayList<String>> array = new ArrayList<ArrayList<String>>();
        array.add(content);
        return new BIComplexExecutData(array, new BIDateDimension[5]);
    }

}
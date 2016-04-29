package com.fr.bi.cal.analyze.cal.result;

import com.fr.bi.conf.report.widget.field.dimension.BIDimension;
import com.fr.general.ComparatorUtils;

import java.util.ArrayList;

/**
 * 记录复杂表格的一些数据
 * Created by sheldon on 14-9-2.
 */
public class BIComplexExecutData {
    private ArrayList<BIDimension[]> arrrayDimensions;

    public BIComplexExecutData(ArrayList<ArrayList<String>> arrayLists, BIDimension[] dimensions) {
        arrrayDimensions = new ArrayList<BIDimension[]>();

        for (int index = 0; index < arrayLists.size(); index++) {

            ArrayList<String> arrayList = arrayLists.get(index);
            int dimensionLen = arrayList.size();

            BIDimension[] dimensions1 = new BIDimension[dimensionLen];

            for (int i = 0; i < dimensionLen; i++) {
                String dimensionName = arrayList.get(i);

                for (int j = 0; j < dimensions.length; j++) {
                    if (ComparatorUtils.equals(dimensions[j].getValue(), dimensionName)) {
                        dimensions1[i] = dimensions[j];
                    }
                }
            }

            arrrayDimensions.add(dimensions1);
        }
    }

    //当一个区域时
    public BIComplexExecutData(BIDimension[] dimensions) {

        arrrayDimensions = new ArrayList<BIDimension[]>();
        arrrayDimensions.add(dimensions);
    }

    public int getDimensionArrayLength() {
        return arrrayDimensions.size();
    }

    /**
     * 获取第index个维度区域
     *
     * @param index 属于第几个
     * @return
     */
    public BIDimension[] getDimensionArray(int index) {
        if (arrrayDimensions.isEmpty() && index == 0) {
            return new BIDimension[0];
        }

        return arrrayDimensions.get(index);
    }

    /**
     * 获取最大的数组里面的元素
     */
    public int getMaxArrayLength() {
        int max = 0;
        for (int i = 0; i < arrrayDimensions.size(); i++) {
            if (arrrayDimensions.get(i).length > max) {
                max = arrrayDimensions.get(i).length;
            }

        }

        return max;
    }

    /**
     * 获取占据多少行
     *
     * @param column             当前的行数
     * @param rowDimensionLength 总共的列维度的个数
     * @return 当column列时，应该占有列长
     */
    public int getColumnRowSpan(int column, int rowDimensionLength) {
        if (arrrayDimensions.size() == 1 || column >= rowDimensionLength) {
            return 1;
        }

        int maxColumnSpan = getMaxArrayLength();
        return column == 0 ? (maxColumnSpan - maxColumnSpan / rowDimensionLength * (rowDimensionLength - 1)) : maxColumnSpan / rowDimensionLength;
    }

    /**
     * column是从第0行开始的
     * 获取某一维度，子节点没有展开时占据的宽度
     */
    public int getNoneChildSpan(int column, int rowDimensionLength) {
        int maxColumnSpan = getMaxArrayLength();
        if (arrrayDimensions.size() == 1) {

            return rowDimensionLength - column;

        } else if (column < rowDimensionLength) {

            if (column == 0) {
                return maxColumnSpan;
            } else {
                return (rowDimensionLength - column) * (maxColumnSpan / rowDimensionLength);
            }
        } else {

            return 1;
        }
    }

    /**
     * 获取正常情况下，第row行数据应该占据的行高
     *
     * @param row                表示实际的行数  表示实际的行数，这里和column的做法不一样
     * @param columnDimensionLen 当前区域的列维度的个数
     * @return 占据的行高
     */
    public int getNormalRowSpan(int row, int columnDimensionLen) {

        int maxRowSpan = this.getMaxArrayLength();
        if (arrrayDimensions.size() == 1 || row >= columnDimensionLen) {
            return 1;
        }

        if (row == 0) {
            return maxRowSpan - maxRowSpan / columnDimensionLen * (columnDimensionLen - 1);
        } else {
            return maxRowSpan / columnDimensionLen;
        }

    }

    /**
     * 获取当前row，在子节点没有展开时应该占据的行高
     *
     * @param row                表示实际的行数，这里和column的做法不一样
     * @param columnDimensionLen 当前区域的列维度的个数
     * @return 占据的行高
     */
    public int getNoneChildRowSpan(int row, int columnDimensionLen) {
        int maxRowSpan = this.getMaxArrayLength();

        if (arrrayDimensions.size() == 1) {
            return columnDimensionLen - row;
        }

        return maxRowSpan - row;
    }

    /**
     * 根据所在 当前行获取这个这个维度属于第几个
     *
     * @param row                表示实际的行数，这里和column的做法不一样
     * @param columnDimensionLen 当前区域的列维度的个数
     * @return 占据的行高
     */
    public int getDimensionIndexFromRow(int row, int columnDimensionLen) {
        if (arrrayDimensions.size() == 1) {
            return row;
        }
        int maxRowSpan = getMaxArrayLength();
        int meanSpan = maxRowSpan / columnDimensionLen;
        if (row == 0) {
            return 0;
        } else {
            return (row - (maxRowSpan - meanSpan * (columnDimensionLen - 1))) / meanSpan + 1;
        }
    }


    /**
     * 通过rowDimensions获取到
     */
    public int getDimensionRegionFromDimension(BIDimension[] dimensions) {
        for (int i = 0; i < arrrayDimensions.size(); i++) {
            if (ComparatorUtils.equals(dimensions, arrrayDimensions.get(i))) {
                return i;
            }
        }

        return 0;
    }


    /**
     *
     */
    public int getRegionIndex() {
        return arrrayDimensions.size();
    }
}
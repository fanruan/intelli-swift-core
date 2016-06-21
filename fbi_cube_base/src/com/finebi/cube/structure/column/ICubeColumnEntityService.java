package com.finebi.cube.structure.column;

import com.finebi.cube.structure.ICubeDetailDataService;
import com.finebi.cube.structure.ICubeIndexDataService;
import com.finebi.cube.structure.ICubeRelationManagerService;
import com.finebi.cube.structure.ITableKey;
import com.finebi.cube.structure.group.ICubeGroupDataService;
import com.fr.bi.stable.gvi.GroupValueIndex;

import java.util.Comparator;

/**
 * Cube中的列实体接口。
 * 负责对列数据的读写。
 * 单词意思解释：groupValue 分组值，指的就是一列数据中的某一个值。与originalValue的区别在于
 * originalValue是可能重复的，而groupValue是不重复的。
 * 列的数据由：详细数据，分组信息，位图数据三部分
 * <p/>
 * 详细数据：是值从数据库或其他数据源按照行号，读取出来的
 * 数据。这里是原始数据，是包含重复数据的。
 * <p/>
 * 分组信息：是groupValue按照规则排序后，按照位置进行记录的。
 * <p/>
 * 位图数据：一个groupValue对应着一个位图索引，按照groupvalue的顺序，将其对应的索引记录下来。
 * <p/>
 * 查询某一个groupValue的位图索引，首先，找到groupValue的位置，然后在位图数据中找到索引
 * <p/>
 * This class created on 2016/3/2.
 *
 * @author Connery
 * @see ICubeDetailDataService 详细信息
 * @see ICubeGroupDataService 分组信息
 * @see ICubeIndexDataService 位图索引
 * @since 4.0
 */
public interface ICubeColumnEntityService<T> extends CubeColumnReaderService<T> {

    void setRelationManagerService(ICubeRelationManagerService relationManagerService);

    Comparator<T> getGroupComparator();


    /**
     * 从数据库中读取的原始数值，
     * 添加数据库原始数据记录，
     *
     * @param rowNumber
     * @param originalValue 数据库中数据值
     */
    void addOriginalDataValue(int rowNumber, T originalValue);

    /**
     * 添加分组值和排序位置的对应关系
     *
     * @param position   排序后的位置
     * @param groupValue 分组值
     */
    void addGroupValue(int position, T groupValue);

    /**
     * 经过排序分组后，以分组值所在的位置代替分组值。
     * 添加对应位置分组值的索引值
     *
     * @param position 排序后分组值的所在位置
     * @param index    分组值的索引
     */
    void addGroupIndex(int position, GroupValueIndex index);


    void recordSizeOfGroup(int size);





    /**
     * 在对应位置添加空值
     *
     * @param position        位置
     * @param groupValueIndex 索引值
     */
    void addNULLIndex(int position, GroupValueIndex groupValueIndex);

    void copyDetailValue(ICubeColumnEntityService columnEntityService, long rowCount);

    void setOwner(ITableKey owner);
}

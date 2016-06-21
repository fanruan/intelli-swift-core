package com.finebi.cube.structure;

import com.finebi.cube.data.input.primitive.ICubePrimitiveReader;
import com.finebi.cube.data.output.primitive.ICubePrimitiveWriter;
import com.finebi.cube.exception.BICubeColumnAbsentException;
import com.finebi.cube.exception.BICubeRelationAbsentException;
import com.finebi.cube.exception.IllegalRelationPathException;
import com.finebi.cube.location.ICubeResourceRetrievalService;
import com.finebi.cube.structure.column.BIColumnKey;
import com.finebi.cube.structure.column.ICubeColumnReaderService;

/**
 * Cube基础数据部分主接口。
 * CUBE整体包括对外提供的服务的内部抽象结构，数据位置检索，数据存储，三个部分。
 * <p/>
 * CUBE的数据结构由三部分组成。分别是表，列，关联。
 * <p/>
 * 其中所有对象都是在接口层面进行实现的。类不依赖具体某一个类型，只依赖接口。可以容易改变实现。
 * <p/>
 * CUBE数据位置检索负责对象于值的映射工作。例如拿到表名，如何获得某一列的值，这就是通过数据位置检索
 * 来到数据存储部分去取的相应数据接口。这里是通过uri进行，其中query标识是读还是写，fragment标识是所获取的类型。
 * <p/>
 * 这里同样是面向接口的。可以很容易实现其他检索方法。
 * <p/>
 * 这部分是面向接口最主要的受益者。可以预见要添加内存cube存储。
 * <p/>
 * This class created on 2016/3/10.
 *
 * @author Connery
 * @see com.fr.bi.cube.structure 包中是对结构的实现
 * @see ICubePrimitiveReader 负责读的全部操作
 * @see ICubePrimitiveWriter 负责写的全部操作
 * @see ICubeResourceRetrievalService 数据位置检索
 * @since 4.0
 */
public interface ICube extends ICubeVersion {
    /**
     * 获得表的操作接口
     *
     * @param tableSource 数据源
     * @return 表的操作对象
     */
    ICubeTableEntityGetterService getCubeTable(ITableKey tableKey);

    ICubeTableEntityService getCubeTableWriter(ITableKey tableKey);

    /**
     * 获取列的操作接口
     *
     * @param key         列
     * @param tableSource 数据源
     * @param field
     * @return 列的操作对象
     */
    ICubeColumnReaderService getCubeColumn(ITableKey tableKey, BIColumnKey field) throws BICubeColumnAbsentException;

    /**
     * 关联的操作接口
     *
     * @param tableSource  数据源
     * @param relationPath 关联路径
     * @return 关联路径的操作对象
     */
    ICubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeTablePath relationPath) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException;


    /**
     * 关联的操作接口
     *
     * @param tableSource  数据源
     * @param relationPath 关联路径
     * @param relation
     * @return 关联路径的操作对象
     */
    ICubeRelationEntityGetterService getCubeRelation(ITableKey tableKey, BICubeRelation relation) throws BICubeRelationAbsentException, BICubeColumnAbsentException, IllegalRelationPathException;

    boolean canRead(ITableKey tableKey);
}

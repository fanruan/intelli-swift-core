package com.fr.bi.stable.gvi;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.fr.bi.base.provider.ParseBytes;
import com.fr.bi.stable.gvi.traversal.BrokenTraversalAction;
import com.fr.bi.stable.gvi.traversal.SingleRowTraversalAction;
import com.fr.bi.stable.gvi.traversal.TraversalAction;

/**
 * @author priest
 */
public interface GroupValueIndex extends java.io.Serializable, ParseBytes {

    /**
     * 与操作
     * 需要行数相同
     * TODO 这里and有一些小问题，在and的过程中若参数为null 则返回原值
     * 在某些地方是需要的，但是在某些地方是不正确的，逻辑上也是不正确的
     * 在外部增加了一些额外的判断在做这个事情，需要简化操作
     *
     * @param valueIndex 另一个
     * @return and结果
     */
    GroupValueIndex AND(GroupValueIndex valueIndex);

    /**
     * 或操作
     * 需要索引行数相同
     *
     * @param valueIndex 另一个
     * @return or结果
     */
    GroupValueIndex OR(GroupValueIndex valueIndex);
    
    GroupValueIndex ANDNOT(GroupValueIndex index);


    /**
     * 非操作
     *
     * @return not结果
     */
    GroupValueIndex NOT(int rowCount);

    /**
     * 生成索引值的方法，需要依次顺序添加
     *
     * @param index 第几个
     * @param value 值
     */
    void addValueByIndex(int index);

    /**
     * 是否为全空索引
     *
     * @return 是否为空
     */
    boolean isAllEmpty();

    /**
     * 循环遍历,寻找二进制“1”的位置,TraversalAction里面的rowIndex就是1所在行的位置
     * 用于计算汇总值以及生成关联索引时使用
     *
     * @param action 操作
     */
    void Traversal(TraversalAction action);

    /**
     * 循环遍历,寻找二进制“1”的位置,TraversalAction里面的rowIndex就是1所在行的位置
     * 用于计算汇总值以及生成关联索引时使用
     *
     * @param action 操作
     */
    void Traversal(SingleRowTraversalAction action);

    /**
     * 循环遍历,寻找“1”的位置,TraversalAction里面的rowIndex就是1所在行的位置.能中途跳出遍历
     * return true break;
     * false continue
     *
     * @param action 操作
     */
    boolean BrokenableTraversal(BrokenTraversalAction action);

    /**
     * 在某位置是否有值(二进制1的位置)
     *
     * @param rowIndex 行号(0 ~ max)
     * @return 在某位置是否有值(二进制1的位置)
     */
    boolean isOneAt(int rowIndex);

    /**
     * 有数据的行数
     *
     * @return 有数据的行数
     */
    public int getRowsCountWithData();

    /**
     * 值相同
     *
     * @param parentIndex 另一个
     * @return 是否相同
     */
    public boolean hasSameValue(GroupValueIndex parentIndex);

    /**
     * 把数据写出到流
     *
     * @param out 输出流
     * @throws IOException
     */
    public void write(DataOutput out) throws IOException;

    /**
     * 根据数据流生成字段
     *
     * @param in 数据流
     * @throws IOException
     */
    public void readFields(DataInput in) throws IOException;

	GroupValueIndex or(GroupValueIndex index);

	GroupValueIndex and(GroupValueIndex index);
	
	GroupValueIndex andnot(GroupValueIndex index);
	
	GroupValueIndex clone();
	

}
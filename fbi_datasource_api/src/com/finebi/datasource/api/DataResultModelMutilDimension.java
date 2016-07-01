package com.finebi.datasource.api;

/**
 * This class created on 2016/6/30.
 *
 * @author Connery
 * @since 4.0
 */
public interface DataResultModelMutilDimension<T> {
    int getSpecificDimesionSize(int dimension);

    T getValue(int... dimensionIndex) throws Exception;

    void setValue(T value, int... dimensionIndex) throws Exception;

    void removeValue(int... dimensionIndex) throws Exception;

    boolean exist(int... dimensionIndex);

    boolean exist(T value);

    void removeValue(T value);
}

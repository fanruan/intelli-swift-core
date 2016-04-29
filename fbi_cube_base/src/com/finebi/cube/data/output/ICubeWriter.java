package com.finebi.cube.data.output;

import com.fr.bi.common.inter.Release;
import com.finebi.cube.BICubeLongTypePosition;

/**
 * This class created on 2016/3/16.
 *
 * @author Connery
 * @since 4.0
 */
public interface ICubeWriter<T> extends Release {
    void recordSpecificValue(int specificPosition, T value);

    void saveStatus();

    void setPosition(BICubeLongTypePosition position);

    void flush();
}

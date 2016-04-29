package com.finebi.cube.tools;

import com.fr.bi.stable.gvi.GroupValueIndex;
import com.fr.bi.stable.gvi.RoaringGroupValueIndex;
import com.fr.bi.stable.utils.algorithem.BIRandomUitils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class created on 2016/3/29.
 *
 * @author Connery
 * @since 4.0
 */
public class GroupValueIndexTestTool {
    public static GroupValueIndex generateSampleIndex() {
        Integer[] values = new Integer[2];
        values[0] = 1;
        values[1] = 4;
        return RoaringGroupValueIndex.createGroupValueIndex(values);
    }

    public static GroupValueIndex generateRandomIndex() {
        int oneSize = 100;
        int indexLength = 10000;
        int size = Math.abs(BIRandomUitils.getRandomInteger()) % oneSize;
        Integer[] values = new Integer[size];
        for (int i = 0; i < size; i++) {
            values[i] = BIRandomUitils.getRandomInteger() % indexLength;
        }
        return RoaringGroupValueIndex.createGroupValueIndex(values);
    }

    public static GroupValueIndex build(List<String> target, String one) {
        ArrayList<Integer> count = new ArrayList<Integer>();
        for (int i = 0; i < target.size(); i++) {
            if (target.get(i).equals(one)) {
                count.add(i);
            }
        }
        Integer[] temp = new Integer[count.size()];
        for (int i = 0; i < count.size(); i++) {
            temp[i] = count.get(i);
        }
        return RoaringGroupValueIndex.createGroupValueIndex(temp);
    }

    public static GroupValueIndex build(List<Long> target, Long one) {
        ArrayList<Integer> count = new ArrayList<Integer>();
        for (int i = 0; i < target.size(); i++) {
            if (target.get(i).equals(one)) {
                count.add(i);
            }
        }
        Integer[] temp = new Integer[count.size()];
        for (int i = 0; i < count.size(); i++) {
            temp[i] = count.get(i);
        }
        return RoaringGroupValueIndex.createGroupValueIndex(temp);
    }
}

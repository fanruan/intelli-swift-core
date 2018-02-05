package com.fr.swift.query.group;

/**
 * @author anchore
 * @date 2018/1/26
 */
public class Groups {
    public static Group newGroup(GroupType type) {
        switch (type) {
            case YEAR:
            case QUARTER:
            case MONTH:
            case WEEK:
            case DAY:
            case HOUR:
            case MINUTE:
            case SECOND:
            case WEEK_OF_YEAR:
            case Y_M_D_H_M_S:
            case Y_M_D_H_M:
            case Y_M_D_H:
            case Y_M_D:
            case Y_Q:
            case Y_M:
            case Y_W:
            case Y_D:
            case M_D:
            case NONE:
//                return new NoGroup(type);
            default:
                return null;
        }
    }
}

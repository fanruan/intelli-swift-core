package com.fr.swift.query.sort;

/**
 * Created by pony on 2018/1/23.
 */
public class ASCSort extends AbstractSort{
    public ASCSort(int targetIndex) {
        super(targetIndex);
    }

    @Override
    public SortType getSortType() {
        return SortType.ASC;
    }
}

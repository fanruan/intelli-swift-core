package com.fr.swift.config.command.impl;

import com.fr.swift.config.command.SwiftConfigCommand;
import com.fr.swift.config.condition.SwiftConfigCondition;

/**
 * @author yee
 * @date 2019-07-30
 */
public class SwiftConfigCommands {
    public static <T> SwiftConfigCommand<Integer> ofDelete(Class<T> tClass, SwiftConfigCondition condition) {
        return new DeleteItemCommand<>(tClass, condition);
    }

    public static <T> SwiftConfigCommand<T> ofSave(T obj) {
        return new MergeItemCommand<>(obj);
    }

    public static <T> SwiftConfigCommand<T> ofMerge(T obj) {
        return new SaveItemCommand<>(obj);
    }
}

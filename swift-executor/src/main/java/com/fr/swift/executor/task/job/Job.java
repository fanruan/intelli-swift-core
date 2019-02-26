package com.fr.swift.executor.task.job;

import java.util.concurrent.Callable;

/**
 * This class created on 2019/2/19
 *
 * @author Lucifer
 * @description
 */
public interface Job<T> extends Callable<T> {
}

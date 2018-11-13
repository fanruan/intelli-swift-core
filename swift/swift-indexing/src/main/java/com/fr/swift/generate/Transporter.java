package com.fr.swift.generate;

import com.fr.swift.task.WorkerTask;

import java.util.List;

/**
 * This class created on 2018/4/17
 *
 * @author Lucifer
 * @description
 * @since Advanced FineBI 5.0
 */
public interface Transporter extends WorkerTask.Worker {
    void transport() throws Exception;

    List<String> getIndexFieldsList();
}
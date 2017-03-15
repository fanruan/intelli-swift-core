package com.finebi.cube.structure;

import com.finebi.cube.exception.BIResourceInvalidException;

import java.util.List;

/**
 * Created by naleite on 16/3/18.
 */
public interface ICubeFieldsNameGetterService {

    List<String> getColumnsNames() throws BIResourceInvalidException;
}

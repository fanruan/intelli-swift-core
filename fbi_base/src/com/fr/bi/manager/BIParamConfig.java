package com.fr.bi.manager;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * This class created on 2017/4/14.
 *
 * @author Each.Zhang
 */
public interface BIParamConfig {
    //读取文件
    Map<String, String> readConfig(InputStream inputStream);

    //写入到文件
    boolean writeConfig(Map<String, String> newParam, OutputStream outputStream);

    //写入方法执行之前需要对写入的参数进行格式化
    Map<String, String> beforeDoWrite(Map<String, String> runParam, Map<String, String> newParam, Map<String, String> resultParam);
}

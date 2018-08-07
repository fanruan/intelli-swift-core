package com.fr.swift.io.nio;

import com.fr.swift.io.Io;
import com.fr.swift.util.Util;

import java.io.File;

/**
 * @author anchore
 * @date 2018/7/20
 */
abstract class BaseNio implements Io {
    NioConf conf;

    BaseNio(NioConf conf) {
        this.conf = conf;
    }

    @Override
    public boolean isReadable() {
        File f = new File(conf.getPath());
        return f.exists() && !Util.isEmpty(f.list());
    }
}
package com.fr.swift.cloud.cube.io.impl.nio;

import com.fr.swift.cloud.cube.io.Io;
import com.fr.swift.cloud.util.Util;

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
        if (!f.exists()) {
            return false;
        }
        String[] children = f.list();
        return !Util.isEmpty(children) && new File(f, children[0]).length() > 0;
    }
}
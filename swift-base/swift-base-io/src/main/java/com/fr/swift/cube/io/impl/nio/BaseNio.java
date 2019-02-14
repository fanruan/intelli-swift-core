package com.fr.swift.cube.io.impl.nio;

import com.fr.swift.cube.io.Io;
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
        if (!f.exists()) {
            return false;
        }
        String[] children = f.list();
        return !Util.isEmpty(children) && new File(f, children[0]).length() > 0;
    }
}
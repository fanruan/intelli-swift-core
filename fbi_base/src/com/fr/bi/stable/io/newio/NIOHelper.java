package com.fr.bi.stable.io.newio;


public interface NIOHelper {

    public static NIOHelper GENERATINGHELPER = new NIOHelper() {

        @Override
        public boolean canCreateNewNIOObject() {
            return true;
        }
    };

    public boolean canCreateNewNIOObject();

}
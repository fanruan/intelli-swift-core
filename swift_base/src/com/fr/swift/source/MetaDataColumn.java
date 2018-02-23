package com.fr.swift.source;

/**
 * 保存列的信息
 */
public class MetaDataColumn implements SwiftMetaDataColumn{
    private static final long serialVersionUID = -3638876557665551219L;
    private static final int DEFAULT_PRECISION = 255;
    private static final int DEFAULT_SCALE = 15;

    private int type;
    private String name;
    private String remark;
    private int precision;
    //小数位数
    private int scale;

    public MetaDataColumn(String name, int sqlType) {
        this(name, null, sqlType, DEFAULT_PRECISION, DEFAULT_SCALE);
    }


    public MetaDataColumn(String name, int sqlType, int size) {
        this(name, null, sqlType, size, DEFAULT_SCALE);
    }

    public MetaDataColumn(String name, int sqlType, int size, int scale) {
        this(name, null, sqlType, size, scale);
    }


    public MetaDataColumn(String name, String remark, int sqlType, int precision, int scale) {
        this.name = name;
        this.remark = remark;
        this.type = sqlType;
        this.precision = precision;
        this.scale = scale;
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getRemark(){
        return remark;
    }

    @Override
    public int getScale() {
        return scale;
    }


    /**
     * 克隆方法
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}

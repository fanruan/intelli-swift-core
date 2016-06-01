package com.fr.bi.stable.utils.file;

import com.finebi.cube.relation.BIBasicRelation;
import com.fr.base.FRContext;
import com.fr.bi.stable.conf.cubeconf.CubeConfManager;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;

import java.io.File;

/**
 * Created by GUY on 2015/3/4.
 */

/**
 * 文件路径生成工具
 */
public class BIPathUtils {

    public static final String ROW_INDEX = "index.row";
    private static final String CUBE = "cubes";
    private final static String BASEPATH = File.separator + ProjectConstants.RESOURCES_NAME + File.separator + CUBE;
    private final static String TEMPPATH = "_temp";
    private final static String GROUPPATH = "group";
    private final static String INDEXPATH = "index";
    private final static String NULLPATH = "_NULLPATH";
    private final static String SINGLEFIELD = "single_field_";
    private final static String DETAIL = "detail";
    private final static String CORRESPOND = "correspond_";
    private final static String MAINPATH = "main";
    private final static String LASTGENERATETIME = "time";
    private final static String REMOVED_LINE = "removedLine";
    private final static String OTHER = "other";
    private final static String SUFFIX = ".fcube";
    private final static String VERSION_CUBE = "version_cube";
    private final static String VERSION_TABLE = "version_table";
    private final static String LINK_INDEX = "linkedindex";
    private static final String GROUPLENGTH = "group_length_";
    private static final String INDEX = "_index";
    private static final String SIZE = "_size";
    private static final String USERETL = "user_etl";

    public static String createIndexPath(String basePath) {
        return basePath + INDEX + SUFFIX;
    }

    public static String createSizePath(String basePath) {
        return basePath + SIZE + SUFFIX;
    }

    public static String createDetailPath(String basePath) {
        return basePath + SUFFIX;
    }


    public static String tablePath(String md5) {
        return File.separator
                + md5;
    }

    /**
     * base路径
     *
     * @return base路径
     */
    public static String createBasePath() {
        return StringUtils.isEmpty(CubeConfManager.getInstance().getCubePath()) ? FRContext.getCurrentEnv().getPath()
                + BASEPATH : CubeConfManager.getInstance().getCubePath();
    }

    private static String createUserETLBasePath() {
        return new File(new File(createBasePath()).getParentFile(), USERETL
                + File.separator
                + File.separator + CUBE).getAbsolutePath();
    }

    public static String createUserETLTableBasePath(String md5) {
        return createUserETLBasePath() + File.separator + md5;
    }

    public static String createUserETLTablePath(String md5, String path) {
        return createUserETLTableBasePath(md5) + File.separator + path;
    }

    public static String createUserETLCubePath(String md5, String path) {
        return createUserETLTableBasePath(md5) + File.separator + path + File.separator + md5;
    }

    public static String createUserTotalPath(long userId) {
        return createBasePath()
                + (BIBaseConstant.BI_MODEL == BIBaseConstant.HIHIDATA ? (File.separator + userId) : "");
    }

    public static String createUserTotalTempPath(long userId) {
        return createBasePath()
                + TEMPPATH
                + (BIBaseConstant.BI_MODEL == BIBaseConstant.HIHIDATA ? (File.separator + userId) : "");
    }

    /**
     * t
     * 创建临时路径
     *
     * @param userId 用户id
     * @return 路径
     */
    public static String createTableTempPath(String md5, long userId) {
        return createUserTotalTempPath(userId)
                + tablePath(md5);
    }

    /**
     * 创建表路径
     *
     * @param userId 用户id
     * @return 字符串
     */
    public static String createTablePath(String md5, long userId) {
        return createUserTotalPath(userId)
                + tablePath(md5);
    }


    public static String createMainPath(String basePath) {
        return basePath + File.separator + MAINPATH + SUFFIX;
    }

    public static String createOtherPath(String basePath) {
        return basePath + File.separator + OTHER + SUFFIX;
    }


    public static String createLastTimePath(String basePath) {
        return basePath + File.separator + LASTGENERATETIME + SUFFIX;
    }

    public static String createVersionTablePath(String basePath) {
        return basePath + File.separator + VERSION_TABLE + SUFFIX;
    }

    public static String createVersionCubePath(String basePath) {
        return basePath + File.separator + VERSION_CUBE + SUFFIX;
    }


    public static String createVersionColumnPath(String basePath) {
        return basePath + VERSION_TABLE + SUFFIX;
    }

    public static String createVersionColumnCubePath(String basePath) {
        return basePath + VERSION_CUBE + SUFFIX;
    }

    public static String createRemovePath(String basePath) {
        return basePath + File.separator + REMOVED_LINE + SUFFIX;
    }


    public static String createSingleFieldBasePath(String basePath, String fieldName) {
        return basePath + File.separator + SINGLEFIELD + BIMD5Utils.getMD5String(new String[]{fieldName}) + File.separator;
    }


    public static String createSingleFieldDetailPath(String basePath) {
        return basePath + DETAIL + SUFFIX;
    }


    public static String createSingleFieldGroupPath(String basePath) {
        return basePath + GROUPPATH + SUFFIX;
    }

    public static String createSingleFieldIndexPath(String basePath) {
        return basePath + INDEXPATH + SUFFIX;
    }

    public static String createSingleFieldNullIndexPath(String basePath) {
        return basePath + INDEXPATH + NULLPATH + SUFFIX;
    }

    public static String createGroupLengthPath(String basePath) {
        return basePath + GROUPLENGTH + SUFFIX;
    }


    public static String createCorrespondPath(String basePath) {
        return basePath + CORRESPOND + SUFFIX;
    }


    public static String createRowIndexPath(String basePath, ArrayKey<? extends BIBasicRelation> key) {
        return createLinkIndexName(basePath, key) + File.separator + ROW_INDEX;
    }

    public static String createColumnLinkIndexPath(String basePath, ArrayKey<? extends BIBasicRelation> key) {
        return createColumnLinkIndexName(basePath, key) + File.separator + ROW_INDEX;
    }

    private static String createColumnLinkIndexName(String basePath, ArrayKey<? extends BIBasicRelation> key) {
        return basePath + LINK_INDEX + File.separator + createLinkIndexName(key);
    }


    /**
     * 关联路径
     *
     * @param basePath 基础路径
     * @param key      关联
     * @return 关联路径
     */
    private static String createLinkIndexName(String basePath, ArrayKey<? extends BIBasicRelation> key) {
        return basePath + File.separator + LINK_INDEX + File.separator + createLinkIndexName(key);
    }

    /**
     * 这里的relation顺序是反的
     *
     * @param key 关联
     * @return 关联路径
     */
    private static String createLinkIndexName(ArrayKey<? extends BIBasicRelation> key) {
        StringBuffer sb = new StringBuffer();
        BIBasicRelation[] relation = key.toArray();
        for (int i = 0; i < relation.length; i++) {
            sb.append(BIPathUtils.createLinkIndexName(relation[i]));
            sb.append("_");
        }
        return BIMD5Utils.getMD5String(new String[]{sb.substring(0, sb.length() - 1)});
    }

    /**
     * 关联路径
     *
     * @param relation 关联
     * @return 关联路径
     */
    private static String createLinkIndexName(BIBasicRelation relation) {
        BIField primaryKey = (BIField) relation.getPrimaryKey();
        BIField foreignKey = (BIField) relation.getForeignKey();
        return LINK_INDEX + primaryKey.getTableBelongTo().getID().getIdentityValue() + "_" + primaryKey.getFieldName() + "_TO_" + foreignKey.getTableBelongTo().getID().getIdentityValue() + "_" + foreignKey.getFieldName();
    }

}
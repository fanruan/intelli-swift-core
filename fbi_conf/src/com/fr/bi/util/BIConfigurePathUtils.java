package com.fr.bi.util;

import com.finebi.cube.relation.BIBasicRelation;
import com.fr.base.FRContext;
import com.fr.bi.conf.provider.BIConfigureManagerCenter;
import com.fr.bi.stable.constant.BIBaseConstant;
import com.fr.bi.stable.data.BIField;
import com.fr.bi.stable.structure.array.ArrayKey;
import com.fr.bi.stable.utils.algorithem.BIMD5Utils;
import com.fr.general.ComparatorUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;

import java.io.File;

/**
 * Created by Young's on 2016/5/19.
 */
public class BIConfigurePathUtils {

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
    private static final String USERETL = "user_etl";

    public static String getProjectLibPath() {
        return FRContext.getCurrentEnv().getPath() + File.separator + "lib";
    }

    public static String checkCubePath(String cubePath) {
        if (StringUtils.isEmpty(cubePath)) {
            return "";
        }
        String oldPath = BIConfigurePathUtils.createBasePath();
        if (ComparatorUtils.equals(cubePath, oldPath)) {
            return oldPath;
        }
        File file = new File(cubePath);
        if (!file.exists()) {
            try {
                boolean success = file.mkdirs();
                if (!success) {
                    return "";
                }
                return file.getAbsolutePath();
            } catch (Exception e) {
                return "";
            } finally {
                if (file.exists()) {
                    file.delete();
                }
            }
        }
        if (!file.isDirectory()) {
            return "";
        }
        if (file.list().length > 0) {
            return "warning";
        }
        return file.getAbsolutePath();
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
        return StringUtils.isEmpty(BIConfigureManagerCenter.getCubeConfManager().getCubePath()) ? FRContext.getCurrentEnv().getPath()
                + BASEPATH : BIConfigureManagerCenter.getCubeConfManager().getCubePath();
    }

    /**
     * 创建螺旋分析根目录, 如果cube根目录有parent目录,则螺旋分析目录user_utl与cube根目录并列
     * 如果cube根目录没有parent目录,user_utl则在cube的根目录下
     *
     */
    public static String createUserETLBasePath() {
        return getUserETLBasePath(createBasePath());
    }

    public static String getUserETLBasePath(String cubeBasePath) {
        File basePathFile = new File(cubeBasePath);
        File parentFile = null;

        if (basePathFile.getParentFile() != null) {
            parentFile = basePathFile.getParentFile();
        } else {
            parentFile = basePathFile;
        }

        return new File(parentFile, USERETL
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
        return createUserETLTablePath(md5, path) + File.separator + md5;
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
            sb.append(createLinkIndexName(relation[i]));
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

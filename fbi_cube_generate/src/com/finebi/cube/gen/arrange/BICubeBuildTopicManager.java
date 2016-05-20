package com.finebi.cube.gen.arrange;

import com.finebi.cube.exception.BIFragmentDuplicateException;
import com.finebi.cube.exception.BITopicAbsentException;
import com.finebi.cube.exception.BITopicDuplicateException;
import com.finebi.cube.gen.mes.BICubeBuildTopicTag;
import com.finebi.cube.gen.mes.BIFragmentUtils;
import com.finebi.cube.gen.mes.BITopicUtils;
import com.finebi.cube.router.IRouter;
import com.finebi.cube.structure.column.BIColumnKey;
import com.fr.bi.common.factory.BIFactoryHelper;
import com.fr.bi.stable.data.db.BICubeFieldSource;
import com.fr.bi.stable.data.source.ICubeTableSource;
import com.fr.bi.stable.relation.BITableSourceRelation;
import com.fr.bi.stable.relation.BITableSourceRelationPath;
import com.fr.bi.stable.utils.program.BINonValueUtils;

import java.util.Iterator;
import java.util.Set;

/**
 * This class created on 2016/4/12.
 *
 * @author Connery
 * @since 4.0
 */
public class BICubeBuildTopicManager {
    private IRouter router;


    public BICubeBuildTopicManager() {
        router = BIFactoryHelper.getObject(IRouter.class);
        router.reset();
        try {

            router.registerTopic(BICubeBuildTopicTag.DATA_SOURCE_TOPIC);
            router.registerTopic(BICubeBuildTopicTag.PATH_TOPIC);
            router.registerTopic(BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC);
            router.registerTopic(BICubeBuildTopicTag.START_BUILD_CUBE);
            router.registerTopic(BICubeBuildTopicTag.FINISH_BUILD_CUBE);
            router.registerTopic(BICubeBuildTopicTag.PATH_FINISH_TOPIC);
            router.registerTopic(BICubeBuildTopicTag.DATA_SOURCE_FINISH_TOPIC);
            router.registerTopic(BICubeBuildTopicTag.BI_CUBE_OCCUPIED_TOPIC);

        } catch (BITopicDuplicateException e) {
            throw BINonValueUtils.beyondControl(e.getMessage(), e);
        }

    }

    public void registerDataSource(Set<ICubeTableSource> tableSourceSet) {
        registerTable(tableSourceSet);
        registerTableField(tableSourceSet);
    }

    public void registerRelation(Set<BITableSourceRelation> relationSet) {
        Iterator<BITableSourceRelation> it = relationSet.iterator();
        while (it.hasNext()) {
            BITableSourceRelation relation = it.next();
            try {
                router.registerFragment(
                        BICubeBuildTopicTag.PATH_TOPIC,
                        BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, relation));
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    public void registerTableRelationPath(Set<BITableSourceRelationPath> relationPathSet) {
        Iterator<BITableSourceRelationPath> it = relationPathSet.iterator();
        while (it.hasNext()) {
            BITableSourceRelationPath path = it.next();
            try {
                router.registerFragment(
                        BICubeBuildTopicTag.PATH_TOPIC,
                        BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, path));
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    public void registerFieldRelationPath(Set<BITableSourceRelationPath> relationPathSet) {
        Iterator<BITableSourceRelationPath> it = relationPathSet.iterator();
        while (it.hasNext()) {
            BITableSourceRelationPath path = it.next();
            try {
                router.registerFragment(
                        BICubeBuildTopicTag.PATH_TOPIC,
                        BIFragmentUtils.generateFragment(BICubeBuildTopicTag.PATH_TOPIC, fieldPathFragmentID(path)));
            } catch (Exception e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    public static String fieldPathFragmentID(BITableSourceRelationPath path) {
        return path.getSourceID() + "FBI_Field";
    }

    private void registerTable(Set<ICubeTableSource> tableSourceSet) {
        Iterator<ICubeTableSource> it = tableSourceSet.iterator();
        while (it.hasNext()) {
            try {
                ICubeTableSource tableSource = it.next();
                /**
                 * 将表注册到数据转移阶段Topic下
                 */
                router.registerFragment(
                        BICubeBuildTopicTag.DATA_SOURCE_TOPIC,
                        BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_SOURCE_TOPIC, tableSource));
                router.registerFragment(
                        BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC,
                        BIFragmentUtils.generateFragment(BICubeBuildTopicTag.DATA_TRANSPORT_TOPIC, tableSource));
                /**
                 * 同时注册一个该表的Topic，Fragment为各个字段。
                 */
                router.registerTopic(BITopicUtils.generateTopicTag(tableSource));
            } catch (BITopicAbsentException e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            } catch (BIFragmentDuplicateException e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            } catch (BITopicDuplicateException e) {
                throw BINonValueUtils.beyondControl(e.getMessage(), e);
            }
        }
    }

    private void registerTableField(Set<ICubeTableSource> tableSourceSet) {
        Iterator<ICubeTableSource> it = tableSourceSet.iterator();
        while (it.hasNext()) {

            ICubeTableSource tableSource = it.next();
            BICubeFieldSource[] fields = tableSource.getFieldsArray(tableSourceSet);
            for (int i = 0; i < fields.length; i++) {
                BICubeFieldSource field = fields[i];
                Iterator<BIColumnKey> columnKeyIterator = BIColumnKey.generateColumnKey(field).iterator();
                while (columnKeyIterator.hasNext()) {
                    BIColumnKey columnKey = columnKeyIterator.next();
                    try {
                        router.registerFragment(
                                BITopicUtils.generateTopicTag(tableSource),
                                BIFragmentUtils.generateFragment(BITopicUtils.generateTopicTag(tableSource), columnKey.getFullName()));
                    } catch (Exception e) {
                        throw BINonValueUtils.beyondControl(e.getMessage(), e);
                    }
                }

            }
        }
    }
}

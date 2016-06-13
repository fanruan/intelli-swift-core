/**
 * Cube日志
 *
 * Created by GUY on 2016/4/6.
 * @class BI.CubeLog
 * @extends BI.Widget
 */
BI.CubeLog = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.CubeLog.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-cube-log"
        });
    },

    _init: function () {
        BI.CubeLog.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.cubeTree = BI.createWidget({
            type: "bi.cube_log_tree"

        });
        this.processBar = BI.createWidget({
            type: "bi.progress_bar",
            width: "100%"
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "refresh-bar",
                items: {
                    left: [{
                        type: "bi.icon_button",
                        cls: "task-list-font" + " task-list-comment-font",
                        width: 30,
                        height: 30
                    }, {
                        type: "bi.label",
                        text: BI.i18nText("BI-Update_Task_List"),
                        height: 30,
                        cls: "task-list-comment-label"
                    }],
                    right: [{
                        type: "bi.button",
                        text: BI.i18nText("BI-Refresh"),
                        height: 28,
                        level: "ignore",
                        handler: function () {
                            self.refreshLog();
                        }
                    }]
                },
                height: 50
            }, {
                type: "bi.htape",
                items: [{
                    el: {
                        type: "bi.label",
                        text: BI.i18nText("BI-All_Proccess"),
                        cls: "process-label",
                        height: 30
                    },
                    width: 90
                }, {
                    el: this.processBar
                }],
                height: 30
            }, this.cubeTree],
            vgap: 10
        });
        this.refreshLog();

    },

    refreshLog: function (isStart) {
        var self = this;
        if (isStart) {
            this.processBar.setValue(0);
        }
        if (BI.isNull(this.interval)) {
            this.interval = setInterval(function () {
                self.refreshLog();
            }, 300);
        }
        BI.Utils.getCubeLog(function (data) {
            if (!isStart && (BI.isNotNull(data.cube_end) || (BI.isNull(data.cube_end) && BI.isNull(data.cube_start)))) {
                self.interval && clearInterval(self.interval);
                delete self.interval;
            }
            !isStart && self._refreshProcess(data);
            self.cubeTree.populate(self._formatItems(data));
        });
    },

    _refreshProcess: function (data) {
        if (BI.isNotNull(data.allRelationInfo)) {
            var allFields = 0, generated = 0;
            BI.each(data.allTableInfo, function (tName, size) {
                allFields += size;
            });
            generated += data.connections.length;
            BI.each(data.tables, function (i, table) {
                generated += table.column.length;
            });
            var process = 1;
            if (allFields !== 0) {
                process = generated / allFields;
            }
            this.processBar.setValue(Math.ceil(process * 100));
        }
    },

    _formatSecond: function (time) {
        if (time < 1000) {
            return time + BI.i18nText("BI-Millisecond");
        }
        return time / 1000 + BI.i18nText("BI-Seconds");
    },

    _formatItems: function (data) {
        var self = this;
        //一些基本节点
        var items = [{
            id: BI.CubeLog.ERROR_MES_NODE,
            pId: -1,
            type: "bi.cube_log_wrong_info_node",
            open: true,
            isParent: true,
            count: data.errors.length
        }, {
            id: BI.CubeLog.READ_DB_NODE,
            pId: -1,
            text: BI.i18nText("BI-Transfer_Data_Time"),
            second: 0,
            isParent: true
        }, {
            id: BI.CubeLog.INDEX_NODE,
            pId: -1,
            text: BI.i18nText("BI-Generate_Data_Time"),
            second: 0,
            isParent: true
        }, {
            id: BI.CubeLog.RELATION_NODE,
            pId: -1,
            text: BI.i18nText("BI-Field_Connection_Time"),
            second: 0,
            isParent: true
        }];
        var readDBTime = 0, createIndexTime = 0, createRelationTime = 0;
        //错误信息
        var errors = data.errors;
        var children = [];
        BI.each(errors, function (i, er) {
            children.push({
                value: er.error_text
            })
        });
        items.push({
            type: "bi.cube_log_wrong_info_item",
            id: BI.UUID(),
            pId: BI.CubeLog.ERROR_MES_NODE,
            items: children
        });

        //读取数据时间
        var readDB = data.readingdb;
        BI.each(readDB, function (i, table) {
            items.push({
                id: BI.UUID(),
                pId: BI.CubeLog.READ_DB_NODE,
                text: table.tableName + BI.i18nText("BI-Init_Fetch_Data") + self._formatSecond(table.time),
                level: 1
            });
            readDBTime = readDBTime > table.time ? readDBTime : table.time;
        });

        //生成索引时间
        var tables = data.tables;
        BI.each(tables, function (i, table) {
            var columns = table.column;
            var id = BI.UUID();
            items.push({
                id: id,
                pId: BI.CubeLog.INDEX_NODE,
                text: table.tableName + BI.i18nText("BI-Generated_Time"),
                second: table.time,
                level: 1
            });
            BI.each(columns, function (j, column) {
                items.push({
                    id: BI.UUID(),
                    pId: id,
                    text: BI.i18nText("BI-Sen_Generated_Field_Index_1", column.name) + self._formatSecond(column.time),
                    level: 2
                })
            });
            createIndexTime = createIndexTime > table.time ? createIndexTime : table.time;
        });

        //关联
        var relations = data.connections;
        BI.each(relations, function (i, relation) {
            var re = relation.relation;
            items.push({
                id: BI.UUID(),
                pId: BI.CubeLog.RELATION_NODE,
                text: BI.i18nText("BI-Relations") + "-" +
                re.primaryTableName + "." + re.primaryFieldName + "->"
                + re.foreignTableName + "." + re.foreignFieldName +
                BI.i18nText("BI-Generated_Time") + self._formatSecond(relation.time),
                level: 1
            });
            createRelationTime = createRelationTime > relation.time ? createRelationTime : relation.time;
        });
        items[1].second = readDBTime;
        items[2].second = createIndexTime;
        items[3].second = createRelationTime;
        return items;
    },

    populate: function () {

    }
});
BI.extend(BI.CubeLog, {
    ERROR_MES_NODE: 1,
    READ_DB_NODE: 2,
    INDEX_NODE: 3,
    RELATION_NODE: 4
});
$.shortcut('bi.cube_log', BI.CubeLog);

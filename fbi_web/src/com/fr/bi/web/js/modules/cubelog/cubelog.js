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
            type: "bi.progress_bar"
        });
        this.processBar.setValue(1);

        this.remainingTimeLabel = BI.createWidget({
            type: "bi.label",
            width: 130,
            textAlign: "left",
            cls: "remaining-time"
        });

        this.finishLable = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Completed"),
            cls: "finish-label"
        });
        this._showFinish();

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
                            if (BI.isNotNull(self.finishLable) && !self.finishLable.isVisible()) {
                                self._refreshLogImmediate();
                            }
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
                    el: {
                        type: "bi.absolute",
                        items: [{
                            el: this.processBar,
                            left: 0,
                            right: 130,
                            top: 0
                        }, {
                            el: this.remainingTimeLabel,
                            right: 0,
                            top: 0
                        }, {
                            el: this.finishLable,
                            left: 0,
                            top: 2
                        }]
                    }
                }],
                height: 30
            }, this.cubeTree],
            vgap: 10
        });
    },

    _showBar: function () {
        this.processBar.setVisible(true);
        this.remainingTimeLabel.setVisible(true);
        this.finishLable.setVisible(false);
    },

    _showFinish: function () {
        this.processBar.setVisible(false);
        this.processBar.setValue(1);
        this.remainingTimeLabel.setVisible(false);
        this.finishLable.setVisible(true);
    },

    setStart: function () {
        this._showBar();
        this.processBar.setValue(1);
        this.remainingTimeLabel.setText(BI.i18nText("BI-Evaluating_Cube_Time"));
        //预估时间 使用开始时间和开始时候已生成数量
        this.startTime = new Date().getTime();
        this.generated = null;
    },

    setEnd: function () {
        var self = this;
        this.processBar.setValue(100);
        BI.delay(function () {
            self._showFinish();
        }, 300);
        this.startTime = null;
        this.generated = null;
    },

    //刷新按钮不要去改状态，因为很有可能立即更新的时候，后台请求到的状态cube还没有开始更新
    _refreshLogImmediate: function () {
        var self = this;
        BI.Utils.getCubeLog(function (data) {
            if (data.hasTask === false) {
                return;
            }
            self._refreshProcess(data);
            self.cubeTree.populate(self._formatItems(data));
        });
    },

    refreshLog: function (data) {
        this._refreshProcess(data);
        this.cubeTree.populate(this._formatItems(data));
    },

    _refreshProcess: function (data) {
        var self = this;
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
            if (data.hasTask === true) {
                if (allFields === 0) {
                    return;
                }
                process = generated / allFields;
                process = process > 0.9 ? 0.9 : process;
            }
            process = Math.ceil(process * 100);
            process = process < 10 ? 10 : process;
            this.processBar.setValue(process);
            if (process < 100) {
                this._showBar();
            } else {
                BI.delay(function () {
                    self._showFinish();
                }, 300);
            }
            if (BI.isNull(this.startTime) || BI.isNull(this.generated)) {
                this.startTime = new Date().getTime();
                this.generated = generated;
                this.remainingTimeLabel.setText(BI.i18nText("BI-Evaluating_Cube_Time"));
            } else {
                if (this.generated === generated || generated >= allFields) {
                    return;
                }
                var seconds = this._formatSecond((allFields - generated) / ((generated - this.generated) / (new Date().getTime() - this.startTime)));
                this.remainingTimeLabel.setText(BI.i18nText("BI-Cube_Time_Remaining", seconds));
            }
        }
    },

    _formatSecond: function (time) {
        if (time < 1000) {
            return BI.parseInt(time) + BI.i18nText("BI-Millisecond");
        }
        var seconds = BI.parseInt(time / 1000);
        if (seconds > 60) {
            return BI.parseInt(seconds / 60) + BI.i18nText("BI-Minute") + seconds % 60 + BI.i18nText("BI-Seconds");
        }
        return seconds + BI.i18nText("BI-Seconds");
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
            var errorInfo = er.error_text;
            if (undefined != er.tableName && '' != er.tableName) {
                errorInfo = BI.i18nText("BI-Table_Name") + '-' + er.tableName + ':' + er.error_text;
            } else if (undefined != er.relation) {
                errorInfo = BI.i18nText("BI-Relations") + "-" +
                    er.relation.primaryTableName + "." + er.relation.primaryFieldName + "->"
                    + er.relation.foreignTableName + "." + er.relation.foreignFieldName + ':' + er.error_text;
            }
            children.push({
                value: BI.i18nText("BI-Error_Infor") + errorInfo
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
            var tableIndexTime = 0;
            BI.each(columns, function (j, column) {
                items.push({
                    id: BI.UUID(),
                    pId: id,
                    text: BI.i18nText("BI-Sen_Generated_Field_Index_1", column.name) + self._formatSecond(column.time),
                    level: 2
                });
                tableIndexTime = tableIndexTime > column.time ? tableIndexTime : column.time;
            });
            items.push({
                id: id,
                pId: BI.CubeLog.INDEX_NODE,
                text: table.tableName + BI.i18nText("BI-Generated_Time"),
                second: tableIndexTime,
                level: 1
            });
            createIndexTime = createIndexTime > tableIndexTime ? createIndexTime : tableIndexTime;
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

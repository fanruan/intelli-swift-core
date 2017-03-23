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
            baseCls: "bi-cube-log",
            driver: BI.createWidget()
        });
    },

    _init: function () {
        BI.CubeLog.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.driver = this.options.driver;
        this._subscribeEvent();

        this.cubeTree = BI.createWidget({
            type: "bi.cube_log_tree",
            driver: this.options.driver,
            items: this._createItems()
        });

        this.processBar = BI.createWidget({
            type: "bi.progress_bar"
        });
        this.processBar.setValue(1);

        this.remainingTimeLabel = BI.createWidget({
            type: "bi.label",
            width: 200,
            textAlign: "left",
            cls: "remaining-time"
        });

        this.finishLable = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Basic_Completed"),
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
                        text: BI.i18nText("BI-Basic_Refresh"),
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
                            right: 200,
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

    _subscribeEvent: function () {
        var self = this;
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_PROCESS_UPDATED, function () {
            self._refreshProcess();
        });
        this.driver.on(BI.DealWithCubeLogDataDriver.EVENT_REMAINING_TIME_UPDATED,function () {
            self._refreshRemainingTime();
        })
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
    },

    setEnd: function () {
        var self = this;
        this.processBar.setValue(100);
        BI.delay(function () {
            self._showFinish();
        }, 300);
    },

    //刷新按钮不要去改状态，因为很有可能立即更新的时候，后台请求到的状态cube还没有开始更新
    _refreshLogImmediate: function () {//todo
        var self = this;
        // BI.Utils.getCubeLog(function (data) {
        //     if (data.hasTask === false) {
        //         return;
        //     }
        //     self._refreshProcess(data);
        //     self.cubeTree.populate(self._formatItems(data));
        // });
    },

    _refreshProcess: function () {
        this.processBar.setValue(this.driver.getProcess());
    },

    _refreshRemainingTime:function () {
        this.remainingTimeLabel.setText(BI.i18nText("BI-Cube_Time_Remaining", this.driver.getRemainingTime()));
    },

    _createItems: function () {
        //一些基本节点
        return items = [{
            id: BI.CubeLog.ERROR_MES_NODE,
            pId: -1,
            type: "bi.cube_log_wrong_info_node",
            open: true,
            isParent: true
            // count: data.errors.length
        }, {
            id: BI.CubeLog.READ_DB_NODE,
            pId: -1,
            text: BI.i18nText("BI-Transfer_Data_Time"),
            dataType: BI.CubeLog.READ_DB_NODE,
            open: true,
            isParent: true
        }, {
            id: BI.CubeLog.INDEX_NODE,
            pId: -1,
            text: BI.i18nText("BI-Generate_Data_Time"),
            dataType: BI.CubeLog.INDEX_NODE,
            open: true,
            isParent: true
        }, {
            id: BI.CubeLog.RELATION_NODE,
            pId: -1,
            text: BI.i18nText("BI-Field_Connection_Time"),
            dataType: BI.CubeLog.RELATION_NODE,
            open: true,
            isParent: true
        }, {
            id: BI.UUID(),
            pId: BI.CubeLog.ERROR_MES_NODE,
            dataType: BI.CubeLog.ERROR_MES_NODE
        }, {
            id: BI.UUID(),
            pId: BI.CubeLog.READ_DB_NODE,
            dataType: BI.CubeLog.READ_DB_NODE
        }, {
            id: BI.UUID(),
            pId: BI.CubeLog.INDEX_NODE,
            dataType: BI.CubeLog.INDEX_NODE
        }, {
            id: BI.UUID(),
            pId: BI.CubeLog.RELATION_NODE,
            dataType: BI.CubeLog.RELATION_NODE
        }];
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

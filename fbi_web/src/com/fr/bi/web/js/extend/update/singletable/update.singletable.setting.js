/**
 * Created by Young's on 2016/4/22.
 */
BI.UpdateSingleTableSetting = BI.inherit(BI.Widget, {

    _constants: {
        PART_ADD: 1,
        PART_DELETE: 2,
        PART_MODIFY: 3
    },

    _defaultConfig: function () {
        return BI.extend(BI.UpdateSingleTableSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-single-table-setting"
        })
    },

    _init: function () {
        BI.UpdateSingleTableSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.UpdateSingleTableSettingModel({
            update_setting: o.update_setting,
            table: o.table,
            currentTable: o.currentTable
        });

        //最上面的更新方式下拉框
        this.updateType = BI.createWidget({
            type: "bi.text_value_check_combo",
            height: 30,
            items: [{
                text: BI.i18nText("BI-Always_Updates"),
                value: BICst.SINGLE_TABLE_UPDATE_TYPE.ALL
            }, {
                text: BI.i18nText("BI-Full_Then_Incremental_Update"),
                value: BICst.SINGLE_TABLE_UPDATE_TYPE.PART
            }, {
                text: BI.i18nText("BI-Full_Then_No_Update"),
                value: BICst.SINGLE_TABLE_UPDATE_TYPE.NEVER
            }]
        });
        this.updateType.setValue(this.model.getUpdateType());
        this.updateType.on(BI.TextValueCheckCombo.EVENT_CHANGE, function () {
            var v = this.getValue()[0];
            self.model.setUpdateType(v);
            switch (v) {
                case BICst.SINGLE_TABLE_UPDATE_TYPE.ALL:
                    partUpdate.setVisible(true);
                    timeSetting.setVisible(true);
                    break;
                case BICst.SINGLE_TABLE_UPDATE_TYPE.PART:
                    partUpdate.setVisible(true);
                    timeSetting.setVisible(true);
                    break;
                case BICst.SINGLE_TABLE_UPDATE_TYPE.NEVER:
                    partUpdate.setVisible(true);
                    timeSetting.setVisible(true);
                    break;
            }
            self.fireEvent(BI.UpdateSingleTableSetting.EVENT_CHANGE);
        });

        //增量更新设置面板
        var partUpdate = this._createPartUpdateTab();
        partUpdate.setVisible(true);


        //定时设置
        var timeSetting = this._createTimeSetting();
        partUpdate.setVisible(true);

        var popup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems([{
                text: BI.i18nText("BI-Full_Updates"),
                value: BICst.SINGLE_TABLE_UPDATE_TYPE.ALL
            }, {
                text: BI.i18nText("BI-Incremental_Update"),
                value: BICst.SINGLE_TABLE_UPDATE_TYPE.PART
            }], {
                type: "bi.single_select_item",
                height: 25
            }),
            chooseType: BI.Selection.None,
            layouts: [{
                type: "bi.vertical"
            }]
        });
        this.immediateButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Update_Table_Immedi"),
            height: 28,
            width: 104
        });
        this.immediateCombo = BI.createWidget({
            type: "bi.combo",
            trigger: "hover",
            el: this.immediateButton,
            popup: {
                el: popup
            }
        });
        popup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.immediateCombo.hideView();
        });
        this.immediateCombo.on(BI.Combo.EVENT_CHANGE, function (v) {
            self._immediateButtonDisable();
            var tableInfo = {
                updateType: v,
                baseTable: self.model.table,
                isETL: false
            };
            if (self.model.options.currentTable.connection_name == "__FR_BI_ETL__") {
                tableInfo.isETL = true;
                tableInfo.ETLTable = self.model.currentTable;
            }
            self._createCheckInterval();
            self.fireEvent(BI.UpdateSingleTableSetting.EVENT_CUBE_SAVE, tableInfo);
        });

        BI.createWidget({
            type: "bi.vertical",
            element: this.element,
            items: [{
                type: "bi.htape",
                cls: "update-type",
                items: [{
                    el: {
                        type: "bi.label",
                        text: BI.i18nText("BI-When_Global_Update") + ": ",
                        height: 30,
                        textAlign: "left",
                        cls: "comment-label"
                    },
                    width: 90
                }, {
                    el: this.updateType,
                    width: "fill"
                }, {
                    el: this.immediateCombo,
                    width: 105
                }],
                hgap: 5,
                height: 30
            }, timeSetting, partUpdate],
            hgap: 10,
            vgap: 10
        })
        self._initImmediateButtonStatus();
    },

    _createPartUpdateTab: function () {
        var self = this;
        //增量增加、增量删除、增量修改
        var buttons = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems([{
                text: BI.i18nText("BI-Incremental_Increase"),
                value: this._constants.PART_ADD
            }, {
                text: BI.i18nText("BI-Incremental_Deletion"),
                value: this._constants.PART_DELETE
            }, {
                text: BI.i18nText("BI-Incremental_Updates"),
                value: this._constants.PART_MODIFY
            }], {
                type: "bi.text_button",
                cls: "part-update-type-button",
                height: 28,
                width: 100
            }),
            layouts: [{
                type: "bi.left",
                rgap: 2
            }]
        });
        buttons.setValue(this._constants.PART_ADD);
        buttons.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.tab.setSelect(this.getValue()[0]);
        });

        //上次更新时间参数
        var param = BI.i18nText("BI-Last_Updated");
        var lastUpdateParam = BI.createWidget({
            type: "bi.text_button",
            text: param,
            cls: "param-button",
            height: 25
        });
        lastUpdateParam.on(BI.TextButton.EVENT_CHANGE, function () {
            var v = self.tab.getSelect();
            switch (v) {
                case self._constants.PART_ADD:
                    self.partAddSql.insertParam(param);
                    break;
                case self._constants.PART_DELETE:
                    self.partDeleteSql.insertParam(param);
                    break;
                case self._constants.PART_MODIFY:
                    self.partModifySql.insertParam(param);
                    break;
            }
        });

        //当前更新时间参数
        var currParam = BI.i18nText("BI-Current_Update_Time");
        var currentUpdateParam = BI.createWidget({
            type: "bi.text_button",
            text: currParam,
            cls: "param-button",
            height: 25
        });
        currentUpdateParam.on(BI.TextButton.EVENT_CHANGE, function () {
            var v = self.tab.getSelect();
            switch (v) {
                case self._constants.PART_ADD:
                    self.partAddSql.insertParam(currParam);
                    break;
                case self._constants.PART_DELETE:
                    self.partDeleteSql.insertParam(currParam);
                    break;
                case self._constants.PART_MODIFY:
                    self.partModifySql.insertParam(currParam);
                    break;
            }
        });


        //预览按钮
        var previewButton = BI.createWidget({
            type: "bi.text_button",
            cls: "",
            text: BI.i18nText("BI-Preview"),
            height: 35
        });
        previewButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self._createPreviewPane();
        });

        //对应的三个sql面板
        this.tab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: buttons,
            cardCreator: BI.bind(this._createPartUpdateCard, this),
            height: 200
        });
        this.tab.setSelect(this._constants.PART_ADD);

        return BI.createWidget({
            type: "bi.absolute",
            element: this.tab,
            cls: "part-update-setting",
            items: [{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    cls: "param-preview-tools",
                    items: {
                        left: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Parameter"),
                            height: 35,
                            cls: "param-comment"
                        }, lastUpdateParam, currentUpdateParam],
                        right: [previewButton]
                    },
                    height: 35,
                    llgap: 10,
                    rrgap: 10
                },
                top: 59,
                left: 0,
                right: 0
            }, {
                el: buttons,
                top: 30,
                left: 0
            }, {
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Incremental_Update_Type") + ": ",
                    height: 30,
                    cls: "comment-label"
                },
                top: 0,
                left: 0
            }]
        })
    },

    _createPreviewPane: function () {
        var self = this;
        BI.Popovers.remove(this.model.getId() + "preview");
        var previewPane = BI.createWidget({
            type: "bi.update_preview_pane",
            table: this.model.getTable()
        });
        previewPane.on(BI.UpdatePreviewPane.EVENT_CHANGE, function () {
            BI.Popovers.remove(self.model.getId() + "preview");
            self.fireEvent(BI.UpdateSingleTableSetting.EVENT_CLOSE_PREVIEW);
        });
        BI.Popovers.create(this.model.getId() + "preview", previewPane);
        var sql = "", type = this.tab.getSelect();
        switch (type) {
            case this._constants.PART_ADD:
                sql = this.model.getAddSql();
                break;
            case this._constants.PART_DELETE:
                sql = this.model.getDeleteSql();
                break;
            case this._constants.PART_MODIFY:
                sql = this.model.getModifySql();
                break;
        }
        previewPane.populate(sql, type);
        BI.Popovers.open(this.model.getId() + "preview");
        this.fireEvent(BI.UpdateSingleTableSetting.EVENT_OPEN_PREVIEW);
    },

    _createPartUpdateCard: function (v) {
        var self = this;
        switch (v) {
            case this._constants.PART_ADD:
                this.partAddSql = BI.createWidget({
                    type: "bi.code_editor",
                    cls: "sql-container"
                });
                this.partAddSql.setValue(this.model.getAddSql()
                    .replaceAll(BICst.LAST_UPDATE_TIME, BI.i18nText("BI-Last_Updated"))
                    .replaceAll(BICst.CURRENT_UPDATE_TIME, BI.i18nText("BI-Current_Update_Time")));
                this.partAddSql.on(BI.CodeEditor.EVENT_BLUR, function () {
                    self.model.setAddSql(self.partAddSql.getValue()
                        .replaceAll(BI.i18nText("BI-Last_Updated"), BICst.LAST_UPDATE_TIME)
                        .replaceAll(BI.i18nText("BI-Current_Update_Time"), BICst.CURRENT_UPDATE_TIME));
                });
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.partAddSql,
                        top: 95,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }]
                });
            case this._constants.PART_DELETE:
                this.partDeleteSql = BI.createWidget({
                    type: "bi.code_editor",
                    cls: "sql-container"
                });
                this.partDeleteSql.setValue(this.model.getDeleteSql()
                    .replaceAll(BICst.LAST_UPDATE_TIME, BI.i18nText("BI-Last_Updated"))
                    .replaceAll(BICst.CURRENT_UPDATE_TIME, BI.i18nText("BI-Current_Update_Time")));
                this.partDeleteSql.on(BI.CodeEditor.EVENT_BLUR, function () {
                    self.model.setDeleteSql(self.partDeleteSql.getValue()
                        .replaceAll(BI.i18nText("BI-Last_Updated"), BICst.LAST_UPDATE_TIME)
                        .replaceAll(BI.i18nText("BI-Current_Update_Time"), BICst.CURRENT_UPDATE_TIME));
                });
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.partDeleteSql,
                        top: 95,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }]
                });
            case this._constants.PART_MODIFY:
                this.partModifySql = BI.createWidget({
                    type: "bi.code_editor",
                    cls: "sql-container"
                });
                this.partModifySql.setValue(this.model.getModifySql()
                    .replaceAll(BICst.LAST_UPDATE_TIME, BI.i18nText("BI-Last_Updated"))
                    .replaceAll(BICst.CURRENT_UPDATE_TIME, BI.i18nText("BI-Current_Update_Time")));
                this.partModifySql.on(BI.CodeEditor.EVENT_BLUR, function () {
                    self.model.setModifySql(self.partModifySql.getValue()
                        .replaceAll(BI.i18nText("BI-Last_Updated"), BICst.LAST_UPDATE_TIME)
                        .replaceAll(BI.i18nText("BI-Current_Update_Time"), BICst.CURRENT_UPDATE_TIME));
                });
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.partModifySql,
                        top: 95,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }]
                });
        }
    },

    _createTimeSetting: function () {
        var self = this;
        var addTime = BI.createWidget({
            type: "bi.button",
            height: 28,
            text: "+" + BI.i18nText("BI-Timing_Set")
        });
        addTime.on(BI.Button.EVENT_CHANGE, function () {
            var item = BI.createWidget({
                type: "bi.single_table_time_setting_item",
                id: BI.UUID(),
                onRemoveSetting: function (id) {
                    self._removeSettingById(id);
                }
            });

            item.on(BI.SingleTableTimeSettingItem.EVENT_CHANGE, function () {
                self.model.setTimeList(self.timeSettingGroup.getValue())
            });
            self.timeSettingGroup.addItems([item]);
            self.model.setTimeList(self.timeSettingGroup.getValue());
        });

        this.timeSettingGroup = BI.createWidget({
            type: "bi.button_group",
            items: self._createTimeSettingListItems(),
            layouts: [{
                type: "bi.vertical"
            }]
        });

        return BI.createWidget({
            type: "bi.vertical",
            cls: "time-setting",
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "add-time-toolbar",
                items: {
                    left: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-Single_Update") + ": ",
                        height: 30,
                        hgap: 10,
                        cls: "add-time-comment"
                    }],
                    right: [addTime]
                },
                height: 30
            }, this.timeSettingGroup]
        })
    },

    _removeSettingById: function (id) {
        var allButtons = this.timeSettingGroup.getAllButtons();
        var index = 0;
        BI.some(allButtons, function (i, button) {
            if (button.getValue().id === id) {
                index = i;
                return true;
            }
        });
        this.timeSettingGroup.removeItemAt(index);
    },

    getValue: function () {
        return this.model.getValue();
    },

    _createTimeSettingListItems: function () {
        var self = this;
        var items = [];
        BI.each(this.model.getTimeList(), function (index, valueObj) {
            var item = BI.createWidget(BI.extend(
                valueObj, {
                    type: "bi.single_table_time_setting_item",
                    id: BI.UUID(),
                    onRemoveSetting: function (id) {
                        self._removeSettingById(id);
                    }
                }
            ));
            item.on(BI.SingleTableTimeSettingItem.EVENT_CHANGE, function () {
                self.model.setTimeList(self.timeSettingGroup.getValue())
            });
            items.push(item);
        });
        return items;
    },

    _createCheckInterval: function () {
        var self = this;
        if (undefined != self.cubeInterval) {
            self._clearCheckInterval();
        }
        self.cubeInterval = setInterval(function () {
            self._getTaskStatus()
        }, 5000)

    },

    _initImmediateButtonStatus: function () {
        var self = this;
        BI.Utils.reqCubeStatusCheck(function (data) {
                if (!data.hasTask) {
                    self._immediateButtonAvailable();
                } else {
                    self._immediateButtonDisable();
                    self._createCheckInterval();
                }
            }
        )
    },

    _immediateButtonAvailable: function () {
        var self = this;
        self.immediateButton.setEnable(true);
        self.immediateButton.setText(BI.i18nText("BI-Update_Table_Immedi"));
    },

    _immediateButtonDisable: function () {
        var self = this;
        self.immediateButton.setEnable(false);
        self.immediateButton.setText(BI.i18nText("BI-Cube_is_Generating"));
    },

    _getTaskStatus: function () {
        var self = this;
        BI.Utils.reqCubeStatusCheck(function (data) {
                if (!data.hasTask) {
                    self._immediateButtonAvailable();
                    self._clearCheckInterval();
                } else {
                    self._immediateButtonDisable()
                }
            }
        )
    },

    _clearCheckInterval: function () {
        var self = this;
        if (undefined != self.cubeInterval) {
            clearInterval(self.cubeInterval);
        }
    }

});
BI.UpdateSingleTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
BI.UpdateSingleTableSetting.EVENT_CUBE_SAVE = "EVENT_CUBE_SAVE";
BI.UpdateSingleTableSetting.EVENT_OPEN_PREVIEW = "EVENT_OPEN_PREVIEW";
BI.UpdateSingleTableSetting.EVENT_CLOSE_PREVIEW = "EVENT_CLOSE_PREVIEW";
$.shortcut("bi.update_single_table_setting", BI.UpdateSingleTableSetting);

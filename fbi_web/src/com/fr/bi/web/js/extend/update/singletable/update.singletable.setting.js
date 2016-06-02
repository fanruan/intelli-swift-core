/**
 * Created by Young's on 2016/4/22.
 */
BI.UpdateSingleTableSetting = BI.inherit(BI.Widget, {

    _constants: {
        PART_ADD: 1,
        PART_DELETE: 2,
        PART_MODIFY: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.UpdateSingleTableSetting.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-update-single-table-setting"
        })
    },

    _init: function(){
        BI.UpdateSingleTableSetting.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.UpdateSingleTableSettingModel({
            update_setting: o.update_setting,
            table: o.table,
            currentTable:o.currentTable
        });

        //最上面的更新方式下拉框
        this.updateType = BI.createWidget({
            type: "bi.text_value_check_combo",
            height: 28,
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
        this.updateType.on(BI.TextValueCheckCombo.EVENT_CHANGE, function(){
            var v = this.getValue()[0];
            switch (v) {
                case BICst.SINGLE_TABLE_UPDATE_TYPE.ALL:
                    partUpdate.setVisible(false);
                    timeSetting.setVisible(true);
                    break;
                case BICst.SINGLE_TABLE_UPDATE_TYPE.PART:
                    partUpdate.setVisible(true);
                    timeSetting.setVisible(true);
                    break;
                case BICst.SINGLE_TABLE_UPDATE_TYPE.NEVER:
                    partUpdate.setVisible(false);
                    timeSetting.setVisible(false);
                    break;
            }
            self.fireEvent(BI.UpdateSingleTableSetting.EVENT_CHANGE);
        });

        //增量更新设置面板
        var partUpdate = this._createPartUpdateTab();
        partUpdate.setVisible(this.model.getUpdateType() === BICst.SINGLE_TABLE_UPDATE_TYPE.PART);


        //定时设置
        var timeSetting = this._createTimeSetting();

        this.immediateButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Update_Table_Immedi"),
            height: 30,
            
            //效果:保存(新增)该表所在业务包的所有操作并更新对应cube
            handler: function () {
                self.immediateButton.setEnable(false);
                self.immediateButton.setText(BI.i18nText("BI-Cube_is_Generating"));
                //若为ETL,使用ETL的id
                if (self.model.options.currentTable.connection_name=="__FR_BI_ETL__"){
                    self.model.table.id=self.model.currentTable.id
                }
                    // self.fireEvent(BI.UpdateSingleTableSetting.EVENT_CUBE_SAVE,self.model.table);
                BI.Utils.generateCubeByTable(self.model.table, function () {
                });
                    self._createCheckInterval();
            }
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
                        text: BI.i18nText("BI-Update_Style"),
                        height: 30,
                        textAlign: "left",
                        cls: "comment-label"
                    },
                    width: 90
                }, {
                    el: this.updateType,
                    width: "fill"
                }, {
                    el: this.immediateButton,
                    width: 105
                }],
                hgap: 5,
                height: 30
            }, partUpdate, timeSetting],
            hgap: 10,
            vgap: 10
        })
    },

    _createPartUpdateTab: function(){
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
        buttons.on(BI.ButtonGroup.EVENT_CHANGE, function(){
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
        lastUpdateParam.on(BI.TextButton.EVENT_CHANGE, function(){
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

        //预览按钮
        var previewButton = BI.createWidget({
            type: "bi.text_button",
            cls: "",
            text: BI.i18nText("BI-Preview"),
            height: 35
        });
        previewButton.on(BI.TextButton.EVENT_CHANGE, function(){
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
                        }, lastUpdateParam],
                        right: [previewButton]
                    },
                    height: 35,
                    llgap: 10,
                    rrgap: 10
                },
                top: 29,
                left: 0,
                right: 0
            }, {
                el: buttons,
                top: 0,
                left: 0
            }]
        })
    },

    _createPreviewPane: function(){
        var self = this;
        BI.Popovers.remove(this.model.getId() + "preview");
        var previewPane = BI.createWidget({
            type: "bi.update_preview_pane",
            table: this.model.getTable()
        });
        previewPane.on(BI.UpdatePreviewPane.EVENT_CHANGE, function(){
            BI.Popovers.remove(self.model.getId() + "preview");
            self.fireEvent(BI.UpdateSingleTableSetting.EVENT_CLOSE_PREVIEW);
        });
        BI.Popovers.create(this.model.getId() + "preview", previewPane);
        var sql = "", type = this.tab.getSelect();
        switch (type) {
            case this._constants.PART_ADD:
                sql = this.partAddSql.getValue();
                break;
            case this._constants.PART_DELETE:
                sql = this.partDeleteSql.getValue();
                break;
            case this._constants.PART_MODIFY:
                sql = this.partModifySql.getValue();
                break;
        }
        previewPane.populate(sql, type);
        BI.Popovers.open(this.model.getId() + "preview");
        this.fireEvent(BI.UpdateSingleTableSetting.EVENT_OPEN_PREVIEW);
    },

    _createPartUpdateCard: function(v) {
        switch (v) {
            case this._constants.PART_ADD:
                this.partAddSql = BI.createWidget({
                    type: "bi.code_editor",
                    cls: "sql-container"
                });
                this.partAddSql.setValue(this.model.getAddSql());
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.partAddSql,
                        top: 65,
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
                this.partDeleteSql.setValue(this.model.getDeleteSql());
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.partDeleteSql,
                        top: 65,
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
                this.partModifySql.setValue(this.model.getModifySql());
                return BI.createWidget({
                    type: "bi.absolute",
                    items: [{
                        el: this.partModifySql,
                        top: 65,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }]
                });
        }
    },

    _createTimeSetting: function(){
        var self = this;
        var addTime = BI.createWidget({
            type: "bi.button",
            height: 28,
            text: "+" + BI.i18nText("BI-Timing_Set")
        });
        addTime.on(BI.Button.EVENT_CHANGE, function(){
            self.timeSettingGroup.addItems([{
                type: "bi.time_setting_item",
                id: BI.UUID(),
                onRemoveSetting: function(id){
                    self._removeSettingById(id);
                }
            }]);
        });

        var globalTimes = BI.createWidget({
            type: "bi.label"
        });

        this.globalUpdateSet = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: [{
                text: BI.i18nText("BI-Update_Together"),
                value: BICst.SINGLE_TABLE_UPDATE.TOGETHER
            }, {
                text: BI.i18nText("BI-No_Update"),
                value: BICst.SINGLE_TABLE_UPDATE.NEVER
            }],
            height: 28
        });
        this.globalUpdateSet.setValue(this.model.getTogetherNever());

        this.timeSettingGroup = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(this.model.getTimeList(), {
                type: "bi.time_setting_item",
                id: BI.UUID(),
                onRemoveSetting: function(id){
                    self._removeSettingById(id);
                }
            }),
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
                        text: BI.i18nText("BI-Timing_Set"),
                        height: 30,
                        hgap: 10,
                        cls: "add-time-comment"
                    }],
                    right: [addTime]
                },
                height: 30
            }, globalTimes, {
                type: "bi.htape",
                cls: "global-update-toolbar",
                items: [{
                    el: {
                        type: "bi.label",
                        text: BI.i18nText("BI-Update_Global_Regularly"),
                        height: 30,
                        textAlign: "left",
                        cls: "global-update-comment"
                    },
                    width: 180
                }, {
                    el: this.globalUpdateSet,
                    width: "fill"
                }],
                hgap: 10,
                vgap: 5,
                height: 40
            }, this.timeSettingGroup]
        })
    },

    _removeSettingById: function(id){
        var allButtons = this.timeSettingGroup.getAllButtons();
        var index = 0;
        BI.some(allButtons, function(i, button) {
            if(button.getValue().id === id) {
                index = i;
                return true;
            }
        });
        this.timeSettingGroup.removeItemAt(index);
    },

    getValue: function(){
        //单个表的更新属性
        var partAddSql = "", partDeleteSql = "", partModifySql = "";
        if(BI.isNotNull(this.partAddSql)) {
            partAddSql = this.partAddSql.getValue();
        }
        if(BI.isNotNull(this.partDeleteSql)) {
            partDeleteSql = this.partDeleteSql.getValue();
        }
        if(BI.isNotNull(this.partModifySql)) {
            partModifySql = this.partModifySql.getValue();
        }

        return {
            update_type: this.updateType.getValue()[0],
            add_sql: partAddSql,
            delete_sql: partDeleteSql,
            modify_sql: partModifySql,
            together_never: this.globalUpdateSet.getValue()[0],
            time_list: this.timeSettingGroup.getValue()
        }
    },
    _createCheckInterval: function () {
        var self = this;
        self.cubeInterval=setInterval(function () {
            BI.Utils.checkCubeStatusByTable(self.model.table, function (data) {
                    if (data.isGenerated == true) {
                        self.immediateButton.setEnable(true);
                        self.immediateButton.setText(BI.i18nText("BI-Update_Table_Immedi"));
                        clearInterval(self.cubeInterval);
                    }
                }
            )

        }, 2000)
    }


});
BI.UpdateSingleTableSetting.EVENT_CHANGE = "EVENT_CHANGE";
BI.UpdateSingleTableSetting.EVENT_OPEN_PREVIEW = "EVENT_OPEN_PREVIEW";
BI.UpdateSingleTableSetting.EVENT_CLOSE_PREVIEW = "EVENT_CLOSE_PREVIEW";
$.shortcut("bi.update_single_table_setting", BI.UpdateSingleTableSetting);

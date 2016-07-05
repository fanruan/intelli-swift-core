/**
 * Created by Young's on 2016/3/19.
 */
BI.EditSQL = BI.inherit(BI.Widget, {

    constants: {
        SQL_EDIT_NORTH_HEIGHT: 40,
        SQL_EDIT_SOUTH_HEIGHT: 60,
        SQL_EDIT_WEST_WIDTH: 580,
        SQL_EDIT_BUTTON_HEIGHT: 30,
        SQL_EDIT_BUTTON_GAP: 20,
        SQL_EDIT_NAME_WIDTH: 50,
        SQL_EDIT_GAP: 20,
        SQL_EDIT_PANE_GAP: 10,
        SQL_EDIT_PANE_V_GAP: 5,
        SQL_EDIT_EMPTY_TIP_HEIGHT: 200,
        SQL_EDIT_LABEL_WIDTH: 140,
        SQL_EDIT_BUTTON_WIDTH: 110,
        SQL_EDIT_COMBO_WIDTH: 380,
        PREVIEW_EMPTY: 0,
        PREVIEW_PANE: 1,
        PREVIEW_ERROR: 2
    },
    
    _defaultConfig: function(){
        return BI.extend(BI.EditSQL.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-edit-sql"
        })
    },

    _init: function(){
        BI.EditSQL.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.EditSQLModel({
            sql: BI.decrypt(o.sql, "sh"),
            dataLinkName: o.dataLinkName
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                west: {
                    el: this._createWest(),
                    width: this.constants.SQL_EDIT_WEST_WIDTH
                },
                center: this._createCenter(),
                south: {
                    el: this._createSouth(),
                    height: this.constants.SQL_EDIT_SOUTH_HEIGHT
                }
            }
        });
        this.model.initData(function(){
            self._populate();
        });
    },

    _createWest: function(){
        var self = this;
        this.connectionCombo = BI.createWidget({
            type: "bi.text_value_check_combo",
            items: [],
            width: this.constants.SQL_EDIT_COMBO_WIDTH,
            height: this.constants.SQL_EDIT_BUTTON_HEIGHT
        });
        this.connectionCombo.on(BI.TextValueCheckCombo.EVENT_CHANGE, function(){
            self.model.setDataLinkName(this.getValue()[0]);
            self.previewTab.setSelect(self.constants.PREVIEW_EMPTY);
        });
        this.sqlEditor = BI.createWidget({
            type: "bi.code_editor",
            watermark: BI.i18nText("BI-Please_Enter_SQL"),
            cls: "sql-editor"
        });
        this.sqlEditor.on(BI.CodeEditor.EVENT_CHANGE, function(){
            var sql = this.getValue();
            self.model.setSQL(sql);
            self.previewTab.setSelect(self.constants.PREVIEW_EMPTY);
            self.saveButton.setEnable(BI.isNotEmptyString(sql));
            self.previewButton.setEnable(BI.isNotEmptyString(sql));
        });
        var sqlWrapper = BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.label",
                    cls: "sql-editor-label",
                    text: BI.i18nText("BI-SQL_Statement"),
                    height: this.constants.SQL_EDIT_BUTTON_HEIGHT
                },
                height: this.constants.SQL_EDIT_BUTTON_HEIGHT
            }, {
                el: this.sqlEditor,
                height: "fill"
            }]
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "edit-sql-pane",
                    items: [{
                        el: {
                            type: "bi.left",
                            cls: "server-set-north",
                            items: [{
                                type: "bi.label",
                                text: BI.i18nText("BI-SQL_Data_Set"),
                                height: this.constants.SQL_EDIT_NORTH_HEIGHT
                            }]
                        },
                        height: this.constants.SQL_EDIT_NORTH_HEIGHT
                    }, {
                        el: {
                            type: "bi.left",
                            items: [{
                                type: "bi.label",
                                cls: "server-data-from-label",
                                text: BI.i18nText("BI-Data_From_Data_Connection"),
                                height: this.constants.SQL_EDIT_BUTTON_HEIGHT,
                                width: this.constants.SQL_EDIT_LABEL_WIDTH,
                                textAlign: "left"
                            }, this.connectionCombo],
                            vgap: this.constants.SQL_EDIT_PANE_V_GAP
                        },
                        height: this.constants.SQL_EDIT_NORTH_HEIGHT
                    }, {
                        el: sqlWrapper,
                        height: "fill"
                    }],
                    hgap: this.constants.SQL_EDIT_PANE_GAP,
                    vgap: this.constants.SQL_EDIT_PANE_V_GAP
                },
                top: this.constants.SQL_EDIT_GAP,
                left: this.constants.SQL_EDIT_GAP,
                right: this.constants.SQL_EDIT_GAP,
                bottom: 0
            }]
        })
    },

    _createCenter: function(){
        var self = this;
        this.previewWrapper = BI.createWidget({
            type: "bi.preview_table"
        });
        this.previewTab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: {},
            cardCreator: BI.bind(this._previewCardCreator, this)
        });
        this.previewTab.setSelect(this.constants.PREVIEW_EMPTY);
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "sql-result-preview",
                    items: [{
                        el: {
                            type: "bi.left",
                            cls: "preview-north",
                            items: [{
                                type: "bi.label",
                                text: BI.i18nText("BI-Table_Data"),
                                height: this.constants.SQL_EDIT_NORTH_HEIGHT
                            }]
                        },
                        height: this.constants.SQL_EDIT_NORTH_HEIGHT
                    }, {
                        el: this.previewTab,
                        height: "fill"
                    }],
                    hgap: this.constants.SQL_EDIT_GAP
                },
                top: this.constants.SQL_EDIT_GAP,
                left: 0,
                bottom: 0,
                right: this.constants.SQL_EDIT_GAP
            }]
        })
    },

    _previewCardCreator: function(v){
        var self = this;
        switch (v){
            case self.constants.PREVIEW_EMPTY:
                this.previewButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Preview"),
                    height: self.constants.SQL_EDIT_BUTTON_HEIGHT,
                    width: self.constants.SQL_EDIT_BUTTON_WIDTH,
                    handler: function(){
                        self._getPreviewResult();
                    }
                });
                this.previewButton.setEnable(false);
                return {
                    type: "bi.horizontal_auto",
                    items: [this.previewButton],
                    tgap: self.constants.SQL_EDIT_BUTTON_GAP
                };
            case self.constants.PREVIEW_PANE:
                return self.previewWrapper;
            case self.constants.PREVIEW_ERROR:
                var cancelButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Cancel"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function(){
                        self.previewTab.setSelect(self.constants.PREVIEW_EMPTY);
                    }
                });
                var retryButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Re_Preview"),
                    level: "ignore",
                    width: 90,
                    height: 28,
                    handler: function(){
                        self._getPreviewResult();
                    }
                });
                return BI.createWidget({
                    type: "bi.center_adapt",
                    items: [{
                        type: "bi.vertical",
                        cls: "preview-fail",
                        items: [{
                            type: "bi.center_adapt",
                            cls: "data-link-test-fail-icon",
                            items: [{
                                type: "bi.icon",
                                width: 126,
                                height: 126
                            }]
                        }, {
                            type: "bi.label",
                            text: BI.i18nText("BI-Preview") + BI.i18nText("BI-Failed"),
                            cls: "preview-fail-comment"
                        }, {
                            type: "bi.horizontal_float",
                            items: [{
                                type: "bi.horizontal",
                                items: [cancelButton, retryButton],
                                hgap: 5
                            }],
                            height: 30,
                            hgap: 5
                        }],
                        width: 500,
                        height: 340,
                        vgap: 10
                    }]
                });
        }
    },

    _createSouth: function(){
        var self = this;
        this.saveButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            text: BI.i18nText("BI-Save"),
            title: BI.i18nText("BI-Save"),
            height: this.constants.SQL_EDIT_BUTTON_HEIGHT
        });
        //保存前需要检查：数据连接是否正常，a:连得上时有无预览结果，b:连不上提示可能会有问题
        this.saveButton.on(BI.Button.EVENT_CHANGE, function(){
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: self.element,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.getTestConnectionByLinkName(self.model.getDataLinkName(), function(res){
                if(BI.isNull(res) || res.success === false){
                    BI.Msg.confirm(BI.i18nText("BI-Prompt"), BI.i18nText("BI-Can_Not_Connect_Connection") + "," + BI.i18nText("BI-Sure_Next_Step"), function(v){
                        v === true && self._saveSql(true);
                    });
                } else {
                    self._saveSql();
                }
                mask.destroy();
            });
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            items: {
                left: [{
                    type: "bi.button",
                    level: "ignore",
                    text: BI.i18nText("BI-Cancel"),
                    height: this.constants.SQL_EDIT_BUTTON_HEIGHT,
                    handler: function () {
                        self.fireEvent(BI.EditSQL.EVENT_CANCEL);
                    }
                }],
                right: [this.saveButton]
            },
            lhgap: this.constants.SQL_EDIT_BUTTON_GAP,
            rhgap: this.constants.SQL_EDIT_BUTTON_GAP
        })
    },

    _saveSql: function(errorConnection){
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTablesDetailInfoByTables([{
            connection_name: BICst.CONNECTION.SQL_CONNECTION,
            dataLinkName: this.model.getDataLinkName(),
            sql: BI.encrypt(this.model.getSQL(), "sh"),
            table_name: this.model.getTableName()
        }], function(res){
            var table = res[0];
            if(!errorConnection && table.md5 === "Empty"){
                BI.Msg.confirm(BI.i18nText("BI-Prompt"), BI.i18nText("BI-Error_SQL_Not_Next_Step") + "," + BI.i18nText("BI-Sure_Next_Step"), function(v){
                    v === true && self.fireEvent(BI.EditSQL.EVENT_SAVE, table);
                });
            } else {
                self.fireEvent(BI.EditSQL.EVENT_SAVE, table);
            }
            mask.destroy();
        })
    },

    _getPreviewResult: function(){
        var self = this;
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getServerSetPreviewBySql({
            data_link: self.model.getDataLinkName(),
            sql: BI.encrypt(self.model.getSQL(), "sh")
        }, function(res){
            if(BI.isNotNull(res) && BI.isNotNull(res.field_names)){
                self._createPreviewTable(res);
                self.previewTab.setSelect(self.constants.PREVIEW_PANE);
            } else {
                self.previewTab.setSelect(self.constants.PREVIEW_ERROR);
            }
            mask.destroy();
        });
    },

    _createPreviewTable: function(data){
        var fieldNames = data.field_names, previewData = data.data;
        var header = [], items = [], columnSize = [];
        BI.each(fieldNames, function(i, field){
            header.push({
                text: field
            });
            columnSize.push("");
        });
        BI.each(previewData, function(i ,row){
            var item = [];
            BI.each(row, function(j, d){
                item.push({text: d});
            });
            items.push(item);
        });
        this.previewWrapper.populate(items, [header]);
    },

    _populate: function(){
        var items = [];
        var connectionNames = this.model.getConnectionNames();
        BI.each(connectionNames, function(i, name){
            items.push({
                text: name,
                value: name
            })
        });
        this.connectionCombo.populate(items);
        this.connectionCombo.setValue(this.model.getDataLinkName());
        this.sqlEditor.setValue(this.model.getSQL());
        if(BI.isEmptyString(this.model.getSQL())){
            this.saveButton.setEnable(false);
        }
    },

    getValue: function(){
        return {
            connection_name: BICst.CONNECTION.SQL_CONNECTION,
            dataLinkName: this.model.getDataLinkName(),
            sql: BI.encrypt(this.model.getSQL(), "sh"),
            table_name: BI.i18nText("BI-Sql_DataSet")
        }
    }
});
BI.EditSQL.EVENT_SAVE = "EVENT_SAVE";
BI.EditSQL.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.edit_sql", BI.EditSQL);
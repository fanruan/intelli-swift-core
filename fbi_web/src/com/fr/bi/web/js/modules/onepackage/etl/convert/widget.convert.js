/**
 * @class BI.Convert
 * @extend BI.Widget
 * @author windy
 */
BI.Convert = BI.inherit(BI.Widget, {

    constants: {
        CONVERT_NORTH_HEIGHT: 50,
        CONVERT_SOUTH_HEIGHT: 60,
        CONVERT_WEST_WIDTH: 530,
        CONVERT_TABLE_NAME_WIDTH: 60,
        CONVERT_BUTTON_GAP: 20,
        CONVERT_BUTTON_HEIGHT: 30,
        CONVERT_EDITOR_HEIGHT: 30,
        CONVERT_EDITOR_WIDTH: 220,
        CONVERT_REGION_HEIGHT: 180,
        CONVERT_GAP_TEN: 10,
        CONVERT_GAP_TWENTY: 20,
        CONVERT_TYPE_STRING: 0,
        CONVERT_TYPE_NUMBER: 1,
        CONVERT_CAUSE_LABEL_WIDTH: 112,
        CONVERT_GENERATED_LABEL_WIDTH: 140,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        CONVERT_GAP_FORTY: 40,
        SHOW_PREVIEW_BUTTON: 1,
        SHOW_PREVIEW_TIP: 2,
        SHOW_PREVIEW_TABLE: 3

    },

    _defaultConfig: function(){
        return BI.extend(BI.Convert.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-convert",
            info: {}
        });
    },

    _init: function(){
        BI.Convert.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.ConvertModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {
                    el: this._buildNorth(),
                    height: this.constants.CONVERT_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.CONVERT_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.CONVERT_WEST_WIDTH
                },
                center: this._buildCenter()
            }
        });
        this.populate(o.info);
    },

    _checkMasker: function(){
        if(BI.isNotNull(this.previewLoadingMasker)){
            this.previewLoadingMasker.destroy();
            this.previewLoadingMasker = null;
        }
        if(BI.isNotNull(this.loadingMasker)){
            this.loadingMasker.destroy();
            this.loadingMasker = null;
        }
    },

    _buildNorth: function(){
        return BI.createWidget({
            type: "bi.left",
            cls: "convert-north",
            items: [{
                type: "bi.label",
                cls: "convert-north-label",
                text: BI.i18nText("BI-Row_Column_Transformation") + BI.i18nText("BI-Configuration"),
                height: this.constants.CONVERT_NORTH_HEIGHT
            }],

            hgap: this.constants.CONVERT_GAP_TWENTY
        })
    },

    _checkOperatorPaneValid: function(){
        var orderValue = this.baseFieldCombo.getValue();
        var lanciValue = this.selectFieldsDataPane.getValue();
        var initialFields = this.initialPane.getValue();
        if(BI.isEmpty(lanciValue.lc_values)){
            this.save.setEnable(false);
            this.save.setTitle(BI.i18nText("BI-Please_Select_Junior_Name"));
            return;
        }
        if(BI.isEmpty(initialFields)){
            this.save.setEnable(false);
            this.save.setTitle(BI.i18nText("BI-Need_Initial_Fields"));
            return;
        }
        if(BI.isEmpty(orderValue)){
            this.save.setEnable(false);
            this.save.setTitle(BI.i18nText("BI-Please_Select_Order_Name"));
            return;
        }
        this.save.setEnable(true);
        this.save.setTitle("");
    },

    _createOpeartorPane: function(){
        var self = this;

        this.baseFieldCombo = BI.createWidget({
            type: "bi.text_value_combo",
            height: 30
        });

        this.baseFieldCombo.on(BI.StaticCombo.EVENT_CHANGE, function(){
            self.model.setGroupName(this.getValue()[0]);
            self._checkOperatorPaneValid();
            self.displayResultArea.setSelect(self.constants.SHOW_PREVIEW_TIP);
        });

        this.selectFieldsDataPane = BI.createWidget({
            type: "bi.convert_select_fields_data_pane",
            tId: this.model.getId()
        });

        this.selectFieldsDataPane.on(BI.ConvertSelectFieldsDataPane.EVENT_LOADING, function(){
            if(!self.loadingMasker){
                self.loadingMasker = BI.createWidget({
                    type: "bi.loading_mask",
                    masker: self.west,
                    text: BI.i18nText("BI-Loading"),
                    offset: {
                        left: self.constants.CONVERT_GAP_TWENTY,
                        top: self.constants.CONVERT_GAP_TEN,
                        right: self.constants.CONVERT_GAP_FORTY,
                        bottom: self.constants.CONVERT_GAP_TWENTY
                    }
                })
            }
        });

        this.selectFieldsDataPane.on(BI.ConvertSelectFieldsDataPane.EVENT_LOADED, function(){
            self.loadingMasker.destroy();
            self.loadingMasker = null;
        });

        this.initialPane = BI.createWidget({
            type: "bi.convert_initial_fields_pane"
        });

        this.genFieldsPane = BI.createWidget({
            type : "bi.convert_gen_fields_pane"
        });

        this.selectFieldsDataPane.on(BI.ConvertSelectFieldsDataPane.EVENT_CHANGE, function(){
            self.genFieldsPane.populate([self.initialPane.getValue(), self.selectFieldsDataPane.getValue()["lc_values"]]);
            var value = this.getValue();
            self.model.setLCName(value.lc_name);
            self.model.setLCValue(value.lc_values);
            self._checkOperatorPaneValid();
            self.displayResultArea.setSelect(self.constants.SHOW_PREVIEW_TIP);
        });

        this.initialPane.on(BI.ConvertSelectFieldsDataPane.EVENT_CHANGE, function(){
            self.genFieldsPane.populate([self.initialPane.getValue(), self.selectFieldsDataPane.getValue()["lc_values"]]);
            self.model.setColumns(this.getValue());
            self._checkOperatorPaneValid();
            self.displayResultArea.setSelect(self.constants.SHOW_PREVIEW_TIP);
        });

        return BI.createWidget({
            type: "bi.division",
            cls: "convert-operator-pane",
            columns: 2,
            rows: 3,
            items:[{
                column : 0,
                row : 0,
                width: 1,
                height: 0.1,
                el : {
                    type: "bi.htape",
                    items:[{
                        type: "bi.label",
                        cls: "table-name-text-twenty",
                        text: BI.i18nText("BI-Sequence_Based_On"),
                        width: this.constants.CONVERT_CAUSE_LABEL_WIDTH
                    },{
                        type: "bi.center_adapt",
                        items: [{
                            el: this.baseFieldCombo
                        }]
                    },{
                        type: "bi.label",
                        cls: "table-name-text-twenty",
                        text: BI.i18nText("BI-Recognize_Columns_Of_Generated"),
                        width: this.constants.CONVERT_GENERATED_LABEL_WIDTH
                    }]
                }
            },{
                column : 1,
                row : 0,
                width: 0,
                height: 0.1,
                el: {
                    type: "bi.layout"
                }
            },{
                column : 0,
                row : 1,
                width: 0.5,
                height: 0.45,
                el: {
                    type: "bi.absolute",
                    items: [{
                        el: this.selectFieldsDataPane,
                        left: 10,
                        right: 5,
                        top: 5,
                        bottom: 10
                    }]
                }
            },{
                column : 1,
                row : 1,
                width: 0.5,
                height: 0.45,
                el: {
                    type: "bi.absolute",
                    items:[{
                        el: this.initialPane,
                        left: 5,
                        right: 10,
                        top: 5,
                        bottom: 10
                    }]
                }
            },{
                column : 0,
                row : 2,
                width: 1,
                height: 0.45,
                el: {
                    type: "bi.absolute",
                    items:[{
                        el: this.genFieldsPane,
                        left: 10,
                        right: 10,
                        top: 0,
                        bottom: 10
                    }]
                }
            },{
                column : 1,
                row : 2,
                width: 0,
                height: 0.45,
                el:{
                    type: "bi.layout"
                }
            }]
        });
    },

    _buildSouth: function(){
        var self = this;

        var cancel = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.CONVERT_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            tipType: "warning",
            height: this.constants.CONVERT_BUTTON_HEIGHT
        });

        cancel.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.Convert.EVENT_CANCEL);
        });

        this.save.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.Convert.EVENT_SAVE, self.model.getValue());
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            height: this.constants.CONVERT_SOUTH_HEIGHT,
            items: {
                left: [cancel],
                right: [this.save]
            },
            lhgap: this.constants.CONVERT_BUTTON_GAP,
            rhgap: this.constants.CONVERT_BUTTON_GAP
        });
    },

    _buildWest: function(){
        this.west = BI.createWidget({

            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.htape",
                    cls: "convert-table-name",
                    items: [{
                        type: "bi.absolute",
                        items:[{
                            el:{
                                type: "bi.label",
                                cls: "table-name-text",
                                textAlign: "left",
                                textHeight: this.constants.CONVERT_EDITOR_HEIGHT,
                                width: this.constants.CONVERT_TABLE_NAME_WIDTH,
                                text:BI.i18nText("BI-Table_Name") + ":"
                            },
                            width: this.constants.CONVERT_TABLE_NAME_WIDTH,
                            top: this.constants.CONVERT_GAP_TEN,
                            left: this.constants.CONVERT_GAP_TEN,
                            bottom: this.constants.CONVERT_GAP_TEN
                        }],
                        width: this.constants.CONVERT_TABLE_NAME_WIDTH
                    }, {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type:"bi.label",
                                height: this.constants.CONVERT_EDITOR_HEIGHT,
                                width: this.constants.CONVERT_EDITOR_WIDTH,
                                cls: "convert-table-name-editor",
                                textAlign: "left",
                                title: BI.i18nText("BI-Cannot_Change_Table_Name_Here"),
                                hgap: 10,
                                value: this.model.getDefaultTableName()
                            },
                            top: this.constants.CONVERT_GAP_TEN,
                            left: this.constants.CONVERT_GAP_TEN,
                            bottom: this.constants.CONVERT_GAP_TEN,
                            right: this.constants.CONVERT_GAP_TEN
                        }]
                    }]
                },
                height: this.constants.CONVERT_NORTH_HEIGHT
            }, {
                type: "bi.default",
                height: this.constants.CONVERT_GAP_TEN
            }, {
                el: this._createOpeartorPane()
            }],
            hgap: this.constants.CONVERT_GAP_TWENTY
        });
        return this.west;
    },

    _buildCenter: function(){
        var self = this;
        this.displayResultArea = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            cardCreator: BI.bind(_createTabs, this)
        });

        function _createTabs(v){
            switch(v) {
                case this.constants.SHOW_PREVIEW_TIP:
                    return BI.createWidget({
                        type: "bi.float_center_adapt",
                        items: [{
                            type: "bi.label",
                            cls: "convert-result-tip",
                            text: BI.i18nText("BI-Generate_Cube_Then_Review")
                        }]
                    });
                case this.constants.SHOW_PREVIEW_BUTTON:
                    var previewButton = BI.createWidget({
                        type: "bi.button",
                        text: BI.i18nText("BI-Preview"),
                        height: this.constants.PREVIEW_BUTTON_HEIGHT,
                        width: this.constants.PREVIEW_BUTTON_WIDTH
                    });
                    previewButton.on(BI.Button.EVENT_CHANGE, function(){
                        if(!self.previewLoadingMasker){
                            self.previewLoadingMasker = BI.createWidget({
                                type: "bi.loading_mask",
                                masker: self.center,
                                text: BI.i18nText("BI-Loading"),
                                offset: {
                                    left: self.constants.CONVERT_GAP_TWENTY,
                                    top: self.constants.CONVERT_GAP_TEN,
                                    right: self.constants.CONVERT_GAP_FORTY,
                                    bottom: self.constants.CONVERT_GAP_TWENTY
                                }
                            })
                        }
                        BI.Utils.getPreviewDataByTableAndFields(self.model.getPreTableStructure(), [], function(data){
                            var fields = data.fields, values = data.value;
                            var header = [], items = [];
                            BI.each(fields, function(i, field){
                                header.push({
                                    text: field
                                });
                            });
                            BI.each(values, function(i, value){
                                BI.each(value, function(j, v){
                                    if(BI.isNotNull(items[j])){
                                        items[j].push({
                                            text: v
                                        });
                                    } else {
                                        items.push([{
                                            text: v
                                        }]);
                                    }
                                });
                            });
                            var tableView = BI.createWidget({
                                type: "bi.preview_table",
                                items: items,
                                header: [header]
                            });
                            self.displayResultArea.setSelect(self.constants.SHOW_PREVIEW_TABLE);
                            self.previewTable.empty();
                            self.previewTable.addItem(tableView);
                            self.previewLoadingMasker.destroy();
                            self.previewLoadingMasker = null;
                        })
                    });
                    return BI.createWidget({
                        type: "bi.horizontal_adapt",
                        items: [previewButton]
                    });
                case this.constants.SHOW_PREVIEW_TABLE:
                    this.previewTable = BI.createWidget({
                        type: "bi.horizontal_adapt",
                        scrollable: true
                    });
                    return this.previewTable;
            }
        }

        this.center = BI.createWidget({
            type: "bi.vtape",
            cls: "convert-preview-result",
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Data"),
                    textAlign: "left",
                    height: this.constants.CONVERT_NORTH_HEIGHT,
                    textHeight: this.constants.CONVERT_NORTH_HEIGHT,
                    cls: "convert-result-text",
                    whiteSpace: "normal"
                },
                height: this.constants.CONVERT_NORTH_HEIGHT
            }, this.displayResultArea],
            lgap: this.constants.CONVERT_GAP_TWENTY,
            rgap: this.constants.CONVERT_GAP_TWENTY,
            bgap: this.constants.CONVERT_GAP_TEN
        });

        return this.center;
    },

    populate: function(info){
        var self = this;
        this.model.populate(info);
        if(!this.loadingMasker){
            this.loadingMasker = BI.createWidget({
                type: "bi.loading_mask",
                masker: this.west,
                text: BI.i18nText("BI-Loading"),
                offset: {
                    left: this.constants.CONVERT_GAP_TWENTY,
                    top: this.constants.CONVERT_GAP_TEN,
                    right: this.constants.CONVERT_GAP_FORTY,
                    bottom: this.constants.CONVERT_GAP_TWENTY
                }
            })
        }
        var fields = [], tables = this.model.getTableStructure();
        this.model.getTablesDetailInfoByTables(function(data){
            fields = BI.flatten(data[0].fields);
            fields = BI.filter(fields, function(idx, field){
                return field.field_type !== BICst.COLUMN.DATE;
            });
            var fieldsName = BI.map(fields, function(idx, field){
                return {
                    value : field.field_name
                }
            });
            self.baseFieldCombo.populate(fieldsName);
            self.baseFieldCombo.setValue(self.model.getGroupName());

            var field = BI.find(fields, function(idx, field){
                return field.field_name == self.model.getLCName();
            });

            BI.Utils.getConfDataByField(tables, BI.isNull(field) ? "" : field.field_name, {
                type: BICst.REQ_DATA_TYPE.REQ_GET_ALL_DATA
            }, function (data) {
                self.selectFieldsDataPane.populate({
                    table: tables,
                    fields: fields,
                    data: data
                });
                self.selectFieldsDataPane.setValue({
                    lc_name: self.model.getLCName(),
                    lc_values: self.model.getLCValue()
                });
                self.initialPane.populate(fields);
                self.initialPane.setValue(self.model.getColumns()||[]);

                self.genFieldsPane.populate([self.initialPane.getValue(), self.selectFieldsDataPane.getValue()["lc_values"]]);
                self.loadingMasker.destroy();
                self.loadingMasker = null;

                self._checkOperatorPaneValid();
            });
        });

        var isGenerated = this.model.isCubeGenerated();
        if(BI.isNotNull(isGenerated) && isGenerated === true){
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_BUTTON);
        } else {
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_TIP);
        }
    }
});

BI.Convert.EVENT_CANCEL = "EVENT_CANCEL";
BI.Convert.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.convert", BI.Convert);
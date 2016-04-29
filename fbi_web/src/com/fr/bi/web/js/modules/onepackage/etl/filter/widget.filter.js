/**
 * @class BI.FilterData
 * @extend BI.Widget
 * @author windy
 */
BI.FilterData = BI.inherit(BI.Widget, {

    constants: {
        FILTER_NORTH_HEIGHT: 50,
        FILTER_SOUTH_HEIGHT: 60,
        FILTER_WEST_WIDTH: 530,
        FILTER_TABLE_NAME_WIDTH: 60,
        FILTER_BUTTON_GAP: 20,
        FILTER_BUTTON_HEIGHT: 30,
        FILTER_EDITOR_HEIGHT: 30,
        FILTER_EDITOR_WIDTH: 220,
        FILTER_REGION_HEIGHT: 180,
        FILTER_GAP_TEN: 10,
        FILTER_GAP_TWENTY: 20,
        FILTER_GAP_FORTY: 40,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        SHOW_PREVIEW_BUTTON: 1,
        SHOW_PREVIEW_TIP: 2,
        SHOW_PREVIEW_TABLE: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.FilterData.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-filter",
            info: {}
        });
    },

    _init: function(){
        BI.FilterData.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.FilterDataModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {
                    el: this._buildNorth(),
                    height: this.constants.FILTER_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.FILTER_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.FILTER_WEST_WIDTH
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
            cls: "filter-north",
            items: [{
                type: "bi.label",
                cls: "filter-north-label",
                text: BI.i18nText("BI-Filter") + BI.i18nText("BI-Management"),
                height: this.constants.FILTER_NORTH_HEIGHT
            }],
            hgap: this.constants.FILTER_GAP_TWENTY
        })
    },

    _buildSouth: function(){
        var self = this;

        var cancel = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.FILTER_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            tipType: "warning",
            height: this.constants.FILTER_BUTTON_HEIGHT
        });

        cancel.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.FilterData.EVENT_CANCEL);
        });

        this.save.on(BI.Button.EVENT_CHANGE, function() {
            self._checkMasker();
            self.fireEvent(BI.FilterData.EVENT_SAVE, self.model.getValue());
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            height: this.constants.FILTER_SOUTH_HEIGHT,
            items: {
                left: [cancel],
                right: [this.save]
            },
            lhgap: this.constants.FILTER_BUTTON_GAP,
            rhgap: this.constants.FILTER_BUTTON_GAP
        });
    },

    _buildWest: function(){
        var self = this;

        this.filterOpeartor = BI.createWidget({
            type: "bi.field_filter",
            cls: "filter-operator-pane"
        });

        this.filterOpeartor.on(BI.FieldFilter.EVENT_CHANGE, function(){
            self.model.setFilterValue(this.getValue());
            if(self.filterOpeartor.isEmpty()){
                self.save.setEnable(false);
                self.save.setTitle(BI.i18nText("BI-Please_Set_Filter_Condition"));
                return;
            }
            self.save.setEnable(true);
            self.save.setTitle("");
            self.displayResultArea.setSelect(self.constants.SHOW_PREVIEW_TIP);
        });

        this.west = BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.htape",
                    cls: "filter-table-name",
                    items: [{
                        type: "bi.absolute",
                        items:[{
                            el:{
                                type: "bi.label",
                                cls: "table-name-text",
                                textAlign: "left",
                                textHeight: this.constants.FILTER_EDITOR_HEIGHT,
                                width: this.constants.FILTER_TABLE_NAME_WIDTH,
                                text:BI.i18nText("BI-Table_Name") + ":"
                            },
                            width: this.constants.FILTER_TABLE_NAME_WIDTH,
                            top: this.constants.FILTER_GAP_TEN,
                            left: this.constants.FILTER_GAP_TEN,
                            bottom: this.constants.FILTER_GAP_TEN
                        }],
                        width: this.constants.FILTER_TABLE_NAME_WIDTH
                    }, {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type:"bi.label",
                                height: this.constants.FILTER_EDITOR_HEIGHT,
                                width: this.constants.FILTER_EDITOR_WIDTH,
                                cls: "filter-table-name-editor",
                                textAlign: "left",
                                title: BI.i18nText("BI-Cannot_Change_Table_Name_Here"),
                                hgap: 10,
                                value: this.model.getDefaultTableName()
                            },
                            top: this.constants.FILTER_GAP_TEN,
                            left: this.constants.FILTER_GAP_TEN,
                            bottom: this.constants.FILTER_GAP_TEN,
                            right: this.constants.FILTER_GAP_TEN
                        }]
                    }]
                },
                height: this.constants.FILTER_NORTH_HEIGHT
            }, {
                type: "bi.default",
                height: this.constants.FILTER_GAP_TEN
            }, {
                el: this.filterOpeartor
            }],
            hgap: this.constants.FILTER_GAP_TWENTY
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
                            cls: "filter-result-tip",
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
                                    left: self.constants.FILTER_GAP_TWENTY,
                                    top: self.constants.FILTER_GAP_TEN,
                                    right: self.constants.FILTER_GAP_FORTY,
                                    bottom: self.constants.FILTER_GAP_TWENTY
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
            cls: "filter-preview-result",
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Data"),
                    textAlign: "left",
                    height: this.constants.FILTER_NORTH_HEIGHT,
                    textHeight: this.constants.FILTER_NORTH_HEIGHT,
                    cls: "filter-result-text",
                    whiteSpace: "normal"
                },
                height: this.constants.FILTER_NORTH_HEIGHT
            }, this.displayResultArea],
            lgap: this.constants.FILTER_GAP_TWENTY,
            rgap: this.constants.FILTER_GAP_TWENTY,
            bgap: this.constants.FILTER_GAP_TEN
        });

        return this.center;
    },

    populate: function(info){
        var self = this;
        this.model.populate(info);
        this.loadingMasker = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.west,
            text: BI.i18nText("BI-Loading"),
            offset: {
                left: self.constants.FILTER_GAP_TWENTY,
                top: self.constants.FILTER_GAP_TEN,
                right: self.constants.FILTER_GAP_FORTY,
                bottom: self.constants.FILTER_GAP_TWENTY
            }
        });

        var fields = [], tables = this.model.getTableStructure();
        var conditions = this.model.getFilterValue();
        self.save.setEnable(!BI.isEmpty(conditions));
        self.save.setTitle(self.save.isEnabled() ? "" : BI.i18nText("BI-Please_Set_Filter_Condition"));
        this.model.getTablesDetailInfoByTables(function(data){
            fields = data[0].fields;
            self.filterOpeartor.populate({
                conditions: conditions || [],
                table: BI.extend(tables, {
                    fields: fields,
                    table_name: self.model.getOperatorTableName()
                })
            });
            self.loadingMasker.destroy();
            self.loadingMasker = null;
        });

        //刷新右侧预览区域
        var isGenerated = this.model.isCubeGenerated();
        if(BI.isNotNull(isGenerated) && isGenerated === true){
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_BUTTON);
        } else {
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_TIP);
        }
    }
});

BI.FilterData.EVENT_CANCEL = "EVENT_CANCEL";
BI.FilterData.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.filter_data", BI.FilterData);

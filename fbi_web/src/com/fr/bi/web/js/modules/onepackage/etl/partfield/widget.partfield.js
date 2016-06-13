/**
 * @class BI.PartField
 * @extend BI.Widget
 * @author windy
 */
BI.PartField = BI.inherit(BI.Widget, {

    constants: {
        PART_FIELD_NORTH_HEIGHT: 50,
        PART_FIELD_SOUTH_HEIGHT: 60,
        PART_FIELD_WEST_WIDTH: 530,
        PART_FIELD_TABLE_NAME_WIDTH: 60,
        PART_FIELD_BUTTON_GAP: 20,
        PART_FIELD_BUTTON_HEIGHT: 30,
        PART_FIELD_EDITOR_HEIGHT: 30,
        PART_FIELD_EDITOR_WIDTH: 220,
        PART_FIELD_REGION_HEIGHT: 180,
        PART_FIELD_GAP_TEN: 10,
        PART_FIELD_GAP_TWENTY: 20,
        PART_FIELD_TYPE_STRING: 0,
        PART_FIELD_TYPE_NUMBER: 1,
        PART_FIELD_TYPE_DATE: 2,
        PART_FIELD_GAP_FORTY: 40,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        SHOW_PREVIEW_BUTTON: 1,
        SHOW_PREVIEW_TIP: 2,
        SHOW_PREVIEW_TABLE: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.PartField.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-selectpartfield",
            info: {}
        });
    },

    _init: function(){
        BI.PartField.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.PartFieldModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {
                    el: this._buildNorth(),
                    height: this.constants.PART_FIELD_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.PART_FIELD_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.PART_FIELD_WEST_WIDTH
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


    _checkStatus: function(){
        var fieldState = this.model.getFieldState();
        var result = BI.filter(fieldState, function(idx, item){
            return item.checked === true;
        });
        if(result.length === 0 || result.length === fieldState.length){
            this.save.setEnable(false);
            this.save.setTitle(BI.i18nText("BI-Please_Select_Needed_Field"));
            return;
        }
        var relations = this.model.getRelations();
        BI.remove(relations.primKeyMap, function(id){
            return BI.contains(diffFieldIds, id);
        }, this);
        BI.remove(relations.foreignKeyMap, function(id){
            return BI.contains(diffFieldIds, id);
        }, this);

        this.save.setEnable(true);
        this.save.setTitle("");
        this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_TIP);
    },

    _buildNorth: function(){
        return BI.createWidget({
            type: "bi.left",
            cls: "part-field-north",
            items: [{
                type: "bi.label",
                cls: "part-field-north-label",
                text: BI.i18nText("BI-Use_Part_Of_Fields"),
                height: this.constants.PART_FIELD_NORTH_HEIGHT
            }],
            hgap: this.constants.PART_FIELD_GAP_TWENTY
        })
    },

    _buildSouth: function(){
        var self = this;

        var cancel = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.PART_FIELD_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            tipType: "warning",
            height: this.constants.PART_FIELD_BUTTON_HEIGHT
        });

        cancel.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker()
            self.fireEvent(BI.PartField.EVENT_CANCEL);
        });

        this.save.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.PartField.EVENT_SAVE, self.model.getValue());
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            height: this.constants.PART_FIELD_SOUTH_HEIGHT,
            items: {
                left: [cancel],
                right: [this.save]
            },
            lhgap: this.constants.PART_FIELD_BUTTON_GAP,
            rhgap: this.constants.PART_FIELD_BUTTON_GAP
        });
    },

    _buildWest: function(){
        var self = this;
        this.fieldList = BI.createWidget({
            type: "bi.select_part_field_list"
        });

        this.fieldList.on(BI.SelectPartFieldList.EVENT_CHANGE,function(){
            var value = self.fieldList.getValue();
            value = value.type === BI.ButtonGroup.CHOOSE_TYPE_MULTI ? value.value : value.assist;
            self.model.setFieldState({
                selectField: value,
                notselectField: self.fieldList.getNotSelectedValue()
            });
            self._checkStatus();
        });

        this.west = BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.htape",
                    cls: "part-field-table-name",
                    items: [{
                        type: "bi.absolute",
                        items:[{
                            el:{
                                type: "bi.label",
                                cls: "table-name-text",
                                textAlign: "left",
                                textHeight: this.constants.PART_FIELD_EDITOR_HEIGHT,
                                width: this.constants.PART_FIELD_TABLE_NAME_WIDTH,
                                text:BI.i18nText("BI-Table_Name") + ":"
                            },
                            width: this.constants.PART_FIELD_TABLE_NAME_WIDTH,
                            top: this.constants.PART_FIELD_GAP_TEN,
                            left: this.constants.PART_FIELD_GAP_TEN,
                            bottom: this.constants.PART_FIELD_GAP_TEN
                        }],
                        width: this.constants.PART_FIELD_TABLE_NAME_WIDTH
                    }, {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type:"bi.label",
                                height: this.constants.PART_FIELD_EDITOR_HEIGHT,
                                width: this.constants.PART_FIELD_EDITOR_WIDTH,
                                cls: "part-field-table-name-editor",
                                textAlign: "left",
                                value: this.model.getDefaultTableName(),
                                title: BI.i18nText("BI-Cannot_Change_Table_Name_Here"),
                                hgap: 10
                            },
                            top: this.constants.PART_FIELD_GAP_TEN,
                            left: this.constants.PART_FIELD_GAP_TEN,
                            bottom: this.constants.PART_FIELD_GAP_TEN,
                            right: this.constants.PART_FIELD_GAP_TEN
                        }]
                    }]
                },
                height: this.constants.PART_FIELD_NORTH_HEIGHT
            }, {
                type: "bi.default",
                height: this.constants.PART_FIELD_GAP_TEN
            }, {
                el: {
                    type: "bi.absolute",
                    cls: "part-field-operator-pane",
                    items: [{
                        el: this.fieldList,
                        top: 10,
                        left: 10,
                        right: 10,
                        bottom:10
                    }]
                }
            }],
            hgap: this.constants.PART_FIELD_GAP_TWENTY
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
                        type: "bi.center_adapt",
                        items: [{
                            type: "bi.label",
                            cls: "part-field-result-tip",
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
                                    left: self.constants.PART_FIELD_GAP_TWENTY,
                                    top: self.constants.PART_FIELD_GAP_TEN,
                                    right: self.constants.PART_FIELD_GAP_FORTY,
                                    bottom: self.constants.PART_FIELD_GAP_TWENTY
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
            cls: "part-field-preview-result",
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Data"),
                    textAlign: "left",
                    height: this.constants.PART_FIELD_NORTH_HEIGHT,
                    textHeight: this.constants.PART_FIELD_NORTH_HEIGHT,
                    cls: "part-field-result-text",
                    whiteSpace: "normal"
                },
                height: this.constants.PART_FIELD_NORTH_HEIGHT
            }, this.displayResultArea],
            lgap: this.constants.PART_FIELD_GAP_TWENTY,
            rgap: this.constants.PART_FIELD_GAP_TWENTY,
            bgap: this.constants.PART_FIELD_GAP_TEN
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
                left: self.constants.PART_FIELD_GAP_TWENTY,
                top: self.constants.PART_FIELD_GAP_TEN,
                right: self.constants.PART_FIELD_GAP_FORTY,
                bottom: self.constants.PART_FIELD_GAP_TWENTY
            }
        });

        //刷新列表
        var states = this.model.getFieldState();
        var notselectedValue = [];
        BI.each(states, function(idx, state){
            if(state["checked"] === false){
                notselectedValue.push(state["field_name"]);
            }
        });
        this._checkStatus();

        var fields = [];
        self.model.getTablesDetailInfoByTables(function(data){
            fields = data[0].fields;
            fields = fields[self.constants.PART_FIELD_TYPE_STRING].concat(fields[self.constants.PART_FIELD_TYPE_NUMBER]).concat(fields[self.constants.PART_FIELD_TYPE_DATE]);
            self.fieldList.populate(fields);
            self.fieldList.setNotSelectedValue(notselectedValue);
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

BI.PartField.EVENT_CANCEL = "EVENT_CANCEL";
BI.PartField.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.part_field", BI.PartField);

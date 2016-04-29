/**
 * @class BI.Circle
 * @extend BI.Widget
 * @author windy
 */
BI.Circle = BI.inherit(BI.Widget, {

    constants: {
        CIRCLE_NORTH_HEIGHT: 50,
        CIRCLE_SOUTH_HEIGHT: 60,
        CIRCLE_WEST_WIDTH: 530,
        CIRCLE_BUTTON_GAP: 20,
        CIRCLE_BUTTON_HEIGHT: 30,
        CIRCLE_EDITOR_HEIGHT: 30,
        CIRCLE_EDITOR_WIDTH: 220,
        CIRCLE_TABLE_NAME_WIDTH: 60,
        CIRCLE_EDITOR_GAP: 5,
        CIRCLE_GAP_TEN: 10,
        CIRCLE_GAP_TWENTY: 20,
        CIRCLE_GAP_FORTY: 40,
        CIRCLE_TYPE_NUMBER: 1,
        CIRCLE_TYPE_STRING: 0,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        SHOW_PREVIEW_BUTTON: 1,
        SHOW_PREVIEW_TIP: 2,
        SHOW_PREVIEW_TABLE: 3,
        SHOW_CONSTRUCT_TIP: 4
    },

    _defaultConfig: function(){
        return BI.extend(BI.Circle.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-circle",
            info: {}
        });
    },

    _init: function(){
        BI.Circle.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.model = new BI.CircleModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {
                    el: this._buildNorth(),
                    height: this.constants.CIRCLE_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.CIRCLE_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.CIRCLE_WEST_WIDTH
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
            cls: "circle-north",
            items: [{
                type: "bi.label",
                cls: "circle-north-label",
                text: BI.i18nText("BI-Self_Cycle_Column_Super"),
                height: this.constants.CIRCLE_NORTH_HEIGHT
            }],
            hgap: this.constants.CIRCLE_GAP_TWENTY
        })
    },

    _buildSouth: function(){
        var self = this;
        var cancel = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.CIRCLE_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            tipType: "warning",
            height: this.constants.CIRCLE_BUTTON_HEIGHT
        });

        cancel.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.Circle.EVENT_CANCEL);
        });

        this.save.on(BI.Button.EVENT_CHANGE,function(){
            self.model.setOperatorValue(self.operatorPane.getValue());
            self._checkMasker();
            self.fireEvent(BI.Circle.EVENT_SAVE, self.model.getValue());
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            height: this.constants.CIRCLE_SOUTH_HEIGHT,
            items: {
                left: [cancel],
                right: [this.save]
            },
            lhgap: this.constants.CIRCLE_BUTTON_GAP,
            rhgap: this.constants.CIRCLE_BUTTON_GAP
        });
    },

    _buildWest: function(){
        var self = this;

        this.operatorPane = BI.createWidget({
            type: "bi.circle_self",
            cls: "circle-operator-pane"
        });

        this.operatorPane.on(BI.CircleSelf.EVENT_SHOW_RESULT_PANE, function(){
            self.save.setEnable(true);
            self.save.setTitle("");
            self.displayResultArea.setSelect(self.constants.SHOW_PREVIEW_TIP);
        });

        this.operatorPane.on(BI.CircleSelf.EVENT_SHOW_OPERATOR_PANE, function(){
            self.save.setEnable(false);
            self.save.setTitle(BI.i18nText("BI-Please_Complete_Circle_Construct"));
            self.displayResultArea.setSelect(self.constants.SHOW_CONSTRUCT_TIP);
        });

        this.west = BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.htape",
                    cls: "circle-table-name",
                    items: [{
                        type: "bi.absolute",
                        items:[{
                            el:{
                                type: "bi.label",
                                cls: "table-name-text",
                                textAlign: "left",
                                textHeight: this.constants.CIRCLE_EDITOR_HEIGHT,
                                width: this.constants.CIRCLE_TABLE_NAME_WIDTH,
                                text:BI.i18nText("BI-Table_Name") + ":"
                            },
                            width: this.constants.CIRCLE_TABLE_NAME_WIDTH,
                            top: this.constants.CIRCLE_GAP_TEN,
                            left: this.constants.CIRCLE_GAP_TEN,
                            bottom: this.constants.CIRCLE_GAP_TEN
                        }],
                        width: this.constants.CIRCLE_TABLE_NAME_WIDTH
                    }, {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type:"bi.label",
                                height: this.constants.CIRCLE_EDITOR_HEIGHT,
                                width: this.constants.CIRCLE_EDITOR_WIDTH,
                                cls: "circle-table-name-editor",
                                textAlign: "left",
                                title: BI.i18nText("BI-Cannot_Change_Table_Name_Here"),
                                hgap: 10,
                                value: this.model.getDefaultTableName()
                            },
                            top: this.constants.CIRCLE_GAP_TEN,
                            left: this.constants.CIRCLE_GAP_TEN,
                            bottom: this.constants.CIRCLE_GAP_TEN,
                            right: this.constants.CIRCLE_GAP_TEN
                        }]
                    }]
                },
                height: this.constants.CIRCLE_NORTH_HEIGHT
            }, {
                type: "bi.default",
                height: this.constants.CIRCLE_GAP_TEN
            }, {
                el: this.operatorPane
            }],
            hgap: this.constants.CIRCLE_GAP_TWENTY
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
                            cls: "circle-result-tip",
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
                                    left: self.constants.CIRCLE_GAP_TWENTY,
                                    top: self.constants.CIRCLE_GAP_TEN,
                                    right: self.constants.CIRCLE_GAP_FORTY,
                                    bottom: self.constants.CIRCLE_GAP_TWENTY
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
                case this.constants.SHOW_CONSTRUCT_TIP:
                    return BI.createWidget({
                        type: "bi.float_center_adapt",
                        items: [{
                            type: "bi.label",
                            cls: "circle-result-tip",
                            text: BI.i18nText("BI-Complete_On_The_Left")
                        }]
                    });
            }
        }

        this.center = BI.createWidget({
            type: "bi.vtape",
            cls: "circle-preview-result",
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Data"),
                    textAlign: "left",
                    height: this.constants.CIRCLE_NORTH_HEIGHT,
                    textHeight: this.constants.CIRCLE_NORTH_HEIGHT,
                    cls: "circle-result-text",
                    whiteSpace: "normal"
                },
                height: this.constants.CIRCLE_NORTH_HEIGHT
            }, this.displayResultArea],
            lgap: this.constants.CIRCLE_GAP_TWENTY,
            rgap: this.constants.CIRCLE_GAP_TWENTY,
            bgap: this.constants.CIRCLE_GAP_TEN
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
                left: self.constants.CIRCLE_GAP_TWENTY,
                top: self.constants.CIRCLE_GAP_TEN,
                right: self.constants.CIRCLE_GAP_FORTY,
                bottom: self.constants.CIRCLE_GAP_TWENTY
            }
        });

        if(this.model.isReopen()){
            this.operatorPane.showResultPane();
            this.save.setEnable(true);
            this.save.setTitle("");
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_TIP);
        }else{
            this.operatorPane.showOperatorPane();
            this.save.setEnable(false);
            this.save.setTitle(BI.i18nText("BI-Please_Complete_Circle_Construct"));
            this.displayResultArea.setSelect(this.constants.SHOW_CONSTRUCT_TIP);
        }

        var fields = [];
        this.model.getTablesDetailInfoByTables(function(data){
            fields = BI.flatten(data[0].fields);
            self.operatorPane.populate({
                fields: fields,
                table: self.model.getPreTableStructure()
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

BI.Circle.EVENT_CANCEL = "EVENT_CANCEL";
BI.Circle.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.circle", BI.Circle);

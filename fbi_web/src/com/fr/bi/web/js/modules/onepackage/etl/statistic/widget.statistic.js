/**
 * @class BI.GroupStatistic
 * @extends BI.Widget
 * @author windy
 */
BI.GroupStatistic = BI.inherit(BI.Widget, {

    constants: {
        GROUP_STATISTIC_NORTH_HEIGHT: 50,
        GROUP_STATISTIC_SOUTH_HEIGHT: 60,
        GROUP_STATISTIC_WEST_WIDTH: 530,
        GROUP_STATISTIC_TABLE_NAME_WIDTH: 60,
        GROUP_STATISTIC_BUTTON_GAP: 20,
        GROUP_STATISTIC_BUTTON_HEIGHT: 30,
        GROUP_STATISTIC_EDITOR_HEIGHT: 30,
        GROUP_STATISTIC_EDITOR_WIDTH: 220,
        GROUP_STATISTIC_REGION_HEIGHT: 180,
        GROUP_STATISTIC_GAP_TEN: 10,
        GROUP_STATISTIC_GAP_TWENTY: 20,
        GROUP_STATISTIC_GAP_FORTY: 40,
        GROUP_STATISTIC_TYPE_NUMBER: 1,
        GROUP_STATISTIC_TYPE_STRING: 0,
        GROUP_STATISTIC_TYPE_DATE: 2,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        SHOW_PREVIEW_BUTTON: 1,
        SHOW_PREVIEW_TIP: 2,
        SHOW_PREVIEW_TABLE: 3
    },

    _defaultConfig: function(){
        return BI.extend(BI.GroupStatistic.superclass._defaultConfig.apply(this, arguments), {
            info: {}
        });
    },

    _init: function(){
        BI.GroupStatistic.superclass._init.apply(this, arguments);
        this.dimensions = {};

        var self = this, o = this.options;
        this.model = new BI.GroupStatisticModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            cls: "bi-etl-group-and-statistic",
            items: {
                north: {
                    el: this._buildNorth(),
                    height: this.constants.GROUP_STATISTIC_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.GROUP_STATISTIC_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.GROUP_STATISTIC_WEST_WIDTH
                },
                center: this._buildCenter()
            }
        });
        this.populate(o.info);
    },

    _dropField: function (item) {
        var self = this;
        return {
            accept:".group-select-fields-item-button",
            tolerance:"pointer",
            drop: function(event,ui){
                item.removePlaceHolder();
                var helper = ui.helper;
                var field = helper.data("field");
                self.model.addDimensionByField({fieldInfo: field,regionType: item.getRegionType()});
                BI.each(self.model.getEditing(), function (idx, id) {
                    self._createDimension(id, item.getRegionType(), self.model.getDimension(id).type);
                });
                self._checkStatus();
                self.save.setEnable(true);
                self.save.setTitle("");
            },
            over: function(event, ui){
                item.addPlaceHolder();
            },
            out: function(event, ui){
                item.removePlaceHolder()
            }
        };
    },

    _sortField: function(item){
        var self = this;
        return {
            containment: item.element,//sortable 对象的(范围)父区域
            tolerance: "pointer",
            items: ".bi-string-dimension-container, .bi-string-target-container",
            scroll: false,
            placeholder: {
                element: function ($currentItem) {
                    var holder = BI.createWidget({
                        type: "bi.label",
                        cls: "ui-sortable-place-holder",
                        height: $currentItem.height() - 2
                    });
                    holder.element.css({"margin": "5px"});
                    return holder.element;
                },
                update: function () {

                }
            },
            update: function (event, ui) {
                var dimensions = item.getAllDimensions();
                self.model.setSortBySortInfo({dimensions: dimensions, regionType: item.getRegionType()});
            },
            start: function (event, ui) {
            },
            stop: function (event, ui) {
            },
            over: function (event, ui) {

            }
        }
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
            cls: "group-statistic-north",
            items:[{
                type: "bi.label",
                cls: "group-statistic-north-label",
                text: BI.i18nText("BI-Grouping_Count") + BI.i18nText("BI-Management"),
                height: this.constants.GROUP_STATISTIC_NORTH_HEIGHT
            }],
            hgap: this.constants.GROUP_STATISTIC_GAP_TWENTY
        })
    },

    _buildSouth:function(){
        var self = this;

        var cancel = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.GROUP_STATISTIC_BUTTON_HEIGHT
        });

        this.save = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            tipType: "warning",
            height: this.constants.GROUP_STATISTIC_BUTTON_HEIGHT
        });

        cancel.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.GroupStatistic.EVENT_CANCEL);
        });

        this.save.on(BI.Button.EVENT_CHANGE,function(){
            self._checkMasker();
            self.fireEvent(BI.GroupStatistic.EVENT_SAVE, self.model.getValue());
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            height: this.constants.GROUP_STATISTIC_SOUTH_HEIGHT,
            items: {
                left: [cancel],
                right: [this.save]
            },
            lhgap: this.constants.GROUP_STATISTIC_BUTTON_GAP,
            rhgap: this.constants.GROUP_STATISTIC_BUTTON_GAP
        });
    },

    _buildWest: function(){
        var self = this;
        this.fieldGroup = BI.createWidget({
            type:"bi.select_single_table_field",
            cls:"select_single_table_field"
        });

        this.regions = {};

        this.regions[BICst.REGION.DIMENSION1] = BI.createWidget({
            type: "bi.string_region",
            cls: "dimension-region",
            titleName: BI.i18nText("BI-Group"),
            regionType: BICst.REGION.DIMENSION1
        });

        this.regions[BICst.REGION.TARGET1] = BI.createWidget({
            type:"bi.string_region",
            cls: "target-region",
            titleName:BI.i18nText("BI-Statistic"),
            regionType: BICst.REGION.TARGET1
        });

        BI.each(this.regions, function(idx, region){
            region.getRegion().element.droppable(self._dropField(region));
            region.getRegion().element.sortable(self._sortField(region));
        });

        this.west = BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.htape",
                    cls: "group-statistic-table-name",
                    items: [{
                        type: "bi.absolute",
                        items:[{
                            el:{
                                type: "bi.label",
                                cls: "table-name-text",
                                textAlign: "left",
                                textHeight: this.constants.GROUP_STATISTIC_EDITOR_HEIGHT,
                                width: this.constants.GROUP_STATISTIC_TABLE_NAME_WIDTH,
                                text:BI.i18nText("BI-Table_Name") + ":"
                            },
                            width: this.constants.GROUP_STATISTIC_TABLE_NAME_WIDTH,
                            top: this.constants.GROUP_STATISTIC_GAP_TEN,
                            left: this.constants.GROUP_STATISTIC_GAP_TEN,
                            bottom: this.constants.GROUP_STATISTIC_GAP_TEN
                        }],
                        width: this.constants.GROUP_STATISTIC_TABLE_NAME_WIDTH
                    }, {
                        type: "bi.absolute",
                        items: [{
                            el: {
                                type:"bi.label",
                                height: this.constants.GROUP_STATISTIC_EDITOR_HEIGHT,
                                width: this.constants.GROUP_STATISTIC_EDITOR_WIDTH,
                                value: this.model.getDefaultTableName(),
                                cls: "group-statistic-table-name-editor",
                                textAlign: "left",
                                title: BI.i18nText("BI-Cannot_Change_Table_Name_Here"),
                                hgap: 10
                            },
                            top: this.constants.GROUP_STATISTIC_GAP_TEN,
                            left: this.constants.GROUP_STATISTIC_GAP_TEN,
                            bottom: this.constants.GROUP_STATISTIC_GAP_TEN,
                            right: this.constants.GROUP_STATISTIC_GAP_TEN
                        }]
                    }]
                },
                height: this.constants.GROUP_STATISTIC_NORTH_HEIGHT
            }, {
                type: "bi.default",
                height: 10
            },{
                el: {
                    type: "bi.center",
                    cls: "group-statistic-operator-pane",
                    items: [{
                        type: "bi.absolute",
                        items: [{
                            el: this.fieldGroup,
                            left: this.constants.GROUP_STATISTIC_GAP_TWENTY,
                            top: this.constants.GROUP_STATISTIC_GAP_TEN,
                            bottom: this.constants.GROUP_STATISTIC_GAP_TWENTY,
                            right: this.constants.GROUP_STATISTIC_GAP_TEN
                        }]
                    }, {
                        type: "bi.grid",
                        columns: 1,
                        rows: 2,
                        items: [{
                            column: 0,
                            row: 0,
                            el: {
                                type: "bi.absolute",
                                items: [{
                                    el: this.regions[BICst.REGION.DIMENSION1],
                                    top: this.constants.GROUP_STATISTIC_GAP_TEN,
                                    right: this.constants.GROUP_STATISTIC_GAP_TEN,
                                    left: this.constants.GROUP_STATISTIC_GAP_TEN,
                                    bottom: this.constants.GROUP_STATISTIC_GAP_TEN
                                }]
                            }
                        }, {
                            column: 0,
                            row: 1,
                            el: {
                                type: "bi.absolute",
                                items: [{
                                    el: this.regions[BICst.REGION.TARGET1],
                                    top: 0,
                                    right: this.constants.GROUP_STATISTIC_GAP_TEN,
                                    left: this.constants.GROUP_STATISTIC_GAP_TEN,
                                    bottom: this.constants.GROUP_STATISTIC_GAP_TWENTY
                                }]
                            }
                        }]
                    }]
                }
            }],
            hgap: this.constants.GROUP_STATISTIC_GAP_TWENTY
        });

        return this.west;
    },

    _buildCenter:function(){
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
                            cls: "group-statistic-result-tip",
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
                                    left: self.constants.GROUP_STATISTIC_GAP_TWENTY,
                                    top: self.constants.GROUP_STATISTIC_GAP_TEN,
                                    right: self.constants.GROUP_STATISTIC_GAP_FORTY,
                                    bottom: self.constants.GROUP_STATISTIC_GAP_TWENTY
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

                            var fieldTypes = [];
                            BI.each(self.model.getAllFields(), function (i, fs) {
                                BI.each(fs, function (j, field) {
                                    fieldTypes.push(field.field_type);
                                });
                            });
                            
                            BI.each(values, function(i, value){
                                var isDate = fieldTypes[i] === BICst.COLUMN.DATE;
                                BI.each(value, function(j, v){
                                    if(BI.isNotNull(items[j])){
                                        items[j].push({
                                            text: isDate === true ? self._formatDate(v) : v
                                        });
                                    } else {
                                        items.push([{
                                            text: isDate === true ? self._formatDate(v) : v
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
            cls: "group-statistic-preview-result",
            items: [{
                el: {
                    type: "bi.label",
                    text: BI.i18nText("BI-Table_Data"),
                    textAlign: "left",
                    height: this.constants.GROUP_STATISTIC_NORTH_HEIGHT,
                    textHeight: this.constants.GROUP_STATISTIC_NORTH_HEIGHT,
                    cls: "group-statistic-result-text",
                    whiteSpace: "normal"
                },
                height: this.constants.GROUP_STATISTIC_NORTH_HEIGHT
            }, this.displayResultArea],
            lgap: this.constants.GROUP_STATISTIC_GAP_TWENTY,
            rgap: this.constants.GROUP_STATISTIC_GAP_TWENTY,
            bgap: this.constants.GROUP_STATISTIC_GAP_TEN
        });

        return this.center;
    },

    _getDimensionTypeByRegionAndDimensionType: function (regionType, type) {
        if(BI.parseInt(regionType) == BICst.REGION.DIMENSION1 ){
            switch (BI.parseInt(type)) {
                case BICst.TARGET_TYPE.DATE:
                    return "bi.date_group_dimension";
                case BICst.TARGET_TYPE.NUMBER :
                    return "bi.number_group_dimension";
                case BICst.TARGET_TYPE.STRING :
                    return "bi.string_group_dimension";
                default :
                    return "bi.number_group_dimension";
            }
        }
        if(BI.parseInt(regionType) == BICst.REGION.TARGET1 ) {
            switch (BI.parseInt(type)) {
                case BICst.TARGET_TYPE.DATE :
                    return "bi.date_statistic_dimension";
                case BICst.TARGET_TYPE.NUMBER :
                    return "bi.number_statistic_dimension";
                case BICst.TARGET_TYPE.STRING :
                    return "bi.string_statistic_dimension";
                default :
                    return "bi.number_statistic_dimension";
            }
        }
    },

    _checkStatus: function(){
        var view = this.model.getView(), self = this;
        BI.each(view,function(region, id){
            self.regions[region].setCommentVisible(BI.isEmpty(id));
        });
        if(BI.size(this.dimensions) === 0){
            this.save.setEnable(false);
            this.save.setTitle(BI.i18nText("BI-Please_Set_Group_Statistic_Type"));
        }else{
            this.save.setEnable(true);
            this.save.setTitle("");
        }
        this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_TIP);
    },

    _createDimension:function(id, regionType, fieldType){
        var self = this;
        var currentRegion = this.regions[regionType], cls = "bi-string-target-container";
        if (BI.parseInt(regionType) === BI.parseInt(BICst.REGION.DIMENSION1)){
            cls = "bi-string-dimension-container";
        }

        if(!this.dimensions[id]){
            currentRegion.setCommentVisible(false);
            this.dimensions[id] = BI.createWidget({
                type: this._getDimensionTypeByRegionAndDimensionType(regionType, fieldType),
                cls: cls + " sign-style-editor",
                attributes: {
                    dimensionid: id
                },
                dId: id,
                model: this.model,
                fieldName: this.model.getDimension(id)._src.field_name,
                table: this.model.getTableStructure()
            });
            this.dimensions[id].on(BI.AbstractDimension.EVENT_DESTROY, function(){
                self.dimensions[id].destroy();
                delete self.dimensions[id];
                self.model.deleteDimension(id);
                self._checkStatus();
            });
            currentRegion.addDimension(this.dimensions[id]);
        }
    },

    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y/%X/%d %H:%M:%S")
    },

    populate: function(info){
        var self = this;
        this.model.populate(info);
        this.loadingMasker = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.west,
            text: BI.i18nText("BI-Loading"),
            offset: {
                left: self.constants.GROUP_STATISTIC_GAP_TWENTY,
                top: self.constants.GROUP_STATISTIC_GAP_TEN,
                right: self.constants.GROUP_STATISTIC_GAP_FORTY,
                bottom: self.constants.GROUP_STATISTIC_GAP_TWENTY
            }
        });

        var fields = [];
        this.model.getTablesDetailInfoByTables(function(data){
            fields = data[0].fields;
            fields = fields[self.constants.GROUP_STATISTIC_TYPE_STRING].concat(fields[self.constants.GROUP_STATISTIC_TYPE_NUMBER]).concat(fields[self.constants.GROUP_STATISTIC_TYPE_DATE]);
            self.fieldGroup.populate(fields);

            //刷新Region中的维度指标
            var view = self.model.getView();
            var dimensions = self.model.getDimension();

            //先遍历一遍dimensions，model没有的删掉
            BI.each(self.dimensions, function(idx, dimension){
                if(!dimensions[idx]){
                    self.dimensions[idx] && self.dimensions[idx].destroy();
                    delete self.dimensions[idx];
                }
            });

            self.regions[BICst.REGION.DIMENSION1].setCommentVisible(true);
            self.regions[BICst.REGION.TARGET1].setCommentVisible(true);

            BI.each(view, function(idx, vc){
                BI.each(vc, function(id, v){
                    if(BI.has(dimensions, v)){
                        self._createDimension(v, idx, dimensions[v].type);
                    }
                });
            });

            var view = self.model.getView();
            if (!BI.has(view, BICst.REGION.DIMENSION1) && !BI.has(view, BICst.REGION.TARGET1)) {
                var numberFields = [];
                BI.each(fields, function (id, field) {
                    var type = field["field_type"];
                    if (type == BICst.COLUMN.NUMBER) {
                        numberFields.push(field)
                    }
                });
                self.model.addDimensionByField({fieldInfo: numberFields, regionType: BICst.REGION.TARGET1});
                BI.each(self.model.getEditing(), function (idx, id) {
                    self._createDimension(id, BICst.REGION.TARGET1, self.model.getDimension(id).type);
                });
                self._checkStatus();
            }
            self.loadingMasker.destroy();
            self.loadingMasker = null;
        });

        //刷新右侧预览界面
        var isGenerated = this.model.isCubeGenerated();
        if(BI.isNotNull(isGenerated) && isGenerated === true){
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_BUTTON);
        } else {
            this.displayResultArea.setSelect(this.constants.SHOW_PREVIEW_TIP);
        }
    }
});

BI.GroupStatistic.EVENT_CANCEL = "EVENT_CANCEL";
BI.GroupStatistic.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.group_statistic", BI.GroupStatistic);


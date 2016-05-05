/**
 * Created by Young's on 2016/3/11.
 */
BI.Join = BI.inherit(BI.Widget, {

    constants: {
        JOIN_NORTH_HEIGHT: 50,
        JOIN_SOUTH_HEIGHT: 60,
        JOIN_WEST_WIDTH: 460,
        JOIN_BUTTON_GAP: 20,
        JOIN_BUTTON_HEIGHT: 28,
        JOIN_EDITOR_HEIGHT: 26,
        JOIN_EDITOR_WIDTH: 220,
        JOIN_EDITOR_GAP: 5,
        JOIN_GAP_TEN: 10,
        JOIN_GAP_TWENTY: 20,
        JOIN_TYPE_GROUP_HEIGHT: 180,
        JOIN_TYPE_TEXT_HEIGHT: 40,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        PREVIEW_AREA_HEIGHT: 260,
        PREVIEW_TABLE_HEIGHT: 150,

        SHOW_HEADER: 1,
        SHOW_TIP: 2,
        SHOW_TABLE: 3,
        SHOW_BUTTON: 4
    },

    _defaultConfig: function(){
        return BI.extend(BI.Join.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-join"
        })
    },

    _init: function(){
        BI.Join.superclass._init.apply(this, arguments);
        var o = this.options;
        this.model = new BI.JoinModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {
                    el: {
                        type: "bi.left",
                        cls: "join-north",
                        items: [{
                            type: "bi.label",
                            cls: "join-north-label",
                            text: "Join" + BI.i18nText("BI-Management"),
                            height: this.constants.JOIN_NORTH_HEIGHT
                        }],
                        hgap: this.constants.JOIN_GAP_TWENTY
                    },
                    height: this.constants.JOIN_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.JOIN_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.JOIN_WEST_WIDTH
                },
                center: this._buildCenter()
            }
        })
    },

    _buildWest: function(){
        var self = this;
        //重新选择表按钮
        this.changeJoinTable = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Modify_Used_Tables"),
            height: this.constants.JOIN_EDITOR_HEIGHT
        });
        this.changeJoinTable.on(BI.Button.EVENT_CHANGE, function(){
            self._reselectJoinTable();
        });
        //合并依据表格
        self.model.isReopen() === true && this.changeJoinTable.setVisible(false);
        var addMergeButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_The_Merged_Basis"),
            height: this.constants.JOIN_BUTTON_HEIGHT
        });
        addMergeButton.on(BI.Button.EVENT_CHANGE, function(){
            var joinFields = self.model.getJoinFields();
            joinFields.push([BI.TableAddUnion.UNION_FIELD_NULL, BI.TableAddUnion.UNION_FIELD_NULL]);
            self.model.setJoinFields(joinFields);
            self._refreshJoinTableAndResult();
        });
        this.joinTableFields = BI.createWidget({
            type: "bi.table_add_union",
            tables: self.model.getAllTables()
        });
        this.joinTableFields.on(BI.TableAddUnion.EVENT_REMOVE_UNION, function(index){
            self._onRemoveOneJoinField(index);
        });
        this.joinTableFields.on(BI.TableAddUnion.EVENT_CHANGE, function(row, col, nValue, oValue){
            self._onJoinComboChanged(row, col, nValue);
        });
        this.mergeArea = BI.createWidget({
            type: "bi.vtape",
            items: [{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    cls: "add-merge-field",
                    height: this.constants.JOIN_NORTH_HEIGHT,
                    items: {
                        left: [{
                            type: "bi.label",
                            cls: "merge-field-text",
                            text: BI.i18nText("BI-Merge_By_Following_Fields")
                        }],
                        right: [addMergeButton]
                    }
                },
                height: this.constants.JOIN_NORTH_HEIGHT
            }, {
                el: this.joinTableFields,
                height: "fill"
            }],
            cls: "join-merge-field",
            vgap: this.constants.JOIN_GAP_TEN,
            hgap: this.constants.JOIN_GAP_TEN
        });

        //合并方式 left, right, inner, outer
        this.joinType = BI.createWidget({
            type: "bi.join_type_group"
        });
        this.joinType.on(BI.JoinTypeGroup.EVENT_CHANGE, function(){
            self.model.setJoinStyle(self.joinType.getValue()[0]);
            self._refreshJoinTableAndResult();
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.left",
                    cls: "join-table-name",
                    items: [{
                        type:"bi.label",
                        cls: "table-name-text",
                        textAlign:"left",
                        textHeight:this.constants.JOIN_EDITOR_HEIGHT,
                        text:BI.i18nText("BI-Table_Name") + "：",
                        lgap: 5
                    }, {
                        type:"bi.label",
                        text: self.model.getDefaultTableName(),
                        height: this.constants.JOIN_EDITOR_HEIGHT,
                        width: this.constants.JOIN_EDITOR_WIDTH,
                        textAlign: "left",
                        hgap: 10
                    }, this.changeJoinTable],
                    hgap: this.constants.JOIN_EDITOR_GAP,
                    vgap: this.constants.JOIN_GAP_TEN
                },
                top: 0,
                left: this.constants.JOIN_GAP_TWENTY,
                right: this.constants.JOIN_GAP_TWENTY
            }, {
                el: {
                    type: "bi.vertical",
                    cls: "join-type-group",
                    items: [{
                        type: "bi.left",
                        cls: "join-type-text",
                        height: this.constants.JOIN_TYPE_TEXT_HEIGHT,
                        items: [{
                            type: "bi.label",
                            text: BI.i18nText("BI-Merge_Type"),
                            height: this.constants.JOIN_TYPE_TEXT_HEIGHT
                        }]
                    }, self.joinType],
                    vgap: this.constants.JOIN_EDITOR_GAP,
                    hgap: this.constants.JOIN_GAP_TEN
                },
                top: this.constants.JOIN_SOUTH_HEIGHT,
                left: this.constants.JOIN_GAP_TWENTY,
                right: this.constants.JOIN_GAP_TWENTY
            }, {
                el: this.mergeArea,
                top: this.constants.JOIN_GAP_TWENTY + this.constants.JOIN_NORTH_HEIGHT + this.constants.JOIN_TYPE_GROUP_HEIGHT,
                left: this.constants.JOIN_GAP_TWENTY,
                right: this.constants.JOIN_GAP_TWENTY,
                bottom: 0
            }]
        })
    },

    _buildSouth:function(){
        var self = this;
        var cancelButton = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.JOIN_BUTTON_HEIGHT
        });
        cancelButton.on(BI.Button.EVENT_CHANGE,function(){
            self.fireEvent(BI.Join.EVENT_CANCEL);
        });

        this.saveButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Save"),
            title: BI.i18nText("BI-Save"),
            height: this.constants.JOIN_BUTTON_HEIGHT
        });

        this.saveButton.on(BI.Button.EVENT_CHANGE,function(){
            var data = {
                connection_name: BICst.CONNECTION.ETL_CONNECTION,
                etl_type: "join",
                etl_value: {
                    join_fields: self.model.getJoinFieldsName(),
                    join_style: self.model.getJoinStyle(),
                    join_names: self.model.getJoinNames()
                },
                tables: self.model.getAllTables()
            };
            self.fireEvent(BI.Join.EVENT_SAVE, data);
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            height: this.constants.JOIN_SOUTH_HEIGHT,
            items: {
                left: [cancelButton],
                right: [this.saveButton]
            },
            lhgap: this.constants.JOIN_BUTTON_GAP,
            rhgap: this.constants.JOIN_BUTTON_GAP
        });
    },

    _buildCenter:function(){
        //原始表预览区域
        this.previewTab = BI.createWidget({
            type: "bi.tab",
            cardCreator: BI.bind(this._createPreviewCard, this)
        });

        //合并结果区域
        this.resultTab = BI.createWidget({
            type: "bi.tab",
            cardCreator: BI.bind(this._createResultCard, this)
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "join-preview-result",
                    items: [{
                        el: {
                            type: "bi.vtape",
                            items: [{
                                el: {
                                    type: "bi.label",
                                    text: BI.i18nText("BI-Original_Data"),
                                    textAlign: "left",
                                    cls: "merge-result-text"
                                },
                                height: this.constants.JOIN_BUTTON_HEIGHT
                            }, {
                                el: this.previewTab,
                                height: "fill"
                            }],
                            hgap: this.constants.JOIN_GAP_TEN,
                            vgap: this.constants.JOIN_GAP_TEN
                        },
                        height: this.constants.PREVIEW_AREA_HEIGHT
                    }, {
                        el: {
                            type: "bi.vtape",
                            items: [{
                                el: {
                                    type: "bi.label",
                                    text: BI.i18nText("BI-Merge_Result"),
                                    textAlign: "left",
                                    cls: "merge-result-text"
                                },
                                height: this.constants.JOIN_BUTTON_HEIGHT
                            }, {
                                el: this.resultTab,
                                height: "fill"
                            }],
                            hgap: this.constants.JOIN_GAP_TEN,
                            vgap: this.constants.JOIN_GAP_TEN
                        },
                        height: "fill"
                    }]
                },
                left: 0,
                right: this.constants.JOIN_GAP_TWENTY,
                top: 0,
                bottom: 0
            }]
        });
    },

    _createPreviewCard: function(v) {
        var self = this;
        switch (v) {
            case this.constants.SHOW_TIP:
                return BI.createWidget({
                    type: "bi.horizontal_adapt",
                    verticalAlign: BI.VerticalAlign.Top,
                    items: [{
                        type: "bi.label",
                        cls: "join-result-tip",
                        text: BI.i18nText("BI-Generate_Cube_Then_Review"),
                        height: this.constants.PREVIEW_TABLE_HEIGHT
                    }]
                });
            case this.constants.SHOW_TABLE:
                return this.originalTablesArea = BI.createWidget({
                    type: "bi.horizontal_adapt",
                    verticalAlign: BI.VerticalAlign.Top,
                    scrollable: true
                });
            case this.constants.SHOW_BUTTON:
                var previewButton = BI.createWidget({
                    type: "bi.button",
                    text: BI.i18nText("BI-Preview"),
                    height: this.constants.PREVIEW_BUTTON_HEIGHT,
                    width: this.constants.PREVIEW_BUTTON_WIDTH
                });
                previewButton.on(BI.Button.EVENT_CHANGE, function(){
                    self.previewTab.setSelect(self.constants.SHOW_TABLE);
                    var tables = self.model.getJoinTables();
                    var count = tables.length + 1;
                    var mask = BI.createWidget({
                        type: "bi.loading_mask",
                        masker: self.element,
                        text: BI.i18nText("BI-Loading")
                    });
                    var interval = setInterval(function(){
                        if(count === 0){
                            mask.destroy();
                            clearInterval(interval);
                        }
                    }, 1000);
                    BI.each(tables, function(i, table){
                        var wrapper = BI.createWidget({
                            type: "bi.vertical",
                            hgap: self.constants.JOIN_GAP_TEN
                        });
                        BI.Utils.getPreviewDataByTableAndFields(table, [], function(data){
                            count--;
                            var item = self._createTableItems(data, i);
                            var tableView = BI.createWidget({
                                type: "bi.preview_table",
                                items: item.items,
                                header: item.header
                            });
                            wrapper.addItems([{
                                type: "bi.label",
                                text: BI.isNotNull(table.table_name) ? table.table_name : self.model.getETLTableNameByTable(table),
                                cls: "original-table-name",
                                height: self.constants.PREVIEW_BUTTON_HEIGHT
                            }, {
                                el: {
                                    type: "bi.vertical",
                                    items: [tableView],
                                    height: self.constants.PREVIEW_TABLE_HEIGHT
                                }
                            }]);
                        });
                        self.originalTablesArea.addItem(wrapper);
                    });
                    self.resultTab.setSelect(self.constants.SHOW_TABLE);
                    BI.Utils.getPreviewDataByTableAndFields(self.model.getTableInfo(), [], function(data){
                        count--;
                        var item = self._createResultTableItems(data);
                        self.resultTable.populate(item.items, item.header);
                    })
                });
                return BI.createWidget({
                    type: "bi.horizontal_adapt",
                    verticalAlign: BI.VerticalAlign.Top,
                    items: [previewButton]
                })
        }
    },

    _createResultCard: function(v){
        var self = this;
        switch (v) {
            case this.constants.SHOW_HEADER:
                this.joinResultHeader = BI.createWidget({
                    type: "bi.join_result_header",
                    mergeResult: this.model.getJoinNames()
                });
                this.joinResultHeader.on(BI.JoinResultHeader.EVENT_CHANGE, function(joinNames){
                    self.model.setJoinNames(joinNames);
                });
                return this.joinResultHeader;
            case this.constants.SHOW_TIP:
                return BI.createWidget({
                    type: "bi.label",
                    text: BI.i18nText("BI-Please_Finish_Merge_Field_Correct"),
                    cls: "join-result-tip",
                    height: 50
                });
            case this.constants.SHOW_TABLE:
                return this.resultTable = BI.createWidget({
                    type: "bi.preview_table"
                });
        }
    },

    /**
     * 刷新合并字段区域（页面左侧）
     * @private
     */
    _populateMergeFieldArea: function(){
        this.joinType.setValue(this.model.getJoinStyle());
        this.joinTableFields.populate(this.model.getAllTables(), this.model.getJoinFields());
    },

    /**
     * 刷新预览区域（右侧）
     * @private
     */
    _populateReviewResult: function(){
        var isGenerated = this.model.isCubeGenerated();
        if(BI.isNotNull(isGenerated) && isGenerated === true){
            this.previewTab.setSelect(this.constants.SHOW_BUTTON);
        } else {
            this.previewTab.setSelect(this.constants.SHOW_TIP);
        }
        if(this.model.checkMergeFields()) {
            this.resultTab.setSelect(this.constants.SHOW_HEADER);
            this.joinResultHeader.populate(this.model.getJoinNames());
        } else {
            this.resultTab.setSelect(this.constants.SHOW_TIP);
        }
    },

    _createTableItems: function(data, index){
        var fields = data.fields, values = data.value;
        var header = [], items = [];
        BI.each(fields, function(i, field){
            header.push({
                text: field,
                height: "100%"
            });
        });
        BI.each(values, function(i, value){
            BI.each(value, function(j, v){
                if(BI.isNotNull(items[j])){
                    items[j].push({
                        text: v,
                        height: "100%",
                        cls: "table-color" + index%5
                    });
                } else {
                    items.push([{
                        text: v,
                        height: "100%",
                        cls: "table-color" + index%5
                    }]);
                }
            });
        });
        return {
            header: [header],
            items: items
        }
    },

    _createResultTableItems: function(data){
        var fields = data.fields, values = data.value;
        var header = [], items = [];
        var joinNames = this.model.getJoinNames();
        BI.each(fields, function(i, field){
            header.push({
                text: field,
                height: "100%"
            });
        });
        BI.each(values, function(i, value){
            BI.each(value, function(j, v){
                if(BI.isNotNull(items[j])){
                    items[j].push({
                        text: v,
                        height: "100%",
                        cls: joinNames[i].isLeft === true ? "table-color0" : "table-color1"
                    });
                } else {
                    items.push([{
                        text: v,
                        height: "100%",
                        cls: joinNames[i].isLeft === true ? "table-color0" : "table-color1"
                    }]);
                }
            });
        });
        return {
            header: [header],
            items: items
        }
    },

    /**
     * 刷新保存按钮状态
     * @private
     */
    _checkSaveButtonStatus: function(){
        var self = this;
        var joinFields = self.model.getJoinFields();
        if(joinFields.length === 0){
            this.saveButton.setEnable(false);
            this.saveButton.setWarningTitle(BI.i18nText("BI-ETL_Join_Merge_Field_Not_Set"));
        } else {
            var isValid = self.model.checkMergeFields();
            if(isValid === true){
                this.saveButton.setEnable(true);
            } else {
                this.saveButton.setEnable(false);
                this.saveButton.setWarningTitle(BI.i18nText("BI-ETL_Join_Wrong_Merge_Field"));
            }
        }
    },

    /**
     * 删除某个合并依据
     * @param index
     * @private
     */
    _onRemoveOneJoinField: function(index){
        var self = this;
        var joinFields = self.model.getJoinFields();
        joinFields.splice(index, 1);
        self.model.setJoinFields(joinFields);
        self._refreshJoinTableAndResult();
    },

    /**
     * 修改合并依据
     * @param row
     * @param col
     * @param nValue
     * @private
     */
    _onJoinComboChanged: function(row, col, nValue){
        var self = this;
        var arr = self.model.getJoinFields();
        //先找到当前列中如果已包含nValue那么置为-1
        BI.each(arr, function(i, fields){
            fields[col] === nValue && (arr[i][col] = -1);
        });
        arr[row][col] = nValue;
        self.model.setJoinFields(arr);
        self._refreshJoinTableAndResult();
    },

    /**
     * 重新选择表
     * @private
     */
    _reselectJoinTable: function(){
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_one_table_pane",
            element: BI.Layers.create(BICst.SELECT_ONE_TABLE_LAYER, BICst.BODY_ELEMENT),
            etl: this.model.getAllETLTables(),
            currentId: this.model.getId(),
            translations: this.model.getTranslations()
        });
        BI.Layers.show(BICst.SELECT_ONE_TABLE_LAYER);
        selectTablePane.on(BI.SelectOneTablePane.EVENT_CHANGE, function(tables){
            self.model.setJoinTables(tables);
            self._refreshJoinTableAndResult();
        });
    },

    _refreshJoinTableAndResult: function(){
        this._populateMergeFieldArea();
        this._populateReviewResult();
        this._checkSaveButtonStatus();
    },

    populate: function(){
        var self = this;
        this.model.initData(function(){
             self._refreshJoinTableAndResult();
        });
    }
});
BI.Join.EVENT_CANCEL = "EVENT_CANCEL";
BI.Join.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.join", BI.Join);
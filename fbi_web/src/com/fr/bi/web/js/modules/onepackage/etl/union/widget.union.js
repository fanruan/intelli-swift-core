/**
 * Created by Young's on 2016/3/11.
 */
BI.Union = BI.inherit(BI.Widget, {

    constants: {
        UNION_NORTH_HEIGHT: 50,
        UNION_SOUTH_HEIGHT: 60,
        UNION_WEST_WIDTH: 460,
        UNION_BUTTON_GAP: 20,
        UNION_BUTTON_HEIGHT: 28,
        UNION_EDITOR_HEIGHT: 26,
        UNION_EDITOR_WIDTH: 220,
        UNION_EDITOR_GAP: 5,
        UNION_GAP_TEN: 10,
        UNION_GAP_TWENTY: 20,
        PREVIEW_BUTTON_HEIGHT: 30,
        PREVIEW_BUTTON_WIDTH: 110,
        PREVIEW_AREA_HEIGHT: 260,
        PREVIEW_TABLE_HEIGHT: 160,

        SHOW_HEADER: 1,
        SHOW_TIP: 2,
        SHOW_TABLE: 3,
        SHOW_BUTTON: 4
    },

    _defaultConfig: function(){
        return BI.extend(BI.Union.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-union"
        })
    },

    _init: function(){
        BI.Union.superclass._init.apply(this, arguments);
        var o = this.options;
        this.model = new BI.UnionModel({
            info: o.info
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                north: {
                    el: {
                        type: "bi.left",
                        cls: "union-north",
                        items: [{
                            type: "bi.label",
                            cls: "union-north-label",
                            text: "Union" + BI.i18nText("BI-Management"),
                            height: this.constants.UNION_NORTH_HEIGHT
                        }],
                        hgap: this.constants.UNION_GAP_TWENTY
                    },
                    height: this.constants.UNION_NORTH_HEIGHT
                },
                south: {
                    el: this._buildSouth(),
                    height: this.constants.UNION_SOUTH_HEIGHT
                },
                west: {
                    el: this._buildWest(),
                    width: this.constants.UNION_WEST_WIDTH
                },
                center: this._buildCenter()
            }
        });
    },

    _buildWest: function(){
        var self = this;
        //重新选择表按钮
        this.changeUnionTable = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Modify_Used_Tables"),
            height: this.constants.UNION_EDITOR_HEIGHT
        });
        this.changeUnionTable.on(BI.Button.EVENT_CHANGE, function(){
            self._reselectUnionTable();
        });
        //合并依据表格
        this.model.isReopen() === true && this.changeUnionTable.setVisible(false);
        var addMergeButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_The_Merged_Basis"),
            height: this.constants.UNION_BUTTON_HEIGHT
        });
        addMergeButton.on(BI.Button.EVENT_CHANGE, function(){
            var mergeFields = self.model.getMergeFields();
            var emptyArray = [];
            BI.each(self.model.getAllTables(), function(i, m){
                emptyArray.push(BI.TableAddUnion.UNION_FIELD_NULL);
            });
            mergeFields.push(emptyArray);
            self.model.setMergeFields(mergeFields);
            self._refreshUnionTableAndResult();
        });
        this.unionTableFields = BI.createWidget({
            type: "bi.table_add_union",
            tables: self.model.getAllTables()
        });
        this.unionTableFields.on(BI.TableAddUnion.EVENT_REMOVE_UNION, function(index){
            self._onRemoveOneUnionField(index);
        });
        this.unionTableFields.on(BI.TableAddUnion.EVENT_CHANGE, function(row, col, nValue, oValue){
            self._onUnionComboChanged(row, col, nValue);
        });
        this.mergeArea = BI.createWidget({
            type: "bi.vtape",
            cls: "union-merge-field",
            items: [{
                el: {
                    type: "bi.left_right_vertical_adapt",
                    cls: "add-merge-field",
                    items: {
                        left: [{
                            type: "bi.label",
                            cls: "merge-field-text",
                            text: BI.i18nText("BI-Merge_By_Following_Fields")
                        }],
                        right: [addMergeButton]
                    }
                },
                height: this.constants.UNION_NORTH_HEIGHT
            }, {
                el: this.unionTableFields,
                height: "fill"
            }],
            vgap: this.constants.UNION_GAP_TEN,
            hgap: this.constants.UNION_GAP_TEN
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.left",
                    cls: "union-table-name",
                    items: [{
                        type:"bi.label",
                        cls: "table-name-text",
                        textAlign:"left",
                        textHeight:this.constants.UNION_EDITOR_HEIGHT,
                        text:BI.i18nText("BI-Table_Name") + "：",
                        lgap: 5
                    }, {
                        type:"bi.label",
                        value: this.model.getDefaultTableName(),
                        height: this.constants.UNION_EDITOR_HEIGHT,
                        width: this.constants.UNION_EDITOR_WIDTH,
                        textAlign: "left",
                        hgap: 10
                    }, this.changeUnionTable],
                    hgap: this.constants.UNION_EDITOR_GAP,
                    vgap: this.constants.UNION_GAP_TEN
                },
                left: this.constants.UNION_GAP_TWENTY,
                right: this.constants.UNION_GAP_TWENTY,
                top: 0
            }, {
                el: this.mergeArea,
                left: this.constants.UNION_GAP_TWENTY,
                right: this.constants.UNION_GAP_TWENTY,
                top: this.constants.UNION_SOUTH_HEIGHT,
                bottom: 0
            }]
        })
    },

    _buildSouth:function(){
        var self = this;
        var cancel = BI.createWidget({
            type:"bi.button",
            level:"ignore",
            text:BI.i18nText("BI-Cancel"),
            height: this.constants.UNION_BUTTON_HEIGHT
        });
        cancel.on(BI.Button.EVENT_CHANGE,function(){
            self.fireEvent(BI.Union.EVENT_CANCEL);
        });

        this.saveButton = BI.createWidget({
            type:"bi.button",
            text:BI.i18nText("BI-Save"),
            title: BI.i18nText("BI-Save"),
            height: this.constants.UNION_BUTTON_HEIGHT
        });
        this.saveButton.on(BI.Button.EVENT_CHANGE,function(){
            var data = {
                connection_name: BICst.CONNECTION.ETL_CONNECTION,
                etl_type: "union",
                etl_value: {
                    union_array: self.model.getUnionArray()
                },
                tables: self.model.getAllTables()
            };
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: self.element,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.getTablesDetailInfoByTables([data], function (sourceTables) {
                mask.destroy();
                var table = sourceTables[0];
                if(BI.isNotNull(table)) {
                    data.fields = table.fields;
                }
                self.fireEvent(BI.Union.EVENT_SAVE, data);
            });
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            height: this.constants.UNION_SOUTH_HEIGHT,
            items: {
                left: [cancel],
                right: [this.saveButton]
            },
            lhgap: this.constants.UNION_BUTTON_GAP,
            rhgap: this.constants.UNION_BUTTON_GAP
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
                    cls: "union-preview-result",
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
                                height: this.constants.UNION_BUTTON_HEIGHT
                            }, {
                                el: this.previewTab,
                                height: "fill"
                            }],
                            hgap: this.constants.UNION_GAP_TEN,
                            vgap: this.constants.UNION_GAP_TEN
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
                                height: this.constants.UNION_BUTTON_HEIGHT
                            }, {
                                el: this.resultTab,
                                height: "fill"
                            }],
                            hgap: this.constants.UNION_GAP_TEN,
                            vgap: this.constants.UNION_GAP_TEN
                        },
                        height: "fill"
                    }]
                },
                left: 0,
                right: this.constants.UNION_GAP_TWENTY,
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
                        cls: "union-result-tip",
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
                    var tables = self.model.getUnionTables();
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
                            hgap: self.constants.UNION_GAP_TEN
                        });
                        BI.Utils.getPreviewDataByTableAndFields(table, [], function(data){
                            count--;
                            var oTable = BI.createWidget({
                                type: "bi.union_preview_table",
                                data: data,
                                index: i,
                                height: self.constants.PREVIEW_TABLE_HEIGHT
                            });
                            wrapper.addItems([{
                                type: "bi.label",
                                text: BI.isNotNull(table.table_name) ? table.table_name : self.model.getETLTableNameByTable(table),
                                cls: "original-table-name",
                                height: self.constants.PREVIEW_BUTTON_HEIGHT
                            }, oTable]);
                        });
                        self.originalTablesArea.addItem(wrapper);
                    });
                    self.resultTab.setSelect(self.constants.SHOW_TABLE);
                    BI.Utils.getPreviewDataByTableAndFields(self.model.getTableInfo(), [], function(data){
                        count--;
                        self.resultTable.empty();
                        var rTable = BI.createWidget({
                            type: "bi.union_preview_table",
                            union_array: self.model.getUnionArray(),
                            data: data
                        });
                        rTable.on(BI.UnionPreviewTable.EVENT_RENAME, function(namesArray){
                            self.model.setUnionArray(namesArray);
                        });
                        self.resultTable.addItem({
                            el: rTable,
                            top: 0,
                            left: 0,
                            right: 0,
                            bottom: 0
                        })
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
                this.unionResultHeader = BI.createWidget({
                    type: "bi.union_result_header",
                    mergeResult: this.model.getUnionArray()
                });
                this.unionResultHeader.on(BI.UnionResultHeader.EVENT_CHANGE, function(unionArray){
                    self.model.setUnionArray(unionArray);
                });
                return this.unionResultHeader;
            case this.constants.SHOW_TIP:
                return BI.createWidget({
                    type: "bi.label",
                    text: BI.i18nText("BI-Please_Finish_Merge_Field_Correct"),
                    cls: "union-result-tip",
                    height: 50
                });
            case this.constants.SHOW_TABLE:
                return this.resultTable = BI.createWidget({
                    type: "bi.absolute"
                });
        }
    },

    /**
     * 刷新合并字段表格
     * @private
     */
    _populateMergeFieldArea: function(){
        this.unionTableFields.populate(this.model.getAllTables(), this.model.getMergeFields());
    },

    _createTableItems: function(data, index){
        var fields = data.fields, values = data.value, fieldTypes = data.type, self = this;
        var header = [], items = [];
        BI.each(fields, function(i, field){
            header.push({
                text: field,
                height: "100%"
            });
        });
        
        BI.each(values, function(i, value){
            var isDate = fieldTypes[i] === BICst.COLUMN.DATE;
            BI.each(value, function(j, v){
                if(BI.isNotNull(items[j])){
                    items[j].push({
                        text: isDate === true ? self._formatDate(v) : v,
                        height: "100%",
                        cls: "table-color" + index%5
                    });
                } else {
                    items.push([{
                        text: isDate === true ? self._formatDate(v) : v,
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

    /**
     * 刷新预览区域
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
            this.unionResultHeader.populate(this.model.getUnionArray());
        } else {
            this.resultTab.setSelect(this.constants.SHOW_TIP);
        }
    },

    /**
     * 删除一个合并依据
     * @param index
     * @private
     */
    _onRemoveOneUnionField: function(index){
        var mergeFields = this.model.getMergeFields();
        mergeFields.splice(index, 1);
        this.model.setMergeFields(mergeFields);
        this._refreshUnionTableAndResult();
    },

    /**
     * 修改合并依据
     * @param row
     * @param col
     * @param nValue
     * @private
     */
    _onUnionComboChanged: function(row, col, nValue){
        var self = this;
        var arr = this.model.getMergeFields();
        //先找到当前列中如果已包含nValue那么置为-1
        BI.each(arr, function(i, fields){
            fields[col] === nValue && (arr[i][col] = -1);
        });
        arr[row][col] = nValue;
        this.model.setMergeFields(arr)
        this._refreshUnionTableAndResult();
    },

    /**
     * 重新选择表
     * @private
     */
    _reselectUnionTable: function(){
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_table_pane",
            element: BI.Layers.create(BICst.SELECT_TABLES_LAYER, BICst.BODY_ELEMENT),
            tables: this.model.getUnionTables(),
            etl: this.model.getAllETLTables(),
            currentId: this.model.getId(),
            translations: this.model.getTranslations()
        });
        BI.Layers.show(BICst.SELECT_TABLES_LAYER);
        selectTablePane.on(BI.SelectTablePane.EVENT_NEXT_STEP, function(tables){
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
            self.model.setUnionTables(tables);
            self._refreshUnionTableAndResult();
        });
        selectTablePane.on(BI.SelectTablePane.EVENT_CANCEL, function(){
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
        })
    },

    /**
     * 刷新保存按钮状态
     * @private
     */
    _checkSaveButtonStatus: function(){
        var isValid = this.model.checkMergeFields();
        if(isValid === true){
            this.saveButton.setEnable(true);
        } else {
            this.saveButton.setEnable(false);
            this.saveButton.setWarningTitle(BI.i18nText("BI-ETL_Join_Wrong_Merge_Field"));
        }
    },

    _formatDate: function (d) {
        if (BI.isNull(d) || !BI.isNumeric(d)) {
            return d || "";
        }
        var date = new Date(BI.parseInt(d));
        return date.print("%Y/%X/%d %H:%M:%S")
    },

    _refreshUnionTableAndResult: function(){
        this._populateMergeFieldArea();
        this._populateReviewResult();
        this._checkSaveButtonStatus();
    },

    populate: function(){
        var self = this;
        this.model.initData(function(){
            self._refreshUnionTableAndResult();
        });
    }
});
BI.Union.EVENT_SAVE = "EVENT_SAVE";
BI.Union.EVENT_CANCEL = "EVENT_CANCEL";
$.shortcut("bi.union", BI.Union);

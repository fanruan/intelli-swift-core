/**
 * Created by Young's on 2016/3/10.
 * etl
 */
BI.ETL = BI.inherit(BI.Widget, {

    constants: {
        ETL_OPERATOR_LAYER: "__etl_operator_layer__",
        EXCEL_VIEW_LAYER: "__excel_view_layer__",
        ETL_PANE_NORTH_HEIGHT: 40,
        ETL_PANE_SOUTH_HEIGHT: 60,
        ETL_PANE_WEST_WIDTH: 580,
        ETL_PANE_BUTTON_HEIGHT: 28,
        ETL_PANE_BUTTON_GAP: 20,
        ETL_TABLE_NAME_WIDTH: 50,
        ETL_TEXT_EDITOR_WIDTH: 300,
        ETL_TEXT_EDITOR_HEIGHT: 25,
        ETL_PANE_GAP: 20,
        ETL_DATA_SET_PANE_GAP: 10,
        ETL_EMPTY_TYPE_HEIGHT: 200
    },

    _defaultConfig: function () {
        return BI.extend(BI.ETL.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl"
        })
    },

    _init: function () {
        BI.ETL.superclass._init.apply(this, arguments);
        var o = this.options;
        this.model = new BI.ETLModel({
            id: o.id,
            table_data: o.table_data,
            relations: o.relations,
            translations: o.translations,
            all_fields: o.all_fields,
            used_fields: o.used_fields,
            excel_view: o.excel_view,
            update_settings: o.update_settings
        });
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            cls: "bi-conf-etl",
            items: {
                west: {
                    el: this._buildWest(),
                    width: this.constants.ETL_PANE_WEST_WIDTH
                },
                center: this._buildCenter(),
                south: {
                    el: this._buildSouth(),
                    height: this.constants.ETL_PANE_SOUTH_HEIGHT
                }
            }
        });
        this._populate();
    },

    _buildWest: function () {
        var self = this;
        this.dataSetTab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: BI.createWidget(),
            cardCreator: BI.bind(this._dataSetTabCreator, this)
        });
        this.dataSetTab.setSelect(BICst.CONF_ETL_DATA_SET_EMPTY_TIP);
        this.tablePreview = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Table_Preview"),
            height: this.constants.ETL_PANE_BUTTON_HEIGHT
        });
        this.tablePreview.on(BI.Button.EVENT_CHANGE, function () {
            var outerTable = self.model.getAllTables()[0];
            self._previewData(outerTable[0].id);
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "etl-data-set-pane",
                    items: [{
                        el: {
                            type: "bi.left_right_vertical_adapt",
                            cls: "data-set-pane-north",
                            items: {
                                left: [{
                                    type: "bi.label",
                                    text: BI.i18nText("BI-Table_Data_Setting"),
                                    height: this.constants.ETL_PANE_NORTH_HEIGHT
                                }],
                                right: [this.tablePreview]
                            }
                        },
                        height: this.constants.ETL_PANE_NORTH_HEIGHT
                    }, {
                        el: this.dataSetTab,
                        height: "fill"
                    }],
                    hgap: this.constants.ETL_DATA_SET_PANE_GAP
                },
                top: this.constants.ETL_PANE_GAP,
                left: this.constants.ETL_PANE_GAP,
                right: this.constants.ETL_PANE_GAP,
                bottom: 0
            }]
        })
    },

    _dataSetTabCreator: function (v) {
        switch (v) {
            case BICst.CONF_ETL_DATA_SET_EMPTY_TIP:
                return BI.createWidget({
                    type: "bi.label",
                    cls: "empty-tip",
                    text: BI.i18nText("BI-Etl_Table_On_The_Right_Cannot_Be_Null"),
                    height: this.constants.ETL_EMPTY_TYPE_HEIGHT
                });
            case BICst.CONF_ETL_DATA_SET_ONLY_ONE_TIP:
                return BI.createWidget({
                    type: "bi.label",
                    cls: "empty-tip",
                    text: BI.i18nText("BI-Final_Table_Only_Be_One"),
                    height: this.constants.ETL_EMPTY_TYPE_HEIGHT
                });
            case BICst.CONF_ETL_DATA_SET_PANE:
                this.dataSetPane = BI.createWidget({
                    type: "bi.vtape",
                    cls: "data-set-main",
                    vgap: this.constants.ETL_DATA_SET_PANE_GAP
                });
                return this.dataSetPane;
        }
    },

    _buildCenter: function () {
        var self = this;
        this.etlSetTab = BI.createWidget({
            type: "bi.tab",
            direction: "custom",
            tab: BI.createWidget(),
            cardCreator: BI.bind(this._etlSetTabCreator, this)
        });
        this.etlSetTab.setSelect(BICst.CONF_ETL_SET_EMPTY_TIP);
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "etl-etl-set-pane",
                    items: [{
                        el: {
                            type: "bi.left_right_vertical_adapt",
                            cls: "etl-set-pane-north",
                            items: {
                                left: [{
                                    type: "bi.label",
                                    cls: "north-label",
                                    text: BI.i18nText("BI-Etl_Manage")
                                }],
                                right: [{
                                    type: "bi.button",
                                    text: "+ " + BI.i18nText("BI-Add_Table"),
                                    height: this.constants.ETL_PANE_BUTTON_HEIGHT,
                                    handler: function () {
                                        self._onClickSelectTable();
                                    }
                                }]
                            }
                        },
                        height: this.constants.ETL_PANE_NORTH_HEIGHT
                    }, {
                        el: this.etlSetTab,
                        height: "fill"
                    }],
                    hgap: this.constants.ETL_PANE_GAP
                },
                top: this.constants.ETL_PANE_GAP,
                left: 0,
                bottom: 0,
                right: this.constants.ETL_PANE_GAP
            }]
        })
    },

    _etlSetTabCreator: function (v) {
        switch (v) {
            case BICst.CONF_ETL_SET_EMPTY_TIP:
                return BI.createWidget({
                    type: "bi.label",
                    cls: "empty-tip",
                    text: BI.i18nText("BI-Click_Add_Table_To_Add_Tables"),
                    height: this.constants.ETL_EMPTY_TYPE_HEIGHT
                });
            case BICst.CONF_ETL_SET_PANE:
                this.etlPane = BI.createWidget({
                    type: "bi.vertical",
                    cls: "bi-etl-setting-pane"
                });
                return this.etlPane;
        }
    },

    _buildSouth: function () {
        var self = this;
        this.updateSetButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Update_Setup"),
            height: this.constants.ETL_PANE_BUTTON_HEIGHT
        });
        this.updateSetButton.on(BI.Button.EVENT_CHANGE, function(){
            self._onClickUpdateSet();
        });
        this.excelViewButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Display_Data_In_Actual"),
            height: this.constants.ETL_PANE_BUTTON_HEIGHT
        });
        this.excelViewButton.on(BI.Button.EVENT_CHANGE, function(){
            self._onClickExcelView();
        });
        this.saveButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            text: BI.i18nText("BI-Save"),
            title: BI.i18nText("BI-Save"),
            height: this.constants.ETL_PANE_BUTTON_HEIGHT,
            handler: function () {
                self.fireEvent(BI.ETL.EVENT_SAVE, self.model.getValue());
            }
        });
        var removeButton = BI.createWidget({
            type: "bi.text_button",
            text: BI.i18nText("BI-Shift_Out_Package"),
            cls: "etl-remove-table",
            handler: function () {
                //TODO 判断该表是否被使用
                BI.Msg.confirm(BI.i18nText("BI-Is_Delete_Table"), BI.i18nText("BI-Is_Delete_Table"), function (flag) {
                    if (flag === true) {
                        self.fireEvent(BI.ETL.EVENT_REMOVE);
                    }
                });
            }
        });
        if (this.model.isNewTable() === true) {
            removeButton.setVisible(false);
        }
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "bi-conf-etl-south",
            items: {
                left: [{
                    type: "bi.button",
                    level: "ignore",
                    text: BI.i18nText("BI-Cancel"),
                    height: this.constants.ETL_PANE_BUTTON_HEIGHT,
                    handler: function () {
                        self.fireEvent(BI.ETL.EVENT_CANCEL);
                    }
                }, removeButton],
                right: [
                    this.updateSetButton,
                    this.excelViewButton,
                    this.saveButton
                ]
            },
            lhgap: this.constants.ETL_PANE_BUTTON_GAP,
            rhgap: this.constants.ETL_PANE_BUTTON_GAP
        })
    },

    _onClickUpdateSet: function(){
        var self = this;
        BI.Popovers.remove(this.model.getId());
        var updateSet = BI.createWidget({
            type: "bi.update_table_data",
        });
        updateSet.on(BI.UpdateTableData.EVENT_SAVE, function(){
            self.model.setUpdateSettings(this.getValue());
        });
        BI.Popovers.create(this.model.getId(), updateSet).open(this.model.getId());
    },

    _onClickExcelView: function(){
        var self = this;
        var excelViewPane = BI.createWidget({
            type: "bi.excel_view_setting",
            element: BI.Layers.create(this.constants.EXCEL_VIEW_LAYER, BICst.BODY_ELEMENT),
            table: this.model.getValue(),
            view: this.model.getExcelView()
        });
        excelViewPane.on(BI.ExcelViewSetting.EVENT_CANCEL, function(){
            BI.Layers.remove(self.constants.EXCEL_VIEW_LAYER);
        });
        excelViewPane.on(BI.ExcelViewSetting.EVENT_SAVE, function(view){
            BI.Layers.remove(self.constants.EXCEL_VIEW_LAYER);
            self.model.setExcelView(view);
        });
        BI.Layers.show(this.constants.EXCEL_VIEW_LAYER);
    },

    /**
     * 添加表
     * @private
     */
    _onClickSelectTable: function () {
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_table_pane",
            element: BI.Layers.create(BICst.SELECT_TABLES_LAYER, BICst.BODY_ELEMENT),
            etl: this.model.getAllTables(),
            translations: this.model.getTranslations()
        });
        BI.Layers.show(BICst.SELECT_TABLES_LAYER);
        selectTablePane.on(BI.SelectTablePane.EVENT_NEXT_STEP, function (tables) {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
            self.model.addNewTables(tables);
            self._populateAfterETLOperator();
        });
        selectTablePane.on(BI.SelectTablePane.EVENT_CANCEL, function () {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
        })
    },

    _populate: function () {
        this._buildDataSetPane();
        this._buildETLSetPane();
        this._changeButtonsStatus();
    },

    _populateAfterETLOperator: function () {
        var self = this;
        this.model.setFields([]);
        var allTables = this.model.getAllTables();
        if (allTables.length === 1) {
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: BICst.BODY_ELEMENT,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.getTablesDetailInfoByTables([BI.extend(allTables[0][0], {id: this.model.getId()})], function (data) {
                self.model.setFields(data[0].fields);
                self._populate();
                mask.destroy();
            });
        }
        self._populate();
    },

    /**
     * 表数据设置界面
     * @private
     */
    _buildDataSetPane: function () {
        var self = this;
        var allTables = this.model.getAllTables();
        if (allTables.length === 0) {
            this.dataSetTab.setSelect(BICst.CONF_ETL_DATA_SET_EMPTY_TIP);
            return;
        } else if (allTables.length > 1) {
            this.dataSetTab.setSelect(BICst.CONF_ETL_DATA_SET_ONLY_ONE_TIP);
            return;
        }
        this.dataSetTab.setSelect(BICst.CONF_ETL_DATA_SET_PANE);
        this.dataSetPane.empty();
        var tableName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "table-name-editor",
            width: this.constants.ETL_TEXT_EDITOR_WIDTH,
            height: this.constants.ETL_TEXT_EDITOR_HEIGHT,
            allowBlank: false,
            errorText: BI.i18nText("BI-Table_Name_Not_Repeat"),
            validationChecker: function (v) {
                return self.model.isValidTableTranName(v);
            }
        });
        tableName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.model.setTranName(tableName.getValue());
        });
        tableName.setValue(this.model.getTranName());

        this.tableNameWrapper = BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: {
                    type: "bi.label",
                    textAlign: "left",
                    cls: "table-name-label",
                    width: this.constants.ETL_TABLE_NAME_WIDTH,
                    text: BI.i18nText("BI-Table_Name_Upper")
                },
                left: 0,
                top: 5
            }, {
                el: tableName,
                left: this.constants.ETL_TABLE_NAME_WIDTH,
                top: 0
            }]
        });
        var tableInfo = BI.createWidget({
            type: "bi.table_field_info_with_search_pane",
            tableInfo: {
                id: this.model.getId(),
                table_name: this.model.getTranName(),
                fields: this.model.getFields(),
                relations: this.model.getRelations(),
                translations: this.model.getTranslations(),
                all_fields: this.model.getAllFields(),
                usedFields: this.model.getUsedFields()
            }
        });
        tableInfo.on(BI.TableFieldWithSearchPane.EVENT_RELATION_CHANGE, function (fieldId) {
            BI.Popovers.remove(fieldId);
            var relationPane = BI.createWidget({
                type: "bi.relation_set_pane",
                field_id: fieldId,
                relations: self.model.getRelations(),
                translations: self.model.getTranslations(),
                all_fields: self.model.getAllFields()
            });
            relationPane.on(BI.RelationSetPane.EVENT_CHANGE, function (relations) {
                self.model.setRelations(relations);
                self._populate();
            });
            BI.Popovers.create(fieldId, relationPane).open(fieldId);
        });
        tableInfo.on(BI.TableFieldWithSearchPane.EVENT_TRANSLATION_CHANGE, function (translations) {
            self.model.setTranslations(translations);
        });
        tableInfo.on(BI.TableFieldWithSearchPane.EVENT_USABLE_CHANGE, function (usedFields) {
            self.model.setUsedFields(usedFields);
        });
        this.dataSetPane.populate([{
            el: this.tableNameWrapper,
            height: this.constants.ETL_TEXT_EDITOR_HEIGHT
        }, {
            el: tableInfo,
            height: "fill"
        }]);
    },

    /**
     * etl设置界面（流程图）
     * @private
     */
    _buildETLSetPane: function () {
        var self = this;
        var allTables = self.model.getAllTables();
        if (allTables.length === 0) {
            this.etlSetTab.setSelect(BICst.CONF_ETL_SET_EMPTY_TIP);
            return;
        }
        this.etlSetTab.setSelect(BICst.CONF_ETL_SET_PANE);
        this.etlPane.empty();
        var items = [];
        BI.each(self.model.getAllTables(), function (i, table) {
            items.push(self._addOneTable2ETLPane(table));
        });
        var wrapper = BI.createWidget({
            type: "bi.horizontal_float",
            scrollable: true,
            width: "100%",
            height: "100%",
            items: [{
                type: "bi.horizontal",
                items: items
            }]
        });
        this.etlPane.addItem(wrapper);
    },

    /**
     * 向右侧流程图界面添加一个表
     * @param table
     * @returns {*}
     * @private
     */
    _addOneTable2ETLPane: function (table) {
        var self = this;
        var etlTablesPane = BI.createWidget({
            type: "bi.etl_tables_pane",
            tables: table
        });
        etlTablesPane.on(BI.ETLTablesPane.EVENT_CLICK_OPERATOR, function (table, isFinal) {
            if (isFinal === false) {
                BI.Msg.confirm(BI.i18nText("BI-Attention"), BI.i18nText("BI-Modify_ETL_Operator_Comment"), function (v) {
                    v === true && self._reopenETLOperator(table);
                });
                return;
            }
            self._reopenETLOperator(table);
        });
        etlTablesPane.on(BI.ETLTablesPane.EVENT_CHANGE, function (tId, etlType, isFinal) {
            if (isFinal === false && etlType !== BICst.ETL_MANAGE_TABLE_PREVIEW) {
                BI.Msg.confirm(BI.i18nText("BI-Attention"), BI.i18nText("BI-Add_ETL_Operator_Comment"), function (v) {
                    v === true && self._etlOperator(etlType, tId);
                });
                return;
            }
            self._etlOperator(etlType, tId);
        });
        return etlTablesPane;
    },

    _etlOperator: function (etlType, tId) {
        switch (etlType) {
            case BICst.ETL_MANAGE_TABLE_PREVIEW:
                this._previewData(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_ADD_FIELD:
                this._addFormulaField(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_JOIN:
                this._joinWithOther(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_UNION:
                this._unionWithOther(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_CONVERT:
                this._convertRowAndColumn(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_PARTIAL:
                this._selectPartFields(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_FILTER:
                this._filterField(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_GROUP:
                this._groupAndStatistic(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_CIRCLE:
                this._constructCircle(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_NEW_GROUP:
                this._addGroupField(tId);
                break;
            case BICst.ETL_MANAGE_TABLE_DELETE:
                this.model.removeOneTable(tId);
                this._populateAfterETLOperator();
                break;
        }
    },

    /**
     * 重新进入etl操作
     * @param table
     * @private
     */
    _reopenETLOperator: function (table) {
        var operator = table.etl_type;
        switch (operator) {
            case BICst.ETL_OPERATOR.JOIN:
                this._reopenJoin(table);
                break;
            case BICst.ETL_OPERATOR.UNION:
                this._reopenUnion(table);
                break;
            case BICst.ETL_OPERATOR.CIRCLE:
                this._reopenCircle(table);
                break;
            case BICst.ETL_OPERATOR.CONVERT:
                this._reopenConvert(table);
                break;
            case BICst.ETL_OPERATOR.FILTER:
                this._reopenFilter(table);
                break;
            case BICst.ETL_OPERATOR.FORMULA:
                this._reopenFormulaField(table);
                break;
            case BICst.ETL_OPERATOR.GROUP:
                this._reopenGroup(table);
                break;
            case BICst.ETL_OPERATOR.PARTIAL:
                this._reopenPartial(table);
                break;
            case BICst.ETL_OPERATOR.NEW_GROUP:
                this._reopenGroupField(table);
                break;
        }
    },

    _reopenFilter: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var filter = BI.createWidget({
                type: "bi.filter_data",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table
                }
            });
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            filter.on(BI.FilterData.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            filter.on(BI.FilterData.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        })
    },

    _reopenConvert: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var convert = BI.createWidget({
                type: "bi.convert",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    id: self.model.getId(),
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table,
                    translations: self.model.getTranslations()
                }
            });
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            convert.on(BI.Convert.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            convert.on(BI.Convert.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        })
    },

    _reopenPartial: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var part = BI.createWidget({
                type: "bi.part_field",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table
                }
            });
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            part.on(BI.PartField.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            part.on(BI.PartField.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        });
    },

    _reopenGroup: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var group = BI.createWidget({
                type: "bi.group_statistic",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table
                }
            });
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            group.on(BI.GroupStatistic.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            group.on(BI.GroupStatistic.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        });
    },

    _reopenJoin: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var join = BI.createWidget({
                type: "bi.join",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    id: tableId,
                    reopen: true,
                    isGenerated: status.isGenerated,
                    joinTables: table.tables,
                    tableInfo: table,
                    allETLTables: self.model.getAllTables()
                },
                translations: self.model.getTranslations()
            });
            join.populate();
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            join.on(BI.Join.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            join.on(BI.Join.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        })
    },

    _reopenUnion: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var union = BI.createWidget({
                type: "bi.union",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    id: tableId,
                    reopen: true,
                    isGenerated: status.isGenerated,
                    unionTables: table.tables,
                    tableInfo: table,
                    allETLTables: self.model.getAllTables()
                },
                translations: self.model.getTranslations()
            });
            union.populate();
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            union.on(BI.Union.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            union.on(BI.Union.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        })
    },

    _reopenCircle: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var circle = BI.createWidget({
                type: "bi.circle",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    id: self.model.getId(),
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table,
                    relations: self.model.getRelations()
                }
            });
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
            circle.on(BI.Circle.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            circle.on(BI.Circle.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
        });
    },

    _reopenGroupField: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var addGroupField = BI.createWidget({
                type: "bi.add_group_field",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    id: tableId,
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table
                }
            });
            addGroupField.on(BI.AddGroupField.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });

            addGroupField.on(BI.AddGroupField.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            addGroupField.populate();
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        })
    },

    _reopenFormulaField: function (table) {
        var self = this;
        var tableId = table.id;
        this._checkCubeStatus(table, function (status) {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            var addFormulaField = BI.createWidget({
                type: "bi.add_formula_field",
                element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                info: {
                    id: tableId,
                    reopen: true,
                    isGenerated: status.isGenerated,
                    tableInfo: table
                }
            });

            addFormulaField.on(BI.AddFormulaField.EVENT_SAVE, function (data) {
                self.model.saveTableById(tableId, data);
                self._populateAfterETLOperator();
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });

            addFormulaField.on(BI.AddFormulaField.EVENT_CANCEL, function () {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
            });
            addFormulaField.populate();
            BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        })
    },

    _checkCubeStatus: function (table, callback) {
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.checkCubeStatusByTable(table, function (status) {
            mask.destroy();
            callback(status);
        })
    },

    _previewData: function (tId) {
        BI.Popovers.remove(tId);
        var tablePreview = BI.createWidget({
            type: "bi.etl_table_preview",
            table: this.model.getTableById(tId)
        });
        BI.Popovers.create(tId, tablePreview).open(tId);
    },

    _addFormulaField: function (tId) {
        var self = this;
        var addFormulaField = BI.createWidget({
            type: "bi.add_formula_field",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId)
            }
        });
        addFormulaField.on(BI.AddFormulaField.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });

        addFormulaField.on(BI.AddFormulaField.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        addFormulaField.populate();
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
    },

    _addGroupField: function (tId) {
        var self = this;
        var addGroupField = BI.createWidget({
            type: "bi.add_group_field",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId)
            }
        });
        addGroupField.on(BI.AddGroupField.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });

        addGroupField.on(BI.AddGroupField.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        addGroupField.populate();
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);

    },

    _joinWithOther: function (tId) {
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_one_table_pane",
            element: BI.Layers.create(BICst.SELECT_ONE_TABLE_LAYER, BICst.BODY_ELEMENT),
            etl: this.model.getAllTables(),
            currentId: tId,
            translations: this.model.getTranslations()
        });
        BI.Layers.show(BICst.SELECT_ONE_TABLE_LAYER);
        selectTablePane.on(BI.SelectOneTablePane.EVENT_CHANGE, function (tables) {
            if (BI.isNotEmptyArray(tables)) {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
                var join = BI.createWidget({
                    type: "bi.join",
                    element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER, BICst.BODY_ELEMENT),
                    info: {
                        id: tId,
                        reopen: false,
                        isGenerated: false,
                        joinTables: tables,
                        tableInfo: self.model.getTableById(tId),
                        allETLTables: self.model.getAllTables()
                    },
                    translations: self.model.getTranslations()
                });
                join.populate();
                BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
                join.on(BI.Join.EVENT_SAVE, function (data) {
                    self.model.saveTableById(tId, data);
                    self._populateAfterETLOperator();
                    BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
                });
                join.on(BI.Join.EVENT_CANCEL, function () {
                    BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
                });
            }
        });
    },

    _unionWithOther: function (tId) {
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_table_pane",
            element: BI.Layers.create(BICst.SELECT_TABLES_LAYER, BICst.BODY_ELEMENT),
            etl: this.model.getAllTables(),
            currentId: tId,
            translations: this.model.getTranslations()
        });
        BI.Layers.show(BICst.SELECT_TABLES_LAYER);
        selectTablePane.on(BI.SelectTablePane.EVENT_NEXT_STEP, function (tables) {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
            if (BI.isNotEmptyArray(tables)) {
                BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
                var union = BI.createWidget({
                    type: "bi.union",
                    element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
                    info: {
                        id: tId,
                        reopen: false,
                        isGenerated: false,
                        unionTables: tables,
                        tableInfo: self.model.getTableById(tId),
                        allETLTables: self.model.getAllTables()
                    },
                    translations: self.model.getTranslations()
                });
                union.populate();
                BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
                union.on(BI.Union.EVENT_SAVE, function (data) {
                    self.model.saveTableById(tId, data);
                    self._populateAfterETLOperator();
                    BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
                });
                union.on(BI.Union.EVENT_CANCEL, function () {
                    BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
                });
            }
        });
        selectTablePane.on(BI.SelectTablePane.EVENT_CANCEL, function () {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
        })
    },

    _groupAndStatistic: function (tId) {
        var self = this;
        BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        var statistic = BI.createWidget({
            type: "bi.group_statistic",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId)
            }
        });
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        statistic.on(BI.GroupStatistic.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        statistic.on(BI.GroupStatistic.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
    },

    _selectPartFields: function (tId) {
        var self = this;
        BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        var partfield = BI.createWidget({
            type: "bi.part_field",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId)
            }
        });
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        partfield.on(BI.PartField.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        partfield.on(BI.PartField.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
    },

    _filterField: function (tId) {
        var self = this;
        BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        var filter = BI.createWidget({
            type: "bi.filter_data",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId)
            }
        });
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        filter.on(BI.FilterData.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        filter.on(BI.FilterData.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
    },

    _convertRowAndColumn: function (tId) {
        var self = this;
        BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        var convert = BI.createWidget({
            type: "bi.convert",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                id: self.model.getId(),
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId),
                translations: self.model.getTranslations()
            }
        });
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        convert.on(BI.Convert.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        convert.on(BI.Convert.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
    },

    _constructCircle: function (tId) {
        var self = this;
        BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        var circle = BI.createWidget({
            type: "bi.circle",
            element: BI.Layers.create(self.constants.ETL_OPERATOR_LAYER),
            info: {
                id: self.model.getId(),
                reopen: false,
                isGenerated: false,
                tableInfo: self.model.getTableById(tId),
                relations: self.model.getRelations()
            }
        });
        BI.Layers.show(self.constants.ETL_OPERATOR_LAYER);
        circle.on(BI.Circle.EVENT_SAVE, function (data) {
            self.model.saveTableById(tId, data);
            self._populateAfterETLOperator();
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
        circle.on(BI.Circle.EVENT_CANCEL, function () {
            BI.Layers.remove(self.constants.ETL_OPERATOR_LAYER);
        });
    },

    /**
     * 页面按钮灰化
     * @private
     */
    _changeButtonsStatus: function () {
        var self = this;
        //表预览按钮
        var allTables = this.model.getAllTables();
        var isEnable = false, warningTitle = "";
        if (allTables.length === 1) {
            //如果不是etl表，也是可以预览的
            if (BI.isNotNull(allTables[0][0].etl_type)) {
                BI.Utils.checkCubeStatusByTable(allTables[0][0], function (data) {
                    if (data.isGenerated === true) {
                        self.tablePreview.setEnable(true);
                    } else {
                        self.tablePreview.setEnable(false);
                        self.tablePreview.setWarningTitle(BI.i18nText("BI-Operation_Need_Generated_Cube"))
                    }
                });
            } else {
                self.tablePreview.setEnable(true);
            }
            isEnable = true;
        } else {
            this.tablePreview.setEnable(false);
            warningTitle = allTables.length === 0 ? BI.i18nText("BI-Etl_Table_On_The_Right_Cannot_Be_Null") : BI.i18nText("BI-Final_Table_Only_Be_One");
        }
        this.updateSetButton.setEnable(isEnable);
        this.excelViewButton.setEnable(isEnable);
        this.saveButton.setEnable(isEnable);

        this.updateSetButton.setWarningTitle(warningTitle);
        this.excelViewButton.setWarningTitle(warningTitle);
        this.saveButton.setWarningTitle(warningTitle);
    },

    getValue: function () {
        return this.model.getValue();
    }
});
BI.ETL.EVENT_REMOVE = "EVENT_REMOVE";
// BI.ETL.EVENT_EVENT_CUBE_SAVE = "EVENT_CUBE_SAVE";
BI.ETL.EVENT_CANCEL = "EVENT_CANCEL";
BI.ETL.EVENT_SAVE = "EVENT_SAVE";
$.shortcut("bi.etl", BI.ETL);

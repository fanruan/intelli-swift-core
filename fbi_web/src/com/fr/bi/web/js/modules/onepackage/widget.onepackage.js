/**
 * created by young
 * 单个业务包
 */
BI.OnePackage = BI.inherit(BI.Widget, {

    _constant: {
        ETL_LAYER: "__etl_layer__",
        EXCEL_LAYER: "__excel_layer__",
        SQL_LAYER: "__sql_layer__",
        BUTTON_HEIGHT: 28,
        COMBO_WIDTH: 135,
        PACKAGE_NAME_WIDTH: 240,
        VIEW_WIDTH: 30,
        VIEW_HEIGHT: 30,
        BUTTON_GAP: 5,
        SEARCH_WIDTH: 230,
        TABLE_GAP: 10,
        EMPTY_TIP_HEIGHT: 40,
        LAYOUT_H_GAP: 20,
        NORTH_HEIGHT: 50,
        SOUTH_HEIGHT: 60
    },

    _defaultConfig: function () {
        return BI.extend(BI.OnePackage.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-business-package-pane"
        })
    },

    _init: function () {
        BI.OnePackage.superclass._init.apply(this, arguments);
        var o = this.options;
        this.model = new BI.OnePackageModel({
            id: o.id,
            gid: o.gid,
            name: o.name
        });
        BI.createWidget({
            type: "bi.border",
            cls: "bi-business-package-pane",
            element: this.element,
            items: {
                north: {
                    el: this._createNorth(),
                    height: this._constant.NORTH_HEIGHT,
                    left: this._constant.LAYOUT_H_GAP,
                    right: this._constant.LAYOUT_H_GAP
                },
                center: {
                    el: this._createCenter()
                },
                south: {
                    el: this._createSouth(),
                    height: this._constant.SOUTH_HEIGHT,
                    left: this._constant.LAYOUT_H_GAP,
                    right: this._constant.LAYOUT_H_GAP
                }
            }
        });
    },

    _createNorth: function () {
        var self = this;
        var packName = self.model.getName();
        var packageName = BI.createWidget({
            type: "bi.sign_editor",
            cls: "business-package-name",
            allowBlank: false,
            text: packName,
            value: packName,
            width: this._constant.PACKAGE_NAME_WIDTH,
            height: this._constant.BUTTON_HEIGHT,
            errorText: BI.i18nText("BI-Busi_Package_Name_Not_Repeat"),
            validationChecker: function () {
                return self.model.checkPackageName(packageName.getValue());
            }
        });
        packageName.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.model.setName(packageName.getValue());
        });

        var viewType = BI.createWidget({
            type: "bi.segment",
            cls: "tables-view-group",
            items: BI.createItems(self.model.getViewType(), {
                type: "bi.icon_button",
                extraCls: "tables-view",
                width: this._constant.VIEW_WIDTH,
                height: this._constant.VIEW_HEIGHT
            }),
            width: 60
        });
        viewType.setValue(BICst.TABLES_VIEW.TILE);
        viewType.on(BI.Segment.EVENT_CHANGE, function () {
            self.showCardLayout.showCardByName(viewType.getValue()[0]);
        });

        this.searcher = BI.createWidget({
            type: "bi.searcher",
            width: this._constant.SEARCH_WIDTH,
            cls: "package-search-editor",
            onSearch: function (op, populate) {
                populate(op, op, op.keyword);
            },
            popup: {
                type: "bi.package_searcher_result_pane",
                onStartSearch: function () {
                    self.addNewTableCombo.setEnable(false);
                },
                onStopSearch: function () {
                    self.addNewTableCombo.setEnable(true);
                }
            }
        });
        this.searcher.on(BI.Searcher.EVENT_CHANGE, function (v) {
            self._onClickOneTable(v);
        });

        this.addNewTableCombo = BI.createWidget({
            type: "bi.combo",
            cls: "add-new-table-combo",
            isNeedAdjustHeight: false,
            trigger: "hover",
            el: {
                type: "bi.text_icon_item",
                cls: "add-new-table-pull-down-font",
                readonly: true,
                textLgap: 10,
                text: "+" + BI.i18nText("BI-Add_Table"),
                height: this._constant.BUTTON_HEIGHT
            },
            popup: {
                el: {
                    type: "bi.button_group",
                    items: BI.createItems(self.model.getNewTableItems(), {
                        type: "bi.text_item",
                        cls: "add-new-table-item",
                        textHgap: this._constant.BUTTON_GAP,
                        height: this._constant.BUTTON_HEIGHT,
                        handler: function () {
                            self.addNewTableCombo.hideView();
                        }
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            },
            width: this._constant.COMBO_WIDTH
        });
        this.addNewTableCombo.on(BI.Combo.EVENT_CHANGE, function (v) {
            switch (v) {
                case BICst.ADD_NEW_TABLE.DATABASE_OR_PACKAGE:
                    self._onClickSelectTable();
                    break;
                case BICst.ADD_NEW_TABLE.ETL:
                    self._onClickAddETL();
                    break;
                case BICst.ADD_NEW_TABLE.SQL:
                    self._onClickAddSQL();
                    break;
                case BICst.ADD_NEW_TABLE.EXCEL:
                    self._onClickAddExcel();
                    break;
            }
        });
        this.addNewTableCombo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.addNewTableCombo.setValue([]);
        });

        var buttonsWrapper = BI.createWidget({
            type: "bi.left",
            items: [this.addNewTableCombo],
            hgap: this._constant.BUTTON_GAP
        });

        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            items: {
                left: [
                    {
                        type: "bi.label",
                        text: BI.i18nText("BI-Package_Name") + ":",
                        cls: "package-name-label",
                        height: this._constant.BUTTON_HEIGHT
                    },
                    packageName, buttonsWrapper
                ],
                right: [this.searcher, viewType]
            },
            lhgap: 8,
            rhgap: 8,
            cls: "one-package-north"
        });
    },

    _createCenter: function () {
        var self = this;
        this.emptyTip = BI.createWidget({
            type: "bi.center_adapt",
            cls: "empty-tip",
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-No_Data_In_Package"),
                height: this._constant.EMPTY_TIP_HEIGHT
            }]
        });

        this.tableList = BI.createWidget({
            type: "bi.package_tables_list_pane"
        });
        this.tableList.on(BI.PackageTablesListPane.EVENT_CLICK_TABLE, function (id) {
            self._onClickOneTable(id);
        });


        this.relationView = BI.createWidget({
            type: "bi.package_table_relations_pane"
        });
        this.relationView.on(BI.PackageTableRelationsPane.EVENT_CLICK_TABLE, function (id) {
            self._onClickOneTable(id);
        });

        this.showCardLayout = BI.createWidget({
            type: "bi.card",
            defaultShowName: BICst.TABLES_VIEW.TILE,
            items: [{
                cardName: BICst.TABLES_VIEW.TILE, el: this.tableList
            }, {
                cardName: BICst.TABLES_VIEW.RELATION, el: this.relationView
            }]
        });

        var center = BI.createWidget({
            type: "bi.default",
            items: [this.emptyTip, this.showCardLayout],
            cls: "one-package-center"
        });
        this.searcher.setAdapter(this.tableList);

        return BI.createWidget({
            type: "bi.center",
            items: [center],
            hgap: this._constant.LAYOUT_H_GAP
        })
    },

    _createSouth: function () {
        var self = this;
        var cancelButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Cancel"),
            level: "ignore",
            height: this._constant.BUTTON_HEIGHT,
            handler: function () {
                self.fireEvent(BI.OnePackage.EVENT_CANCEL);
            }
        });
        var finishButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-OK"),
            height: this._constant.BUTTON_HEIGHT,
            level: "common",
            handler: function () {
                var mask = BI.createWidget({
                    type: "bi.loading_mask",
                    masker: self.element,
                    text: BI.i18nText("BI-Loading")
                });
                var data = self.model.getValue();
                //update sharing pool
                Data.SharingPool.put("translations", data.translations);
                Data.SharingPool.put("relations", data.relations);
                Data.SharingPool.put("fields", self.model.getAllFields());
                Data.SharingPool.put("update_settings", self.model.getUpdateSettings());
                BI.Utils.updateTablesOfOnePackage(data, function () {
                    self.fireEvent(BI.OnePackage.EVENT_SAVE);
                    mask.destroy();
                });
            }
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "south",
            items: {
                left: [cancelButton],
                right: [finishButton]
            }
        });
    },

    _createItemsForTableList: function () {
        var self = this;
        var tableIds = this.model.getTables();
        var tablesData = this.model.getTablesData();
        return BI.map(tableIds, function (i, table) {
            var id = table.id;
            return {
                id: id,
                text: self.model.getTableTranName(id),
                connName: tablesData[id].connection_name
            };
        });
    },

    _refreshTablesInPackage: function () {
        this.showCardLayout.showCardByName(this.showCardLayout.getDefaultShowName());
        this.tableList.populate(this._createItemsForTableList());
        //this.relationView.populate({
        //    tableIds: this.model.getTables(),
        //    translations: this.model.getTranslations(),
        //    relations: this.model.getRelations(),
        //    all_fields: this.model.getAllFields(),
        //    tableData: this.model.getTablesData()
        //});
        this._refreshEmptyTip();
        //避免出现停留在前面的搜索面板
        this.searcher.stopSearch();
    },

    _refreshEmptyTip: function () {
        if (BI.isEmptyArray(this.model.getTables())) {
            this._onClickSelectTable();
            this.emptyTip.setVisible(true);
            return;
        }
        this.emptyTip.setVisible(false);
    },

    _onClickSelectTable: function () {
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_table_pane",
            tables: this.model.getTablesData(),
            element: BI.Layers.create(BICst.SELECT_TABLES_LAYER, BICst.BODY_ELEMENT),
            translations: this.model.getTranslations()
        });
        BI.Layers.show(BICst.SELECT_TABLES_LAYER);
        selectTablePane.on(BI.SelectTablePane.EVENT_NEXT_STEP, function (tables) {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
            self.model.addTablesToPackage(tables, function () {
                self._refreshTablesInPackage();
            });
        });
        selectTablePane.on(BI.SelectTablePane.EVENT_CANCEL, function () {
            BI.Layers.remove(BICst.SELECT_TABLES_LAYER);
        });
    },

    _onClickAddETL: function () {
        var self = this;
        var tableId = BI.UUID();
        BI.Layers.remove(this._constant.ETL_LAYER);
        var etl = BI.createWidget({
            type: "bi.etl",
            element: BI.Layers.create(this._constant.ETL_LAYER),
            id: tableId,
            relations: this.model.getRelations(),
            translations: this.model.getTranslations(),
            all_fields: this.model.getAllFields(),
            excel_view: this.model.getExcelViews()[tableId],
            update_settings: this.model.getUpdateSettings()
        });
        BI.Layers.show(this._constant.ETL_LAYER);
        etl.on(BI.ETL.EVENT_SAVE, function (data) {
            self.model.changeTableInfo(tableId, data);
            self._refreshTablesInPackage();
            BI.Layers.remove(self._constant.ETL_LAYER);
        });
        etl.on(BI.ETL.EVENT_CANCEL, function () {
            BI.Layers.remove(self._constant.ETL_LAYER);
        });
    },

    _onClickAddSQL: function () {
        var self = this;
        BI.Layers.remove(this._constant.SQL_LAYER);
        var editSQL = BI.createWidget({
            type: "bi.edit_sql",
            element: BI.Layers.create(this._constant.SQL_LAYER)
        });
        BI.Layers.show(this._constant.SQL_LAYER);
        editSQL.on(BI.EditSQL.EVENT_CANCEL, function () {
            BI.Layers.remove(self._constant.SQL_LAYER);
        });
        editSQL.on(BI.EditSQL.EVENT_SAVE, function (data) {
            BI.Layers.remove(self._constant.SQL_LAYER);
            var tableId = BI.UUID();
            var usedFields = [];
            var allFields = self.model.getAllFields();
            BI.each(data.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    field.table_id = tableId;
                    field.id = BI.UUID();
                    usedFields.push(field.field_name);
                    allFields[field.id] = field;
                });
            });
            var translations = self.model.getTranslations();
            var tableName = self.model.createDistinctTableTranName(BI.i18nText("BI-Sql_DataSet"));
            translations[tableId] = tableName;
            data.table_name = tableName;
            BI.Layers.remove(self._constant.ETL_LAYER);
            var etl = BI.createWidget({
                type: "bi.etl_sql",
                element: BI.Layers.create(self._constant.ETL_LAYER),
                id: tableId,
                table_data: data,
                used_fields: usedFields,
                relations: self.model.getRelations(),
                translations: translations,
                all_fields: allFields,
                excel_view: self.model.getExcelViews()[tableId],
                update_settings: self.model.getUpdateSettings()
            });
            BI.Layers.show(self._constant.ETL_LAYER);
            etl.on(BI.ETL.EVENT_SAVE, function (data) {
                self.model.changeTableInfo(tableId, data);
                self._refreshTablesInPackage();
                BI.Layers.remove(self._constant.ETL_LAYER);
            });
            etl.on(BI.ETL.EVENT_CANCEL, function () {
                BI.Layers.remove(self._constant.ETL_LAYER);
            });
        });
    },

    _onClickAddExcel: function () {
        var self = this;
        BI.Layers.remove(this._constant.EXCEL_LAYER);
        var excelUpload = BI.createWidget({
            type: "bi.excel_upload",
            element: BI.Layers.create(this._constant.EXCEL_LAYER)
        });
        BI.Layers.show(this._constant.EXCEL_LAYER);
        excelUpload.on(BI.ExcelUpload.EVENT_CANCEL, function () {
            BI.Layers.remove(self._constant.EXCEL_LAYER);
        });
        excelUpload.on(BI.ExcelUpload.EVENT_SAVE, function (data) {
            BI.Layers.remove(self._constant.EXCEL_LAYER);
            var tableId = BI.UUID();
            var usedFields = [];
            var allFields = self.model.getAllFields();
            BI.each(data.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    field.table_id = tableId;
                    field.id = BI.UUID();
                    usedFields.push(field.field_name);
                    allFields[field.id] = field;
                });
            });
            var tableName = self.model.createDistinctTableTranName(BI.i18nText("BI-Excel_Dataset"));
            var translations = self.model.getTranslations();
            translations[tableId] = tableName;
            data.table_name = tableName;
            BI.Layers.remove(self._constant.ETL_LAYER);
            var etl = BI.createWidget({
                type: "bi.etl_excel",
                element: BI.Layers.create(self._constant.ETL_LAYER),
                id: tableId,
                table_data: data,
                used_fields: usedFields,
                relations: self.model.getRelations(),
                translations: translations,
                all_fields: allFields,
                excel_view: self.model.getExcelViews()[tableId],
                update_settings: self.model.getUpdateSettings()
            });
            BI.Layers.show(self._constant.ETL_LAYER);
            etl.on(BI.ETL.EVENT_SAVE, function (data) {
                self.model.changeTableInfo(tableId, data);
                self._refreshTablesInPackage();
                BI.Layers.remove(self._constant.ETL_LAYER);
            });

            etl.on(BI.ETL.EVENT_CANCEL, function () {
                BI.Layers.remove(self._constant.ETL_LAYER);
            });
        });
    },

    _onClickOneTable: function (id) {
        var self = this;
        BI.Layers.remove(this._constant.ETL_LAYER);
        var etl = BI.createWidget({
            type: "bi.etl",
            element: BI.Layers.create(this._constant.ETL_LAYER),
            id: id,
            table_data: this.model.getTablesData()[id],
            used_fields: this.model.getUsedFields()[id],
            relations: this.model.getRelations(),
            translations: this.model.getTranslations(),
            all_fields: this.model.getAllFields(),
            excel_view: this.model.getExcelViews()[id],
            update_settings: this.model.getUpdateSettings()
        });
        BI.Layers.show(this._constant.ETL_LAYER);
        etl.on(BI.ETL.EVENT_CUBE_SAVE, function (obj) {
            var data = self.model.getValue();
            Data.SharingPool.put("translations", data.translations);
            Data.SharingPool.put("relations", data.relations);
            Data.SharingPool.put("fields", self.model.getAllFields());
            Data.SharingPool.put("update_settings", self.model.getUpdateSettings());
            BI.Utils.updateTablesOfOnePackage(data, function () {
                // self.fireEvent(BI.OnePackage.EVENT_CUBE_SAVE);
                BI.Utils.generateCubeByTable(obj, function () {
                });
            });
        });
        etl.on(BI.ETL.EVENT_SAVE, function (data) {
            self.model.changeTableInfo(id, data);
            self._refreshTablesInPackage();
            BI.Layers.remove(self._constant.ETL_LAYER);
        });
        etl.on(BI.ETL.EVENT_REMOVE, function () {
            self.model.removeTable(id);
            self._refreshTablesInPackage();
            BI.Layers.remove(self._constant.ETL_LAYER);
        });
        etl.on(BI.ETL.EVENT_CANCEL, function () {
            BI.Layers.remove(self._constant.ETL_LAYER);
        });
    },

    populate: function () {
        var self = this;
        this.model.initData(function () {
            self._refreshTablesInPackage();
        });
    },

    getValue: function () {
        return this.model.getValue();
    }
});
BI.OnePackage.EVENT_CANCEL = "EVENT_CANCEL";
BI.OnePackage.EVENT_SAVE = "EVENT_SAVE";
BI.OnePackage.EVENT_CUBE_SAVE = "EVENT_CUBE_SAVE";
$.shortcut("bi.one_package", BI.OnePackage);

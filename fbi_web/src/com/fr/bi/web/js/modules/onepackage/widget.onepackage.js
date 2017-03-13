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
        SOUTH_HEIGHT: 60,

        SHOW_TIP: 1,
        SHOW_TABLE: 2
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

        var addNewTableCombo = this._createAddTableCombo();

        this.searcher = BI.createWidget({
            type: "bi.searcher",
            width: this._constant.SEARCH_WIDTH,
            cls: "package-search-editor",
            onSearch: function (op, populate) {
                populate(op, op, op.keyword);
            },
            popup: {
                type: "bi.package_searcher_result_pane",
                connNames: self.model.getConnNames(),
                onStartSearch: function () {
                    addNewTableCombo.setEnable(false);
                    self.tabButtons.setEnable(false);
                },
                onStopSearch: function () {
                    addNewTableCombo.setEnable(true);
                    self.tabButtons.setEnable(true);
                }
            }
        });
        this.searcher.on(BI.Searcher.EVENT_CHANGE, function (v) {
            self._onClickOneTable(v);
        });

        this.relationSearcher = BI.createWidget({
            type: "bi.searcher",
            width: this._constant.SEARCH_WIDTH,
            cls: "package-search-editor",
            onSearch: function (op, callback) {
                callback(self.relationView.getCacheItems(), BI.keys(self.model.getTables()), op.keyword);
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: {
                type: "bi.package_table_relation_result_pane",
                packageId: self.model.getId(),
                onStartSearch: function () {
                    addNewTableCombo.setEnable(false);
                    self.tabButtons.setEnable(false);
                },
                onStopSearch: function () {
                    addNewTableCombo.setEnable(true);
                    self.tabButtons.setEnable(true);
                }
            }
        });
        this.relationSearcher.on(BI.Searcher.EVENT_CHANGE, function (v) {
            self._onClickOneTable(v);
        });
        this.relationSearcher.setVisible(false);

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
                    packageName, addNewTableCombo
                ],
                right: [this.searcher, this.relationSearcher]
            },
            lhgap: 10,
            rrgap: 90,
            cls: "one-package-north"
        });
    },

    _createAddTableCombo: function () {
        var self = this;
        var addNewTableCombo = BI.createWidget({
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
                            addNewTableCombo.hideView();
                        }
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            },
            width: this._constant.COMBO_WIDTH
        });
        addNewTableCombo.on(BI.Combo.EVENT_CHANGE, function (v) {
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
        addNewTableCombo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            addNewTableCombo.setValue([]);
        });
        return addNewTableCombo;
    },

    _viewTypeCreator: function (v) {
        var self = this;
        switch (v) {
            case BICst.TABLES_VIEW.TILE:
                this.tableList = BI.createWidget({
                    type: "bi.package_tables_list_pane",
                    connNames: self.model.getConnNames()
                });
                this.tableList.on(BI.PackageTablesListPane.EVENT_CLICK_TABLE, function (id) {
                    self._onClickOneTable(id);
                });
                this.searcher.setAdapter(this.tableList);
                return this.tableList;
            case BICst.TABLES_VIEW.RELATION:
                this.relationView = BI.createWidget({
                    type: "bi.package_table_relations_pane",
                    packageId: self.model.getId()
                });
                this.relationView.on(BI.PackageTableRelationsPane.EVENT_CLICK_TABLE, function (id) {
                    self._onClickOneTable(id);
                });
                this.relationSearcher.setAdapter(this.relationView);
                return this.relationView;
        }
    },

    _tipAndTablesCreator: function (v) {
        var self = this;
        switch (v) {
            case this._constant.SHOW_TIP:
                return {
                    type: "bi.center_adapt",
                    cls: "empty-tip",
                    items: [{
                        type: "bi.label",
                        text: BI.i18nText("BI-No_Data_In_Package"),
                        height: this._constant.EMPTY_TIP_HEIGHT
                    }]
                };
            case this._constant.SHOW_TABLE:
                this.tabButtons = BI.createWidget({
                    type: "bi.segment",
                    cls: "tables-view-group",
                    items: BI.createItems(this.model.getViewType(), {
                        type: "bi.icon_button",
                        extraCls: "tables-view",
                        width: this._constant.VIEW_WIDTH,
                        height: this._constant.VIEW_HEIGHT,
                        warningTitle: BI.i18nText("BI-No_Switch_Searching")
                    }),
                    width: 60
                });
                this.viewType = BI.createWidget({
                    type: "bi.tab",
                    tab: this.tabButtons,
                    direction: "custom",
                    defaultShowIndex: BICst.TABLES_VIEW.TILE,
                    cardCreator: BI.bind(this._viewTypeCreator, this)
                });

                this.viewType.on(BI.Tab.EVENT_CHANGE, function () {
                    if (this.getSelect() === BICst.TABLES_VIEW.RELATION) {
                        this.populate({
                            tablesData: self.model.getTables()
                        });
                        self.relationSearcher.setVisible(true);
                        self.searcher.setVisible(false);
                    } else {
                        self.relationSearcher.setVisible(false);
                        self.searcher.setVisible(true);
                    }
                });

                return BI.createWidget({
                    type: "bi.absolute",
                    element: this.viewType,
                    items: [{
                        el: this.tabButtons,
                        top: -40,
                        right: 20
                    }]
                });
        }
    },

    _createCenter: function () {
        // 中心区域tab，用于切换提示和表界面
        this.centerTab = BI.createWidget({
            type: "bi.tab",
            cls: "one-package-center",
            defaultShowIndex: false,
            cardCreator: BI.bind(this._tipAndTablesCreator, this)
        });
        return BI.createWidget({
            type: "bi.absolute",
            items: [{
                el: this.centerTab,
                top: 0,
                right: 20,
                bottom: 0,
                left: 20
            }]
        })
    },

    _createSouth: function () {
        var self = this;
        var finishButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-OK"),
            height: this._constant.BUTTON_HEIGHT,
            level: "common"
        });
        finishButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.OnePackage.EVENT_SAVE);
        });
        return BI.createWidget({
            type: "bi.left_right_vertical_adapt",
            cls: "south",
            items: {
                right: [finishButton]
            }
        });
    },

    _createItemsForTableList: function () {
        var self = this;
        var tables = this.model.getSortedTables();
        return BI.map(tables, function (i, table) {
            return {
                id: table.id,
                text: self.model.getTableTranName(table),
                value: table.id,
                connName: table.connection_name
            };
        });
    },

    _refreshTablesInPackage: function () {
        this._refreshTabs();
        //避免出现停留在前面的搜索面板
        this.searcher.stopSearch();
    },

    _refreshTabs: function () {
        if (BI.size(this.model.getTables()) === 0) {
            this.centerTab.setSelect(this._constant.SHOW_TIP);
        } else {
            this.centerTab.setSelect(this._constant.SHOW_TABLE);
            this.viewType.setSelect(BICst.TABLES_VIEW.TILE);
            this.tableList.populate(this._createItemsForTableList());
        }
    },

    _onClickSelectTable: function () {
        var self = this;
        var selectTablePane = BI.createWidget({
            type: "bi.select_table_pane",
            element: BI.Layers.create(BICst.SELECT_TABLES_LAYER, BICst.BODY_ELEMENT),
            tables: this.model.getTables(),
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
            packageId: this.model.getId()
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
        etl.on(BI.ETL.EVENT_CUBE_SAVE, function (table) {
            self.model.changeTableInfo(tableId, table);
            self._refreshTablesInPackage();
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
            BI.each(data.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    field.table_id = tableId;
                    field.id = BI.UUID();
                });
            });
            data.table_name = BI.i18nText("BI-Sql_DataSet");
            BI.Layers.remove(self._constant.ETL_LAYER);
            var etl = BI.createWidget({
                type: "bi.etl_sql",
                element: BI.Layers.create(self._constant.ETL_LAYER),
                id: tableId,
                table: data,
                packageId: self.model.getId()
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
            BI.each(data.fields, function (i, fs) {
                BI.each(fs, function (j, field) {
                    field.table_id = tableId;
                    field.id = BI.UUID();
                });
            });
            data.table_name = BI.i18nText("BI-Excel_Dataset");
            BI.Layers.remove(self._constant.ETL_LAYER);
            var etl = BI.createWidget({
                type: "bi.etl_excel",
                element: BI.Layers.create(self._constant.ETL_LAYER),
                id: tableId,
                table: data,
                packageId: self.model.getId()
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
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: BICst.BODY_ELEMENT,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTableInfoByTableId4Conf({id: id}, function (res) {
            if (BI.isNotNull(res) && BI.isNotNull(res.lockedBy)) {
                BI.Msg.toast(BI.i18nText("BI-Is_Editing_Or_Relation_The_Table", res.lockedBy), "warning");
                return;
            }
            self._buildOneTablePane(id, res);
        }, function () {
            mask.destroy();
        });
    },

    _buildOneTablePane: function (id, data) {
        BI.Layers.remove(this._constant.ETL_LAYER);
        var self = this;
        var type = "bi.etl";
        var tableData = this.model.getTableByTableId(id);
        var connName = tableData.connection_name;
        if (connName === BICst.CONNECTION.EXCEL_CONNECTION) {
            type = "bi.etl_excel"
        } else if (connName === BICst.CONNECTION.SQL_CONNECTION) {
            type = "bi.etl_sql";
        }
        var etl = BI.createWidget({
            type: type,
            element: BI.Layers.create(this._constant.ETL_LAYER),
            id: id,
            packageId: this.model.getId(),
            table: data.table,
            excelView: data.excelView,
            updateSettings: data.updateSettings
        });
        BI.Layers.show(this._constant.ETL_LAYER);
        etl.on(BI.ETL.EVENT_CUBE_SAVE, function (table) {
            self.model.changeTableInfo(id, table);
            self._refreshTablesInPackage();
        });
        etl.on(BI.ETL.EVENT_SAVE, function (data) {
            self.model.changeTableInfo(id, data);
            self._refreshTablesInPackage();
            BI.Layers.remove(self._constant.ETL_LAYER);
        });
        etl.on(BI.ETL.EVENT_REMOVE, function () {
            self.model.removeTable(id, function () {
                self._refreshTablesInPackage();
                BI.Layers.remove(self._constant.ETL_LAYER);
            });
        });
        etl.on(BI.ETL.EVENT_CANCEL, function () {
            BI.Layers.remove(self._constant.ETL_LAYER);
            BI.Utils.releaseTableLock4Conf({id: id});
        });
    },

    populate: function () {
        var self = this;
        this.model.initData(function () {
            self._refreshTablesInPackage();
        });
    }
});
BI.OnePackage.EVENT_CANCEL = "EVENT_CANCEL";
BI.OnePackage.EVENT_SAVE = "EVENT_SAVE";
BI.OnePackage.EVENT_CUBE_SAVE = "EVENT_CUBE_SAVE";
$.shortcut("bi.one_package", BI.OnePackage);

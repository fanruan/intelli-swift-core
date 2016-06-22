/**
 * @class BI.ETLTableCombo
 * @extend BI.Widget
 * etl 右侧表的combo
 */
BI.ETLTableCombo = BI.inherit(BI.Widget, {

    constants: {
        WRAPPER_WIDTH: 180,
        WRAPPER_HEIGHT: 100,
        COMBO_WIDTH: 170,
        COMBO_HEIGHT: 30,
        ITEM_HEIGHT: 25,
        ITEM_TEXT_GAP: 10,
        WRAPPER_GAP: 5,
        COMBO_BOTTOM_GAP: 10,
        OPERATOR_TOP_GAP: 20,
        OPERATOR_LEFT_GAP: 20
    },

    _defaultConfig: function () {
        return BI.extend(BI.ETLTableCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-table-combo",
            height: this.constants.WRAPPER_HEIGHT,
            width: this.constants.WRAPPER_WIDTH
        });
    },

    _init: function () {
        BI.ETLTableCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var tableInfo = o.tableInfo;
        this.oneTable = BI.createWidget({
            type: "bi.absolute",
            element: this.element
        });
        //ETL 表（包括中间层）
        if (BI.isNotNull(tableInfo["tables"])) {
            var operator = BI.createWidget({
                type: "bi.etl-table-operator",
                tableInfo: tableInfo
            });
            operator.on(BI.ETLTableOperator.EVENT_CHANGE, function (table) {
                self.fireEvent(BI.ETLTableCombo.EVENT_CLICK_OPERATOR, table);
            });
            self.oneTable.addItem({
                el: operator,
                top: this.constants.OPERATOR_TOP_GAP,
                left: this.constants.OPERATOR_LEFT_GAP
            });
            BI.Utils.checkCubeStatusByTable(tableInfo, function (data) {
                var items = data.isGenerated === true ? BI.deepClone(BICst.ETL_MANAGE_ITEMS) :
                    self._setPartItemsDisable();
                if (tableInfo.isFinal !== true) {
                    items[items.length - 1].disabled = true;
                    items[items.length - 1].warningTitle = BI.i18nText("BI-Temp_Tables_No_Remove")
                }
                self._createCombo(items);
            });
        } else {
            //原始表（非最终表的情况下都不可删除）
            var items = BI.deepClone(BICst.ETL_MANAGE_ITEMS);
            if (tableInfo.isFinal !== true) {
                items[items.length - 1].disabled = true;
                items[items.length - 1].warningTitle = BI.i18nText("BI-Temp_Tables_No_Remove")
            }
            if(tableInfo.connection_name === BICst.CONNECTION.EXCEL_CONNECTION) {
                items.splice(0, 0, {
                    text: BI.i18nText("BI-Remodify_Excel"),
                    title: BI.i18nText("BI-Remodify_Excel"),
                    value: BICst.ETL_MANAGE_EXCEL_CHANGE
                });
            }
            if(tableInfo.connection_name === BICst.CONNECTION.SQL_CONNECTION) {
                items.splice(0, 0, {
                    text: BI.i18nText("BI-Remodify_Sql"),
                    title: BI.i18nText("BI-Remodify_Sql"),
                    value: BICst.ETL_MANAGE_SQL_CHANGE
                });
            }
            this._createCombo(items);
        }
    },

    _createCombo: function (items) {
        var self = this;
        var tableInfo = self.options.tableInfo;
        var tableName = this._getTableName();
        self.tableCombo = BI.createWidget({
            type: "bi.combo",
            cls: "bi-etl-table",
            isNeedAdjustHeight: true,
            el: {
                type: "bi.button",
                text: tableName,
                title: tableName,
                level: tableInfo.isFinal === true ? "common" : "ignore",
                height: self.constants.COMBO_HEIGHT
            },
            popup: {
                el: {
                    type: "bi.button_group",
                    items: BI.createItems(items, {
                        type: "bi.text_item",
                        cls: "bi-list-item",
                        textHgap: self.constants.ITEM_TEXT_GAP,
                        height: self.constants.ITEM_HEIGHT,
                        handler: function () {
                            self.tableCombo.hideView();
                        }
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                },
                maxHeight: 300
            },
            width: self.constants.COMBO_WIDTH
        });
        self._setETLEvents();
        self.oneTable.addItem({
            el: self.tableCombo,
            bottom: self.constants.COMBO_BOTTOM_GAP,
            left: self.constants.WRAPPER_GAP
        });
    },

    _getTableName: function () {
        var table = this.options.tableInfo;
        var tableNameText = "";
        if (BI.isNotNull(table.etl_type)) {
            tableNameText = this._getCenterETLName(table.tables) + "_" + table.etl_type;
        } else {
            tableNameText = table.table_name
        }
        return tableNameText;
    },

    _getCenterETLName: function (tables) {
        var tableName = [];

        function getDefaultName(tables) {
            //只取tables[0]
            if (BI.isNotNull(tables[0].etl_type)) {
                tableName.push("_" + tables[0].etl_type);
                getDefaultName(tables[0].tables);
            } else {
                tableName.push(tables[0].table_name);
            }
        }

        getDefaultName(tables);
        //反向遍历
        tableName.reverse();
        var tableNameString = "";
        BI.each(tableName, function (i, name) {
            tableNameString += name;
        });
        return tableNameString;
    },

    _setPartItemsDisable: function () {
        var items = [];
        BI.each(BI.deepClone(BICst.ETL_MANAGE_ITEMS), function (i, item) {
            if (item.value !== BICst.ETL_MANAGE_TABLE_ADD_FIELD &&
                item.value !== BICst.ETL_MANAGE_TABLE_JOIN &&
                item.value !== BICst.ETL_MANAGE_TABLE_UNION &&
                item.value !== BICst.ETL_MANAGE_TABLE_PARTIAL &&
                item.value !== BICst.ETL_MANAGE_TABLE_DELETE) {
                item.disabled = true;
                item.warningTitle = BI.i18nText("BI-Operation_Need_Generated_Cube");
            }
            items.push(item);
        });
        return items;
    },

    _setETLEvents: function () {
        var self = this;
        this.tableCombo.on(BI.Combo.EVENT_CHANGE, function () {
            self.fireEvent(BI.ETLTableCombo.EVENT_CHANGE);
        });
        this.tableCombo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.tableCombo.setValue([]);
        });
    },

    getValue: function () {
        return this.tableCombo.getValue();
    },

    setValue: function (v) {
        this.tableCombo.setValue(v);
    }
});
BI.ETLTableCombo.EVENT_CLICK_OPERATOR = "EVENT_CLICK_OPERATOR";
BI.ETLTableCombo.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_table_combo", BI.ETLTableCombo);

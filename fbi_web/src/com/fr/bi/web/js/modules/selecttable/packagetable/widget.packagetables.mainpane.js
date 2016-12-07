/**
 * Created by Young's on 2016/10/24.
 */
BI.PackageTablesMainPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.PackageTablesMainPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-package-tables-main-pane"
        });
    },

    _init: function () {
        BI.PackageTablesMainPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var searcher = BI.createWidget({
            type: "bi.searcher",
            width: 230,
            onSearch: function (op, callback) {
                var res = BI.Func.getSearchResult(self.tables, op.keyword, "text");
                callback(res.finded, res.matched, op.keyword, self.tablesPane.getValue());
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: {
                type: "bi.package_table_search_result_pane"
            }
        });
        searcher.on(BI.Searcher.EVENT_STOP, function () {

        });
        searcher.on(BI.Searcher.EVENT_CHANGE, function (value, obj) {
            var selectedTables = self.tablesPane.getValue();
            if (obj.isSelected() === false) {
                BI.some(selectedTables, function (i, table) {
                    if (table.value === obj.getValue().value) {
                        selectedTables.splice(i, 1);
                        return true;
                    }
                });
            } else {
                selectedTables.push(obj.getValue());
            }
            self.tablesPane.setValue(selectedTables);
            self.fireEvent(BI.DatabaseTablesMainPane.EVENT_CHANGE);
        });

        this.tablesPane = BI.createWidget({
            type: "bi.package_table_pane",
            packId: o.packId,
            translations: o.translations
        });
        this.tablesPane.on(BI.PackageTablePane.EVENT_CHANGE, function () {
            self.fireEvent(BI.PackageTablesMainPane.EVENT_CHANGE);
        });
        var mask = BI.createWidget({
            type: "bi.loading_mask",
            masker: this.element,
            text: BI.i18nText("BI-Loading")
        });
        BI.Utils.getTablesByPackId(o.packId, function (tables) {
            self.tables = [];
            BI.each(tables, function (id, table) {
                self.tables.push({
                    type: "bi.database_table",
                    cls: "bi-table-ha-button",
                    connName: table.connection_name,
                    text: self._getTableTranName(id, table),
                    linkNames: o.linkNames,
                    value: BI.extend(table, {
                        id: id,
                        temp_name: o.translations[id]
                    })
                })
            });
            self.tablesPane.populate(self.tables);
        }, function () {
            mask.destroy();
        });

        searcher.setAdapter(this.tablesPane);
        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: {
                    type: "bi.vtape",
                    cls: "tables-pane",
                    items: [{
                        el: {
                            type: "bi.right",
                            cls: "top-searcher",
                            items: [searcher],
                            hgap: 10,
                            vgap: 15
                        },
                        height: 60
                    }, {
                        el: {
                            type: "bi.left",
                            cls: "north-bottom-line"
                        },
                        height: 2
                    }, {
                        el: this.tablesPane,
                        height: "fill"
                    }]
                },
                top: 20,
                right: 20,
                bottom: 0,
                left: 0
            }]
        });
    },

    _getTableTranName: function (id, table) {
        var tableNameText = table.table_name;
        var translations = this.options.translations;
        //ETL 表
        if (table.connection_name === BICst.CONNECTION.ETL_CONNECTION) {
            tableNameText = translations[id];
        } else if (BI.isNotNull(translations[id]) && translations[id] !== tableNameText) {
            tableNameText = translations[id] + "(" + tableNameText + ")";
        }
        return tableNameText;
    },

    getValue: function () {
        return this.tablesPane.getValue();
    }
});
BI.PackageTablesMainPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.package_tables_main_pane", BI.PackageTablesMainPane);
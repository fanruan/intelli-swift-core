/**
 * @class BI.DatabaseTablesMainPane
 * @extend BI.Widget
 * 单个数据连接中所有表 tab
 */
BI.DatabaseTablesMainPane = BI.inherit(BI.Widget, {

    _constant: {
        NORTH_HEIGHT: 60,
        PANE_GAP: 20
    },

    _defaultConfig: function () {
        return BI.extend(BI.DatabaseTablesMainPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-data-link-tables-pane"
        })
    },

    _init: function () {
        BI.DatabaseTablesMainPane.superclass._init.apply(this, arguments);
        var self = this;

        //所有表面板
        this.tablesPane = BI.createWidget({
            type: "bi.database_tables_pane",
            tables: this.options.tables,
            container: this.element
        });
        this.tablesPane.on(BI.DatabaseTablesPane.EVENT_CHANGE, function () {
            self.fireEvent(BI.DatabaseTablesMainPane.EVENT_CHANGE);
        });

        //搜索
        this.seacher = BI.createWidget({
            type: "bi.searcher",
            width: 230,
            onSearch: function (op, callback) {
                var allTableGroups = self.tablesPane.getAllTables();
                var needSearched = [];
                BI.each(allTableGroups, function (i, groups) {
                    BI.each(groups, function (j, group) {
                        BI.each(group.group, function (k, table) {
                            needSearched.push(table);
                        });
                    })
                });
                var res = BI.Func.getSearchResult(needSearched, op.keyword, "value");
                var searchResult = res.matched.concat(res.finded);
                callback(searchResult, [], op.keyword, self.tablesPane.getSelectedTables(), self.tablesPane.getConnectionName());
            },
            isAutoSearch: false,
            isAutoSync: false,
            popup: {
                type: "bi.database_tables_search_result_pane",
                tables: this.options.tables
            }
        });
        this.seacher.on(BI.Searcher.EVENT_START, function () {
            self.tablesPane.setComboEnable(false);
        });
        this.seacher.on(BI.Searcher.EVENT_STOP, function () {
            self.tablesPane.setComboEnable(true);
            self.tablesPane.refreshSelectedTables();
        });
        this.seacher.on(BI.Searcher.EVENT_CHANGE, function (value, obj) {
            //搜索的时候不做setValue操作，而是在结束的时候对所有已存在的page做，同时createPage的时候也是需要做的
            var selectedTables = self.tablesPane.getSelectedTables();
            if (obj.isSelected() === false) {
                BI.some(selectedTables, function (i, table) {
                    if (table.value === obj.getValue().value) {
                        selectedTables.splice(i, 1);
                        return true;
                    }
                });
                self.tablesPane.setSelectedTables(selectedTables);
            } else {
                selectedTables.push(obj.getValue());
                self.tablesPane.setSelectedTables(selectedTables);
            }
            self.fireEvent(BI.DatabaseTablesMainPane.EVENT_CHANGE);
        });

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
                            items: [this.seacher],
                            hgap: 10,
                            vgap: 15
                        },
                        height: this._constant.NORTH_HEIGHT
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
                top: this._constant.PANE_GAP,
                right: this._constant.PANE_GAP,
                bottom: 0,
                left: 0
            }]
        });
        this.seacher.setAdapter(this.tablesPane);
    },

    populate: function (connectionName) {
        this.tablesPane.populateTab(connectionName);
    },

    getValue: function () {
        return this.tablesPane.getValue();
    },

    setValue: function () {

    }

});
BI.DatabaseTablesMainPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.database_tables_main_pane", BI.DatabaseTablesMainPane);
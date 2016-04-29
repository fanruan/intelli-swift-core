/**
 * 选择表界面
 * @class BI.SelectTablePane
 * @extend BI.Widget
 */
BI.SelectTablePane = BI.inherit(BI.LoadingPane, {

    constants: {
        SOUTH_HEIGHT: 60,
        BUTTON_GAP: 20,
        BUTTON_HEIGHT: 28
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectTablePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.SelectTablePane.superclass._init.apply(this, arguments);
        BI.createWidget({
            type: "bi.border",
            element: this.element,
            items: {
                center: this._createCenter(),
                south: {
                    el: this._createSouth(),
                    height: this.constants.SOUTH_HEIGHT
                }
            }
        });
    },

    _createCenter: function () {
        var self = this, o = this.options;
        this.tab = BI.createWidget({
            type: "bi.data_links_tab",
            tables: o.tables,
            etl: o.etl,
            currentId: o.currentId,
            translations: o.translations
        });
        Data.BufferPool.getConnectionName(function (linkNames) {
            self.tab.populate(linkNames);
        });
        this.tab.on(BI.DataLinksTab.EVENT_CHANGE, function () {
            self._setTableCount();
        });
        return this.tab;
    },

    _createSouth: function () {
        var self = this;
        this.tableCount = BI.createWidget({
            type: "bi.label",
            text: BI.i18nText("BI-Please_Select_Table")
        });
        this.nextButton = BI.createWidget({
            type: "bi.button",
            level: "common",
            text: BI.i18nText("BI-Next_Step"),
            height: this.constants.BUTTON_HEIGHT,
            handler: function () {
                self.loading();
                var allTables = self.tab.getValue();
                BI.Utils.getTablesDetailInfoByTables(allTables.sTables, function (sourceTables) {
                    self.loaded();
                    var tables = [];
                    tables = tables.concat(sourceTables);
                    tables = tables.concat(allTables.pTables);
                    tables = tables.concat(allTables.eTables);
                    self.fireEvent(BI.SelectTablePane.EVENT_NEXT_STEP, tables);
                });
            }
        });
        this._setTableCount();
        var cancelButton = BI.createWidget({
            type: "bi.button",
            level: "ignore",
            text: BI.i18nText("BI-Back_Step"),
            height: this.constants.BUTTON_HEIGHT
        });
        cancelButton.on(BI.Button.EVENT_CHANGE, function () {
            self.fireEvent(BI.SelectTablePane.EVENT_CANCEL);
        });
        return BI.createWidget({
            type: "bi.center",
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "bi-data-link-pane-south",
                items: {
                    left: [cancelButton],
                    right: [
                        this.tableCount,
                        this.nextButton
                    ]
                },
                lhgap: this.constants.BUTTON_GAP,
                rhgap: this.constants.BUTTON_GAP
            }]
        })
    },

    _setTableCount: function () {
        var tables = this.tab.getValue();
        var count = tables.sTables.length + tables.pTables.length + tables.eTables.length;
        if (count === 0) {
            this.tableCount.setText(BI.i18nText("BI-Please_Select_Table"));
            this.nextButton.setEnable(false);
        } else {
            this.tableCount.setText(BI.i18nText("BI-Already_Selected_Tables", count));
            this.nextButton.setEnable(true);
        }
    }
});
BI.SelectTablePane.EVENT_CANCEL = "EVENT_CANCEL";
BI.SelectTablePane.EVENT_NEXT_STEP = "EVENT_NEXT_STEP";
$.shortcut("bi.select_table_pane", BI.SelectTablePane);
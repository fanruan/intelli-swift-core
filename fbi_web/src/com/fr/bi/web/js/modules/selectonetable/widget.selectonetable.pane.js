/**
 * 选择单个表界面
 * @class BI.SelectOneTablePane
 * @extend BI.Widget
 */
BI.SelectOneTablePane = BI.inherit(BI.Widget, {

    constants: {
        SOUTH_HEIGHT: 60,
        BUTTON_GAP: 20,
        BUTTON_HEIGHT: 30
    },

    _defaultConfig: function () {
        return BI.extend(BI.SelectOneTablePane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-select-table-pane"
        })
    },

    _init: function () {
        BI.SelectOneTablePane.superclass._init.apply(this, arguments);
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
            BI.Layers.remove(BICst.SELECT_ONE_TABLE_LAYER);
            var allTables = self.tab.getValue();
            var tables = [];
            if (BI.isEmptyArray(allTables.sTables)) {
                tables = tables.concat(allTables.pTables);
                tables = tables.concat(allTables.eTables);
                self.fireEvent(BI.SelectOneTablePane.EVENT_CHANGE, tables);
                return;
            }
            var mask = BI.createWidget({
                type: "bi.loading_mask",
                masker: self.element,
                text: BI.i18nText("BI-Loading")
            });
            BI.Utils.getTablesDetailInfoByTables(allTables.sTables, function (tables) {
                mask.destroy();
                self.fireEvent(BI.SelectOneTablePane.EVENT_CHANGE, tables);
            });
        });
        return this.tab;
    },

    _createSouth: function () {
        return BI.createWidget({
            type: "bi.center",
            items: [{
                type: "bi.left_right_vertical_adapt",
                cls: "bi-data-link-pane-south",
                items: {
                    left: [{
                        type: "bi.button",
                        level: "ignore",
                        text: BI.i18nText("BI-Back_Step"),
                        height: this.constants.BUTTON_HEIGHT,
                        handler: function () {
                            BI.Layers.remove(BICst.SELECT_ONE_TABLE_LAYER);
                        }
                    }],
                    right: []
                },
                lhgap: this.constants.BUTTON_GAP,
                rhgap: this.constants.BUTTON_GAP
            }]
        })
    }
});
BI.SelectOneTablePane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.select_one_table_pane", BI.SelectOneTablePane);
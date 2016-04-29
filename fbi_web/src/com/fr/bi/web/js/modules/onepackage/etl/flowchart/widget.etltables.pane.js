/**
 * @class BI.ETLTablesPane
 * @extend BI.Widget
 * etl 画图面板
 */
BI.ETLTablesPane = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        return BI.extend(BI.ETLTablesPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-tables-pane"
        })
    },

    _init: function () {
        BI.ETLTablesPane.superclass._init.apply(this, arguments);
        var self = this, tables = this.options.tables;
        this.tItems = [];
        this._itemCreator(tables);
        BI.createWidget({
            type: "bi.branch_relation",
            element: this.element,
            items: this.tItems,
            centerOffset: -30
        });
    },

    _itemCreator: function (tables, pId) {
        var self = this, o = this.options;
        BI.each(tables, function (i, table) {
            var isFinal = BI.isNull(pId);
            var tableInfo = BI.extend(table, {
                isFinal: isFinal
            });

            var etlTableCombo = BI.createWidget({
                type: "bi.etl_table_combo",
                tableInfo: tableInfo
            });
            etlTableCombo.on(BI.ETLTableCombo.EVENT_CHANGE, function () {
                self.fireEvent(BI.ETLTablesPane.EVENT_CHANGE, BI.deepClone(table.id), etlTableCombo.getValue()[0], isFinal);
            });
            etlTableCombo.on(BI.ETLTableCombo.EVENT_CLICK_OPERATOR, function (t) {
                self.fireEvent(BI.ETLTablesPane.EVENT_CLICK_OPERATOR, BI.deepClone(t), isFinal);
            });
            self.tItems.push({
                pId: pId,
                id: table.id,
                el: etlTableCombo,
                width: etlTableCombo.getWidth(),
                height: etlTableCombo.getHeight()
            });
            if (BI.isNotNull(table.tables)) {
                self._itemCreator(table.tables, table.id);
            }
        })
    }
});
BI.ETLTablesPane.EVENT_CLICK_OPERATOR = "EVENT_CLICK_OPERATOR";
BI.ETLTablesPane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.etl_tables_pane", BI.ETLTablesPane);
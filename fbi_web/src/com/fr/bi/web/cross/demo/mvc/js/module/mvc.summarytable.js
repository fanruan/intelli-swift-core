SummaryTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SummaryTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-summary-table bi-mvc-layout"
        })
    },

    _init: function () {
        SummaryTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var table = BI.createWidget({
            type: "bi.summary_table",
            width: 600,
            height: 400,
            wId: BI.Utils.getAllWidgetIDs()[0]
        });
        table.populate();
        BI.createWidget({
            type: "bi.left",
            hgap: 200,
            vgap: 10,
            element: vessel,
            items: [table]
        })
    }
});

SummaryTableModel = BI.inherit(BI.Model, {});
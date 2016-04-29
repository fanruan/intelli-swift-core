SelectTableView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(SelectTableView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-select-table bi-mvc-layout"
        })
    },

    _init: function () {
        SelectTableView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        BI.createWidget({
            type: "bi.select_table_pane",
            element: vessel
        });
    }
});

SelectTableModel = BI.inherit(BI.Model, {});
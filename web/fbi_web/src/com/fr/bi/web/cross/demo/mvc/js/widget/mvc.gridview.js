Grid_View = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(Grid_View.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-grid-view bi-mvc-layout"
        })
    },

    _init: function () {
        Grid_View.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var items = [];
        var rowCount = 10000, columnCount = 100;
        for (var i = 0; i < rowCount; i++) {
            items[i] = [];
            for (var j = 0; j < columnCount; j++) {
                items[i][j] = {
                    type: "bi.label",
                    text: i + "-" + j
                }
            }
        }
        var grid = BI.createWidget({
            type: "bi.grid_view",
            items: items,
            scrollTop: 100,
            rowHeightGetter: function () {
                return 30;
            },
            columnWidthGetter: function () {
                return 100;
            }
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: {
                    type: "bi.grid",
                    columns: 1,
                    rows: 1,
                    items: [{
                        column: 0,
                        row: 0,
                        el: grid
                    }]
                },
                left: 10,
                right: 10,
                top: 10,
                bottom: 10
            }]
        })
    }
});

Grid_Model = BI.inherit(BI.Model, {});
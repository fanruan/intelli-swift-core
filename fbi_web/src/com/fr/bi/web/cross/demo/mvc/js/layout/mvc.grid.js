//grid布局
GridView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(GridView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-grid bi-mvc-layout"
        })
    },

    _init: function(){
        GridView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.grid",
            element: vessel,
            columns: 5,
            rows: 3,
            items: [{
                column: 0,
                row: 0,
                el: {
                    type: "bi.label",
                    text: "column-0, row-0",
                    cls: "layout-bg1"
                }
            }, {
                column: 1,
                row: 0,
                el: {
                    type: "bi.label",
                    text: "column-1, row-0",
                    cls: "layout-bg2"
                }
            }, {
                column: 2,
                row: 0,
                el: {
                    type: "bi.label",
                    text: "column-2, row-0",
                    cls: "layout-bg6"
                }
            }, {
                column: 3,
                row: 0,
                el: {
                    type: "bi.label",
                    text: "column-3, row-0",
                    cls: "layout-bg3"
                }
            }, {
                column: 4,
                row: 0,
                el: {
                    type: "bi.label",
                    text: "column-4, row-0",
                    cls: "layout-bg4"
                }
            }, {
                column: 0,
                row: 1,
                el: {
                    type: "bi.label",
                    text: "column-0, row-1",
                    cls: "layout-bg5"
                }
            }, {
                column: 1,
                row: 1,
                el: {
                    type: "bi.label",
                    text: "column-1, row-1",
                    cls: "layout-bg6"
                }
            }, {
                column: 2,
                row: 1,
                el: {
                    type: "bi.label",
                    text: "column-2, row-1",
                    cls: "layout-bg7"
                }
            }, {
                column: 3,
                row: 1,
                el: {
                    type: "bi.label",
                    text: "column-3, row-1",
                    cls: "layout-bg1"
                }
            }, {
                column: 4,
                row: 1,
                el: {
                    type: "bi.label",
                    text: "column-4, row-1",
                    cls: "layout-bg3"
                }
            }, {
                column: 0,
                row: 2,
                el: {
                    type: "bi.label",
                    text: "column-0, row-2",
                    cls: "layout-bg2"
                }
            }, {
                column: 1,
                row: 2,
                el: {
                    type: "bi.label",
                    text: "column-1, row-2",
                    cls: "layout-bg3"
                }
            }, {
                column: 2,
                row: 2,
                el: {
                    type: "bi.label",
                    text: "column-2, row-2",
                    cls: "layout-bg4"
                }
            }, {
                column: 3,
                row: 2,
                el: {
                    type: "bi.label",
                    text: "column-3, row-2",
                    cls: "layout-bg5"
                }
            }, {
                column: 4,
                row: 2,
                el: {
                    type: "bi.label",
                    text: "column-4, row-2",
                    cls: "layout-bg6"
                }
            }]
        })
    }
});

GridModel = BI.inherit(BI.Model, {
    _init: function(){
        GridModel.superclass._init.apply(this, arguments);
    }
});
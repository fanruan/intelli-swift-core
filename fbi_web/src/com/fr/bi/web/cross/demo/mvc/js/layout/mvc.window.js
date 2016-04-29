//window布局
WindowView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(WindowView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-window bi-mvc-layout"
        })
    },

    _init: function(){
        WindowView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.window",
            element: vessel,
            columns: 3,
            rows : 2,
            columnSize: [100, "fill", 200],
            rowSize: ['fill', 0.3],
            items : [
                [{
                    el : {
                        type : 'bi.label',
                        text : 'column-0, row-0',
                        cls: "layout-bg1"
                    }
                }, {
                    el : {
                        type : 'bi.label',
                        text : 'column-1, row-0',
                        cls: "layout-bg2"
                    }
                }, {
                    el : {
                        type : 'bi.label',
                        text : 'column-2, row-0',
                        cls: "layout-bg3"
                    }
                }], [{
                    el : {
                        type : 'bi.label',
                        text : 'column-0, row-1',
                        cls: "layout-bg5"
                    }
                }, {
                    el : {
                        type : 'bi.label',
                        text : 'column-1, row-1',
                        cls: "layout-bg6"
                    }
                }, {
                    el : {
                        type : 'bi.label',
                        text : 'column-2, row-1',
                        cls: "layout-bg7"
                    }
                }]
            ]
        })
    }
});

WindowModel = BI.inherit(BI.Model, {
    _init: function(){
        WindowModel.superclass._init.apply(this, arguments);
    }
});
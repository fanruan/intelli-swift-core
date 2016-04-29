//division布局
DivisionView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DivisionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-division bi-mvc-layout"
        })
    },

    _init: function(){
        DivisionView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.division",
            element: vessel,
            columns: 3,
            rows : 2,
            items : [
                {
                    column : 0,
                    row : 0,
                    width: 0.20,
                    height: 0.23,
                    el : {
                        type : 'bi.label',
                        text : 'column-0, row-0',
                        cls: "layout-bg1"
                    }
                }, {
                    column : 1,
                    row : 0,
                    width: 0.27,
                    height: 0.39,
                    el : {
                        type : 'bi.label',
                        text : 'column-1, row-0',
                        cls: "layout-bg2"
                    }
                }, {
                    column : 2,
                    row : 0,
                    width: 0.12,
                    height: 0.13,
                    el : {
                        type : 'bi.label',
                        text : 'column-2, row-0',
                        cls: "layout-bg3"
                    }
                }, {
                    column : 0,
                    row : 1,
                    width: 0.23,
                    height: 0.43,
                    el : {
                        type : 'bi.label',
                        text : 'column-0, row-1',
                        cls: "layout-bg5"
                    }
                }, {
                    column : 1,
                    row : 1,
                    width: 0.27,
                    height: 0.26,
                    el : {
                        type : 'bi.label',
                        text : 'column-1, row-1',
                        cls: "layout-bg6"
                    }
                }, {
                    column : 2,
                    row : 1,
                    width: 0.20,
                    height: 0.23,
                    el : {
                        type : 'bi.label',
                        text : 'column-2, row-1',
                        cls: "layout-bg7"
                    }
                }
            ]
        })
    }
});

DivisionModel = BI.inherit(BI.Model, {
    _init: function(){
        DivisionModel.superclass._init.apply(this, arguments);
    }
});
HTapeView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(HTapeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-h-tape bi-mvc-layout"
        })
    },

    _init: function(){
        HTapeView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.htape",
            element: vessel,
            items : [
                {
                    width: 100,
                    el : {
                        type : 'bi.label',
                        text : '1',
                        cls: "layout-bg1"
                    }
                }, {
                    width: 200,
                    el : {
                        type : 'bi.label',
                        text : '2',
                        cls: "layout-bg2"
                    }
                }, {
                    width: 'fill',
                    el : {
                        type : 'bi.label',
                        text : '3',
                        cls: "layout-bg3"
                    }
                }
            ]
        })
    }
});

HTapeModel = BI.inherit(BI.Model, {
    _init: function(){
        HTapeModel.superclass._init.apply(this, arguments);
    }
});
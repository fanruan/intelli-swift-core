VTapeView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(VTapeView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-v-tape bi-mvc-layout"
        })
    },

    _init: function(){
        VTapeView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            items : [
                {
                    height: 100,
                    el : {
                        type : 'bi.label',
                        text : '1',
                        cls: "layout-bg1"
                    }
                }, {
                    height: 200,
                    el : {
                        type : 'bi.label',
                        text : '2',
                        cls: "layout-bg2"
                    }
                }, {
                    height: 'fill',
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

VTapeModel = BI.inherit(BI.Model, {
    _init: function(){
        VTapeModel.superclass._init.apply(this, arguments);
    }
});
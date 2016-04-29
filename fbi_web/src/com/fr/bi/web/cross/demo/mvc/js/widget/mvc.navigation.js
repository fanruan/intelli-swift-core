NavigationView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(NavigationView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-navigation bi-mvc-layout"
        })
    },

    _init: function(){
        NavigationView.superclass._init.apply(this, arguments);
    },

    _createNav: function(v){
        return BI.createWidget({
            type: "bi.label",
            cls: "layout-bg" + BI.random(1, 8),
            text: "第" + v + "页"
        })
    },

    _render: function(vessel){
        var navigation = BI.createWidget({
            type: "bi.navigation",
            element: vessel,
            tab: {
                height: 30,
                items: [{
                    once: false,
                    text: "后退",
                    value: -1,
                    cls: "mvc-button layout-bg3"
                },{
                    once: false,
                    text: "前进",
                    value: 1,
                    cls: "mvc-button layout-bg4"
                }]
            },
            cardCreator: BI.bind(this._createNav, this)
        })
    }
});
NavigationModel = BI.inherit(BI.Model, {

});
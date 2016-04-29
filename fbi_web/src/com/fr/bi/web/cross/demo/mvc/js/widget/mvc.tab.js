TabView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(TabView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-tab bi-mvc-layout"
        })
    },

    _init: function(){
        TabView.superclass._init.apply(this, arguments);
    },

    _createTabs: function(v){
        switch (v){
            case 1:
                return BI.createWidget({
                    type: "bi.label",
                    cls: "layout-bg1",
                    text: "面板1"
                })
            case 2:
                return BI.createWidget({
                    type: "bi.label",
                    cls: "layout-bg2",
                    text: "面板2"
                })
        }
    },

    _render: function(vessel){
        this.tab = BI.createWidget({
            type: "bi.button_group",
            height: 30,
            items: [{
                text: "Tab1",
                value: 1,
                width: 50,
                cls: "mvc-button layout-bg3"
            },{
                text: "Tab2",
                value: 2,
                width: 50,
                cls: "mvc-button layout-bg4"
            }],
            layouts: [{
                type: "bi.center_adapt",
                items: [{
                    el: {
                        type: "bi.horizontal",
                        width: 100
                    }
                }]
            }]
        });

        var tab = BI.createWidget({
            direction: "custom",
            type: "bi.tab",
            element: vessel,
            tab: this.tab,
            cardCreator: BI.bind(this._createTabs, this)
        });
        BI.createWidget({
            type: "bi.absolute",
            element: vessel,
            items: [{
                el: this.tab,
                left: 200,
                top: 200
            }]
        })

        tab.setSelect(2);
    }
});

TabModel = BI.inherit(BI.Model, {

});
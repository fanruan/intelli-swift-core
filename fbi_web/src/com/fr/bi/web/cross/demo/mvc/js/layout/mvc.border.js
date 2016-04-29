//vertical布局
BorderView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(BorderView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-border bi-mvc-layout"
        })
    },

    _init: function(){
        BorderView.superclass._init.apply(this, arguments);
    },

    _createNorth: function(){
        return BI.createWidget({
            type: "bi.label",
            text: "North",
            cls: "layout-bg1",
            height: 30
        })
    },

    _createWest: function(){
        return BI.createWidget({
            type: "bi.center",
            cls: "layout-bg2",
            items:[{
                type: "bi.label",
                text: "West",
                whiteSpace: "normal"
            }]
        })
    },

    _createCenter: function(){
        return BI.createWidget({
            type: "bi.center",
            cls: "layout-bg3",
            items: [{
                type: "bi.label",
                text: "Center",
                whiteSpace: "normal"
            }]
        })
    },

    _createEast: function(){
        return BI.createWidget({
            type: "bi.center",
            cls: "layout-bg5",
            items: [{
                type: "bi.label",
                text: "East",
                whiteSpace: "normal"
            }]
        })
    },

    _createSouth: function(){
          return BI.createWidget({
              type: "bi.label",
              text: "South",
              cls: "layout-bg6",
              height: 50
          })
    },

    _render: function(vessel){
        BI.createWidget({
            element: vessel,
            type: "bi.border",
            cls: "",
            items: {
                north: {
                    el: this._createNorth(),
                    height: 30,
                    top: 20,
                    left: 20,
                    right: 20
                },
                south: {
                    el: this._createSouth(),
                    height: 50,
                    bottom: 20,
                    left: 20,
                    right: 20
                },
                west: {
                    el: this._createWest(),
                    width: 200,
                    left: 20
                },
                east: {
                    el: this._createEast(),
                    width: 300,
                    right: 20
                },
                center: this._createCenter()
            }
        })
    }

});

BorderModel = BI.inherit(BI.Model, {
    _defaultConfig: function(){
        return BI.extend(BorderModel.superclass._defaultConfig.apply(this, arguments), {
        })
    },

    _init: function(){
        BorderModel.superclass._init.apply(this, arguments);
    }
});
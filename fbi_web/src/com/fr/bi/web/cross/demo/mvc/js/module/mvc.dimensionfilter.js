DimensionFilterView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(DimensionFilterView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-dimension-filter"
        })
    },

    _init: function(){
        DimensionFilterView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var filterPane = this._createFilterPane();
        BI.createWidget({
            type: "bi.left",
            element: vessel,
            items: [{
                type: "bi.center",
                width: 580,
                height: 400,
                items: [filterPane]
            }, {
                type: "bi.button",
                text: "维度过滤——getValue()",
                height: 30,
                handler: function(){
                    BI.Msg.alert("维度过滤", JSON.stringify(filterPane.getValue()));
                }
            }],
            width: 600,
            vgap: 20,
            hgap: 10
        })
    },

    _createFilterPane: function(){
        var wIds = BI.Utils.getAllWidgetIDs();
        var dId = BI.Utils.getAllDimDimensionIDs(wIds[0])[0];
        return BI.createWidget({
            type: "bi.dimension_filter_pane",
            dId: dId
        })
    }
});

DimensionFilterModel = BI.inherit(BI.Model, {});
LoaderView = BI.inherit(BI.View, {
    _defaultConfig: function(){
        return BI.extend(LoaderView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-loader bi-mvc-layout"
        })
    },

    _init: function(){
        LoaderView.superclass._init.apply(this, arguments);
    },

    _render: function(vessel){
        var self = this;
        this.all = 0;
        BI.createWidget({
            type: "bi.loader",
            element: vessel,
            itemsCreator: function(options, populate){
                BI.requestAsync("bi_demo", "get_large_data", options, function(json){
                    self.all = json.all;
                    populate(BI.map(json.items, function(i, v){
                        return BI.extend(v, {
                            type: "bi.single_select_item",
                            height: 25
                        })
                    }))
                })
            },
            hasNext: function(option){
                return option.count< self.all;
            }
        })
    }
});

LoaderModel = BI.inherit(BI.Model, {

});
DynamicGroupTabView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(DynamicGroupTabView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-skip-pager bi-mvc-layout"
        })
    },

    _init: function () {
        DynamicGroupTabView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this;
        this.tab = BI.createWidget({
            type: "bi.dynamic_group_tab",
            cardCreator: function(v){
                return BI.createWidget({
                    type: "bi.center",
                    items: [{
                        type: "bi.label",
                        text: v,
                        value: v
                    }]
                });
            }
        });

        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            hgap: 200,
            vgap: 50,
            items: [{
                el: this.tab
            }]
        })
    }
});

DynamicGroupTabModel = BI.inherit(BI.Model, {});
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
            type: "bi.dynamic_group_tab"
        });

        BI.createWidget({
            type: "bi.vtape",
            element: vessel,
            hgap: 200,
            vgap: 50,
            items: [{
                el: this.tab
            }, {
                el: {
                    type: "bi.button",
                    value: "populate",
                    height: 30,
                    wudth: 200,
                    handler: function(){
                        self.tab.populate([]);
                    }
                },
                height: 30
            }]
        })
    },

    _addNewButton : function(value) {
        var item = {
            type:"bi.dynamic_group_tab_sheet_button",
            height: 29,
            width: 90,
            value : value,
            text : "Sheet" + this.tab.getAllButtons().length
        }
        var button = BI.createWidget(item);
        this.tab.addItems([button]);
    },
});

DynamicGroupTabModel = BI.inherit(BI.Model, {});
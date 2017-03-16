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
        var addButton = BI.createWidget({
            type:"bi.button",
            width: 90,
            level: 'ignore',
            text: BI.i18nText("BI-Add_sheet"),
            title: BI.i18nText("BI-Add_sheet")
        });
        addButton.on(BI.Button.EVENT_CHANGE, function(){
            self.tab.addItems([{
                type: "bi.button",
                text: BI.parseInt((Math.random() * 100) % 100)
            }]);
        });
        var mergeButton = BI.createWidget({
            type:"bi.button",
            width: 90,
            level: 'ignore',
            text: BI.i18nText("BI-merge_sheet"),
            title: BI.i18nText("BI-merge_sheet")
        });
        this.tab = BI.createWidget({
            type: "bi.dynamic_group_tab_button_group",
            items: [],
            frozenButtons: [addButton, mergeButton],
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
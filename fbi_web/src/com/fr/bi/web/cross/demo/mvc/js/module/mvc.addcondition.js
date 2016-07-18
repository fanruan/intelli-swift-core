AddConditionView = BI.inherit(BI.View, {
    _defaultConfig: function () {
        return BI.extend(AddConditionView.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-mvc-add-condition bi-mvc-layout"
        })
    },

    _init: function () {
        AddConditionView.superclass._init.apply(this, arguments);
    },

    _render: function (vessel) {
        var self = this, o = this.options;

        this.styleRadio = BI.createWidget({
            type: "bi.button_group",
            items: BI.createItems(BICst.CHART_SCALE_SETTING , {
                type: "bi.single_select_radio_item",
                width: 100,
                height: 60
            }),
            layouts: [{
                type: "bi.horizontal_adapt",
                height: 60
            }]
        });

        this.addConditionButton = BI.createWidget({
            type: "bi.button",
            text: BI.i18nText("BI-Add_Condition"),
            height: 30
        });

        this.addConditionButton.on(BI.Button.EVENT_CHANGE, function() {
           self.conditions.addItem()
        });

        this.conditions = BI.createWidget({
            type: "bi.chart_add_gradient_condition_group"
        });

        var interval = BI.createWidget({
            type: "bi.vtape",
            height: 60,
            cls: "single-line-setting",
            items: [{
                height: 60,
                el: {
                    type: "bi.left",
                    items: BI.createItems([{
                        type: "bi.center_adapt",
                        items: [this.styleRadio , this.addConditionButton]
                    }] , {
                        height: 60
                    }),
                    lgap: 10
                }
            } , {
                height: 240,
                el: {
                    type: "bi.vertical",
                    items: [this.conditions]
                }
            }]
        });

        var formula = BI.createWidget({
            type: "bi.custom_scale_trigger",
            height: 30,
            width: 120
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [interval , formula],
            hgap: 10
        });
    }

});

AddConditionModel = BI.inherit(BI.Model, {});

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

        this.conditions = BI.createWidget({
            type: "bi.chart_add_condition_group"
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
                height: 60,
                el: {
                    type: "bi.vertical",
                    items: [this.conditions]
                }
            }]
        });

        var intervalSetting = BI.createWidget({
            type: "bi.horizontal_adapt",
            cls: "single-line-setting",
            columnSize: [80],
            items: [{
                type: "bi.label",
                text: BI.i18nText("BI-Interval_Setting"),
                height: 60,
                textAlign: "left",
                lgap: 10,
                cls: "line-title"
            } , {
                type: "bi.left",
                items: BI.createItems([{
                    type: "bi.center_adapt",
                    items: [interval]
                }]),
                lgap: 10
            }]
        });

        BI.createWidget({
            type: "bi.vertical",
            element: vessel,
            items: [intervalSetting],
            hgap: 10
        });
    }

});

AddConditionModel = BI.inherit(BI.Model, {});

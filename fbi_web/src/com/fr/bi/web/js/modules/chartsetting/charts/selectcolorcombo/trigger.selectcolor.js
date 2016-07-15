/**
 *
 * @class BI.ChartSettingSelectColorTrigger
 * @extends BI.Trigger
 */
BI.ChartSettingSelectColorTrigger = BI.inherit(BI.Trigger, {

    _defaultConfig: function () {
        var conf = BI.ChartSettingSelectColorTrigger.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-chart-setting-select-color-trigger",
            height: 30,
            items: []
        })
    },

    _init: function () {
        BI.ChartSettingSelectColorTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var colorBlocks = BI.map(o.text, function(idx, v){
            var a = BI.createWidget({
                type: "bi.layout"
            });
            a.element.css("background-color", v);
            return a;
        });

        this.down = BI.createWidget({
            type: "bi.icon_button",
            disableSelected: true,
            cls: "icon-combo-down-icon trigger-triangle-font",
            width: 12,
            height: 8
        });

        this.colorContainer = BI.createWidget({
            type: "bi.button_group",
            items: colorBlocks,
            layouts: [{
                type: "bi.center",
                hgap: 1,
                vgap: 3
            }]
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.colorContainer,
                left: 0,
                right: 0,
                top: 0,
                bottom: 0
            }, {
                el: this.down,
                right: 0,
                bottom: 3
            }]
        })
    },

    populate: function (items) {
        this.options.items = items;
    },

    setValue: function (vals) {
        BI.ChartSettingSelectColorTrigger.superclass.setValue.apply(this, arguments);
        this.value = vals;
        var result = [];
        BI.each(this.options.items, function (i, item) {
            if (BI.isEqual(item.value, vals)) {
                result = BI.map(item.text, function(idx, v){
                    var a = BI.createWidget({
                        type: "bi.layout"
                    });
                    a.element.css("background-color", v);
                    return a;
                });
            }
        });
        this.colorContainer.populate(result);
    },

    getValue: function(){
        return this.value;
    }
});
BI.ChartSettingSelectColorTrigger.EVENT_CHANGE = "ChartSettingSelectColorTrigger.EVENT_CHANGE";
$.shortcut("bi.chart_setting_select_color_trigger", BI.ChartSettingSelectColorTrigger);
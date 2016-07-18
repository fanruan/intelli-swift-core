/**
 * @class BI.ChartSettingSelectColorItem
 * @extends BI.BasicButton
 */
BI.ChartSettingSelectColorItem = BI.inherit(BI.BasicButton, {

    _defaultConfig: function () {
        var conf = BI.ChartSettingSelectColorItem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-chart-setting-select-color-item",
            value: [],
            text: [],
            once: true,
            height: 50,
            width: 130
        })
    },
    _init: function () {
        BI.ChartSettingSelectColorItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        var colorBlocks = BI.map(o.text, function(idx, v){
            var a = BI.createWidget({
                type: "bi.layout"
            });
            a.element.css("background-color", v);
            return a;
        });

        BI.createWidget({
            type: "bi.button_group",
            element: this.element,
            items: colorBlocks,
            layouts: [{
                type: "bi.center",
                hgap: 1,
                vgap: 3
            }]
        })
    },

    doClick: function () {
        BI.ChartSettingSelectColorItem.superclass.doClick.apply(this, arguments);
        if (this.isValid()) {
            this.fireEvent(BI.ChartSettingSelectColorItem.EVENT_CHANGE, this.getValue(), this);
        }
    },

    doRedMark: function () {

    },

    unRedMark: function () {

    }
});
BI.ChartSettingSelectColorItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_setting_select_color_item", BI.ChartSettingSelectColorItem);
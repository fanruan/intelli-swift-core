/**
 * @class BI.ChartSettingWrapSelectColorItem
 * @extends BI.BasicButton
 */
BI.ChartSettingWrapSelectColorItem = BI.inherit(BI.Single, {

    _defaultConfig: function () {
        var conf = BI.ChartSettingWrapSelectColorItem.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: (conf.baseCls || "") + " bi-chart-setting-wrap-select-color-item",
            text: "",
            header: "",
            value: [],
            height: 50,
            width: 130
        })
    },
    _init: function () {
        BI.ChartSettingWrapSelectColorItem.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.item = BI.createWidget({
            type: "bi.chart_setting_select_color_item",
            value: o.value,
            text: o.text,
        });
        BI.createWidget({
            type: "bi.vtape",
            hgap: 2,
            element: this.element,
            items: [{
                type: "bi.label",
                textAlign: "left",
                text: o.header,
                lgap: 3
            }, {
                el: this.item,
                height: 30
            }]
        });

        this.item.on(BI.Controller.EVENT_CHANGE, function(){
            self.fireEvent(BI.Controller.EVENT_CHANGE, arguments);
        });

    },

    setSelected: function(v){
        this.item.setSelected(v);
    },

    isSelected: function(){
        return this.item.isSelected();
    },

    doRedMark: function () {

    },

    unRedMark: function () {

    }
});
BI.ChartSettingWrapSelectColorItem.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.chart_setting_wrap_select_color_item", BI.ChartSettingWrapSelectColorItem);
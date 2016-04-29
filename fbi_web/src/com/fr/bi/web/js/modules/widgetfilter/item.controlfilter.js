/**
 * Created by Young's on 2016/4/7.
 */
BI.ControlFilterItem = BI.inherit(BI.Widget, {
    _defaultConfig: function(){
        return BI.extend(BI.ControlFilterItem.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-control-filter-item"
        })
    },

    _init: function(){
        BI.ControlFilterItem.superclass._init.apply(this, arguments);
        var wId = this.options.wId;
        var widgetType = BI.Utils.getWidgetTypeByID(wId);
        var widgetName = BI.Utils.getWidgetNameByID(wId);
        var widgetIcon = "chart-string-font";
        switch (widgetType) {
            case BICst.Widget.STRING:
                widgetIcon = "chart-string-font";
                break;
            case BICst.Widget.NUMBER:
                widgetIcon = "chart-number-font";
                break;
            case BICst.Widget.DATE:
                widgetIcon = "chart-date-range-font";
                break;
            case BICst.Widget.YEAR:
                widgetIcon = "chart-year-font";
                break;
            case BICst.Widget.MONTH:
                widgetIcon = "chart-month-font";
                break;
            case BICst.Widget.QUARTER:
                widgetIcon = "chart-quarter-font";
                break;
            case BICst.Widget.YMD:
                widgetIcon = "chart-ymd-font";
                break;
        }

        BI.createWidget({
            type: "bi.left",
            element: this.element,
            items: [{
                type: "bi.center_adapt",
                cls: widgetIcon + " control-widget-font",
                items: [{
                    type: "bi.icon"
                }],
                width: 20,
                height: 30
            }, {
                type: "bi.label",
                text: widgetName,
                height: 30
            }, {
                type: "bi.label",
                text: this.options.text.toString(),
                height: 30
            }],
            hgap: 5,
            vgap: 5
        })
    }
});
$.shortcut("bi.control_filter_item", BI.ControlFilterItem);
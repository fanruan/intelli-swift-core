/**
 * @class BI.DragIconButton
 * @extends BI.Widget
 * 图标的button
 */
BI.DragIconButton = BI.inherit(BI.Widget, {
    _defaultConfig: function () {
        var conf = BI.DragIconButton.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-drag-icon-button",
            drag: BI.emptyFn,
            stop: BI.emptyFn
        })
    },

    _getDefaultWidgetNameByWidgetType: function (widgetType) {
        switch (widgetType) {
            case BICst.Widget.TABLE:
            case BICst.Widget.CROSS_TABLE:
            case BICst.Widget.COMPLEX_TABLE:
            case BICst.Widget.BAR:
            case BICst.Widget.ACCUMULATE_BAR:
            case BICst.Widget.PIE:
            case BICst.Widget.DASHBOARD:
            case BICst.Widget.AXIS:
            case BICst.Widget.MAP:
            case BICst.Widget.DOUGHNUT:
            case BICst.Widget.BUBBLE:
            case BICst.Widget.SCATTER:
            case BICst.Widget.RADAR:
                return BI.i18nText("BI-Statistical_Component");
            case BICst.Widget.DETAIL:
                return BI.i18nText("BI-Detail_Table");
            case BICst.Widget.STRING:
                return BI.i18nText("BI-Text_Control");
            case BICst.Widget.DATE:
                return BI.i18nText("BI-Date_Range_Control");
            case BICst.Widget.NUMBER:
                return BI.i18nText("BI-Number_Control");
            case BICst.Widget.QUERY:
                return BI.i18nText("BI-Query_Button");
            case BICst.Widget.YEAR:
                return BI.i18nText("BI-Year_Control");
            case BICst.Widget.QUARTER:
                return BI.i18nText("BI-Year_Quarter_Con");
            case BICst.Widget.MONTH:
                return BI.i18nText("BI-Year_Month_Con");
            case BICst.Widget.YMD:
                return BI.i18nText("BI-Date_Control");
            case BICst.Widget.TREE:
                return BI.i18nText("BI-Tree_Control");
            case BICst.Widget.RESET:
                return BI.i18nText("BI-Reset_Button");
        }
    },

    _init: function () {
        BI.DragIconButton.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.button = BI.createWidget({
            type: "bi.icon_button",
            element: this.element,
            height: o.height,
            width: o.width,
            text: o.text,
            value: o.value,
            cls: o.cls
        });
        var value = {
            type: o.value,
            name: this._getDefaultWidgetNameByWidgetType(o.value),
            bounds: {
                height:BICst.Widget.Heights[o.value],
                width: BICst.Widget.Widths[o.value]
            }
        };
        this.button.element.draggable({
            cursor: BICst.cursorUrl,
            cursorAt: {left: 0, top: 0},
            drag: function(e, ui){
                o.drag.apply(self, [value, ui.position]);
            },
            stop: function(e, ui){
                o.stop.apply(self, [value, ui.position]);
            },
            helper: o.helper
        });
    }
});
$.shortcut("bi.drag_icon_button", BI.DragIconButton);
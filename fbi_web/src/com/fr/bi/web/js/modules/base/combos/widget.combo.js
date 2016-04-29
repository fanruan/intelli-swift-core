/**
 * 统计组件上的下拉框
 *
 * Created by GUY on 2015/10/14.
 * @class BI.WidgetCombo
 * @extends BI.Widget
 */
BI.WidgetCombo = BI.inherit(BI.Widget, {

    _const: {
        widgetSetting: function (type) {
            switch (type) {
                case     BICst.Widget.TABLE:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.TABLE_SHOW:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS_SHOW;
                case     BICst.Widget.BAR:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.ACCUMULATE_BAR:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.PIE:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.DASHBOARD:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.AXIS:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.MAP:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.DETAIL:
                    return BICst.DETIAL_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.DOUGHNUT:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.BUBBLE :
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.SCATTER:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.RADAR:
                    return BICst.STATISTICS_WIDGET_SETCOMBO_ITEMS;
                case     BICst.Widget.DATE:
                    return BICst.TIME_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.STRING:
                    return BICst.STRING_TREE_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.NUMBER :
                    return BICst.NUMBER_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.QUERY :
                    return BICst.STRING_TREE_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.YEAR :
                    return BICst.TIME_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.QUARTER :
                    return BICst.TIME_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.MONTH:
                    return BICst.TIME_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.YMD :
                    return BICst.TIME_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.TREE :
                    return BICst.STRING_TREE_CONTROL_SETCOMBO_ITEMS;
                case     BICst.Widget.RESET:
                    break;
            }
        }
    },

    _defaultConfig: function () {
        var conf = BI.WidgetCombo.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            baseCls: "bi-widget-combo",
            widgetType: BICst.Widget.TABLE
        })
    },

    _init: function () {
        BI.WidgetCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.combo = BI.createWidget({
            type: "bi.combo",
            element: this.element,
            isNeedAdjustWidth: false,
            el: {
                type: "bi.icon_trigger",
                width: 32,
                height: 32
            },
            popup: {
                el: {
                    type: "bi.button_group",
                    chooseType: BI.ButtonGroup.CHOOSE_TYPE_NONE,
                    width: 200,
                    items: BI.createItems(this._const.widgetSetting(o.widgetType), {
                        type: "bi.icon_text_item",
                        cls: "bi-list-item-hover",
                        textHgap: 10,
                        height: 30
                    }),
                    layouts: [{
                        type: "bi.vertical"
                    }]
                }
            }
        });
        this.combo.on(BI.Combo.EVENT_CHANGE, function (v) {
            self.fireEvent(BI.WidgetCombo.EVENT_CHANGE, v);
            this.hideView();
        });
    },

    setValue: function (v) {
        this.combo.setValue(v);
    },

    getValue: function () {
        return this.combo.getValue();
    }
});
BI.WidgetCombo.EVENT_CHANGE = "WidgetCombo.EVENT_CHANGE";
$.shortcut('bi.widget_combo', BI.WidgetCombo);

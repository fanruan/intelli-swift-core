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
            case BICst.WIDGET.TABLE:
            case BICst.WIDGET.CROSS_TABLE:
            case BICst.WIDGET.COMPLEX_TABLE:
            case BICst.WIDGET.BAR:
            case BICst.WIDGET.ACCUMULATE_BAR:
            case BICst.WIDGET.PIE:
            case BICst.WIDGET.DASHBOARD:
            case BICst.WIDGET.AXIS:
            case BICst.WIDGET.MAP:
            case BICst.WIDGET.DONUT:
            case BICst.WIDGET.BUBBLE:
            case BICst.WIDGET.SCATTER:
            case BICst.WIDGET.RADAR:
                return BI.i18nText("BI-Statistical_Component");
            case BICst.WIDGET.DETAIL:
                return BI.i18nText("BI-Detail_Table");
            case BICst.WIDGET.STRING:
                return BI.i18nText("BI-Text_Control");
            case BICst.WIDGET.DATE:
                return BI.i18nText("BI-Date_Range_Control");
            case BICst.WIDGET.NUMBER:
                return BI.i18nText("BI-Number_Control");
            case BICst.WIDGET.QUERY:
                return BI.i18nText("BI-Query_Button");
            case BICst.WIDGET.YEAR:
                return BI.i18nText("BI-Year_Control");
            case BICst.WIDGET.QUARTER:
                return BI.i18nText("BI-Year_Quarter_Con");
            case BICst.WIDGET.MONTH:
                return BI.i18nText("BI-Year_Month_Con");
            case BICst.WIDGET.YMD:
                return BI.i18nText("BI-Date_Control");
            case BICst.WIDGET.TREE:
                return BI.i18nText("BI-Tree_Control");
            case BICst.WIDGET.RESET:
                return BI.i18nText("BI-Reset_Button");
            case BICst.WIDGET.GENERAL_QUERY:
                return BI.i18nText("BI-General_Query");
            default:
                return BI.i18nText("BI-Statistical_Component");
        }
    },

    _init: function () {
        BI.DragIconButton.superclass._init.apply(this, arguments);
        var o = this.options, self = this;
        this.button = BI.createWidget({
            type: "bi.icon_button",
            element: this.element,
            iconHeight: 24,
            iconWidth: 24,
            text: o.text,
            title: o.title,
            value: o.value,
            cls: o.cls
        });
        var value = {
            type: o.value,
            name: this._getDefaultWidgetNameByWidgetType(o.value)
        };
        this.button.element.draggable({
            cursor: BICst.cursorUrl,
            cursorAt: {left: 0, top: 0},
            drag: function (e, ui) {
                o.drag.apply(self, [{
                    height: BICst.WIDGET.Heights[o.value],
                    width: BICst.WIDGET.Widths[o.value]
                }, ui.position, value]);
            },
            stop: function (e, ui) {
                o.stop.apply(self, [{
                    height:BICst.WIDGET.Heights[o.value],
                    width: BICst.WIDGET.Widths[o.value]
                }, ui.position, value]);
            },
            helper: o.helper
        });

        BI.nextTick(function(){
            self._refreshButtonStatus();
        }, 500);
        BI.Broadcasts.on(BICst.BROADCAST.WIDGETS_PREFIX, function () {
            self._refreshButtonStatus();
        });
    },

    _refreshButtonStatus: function(){
        var self = this, o = this.options;
        var allWIds = BI.Utils.getAllWidgetIDs();
        if(this._isWidget(o.value)) {
            if (!BI.Utils.supportMultiStatisticsWidget()) {
                var hasWidget = BI.some(allWIds, function (j, wId) {
                    return self._isWidget(BI.Utils.getWidgetTypeByID(wId));
                });
                self.setEnable(!hasWidget);
                if(hasWidget) {
                    self.button.setWarningTitle(BI.i18nText("BI-Only_Supports_One_Component"));
                }
            }
        }
        if(this._isControl(o.value)) {
            if (!BI.Utils.supportSimpleControl()) {
                self.setEnable(false);
                self.button.setWarningTitle(BI.i18nText("BI-License_Not_Support_Control"));
                return;
            }
            if(o.value === BICst.WIDGET.GENERAL_QUERY && !BI.Utils.supportGeneralControl()) {
                self.setEnable(false);
                self.button.setWarningTitle(BI.i18nText("BI-License_Not_Support_Control"));
                return;
            }
            //通用查询、查询、重置 只能有一个
            if (this._isTheQuerys(o.value)) {
                var found = BI.some(allWIds, function (j, wId) {
                    return o.value === BI.Utils.getWidgetTypeByID(wId);
                });
                self.setEnable(!found);
                if(found) {
                    self.button.setWarningTitle(BI.i18nText("BI-The_Control_Has_Exist"));
                }
            }
        }
    },

    _isWidget: function(type) {
        return type < BICst.WIDGET.STRING || type === BICst.WIDGET.CONTENT ||
            type === BICst.WIDGET.WEB || type === BICst.WIDGET.IMAGE;
    },

    _isControl: function(type) {
        return (type >= BICst.WIDGET.STRING && type <= BICst.WIDGET.RESET) ||
            type === BICst.WIDGET.GENERAL_QUERY;
    },

    _isTheQuerys: function(type) {
        return type === BICst.WIDGET.GENERAL_QUERY ||
            type === BICst.WIDGET.QUERY ||
            type === BICst.WIDGET.RESET;
    },

    setEnable: function(v) {
        BI.DragIconButton.superclass.setEnable.apply(this, arguments);
        this.button.setEnable(v);
        try {
            this.button.element.draggable(v ? "enable" : "disable");
        } catch (e) {

        }
    }
});
$.shortcut("bi.drag_icon_button", BI.DragIconButton);
/**
 * 多路径设置
 *
 * @class BI.MultiDateParamPane
 * @extends BI.Widget
 */

BI.MultiDateParamPane = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.MultiDateParamPane.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-multi-date-param-pane"
        });
    },

    _init: function () {
        BI.MultiDateParamPane.superclass._init.apply(this, arguments);
        var self = this, o = this.options;

        this.tree = BI.createWidget({
            type: "bi.single_tree_combo",
            items: self._getDateWidgetStructure(),
            width: 200
        });

        this.yearParam = BI.createWidget({
            type: "bi.param_item"
        });

        BI.createWidget({
            type: "bi.vertical",
            hgap: 10,
            element: this.element,
            items: [{
                type: "bi.center_adapt",
                height: 30,
                items: [{
                    type: "bi.label",
                    text: BI.i18nText("BI-Relative")
                }, this.tree, {
                    type: "bi.label",
                    text: BI.i18nText("BI-De")
                }],
                tgap:5,
                bgap: 50
            }, this.yearParam]
        })
    },

    _getDateWidgetStructure: function(){
        var targetWidgetType = [BICst.WIDGET.DATE, BICst.WIDGET.YMD];
        var targetWidgetIds = BI.filter(BI.Utils.getAllWidgetIDs(), function(i, id){
            return BI.contains(targetWidgetType, BI.Utils.getWidgetTypeByID(id));
        });
        var widgetItems = [];
        BI.each(targetWidgetIds, function(idx, w){
            var wType = BI.Utils.getWidgetTypeByID(w);
            widgetItems.push({
                id: w,
                text: BI.Utils.getWidgetNameByID(w),
                title: BI.Utils.getWidgetNameByID(w),
                value: w,
                isParent: wType === BICst.WIDGET.DATE,
                open: false
            });
            if(wType === BICst.WIDGET.DATE){
                widgetItems.push({
                    id: BI.UUID(),
                    pId: w,
                    text: BI.i18nText("BI-Start_Time"),
                    title: BI.i18nText("BI-Start_Time"),
                    value: {
                        wId: w,
                        startOrEnd: BI.MultiDateParamPane.start
                    }
                });
                widgetItems.push({
                    id: BI.UUID(),
                    pId: w,
                    text: BI.i18nText("BI-End_Time"),
                    title: BI.i18nText("BI-End_Time"),
                    value: {
                        wId: w,
                        startOrEnd: BI.MultiDateParamPane.end
                    }
                });
            }
        });
        return BI.sortBy(widgetItems, "text");
    },

    _assertValue: function(v){
        v = v || {};
        v.widgetInfo = v.widgetInfo || {};
        return v;
    },

    setValue: function (v) {
        v = this._assertValue(v);
        this.tree.setValue([v.widgetInfo]);
        this.yearParam.setValue(v.offset);
    },

    getValue: function () {
        return {
            widgetInfo: this.tree.getValue(),
            offset: this.yearParam.getValue()
        };
    }
});
BI.extend(BI.MultiDateParamPane, {
    start: 0,
    end: 1
});
BI.MultiDateParamPane.EVENT_CHANGE = "MultiDateParamPane.EVENT_CHANGE";
$.shortcut('bi.multi_date_param_pane', BI.MultiDateParamPane);
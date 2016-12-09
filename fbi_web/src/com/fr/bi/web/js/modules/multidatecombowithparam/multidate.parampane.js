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

        var items = self._getDateWidgetStructure();
        var triggerItems = BI.deepClone(items);
        BI.each(triggerItems, function(idx, item){
            if(BI.has(item, "pId")){
                item.text = item.title = BI.Utils.getWidgetNameByID(item.pId) + "(" + item.text +")"
            }
        });
        this.tree = BI.createWidget({
            type: "bi.single_tree_combo",
            trigger: {
                type: "bi.single_tree_trigger",
                items: triggerItems
            },
            items: items,
            width: 200
        });

        this.tree.on(BI.SingleTreeCombo.EVENT_CHANGE,function(){
            self.fireEvent(BI.MultiDateParamPane.EVENT_CHANGE);
        });

        this.yearParam = BI.createWidget({
            type: "bi.param_item"
        });

        this.yearParam.on(BI.ParamItem.EVENT_CHANGE, function(){
            self.fireEvent(BI.MultiDateParamPane.EVENT_CHANGE);
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
                value: {
                    wId: w
                },
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

    getCalculationValue: function(){
        var value = this.getValue();
        var widgetInfo = value.widgetInfo, offset = value.offset;
        if (BI.isNull(widgetInfo) || BI.isNull(offset)) {
            return;
        }
        var paramdate;
        var wWid = widgetInfo.wId, se = widgetInfo.startOrEnd;
        if (BI.isNotNull(wWid) && BI.isNotNull(se)) {
            var wWValue = BI.Utils.getWidgetValueByID(wWid);
            if (BI.isNull(wWValue) || BI.isEmptyObject(wWValue)) {
                return;
            }
            if (se === BI.MultiDateParamPane.start && BI.isNotNull(wWValue.start)) {
                paramdate = this.parseComplexDateCommon(wWValue.start);
            }
            if (se === BI.MultiDateParamPane.end && BI.isNotNull(wWValue.end)) {
                paramdate = this.parseComplexDateCommon(wWValue.end);
            }
        } else {
            if (BI.isNull(widgetInfo.wId) || BI.isNull(BI.Utils.getWidgetValueByID(widgetInfo.wId))) {
                return;
            }
            paramdate = this.parseComplexDateCommon(BI.Utils.getWidgetValueByID(widgetInfo.wId));
        }
        if(BI.isNotNull(paramdate)){
            return this.parseComplexDateCommon(offset, new Date(paramdate))
        }
    },

    parseComplexDateCommon: function (v, consultedDate) {
        var type = v.type, value = v.value;
        var date = BI.isNull(consultedDate) ? new Date() : consultedDate;
        var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
        if (BI.isNull(type) && isValidDate(v)) {
            return new Date(v.year, v.month, v.day);
        }
        switch (type) {
            case BICst.MULTI_DATE_YEAR_PREV:
                return new Date(currY - 1 * value, currM, currD);
            case BICst.MULTI_DATE_YEAR_AFTER:
                return new Date(currY + 1 * value, currM, currD);
            case BICst.MULTI_DATE_YEAR_BEGIN:
                return new Date(currY, 0, 1);
            case BICst.MULTI_DATE_YEAR_END:
                return new Date(currY, 11, 31);
            case BICst.MULTI_DATE_MONTH_PREV:
                return new Date().getBeforeMultiMonth(value);
            case BICst.MULTI_DATE_MONTH_AFTER:
                return new Date().getAfterMultiMonth(value);
            case BICst.MULTI_DATE_MONTH_BEGIN:
                return new Date(currY, currM, 1);
            case BICst.MULTI_DATE_MONTH_END:
                return new Date(currY, currM, (date.getLastDateOfMonth()).getDate());
            case BICst.MULTI_DATE_QUARTER_PREV:
                return new Date().getBeforeMulQuarter(value);
            case BICst.MULTI_DATE_QUARTER_AFTER:
                return new Date().getAfterMulQuarter(value);
            case BICst.MULTI_DATE_QUARTER_BEGIN:
                return new Date().getQuarterStartDate();
            case BICst.MULTI_DATE_QUARTER_END:
                return BI.Utils.getQuarterEndDate();
            case BICst.MULTI_DATE_WEEK_PREV:
                return date.getOffsetDate(-7 * value);
            case BICst.MULTI_DATE_WEEK_AFTER:
                return date.getOffsetDate(7 * value);
            case BICst.MULTI_DATE_DAY_PREV:
                return date.getOffsetDate(-1 * value);
            case BICst.MULTI_DATE_DAY_AFTER:
                return date.getOffsetDate(1 * value);
            case BICst.MULTI_DATE_DAY_TODAY:
                return date;
            case BICst.MULTI_DATE_CALENDAR:
                return new Date(value.year, value.month, value.day);
            default:
                break;
        }

        function isValidDate(v){
            return BI.isNotNull(v.year) && BI.isNotNull(v.month) && BI.isNotNull(v.day);
        }
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
            widgetInfo: this.tree.getValue()[0],
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
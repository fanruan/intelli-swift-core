/**
 * Created by Young's on 2016/4/14.
 */
BI.WidgetFilterModel = BI.inherit(FR.OB, {
    _init: function(){
        BI.WidgetFilterModel.superclass._init.apply(this, arguments);

    },

    _parseComplexDate: function(v) {
        var type = v.type, value = v.value;
        var date = new Date();
        var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
        var tool = new BI.MultiDateParamTrigger();
        if(BI.isNull(type) && BI.isNotNull(v.year)) {
            return new Date(v.year, v.month, v.day);
        }
        switch (type) {
            case BICst.MULTI_DATE_YEAR_PREV:
                return new Date(currY - 1 * value, currM, currD);
            case BICst.MULTI_DATE_YEAR_AFTER:
                return new Date(currY + 1 * value, currM, currD);
            case BICst.MULTI_DATE_YEAR_BEGIN:
                return new Date(currY, 1, 1);
            case BICst.MULTI_DATE_YEAR_END:
                return new Date(currY, 11, 31);

            case BICst.MULTI_DATE_MONTH_PREV:
                return tool._getBeforeMultiMonth(value);
            case BICst.MULTI_DATE_MONTH_AFTER:
                return tool._getAfterMultiMonth(value);
            case BICst.MULTI_DATE_MONTH_BEGIN:
                return new Date(currY, currM, 1);
            case BICst.MULTI_DATE_MONTH_END:
                return new Date(currY, currM, (date.getLastDateOfMonth()).getDate());

            case BICst.MULTI_DATE_QUARTER_PREV:
                return tool._getBeforeMulQuarter(value);
            case BICst.MULTI_DATE_QUARTER_AFTER:
                return tool._getAfterMulQuarter(value);
            case BICst.MULTI_DATE_QUARTER_BEGIN:
                return tool._getQuarterStartDate();
            case BICst.MULTI_DATE_QUARTER_END:
                return tool._getQuarterEndDate();

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

            case BICst.MULTI_DATE_PARAM:
                var wWid = value.wId, se = value.startOrEnd;
                if(BI.isNotNull(wWid) && BI.isNotNull(se)) {
                    var wWValue = BI.Utils.getWidgetValueByID(wWid);
                    if(se === BI.MultiDateParamPane.start && BI.isNotNull(wWValue.start)) {
                        return new Date(wWValue.start.year, wWValue.start.month, wWValue.start.day);
                    } else {
                        return new Date(wWValue.end.year, wWValue.end.month, wWValue.end.day);
                    }
                } else {
                    if(BI.isNotNull(value.year) && BI.isNotNull(value.month) && BI.isNotNull(value.day)) {
                        return new Date(value.year, value.month, value.day);
                    }
                }
                break;
            case BICst.MULTI_DATE_CALENDAR:
                return new Date(value.year, value.month, value.day);

        }
    },

    parseTargetFilter: function(targetId, filter) {
        var self = this;
        if(filter.filter_type === BICst.FILTER_TYPE.AND || filter.filter_type === BICst.FILTER_TYPE.OR) {
            var children = [];
            BI.each(filter.filter_value, function(i, value){
                children.push(self.parseTargetFilter(targetId, value));
            });
            return {
                id: BI.UUID(),
                value: filter.filter_type,
                children: children
            };
        } else {
            return {
                id: BI.UUID(),
                type: "bi.target_filter_item",
                tId: targetId,
                filter: filter
            }
        }
    },

    isEmptyDrillById: function(wId){
        var drills = BI.Utils.getDrillByID(wId);
        var isEmpty = true;
        BI.some(drills, function(id, drill){
            if( BI.isNotEmptyArray(drill)) {
                isEmpty = false;
                return true;
            }
        });
        return isEmpty;
    },

    getControlWidgetValueTextByID: function(wid) {
        var widgetValue = BI.Utils.getWidgetValueByID(wid);
        var widgetType = BI.Utils.getWidgetTypeByID(wid);
        var text = "";
        if(BI.isNull(widgetValue)) {
            return text;
        }
        switch (widgetType) {
            case BICst.Widget.STRING:
                if(widgetValue.type === BI.Selection.Multi) {
                    text = BI.i18nText("BI-In") + " " +  widgetValue.value;
                } else if(widgetValue.type === BI.Selection.All) {
                    text = BI.i18nText("BI-Not_In") + " " + widgetValue.value;
                }
                return text;
            case BICst.Widget.NUMBER:
                return this.getNumberRangeText(widgetValue);
            case BICst.Widget.DATE:
                return this.getDateRangeText(widgetValue);
            case BICst.Widget.MONTH:
                var year = widgetValue.year, month = widgetValue.month;
                if(BI.isNumeric(year) && BI.isNumeric(month)) {
                    text = year + "/" + (month + 1);
                } else if (BI.isNumeric(year)) {
                    text = year;
                } else if(BI.isNumeric(month)) {
                    text = month + 1;
                }
                return text;
            case BICst.Widget.QUARTER:
                var year = widgetValue.year, quarter = widgetValue.quarter;
                if(BI.isNumeric(year) && BI.isNumeric(quarter)) {
                    text = year + " " + BI.i18nText("BI-Di") + quarter + BI.i18nText("BI-Quarter");
                } else if (BI.isNumeric(year)) {
                    text = year;
                } else if(BI.isNumeric(quarter)) {
                    text = BI.i18nText("BI-Di") + quarter + BI.i18nText("BI-Quarter");
                }
                return text;
            case BICst.Widget.YEAR:
                return widgetValue;
            case BICst.Widget.YMD:
                if(BI.isNotNull(widgetValue)) {
                    var date = this._parseComplexDate(widgetValue);
                    text = date.getFullYear() + "/" + (date.getMonth() + 1) + "/" + date.getDate();
                }
                return text;
        }
    },

    getNumberRangeText: function( filterValue) {
        var text = "";
        var closemin = filterValue.closemin, closemax = filterValue.closemax, min = filterValue.min, max = filterValue.max;
        if(BI.isNotNull(min) && BI.isNotEmptyString(min) && BI.isNotNull(max) && BI.isNotEmptyString(max)) {
            text = min + (closemin ? "<=" : "<") + BI.i18nText("BI-Value") + (closemax ? "<=" : "<") + max;
        } else if(BI.isNotNull(min) && BI.isNotEmptyString(min)) {
            text = min + (closemin ? "<=" : "<") + BI.i18nText("BI-Value");
        } else if(BI.isNotNull(max) && BI.isNotEmptyString(max)) {
            text = BI.i18nText("BI-Value") + (closemax ? "<=" : "<") + max;
        }
        return text;
    },

    getDateRangeText: function(filterValue) {
        var start = filterValue.start, end = filterValue.end;
        if(BI.isNotNull(start) || BI.isNotNull(end)){
            return (BI.isNotNull(start) ? (start.year + "/" + (start.month + 1) + "/" + start.day) : "")
                + "-" + (BI.isNotNull(end) ? (end.year + "/" + (end.month + 1) + "/" + end.day) : "");
        } else {
            return "";
        }
    }
});

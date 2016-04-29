/**
 * 普通控件
 *
 * @class BI.ParamPopupView
 * @extends BI.Widget
 * @abstract
 */
BI.ParamPopupView = BI.inherit(BI.Widget, {

    _defaultConfig: function () {
        return BI.extend(BI.ParamPopupView.superclass._defaultConfig.apply(this, arguments), {});
    },

    dateConfig: function(){

    },

    _init: function () {
        BI.ParamPopupView.superclass._init.apply(this, arguments);
        var self = this;

        this.radioGroup = BI.createWidget({
            type: "bi.button_group",
            chooseType: 0,
            items: this.dateConfig(),
            layouts: [{
                type: "bi.vertical",
                items: [{
                    type: "bi.vertical",
                    vgap: 5
                }],
                vgap: 5,
                hgap: 5
            }]
        });

        this.radioGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
            self.setValue(self.getValue());
            self.fireEvent(BI.ParamPopupView.EVENT_CHANGE);
        });
        this.popup = BI.createWidget({
            type: 'bi.multi_popup_view',
            element: this.element,
            el: this.radioGroup,
            minWidth: 310,
            stopPropagation: false
        });

        this.popup.on(BI.MultiPopupView.EVENT_CLICK_TOOLBAR_BUTTON, function () {
            self.fireEvent(BI.ParamPopupView.EVENT_CONFIRM);
        });

    },

    _getQuarterStartMonth: function (date) {
        var quarterStartMonth = 0;
        var nowMonth = date.getMonth();
        if (nowMonth < 3) {
            quarterStartMonth = 0;
        }
        if (2 < nowMonth && nowMonth < 6) {
            quarterStartMonth = 3;
        }
        if (5 < nowMonth && nowMonth < 9) {
            quarterStartMonth = 6;
        }
        if (nowMonth > 8) {
            quarterStartMonth = 9;
        }
        return quarterStartMonth;
    },

    //获得指定日期所在季度的起始日期
    _getQuarterStartDate: function (date) {
        return new Date(date.getFullYear(), this._getQuarterStartMonth(date), 1);
    },

    //获得指定日期所在季度的结束日期
    _getQuarterEndDate: function (date) {
        var quarterEndMonth = this._getQuarterStartMonth(date) + 2;
        return new Date(date.getFullYear(), quarterEndMonth, date.getMonthDays(quarterEndMonth));
    },

    //指定日期n个月之前或之后的日期
    _getOffsetMonth: function (date, n) {
        var dt = new Date(date);
        dt.setMonth(dt.getMonth() + parseInt(n));
        return dt;
    },

    //指定日期n个季度之前或之后的日期
    _getOffsetQuarter: function (date, n) {
        var dt = new Date(date);
        dt.setMonth(dt.getMonth() + n * 3);
        return dt;
    },

    setValue: function (v) {
        this.radioGroup.setValue(v.type);
        BI.each(this.radioGroup.getAllButtons(), function (i, button) {
            if (button.isSelected()) {
                button.setEnable(true);
                button.setInputValue(v.value);
            } else {
                button.setEnable(false);
            }
        });
    },

    getValue: function () {
        var button = this.radioGroup.getSelectedButtons()[0];
        var type = button.getValue(), value = button.getInputValue();
        return {
            type: type,
            value: value
        }
    },

    getCalculationValue: function () {
        var valueObject = this.getValue();
        var type = valueObject.type, value = valueObject.value;
        var fPrevOrAfter = value.foffset === 0 ? -1 : 1;
        var sPrevOrAfter = value.soffset === 0 ? -1 : 1;
        var start, end;
        start = end = new Date();
        var ydate = new Date((new Date().getFullYear() + fPrevOrAfter * value.fvalue), new Date().getMonth(), new Date().getDate());
        switch (type) {
            case BICst.YEAR:
                start = new Date((new Date().getFullYear() + fPrevOrAfter * value.fvalue), 0, 1);
                end = new Date(start.getFullYear(), 11, 31);
                break;
            case BICst.YEAR_QUARTER:
                ydate = this._getOffsetQuarter(ydate, sPrevOrAfter * value.svalue);
                start = this._getQuarterStartDate(ydate);
                end = this._getQuarterEndDate(ydate);
                break;
            case BICst.YEAR_MONTH:
                ydate = this._getOffsetMonth(ydate, sPrevOrAfter * value.svalue);
                start = new Date(ydate.getFullYear(), ydate.getMonth(), 1);
                end  = new Date(ydate.getFullYear(), ydate.getMonth(), (ydate.getLastDateOfMonth()).getDate());
                break;
            case BICst.YEAR_WEEK:
                start = ydate.getOffsetDate(sPrevOrAfter * 7 * value.svalue);
                end = start.getOffsetDate(7);
                break;
            case BICst.YEAR_DAY:
                start = ydate.getOffsetDate(sPrevOrAfter * value.svalue);
                end = start.getOffsetDate(1);
                break;
            case BICst.MONTH_WEEK:
                var mdate = this._getOffsetMonth(new Date(), fPrevOrAfter * value.fvalue);
                start = mdate.getOffsetDate(sPrevOrAfter * 7 * value.svalue);
                end = start.getOffsetDate(7);
                break;
            case BICst.MONTH_DAY:
                var mdate = this._getOffsetMonth(new Date(), fPrevOrAfter * value.fvalue);
                start = mdate.getOffsetDate(sPrevOrAfter * value.svalue);
                end = start.getOffsetDate(1);
                break;
        }
        return {
            start: start,
            end: end
        };
    },

    resetWidth: function(w){
        this.popup.resetWidth(w);
    },

    resetHeight: function(h){
        this.popup.resetHeight(h);
    }
});
BI.ParamPopupView.EVENT_CHANGE = "EVENT_CHANGE";
BI.ParamPopupView.EVENT_CONFIRM = "EVENT_CONFIRM";
$.shortcut("bi.param_popup_view", BI.ParamPopupView);
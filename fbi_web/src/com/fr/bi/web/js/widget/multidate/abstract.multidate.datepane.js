(function ($) {
    /**
     * 普通控件
     *
     * @class BI.MultiDateCard
     * @extends BI.Widget
     * @abstract
     */
    BI.MultiDateCard = BI.inherit(BI.Widget, {

        constants: {
            lgap: 80,
            itemHeight: 35,
            defaultEditorValue: "1"
        },

        _defaultConfig: function () {
            return $.extend(BI.MultiDateCard.superclass._defaultConfig.apply(this, arguments), {});
        },

        dateConfig: function () {

        },

        defaultSelectedItem: function () {

        },

        _init: function () {
            BI.MultiDateCard.superclass._init.apply(this, arguments);
            var self = this, opts = this.options;

            this.label = BI.createWidget({
                type: 'bi.label',
                height: this.constants.itemHeight,
                textAlign: "left",
                text: BI.i18nText("BI-Multi_Date_Relative_Current_Time"),
                cls: 'bi-multidate-inner-label'
            });
            this.radioGroup = BI.createWidget({
                type: "bi.button_group",
                chooseType: 0,
                items: BI.createItems(this.dateConfig(), {
                    type: 'bi.multidate_segment',
                    height: this.constants.itemHeight
                }),
                layouts: [{
                    type: "bi.vertical"
                }]
            });

            this.radioGroup.on(BI.Controller.EVENT_CHANGE, function (type) {
                if (type === BI.Events.CONFIRM) {
                    self.fireEvent(BI.MultiDateCard.EVENT_CHANGE);
                }
            });
            this.radioGroup.on(BI.ButtonGroup.EVENT_CHANGE, function () {
                self.setValue(self.getValue());
                self.fireEvent(BI.MultiDateCard.EVENT_CHANGE);
            });
            BI.createWidget({
                element: this.element,
                type: 'bi.center_adapt',
                lgap: this.constants.lgap,
                items: [{
                    type: 'bi.vertical',
                    items: [this.label, this.radioGroup]
                }]
            });
        },

        //n个月之后的日期
        _getAfterMultiMonth: function (n) {
            var dt = new Date();
            dt.setMonth(dt.getMonth() + parseInt(n));
            return dt;
        },

        //n个月之前的日期
        _getBeforeMultiMonth: function (n) {
            var dt = new Date();
            dt.setMonth(dt.getMonth() - parseInt(n));
            return dt;
        },

        //n个季度之后的日期
        _getAfterMulQuarter: function (n) {
            var dt = new Date();
            dt.setMonth(dt.getMonth() + n * 3);
            return dt;
        },

        //n个季度之前的日期
        _getBeforeMulQuarter: function (n) {
            var dt = new Date();
            dt.setMonth(dt.getMonth() - n * 3);
            return dt;
        },

        //得到本季度的起始月份
        _getQuarterStartMonth: function () {
            var quarterStartMonth = 0;
            var nowMonth = new Date().getMonth();
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

        //获得本季度的起始日期
        _getQuarterStartDate: function () {
            return new Date(new Date().getFullYear(), this._getQuarterStartMonth(), 1);
        },

        //得到本季度的结束日期
        _getQuarterEndDate: function () {
            var quarterEndMonth = this._getQuarterStartMonth() + 2;
            return new Date(new Date().getFullYear(), quarterEndMonth, new Date().getMonthDays(quarterEndMonth));
        },

        getValue: function () {
            var button = this.radioGroup.getSelectedButtons()[0];
            var type = button.getValue(), value = button.getInputValue();
            return {
                type: type,
                value: value
            }
        },

        _isTypeAvaliable: function (type) {
            var res = false;
            BI.find(this.dateConfig(), function (i, item) {
                if (item.value === type) {
                    res = true;
                    return true;
                }
            });
            return res;
        },

        setValue: function (v) {
            var self = this;
            if (BI.isNotNull(v) && this._isTypeAvaliable(v.type)) {
                this.radioGroup.setValue(v.type);
                BI.each(this.radioGroup.getAllButtons(), function (i, button) {
                    if (button.isEditorExist() === true && button.isSelected()) {
                        button.setInputValue(v.value);
                        button.setEnable(true);
                    } else {
                        button.setInputValue(self.constants.defaultEditorValue);
                        button.setEnable(false);
                    }
                });
            } else {
                this.radioGroup.setValue(this.defaultSelectedItem());
                BI.each(this.radioGroup.getAllButtons(), function (i, button) {
                    button.setInputValue(self.constants.defaultEditorValue);
                    if (button.isEditorExist() === true && button.isSelected()) {
                        button.setEnable(true);
                    } else {
                        button.setEnable(false);
                    }
                });
            }
        },

        getCalculationValue: function () {
            var valueObject = this.getValue();
            var type = valueObject.type, value = valueObject.value;
            switch (type) {
                case BICst.MULTI_DATE_DAY_PREV:
                    return new Date().getOffsetDate(-1 * value);
                case BICst.MULTI_DATE_DAY_AFTER:
                    return new Date().getOffsetDate(value);
                case BICst.MULTI_DATE_DAY_TODAY:
                    return new Date();
                case BICst.MULTI_DATE_MONTH_PREV:
                    return this._getBeforeMultiMonth(value);
                case BICst.MULTI_DATE_MONTH_AFTER:
                    return this._getAfterMultiMonth(value);
                case BICst.MULTI_DATE_MONTH_BEGIN:
                    return new Date(new Date().getFullYear(), new Date().getMonth(), 1);
                case BICst.MULTI_DATE_MONTH_END:
                    return new Date(new Date().getFullYear(), new Date().getMonth(), (new Date().getLastDateOfMonth()).getDate());
                case BICst.MULTI_DATE_QUARTER_PREV:
                    return this._getBeforeMulQuarter(value);
                case BICst.MULTI_DATE_QUARTER_AFTER:
                    return this._getAfterMulQuarter(value);
                case BICst.MULTI_DATE_QUARTER_BEGIN:
                    return this._getQuarterStartDate();
                case BICst.MULTI_DATE_QUARTER_END:
                    return this._getQuarterEndDate();
                case BICst.MULTI_DATE_WEEK_PREV:
                    return new Date().getOffsetDate(-7 * value);
                case BICst.MULTI_DATE_WEEK_AFTER:
                    return new Date().getOffsetDate(7 * value);
                case BICst.MULTI_DATE_YEAR_PREV:
                    return new Date((new Date().getFullYear() - 1 * value), new Date().getMonth(), new Date().getDate());
                case BICst.MULTI_DATE_YEAR_AFTER:
                    return new Date((new Date().getFullYear() + 1 * value), new Date().getMonth(), new Date().getDate());
                case BICst.MULTI_DATE_YEAR_BEGIN:
                    return new Date(new Date().getFullYear(), 0, 1);
                case BICst.MULTI_DATE_YEAR_END:
                    return new Date(new Date().getFullYear(), 11, 31);
            }
        }
    });
    BI.MultiDateCard.EVENT_CHANGE = "EVENT_CHANGE";
})(jQuery);
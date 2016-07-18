(function ($) {
    /**
     * 日期控件
     * @class BI.MultiDatePopup
     * @extends BI.Widget
     */
    BI.MultiDatePopup = BI.inherit(BI.Widget, {
        constants: {
            tabHeight: 30,
            tabWidth: 42,
            titleHeight: 27,
            itemHeight: 30,
            triggerHeight: 24,
            buttonWidth: 90,
            buttonHeight: 25,
            cardHeight: 229,
            cardWidth: 270,
            popupHeight: 259,
            popupWidth: 270,
            comboAdjustHeight: 1,
            ymdWidth: 58,
            lgap: 2,
            border: 1
        },
        _defaultConfig: function () {
            return BI.extend(BI.MultiDatePopup.superclass._defaultConfig.apply(this, arguments), {
                baseCls: 'bi-multidate-popup',
                width: 268,
                height: 260
            });
        },
        _init: function () {
            BI.MultiDatePopup.superclass._init.apply(this, arguments);
            var self = this, opts = this.options;
            this.storeValue = "";
            this.textButton = BI.createWidget({
                type: 'bi.text_button',
                forceCenter: true,
                cls: 'bi-multidate-popup-label',
                shadow: true,
                text: BI.i18nText("BI-Multi_Date_Today")
            });
            this.textButton.on(BI.TextButton.EVENT_CHANGE, function () {
                self.fireEvent(BI.MultiDatePopup.BUTTON_lABEL_EVENT_CHANGE);
            });
            this.clearButton = BI.createWidget({
                type: "bi.text_button",
                forceCenter: true,
                cls: 'bi-multidate-popup-button',
                shadow: true,
                text: BI.i18nText("BI-Clear")
            });
            this.clearButton.on(BI.TextButton.EVENT_CHANGE, function () {
                self.fireEvent(BI.MultiDatePopup.BUTTON_CLEAR_EVENT_CHANGE);
            });
            this.okButton = BI.createWidget({
                type: "bi.text_button",
                forceCenter: true,
                cls: 'bi-multidate-popup-button',
                shadow: true,
                text: BI.i18nText("BI-OK")
            });
            this.okButton.on(BI.TextButton.EVENT_CHANGE, function () {
                self.fireEvent(BI.MultiDatePopup.BUTTON_OK_EVENT_CHANGE);
            });
            this.ymd = BI.createWidget({
                type: "bi.date_calendar_popup",
                min: this.options.min,
                max: this.options.max
            });
            this.year = BI.createWidget({
                type: "bi.yearcard"
            });
            this.quarter = BI.createWidget({
                type: 'bi.quartercard'
            });
            this.month = BI.createWidget({
                type: 'bi.monthcard'
            });
            this.week = BI.createWidget({
                type: 'bi.weekcard'
            });
            this.day = BI.createWidget({
                type: 'bi.daycard'
            });
            this.dateTab = BI.createWidget({
                type: 'bi.tab',
                tab: {
                    cls: "bi-multidate-popup-tab",
                    height: this.constants.tabHeight,
                    items: BI.createItems([{
                        text: BI.i18nText("BI-Multi_Date_YMD"),
                        value: BICst.MULTI_DATE_YMD_CARD,
                        width: this.constants.ymdWidth
                    }, {
                        text: BI.i18nText("BI-Multi_Date_Year"),
                        value: BICst.MULTI_DATE_YEAR_CARD
                    }, {
                        text: BI.i18nText("BI-Multi_Date_Quarter"),
                        value: BICst.MULTI_DATE_QUARTER_CARD
                    }, {
                        text: BI.i18nText("BI-Multi_Date_Month"),
                        value: BICst.MULTI_DATE_MONTH_CARD
                    }, {
                        text: BI.i18nText("BI-Multi_Date_Week"),
                        value: BICst.MULTI_DATE_WEEK_CARD
                    }, {
                        text: BI.i18nText("BI-Multi_Date_Day"),
                        value: BICst.MULTI_DATE_DAY_CARD
                    }], {
                        width: this.constants.tabWidth,
                        textAlign: "center",
                        height: this.constants.itemHeight,
                        cls: 'bi-multidate-popup-item'
                    }),
                    layouts: [{
                        type: 'bi.left'
                    }]
                },
                cardCreator: function (v) {
                    switch (v) {
                        case BICst.MULTI_DATE_YMD_CARD:
                            return self.ymd;
                        case BICst.MULTI_DATE_YEAR_CARD:
                            return self.year;
                        case BICst.MULTI_DATE_QUARTER_CARD:
                            return self.quarter;
                        case BICst.MULTI_DATE_MONTH_CARD:
                            return self.month;
                        case BICst.MULTI_DATE_WEEK_CARD:
                            return self.week;
                        case BICst.MULTI_DATE_DAY_CARD:
                            return self.day;
                    }
                }
            });
            this.dateTab.setSelect(BICst.MULTI_DATE_YMD_CARD);
            this.year.on(BI.MultiDateCard.EVENT_CHANGE, function (v) {
                self._setInnerValue(self.year, v);
            });
            this.quarter.on(BI.MultiDateCard.EVENT_CHANGE, function (v) {
                self._setInnerValue(self.quarter, v);
            });
            this.month.on(BI.MultiDateCard.EVENT_CHANGE, function (v) {
                self._setInnerValue(self.month, v);
            });
            this.week.on(BI.MultiDateCard.EVENT_CHANGE, function (v) {
                self._setInnerValue(self.week, v);
            });
            this.day.on(BI.MultiDateCard.EVENT_CHANGE, function (v) {
                self._setInnerValue(self.day, v);
            });
            this.ymd.on(BI.DateCalendarPopup.EVENT_CHANGE, function () {
                self.fireEvent(BI.MultiDatePopup.CALENDAR_EVENT_CHANGE);
            });
            this.dateTab.on(BI.Tab.EVENT_CHANGE, function () {
                var v = self.dateTab.getSelect();
                switch (v) {
                    case BICst.MULTI_DATE_YMD_CARD:
                        self.ymd.setValue(self.ymd.getValue());
                        self._setInnerValue(self.ymd);
                        break;
                    case BICst.MULTI_DATE_YEAR_CARD:
                        self.year.setValue(self.storeValue);
                        self._setInnerValue(self.year);
                        break;
                    case BICst.MULTI_DATE_QUARTER_CARD:
                        self.quarter.setValue(self.storeValue);
                        self._setInnerValue(self.quarter);
                        break;
                    case BICst.MULTI_DATE_MONTH_CARD:
                        self.month.setValue(self.storeValue);
                        self._setInnerValue(self.month);
                        break;
                    case BICst.MULTI_DATE_WEEK_CARD:
                        self.week.setValue(self.storeValue);
                        self._setInnerValue(self.week);
                        break;
                    case BICst.MULTI_DATE_DAY_CARD:
                        self.day.setValue(self.storeValue);
                        self._setInnerValue(self.day);
                        break;
                }
            });
            this.dateButton = BI.createWidget({
                type: "bi.grid",
                items: [[this.clearButton, this.textButton, this.okButton]]
            });
            BI.createWidget({
                element: this.element,
                type: "bi.vtape",
                items: [{
                    el: this.dateTab
                }, {
                    el: this.dateButton,
                    height: 30
                }]
            });
        },
        _setInnerValue: function (obj) {
            if (this.dateTab.getSelect() === BICst.MULTI_DATE_YMD_CARD) {
                this.textButton.setValue(BI.i18nText("BI-Multi_Date_Today"));
                this.textButton.setEnable(true);
            } else {
                var date = obj.getCalculationValue();
                date = date.print("%Y-%x-%e");
                this.textButton.setValue(date);
                this.textButton.setEnable(false);
            }
        },
        setValue: function (v) {
            this.storeValue = v;
            var self = this, date;
            var type, value;
            if (BI.isNotNull(v)) {
                type = v.type || BICst.MULTI_DATE_CALENDAR; value = v.value;
                if(BI.isNull(value)){
                    value = v;
                }
            }
            switch (type) {
                case BICst.MULTI_DATE_YEAR_PREV:
                case BICst.MULTI_DATE_YEAR_AFTER:
                case BICst.MULTI_DATE_YEAR_BEGIN:
                case BICst.MULTI_DATE_YEAR_END:
                    this.dateTab.setSelect(BICst.MULTI_DATE_YEAR_CARD);
                    this.year.setValue(value);
                    self._setInnerValue(this.year);
                    date = this.year.getCalculationValue();
                    this.ymd.setValue({
                        year: date.getFullYear(),
                        month: date.getMonth(),
                        day: date.getDate()
                    });
                    break;
                case BICst.MULTI_DATE_QUARTER_PREV:
                case BICst.MULTI_DATE_QUARTER_AFTER:
                case BICst.MULTI_DATE_QUARTER_BEGIN:
                case BICst.MULTI_DATE_QUARTER_END:
                    this.dateTab.setSelect(BICst.MULTI_DATE_QUARTER_CARD);
                    this.quarter.setValue(value);
                    self._setInnerValue(this.quarter);
                    date = this.quarter.getCalculationValue();
                    this.ymd.setValue({
                        year: date.getFullYear(),
                        month: date.getMonth(),
                        day: date.getDate()
                    });
                    break;
                case BICst.MULTI_DATE_MONTH_PREV:
                case BICst.MULTI_DATE_MONTH_AFTER:
                case BICst.MULTI_DATE_MONTH_BEGIN:
                case BICst.MULTI_DATE_MONTH_END:
                    this.dateTab.setSelect(BICst.MULTI_DATE_MONTH_CARD);
                    this.month.setValue(value);
                    self._setInnerValue(this.month);
                    date = this.month.getCalculationValue();
                    this.ymd.setValue({
                        year: date.getFullYear(),
                        month: date.getMonth(),
                        day: date.getDate()
                    });
                    break;
                case BICst.MULTI_DATE_WEEK_PREV:
                case BICst.MULTI_DATE_WEEK_AFTER:
                    this.dateTab.setSelect(BICst.MULTI_DATE_WEEK_CARD);
                    this.week.setValue(value);
                    self._setInnerValue(this.week);
                    date = this.week.getCalculationValue();
                    this.ymd.setValue({
                        year: date.getFullYear(),
                        month: date.getMonth(),
                        day: date.getDate()
                    });
                    break;
                case BICst.MULTI_DATE_DAY_PREV:
                case BICst.MULTI_DATE_DAY_AFTER:
                case BICst.MULTI_DATE_DAY_TODAY:
                    this.dateTab.setSelect(BICst.MULTI_DATE_DAY_CARD);
                    this.day.setValue(value);
                    self._setInnerValue(this.day);
                    date = this.day.getCalculationValue();
                    this.ymd.setValue({
                        year: date.getFullYear(),
                        month: date.getMonth(),
                        day: date.getDate()
                    });
                    break;
                default:
                    if (BI.isNull(value)) {
                        var date = new Date();
                        this.ymd.setValue({
                            year: date.getFullYear(),
                            month: date.getMonth(),
                            day: date.getDate()
                        });
                        this.year.setValue();
                        this.quarter.setValue();
                        this.month.setValue();
                        this.week.setValue();
                        this.day.setValue();
                        this.dateTab.setSelect(BICst.MULTI_DATE_YMD_CARD);
                        this.textButton.setValue(BI.i18nText("BI-Multi_Date_Today"));
                    } else {
                        this.ymd.setValue(value);
                        this.dateTab.setSelect(BICst.MULTI_DATE_YMD_CARD);
                        this.textButton.setValue(BI.i18nText("BI-Multi_Date_Today"));
                    }
                    break;
            }
        },
        getValue: function () {
            var tab = this.dateTab.getSelect();
            switch (tab) {
                case BICst.MULTI_DATE_YMD_CARD:
                    return this.ymd.getValue();
                case BICst.MULTI_DATE_YEAR_CARD:
                    return this.year.getValue();
                case BICst.MULTI_DATE_QUARTER_CARD:
                    return this.quarter.getValue();
                case BICst.MULTI_DATE_MONTH_CARD:
                    return this.month.getValue();
                case BICst.MULTI_DATE_WEEK_CARD:
                    return this.week.getValue();
                case BICst.MULTI_DATE_DAY_CARD:
                    return this.day.getValue();
            }
        }
    });
    BI.MultiDatePopup.BUTTON_OK_EVENT_CHANGE = "BUTTON_OK_EVENT_CHANGE";
    BI.MultiDatePopup.BUTTON_lABEL_EVENT_CHANGE = "BUTTON_lABEL_EVENT_CHANGE";
    BI.MultiDatePopup.BUTTON_CLEAR_EVENT_CHANGE = "BUTTON_CLEAR_EVENT_CHANGE";
    BI.MultiDatePopup.CALENDAR_EVENT_CHANGE = "CALENDAR_EVENT_CHANGE";
    $.shortcut('bi.multidate_popup', BI.MultiDatePopup);
})(jQuery);
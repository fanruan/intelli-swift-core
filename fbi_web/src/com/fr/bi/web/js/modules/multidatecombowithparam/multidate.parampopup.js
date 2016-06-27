/**
 * 带参数的日期控件
 * @class BI.MultiDateParamPopup
 * @extends BI.Widget
 */
BI.MultiDateParamPopup = BI.inherit(BI.Widget, {
    constants: {
        tabHeight: 30,
        tabWidth: 42,
        titleHeight: 27,
        itemHeight: 30,
        triggerHeight: 24,
        buttonWidth: 90,
        dateButtonWidth: 130,
        buttonHeight: 30,
        cardHeight: 229,
        cardWidth: 310,
        popupHeight: 259,
        popupWidth: 310,
        comboAdjustHeight: 1,
        ymdWidth: 56,
        lgap: 2,
        border: 1
    },
    _defaultConfig: function () {
        return BI.extend(BI.MultiDateParamPopup.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-multidate-param-popup'
        });
    },
    _init: function () {
        BI.MultiDateParamPopup.superclass._init.apply(this, arguments);
        var self = this, opts = this.options;
        this.storeValue = "";
        this.textButton = BI.createWidget({
            type: 'bi.text_button',
            text: BI.i18nText("BI-Multi_Date_Today"),
            height: this.constants.buttonHeight - this.constants.border * 2,
            width: this.constants.dateButtonWidth
        });
        this.textButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiDateParamPopup.BUTTON_lABEL_EVENT_CHANGE);
        });
        this.clearButton = BI.createWidget({
            type: "bi.text_button",
            cls: 'bi-multidate-popup-button',
            text: BI.i18nText("BI-Clear"),
            height: this.constants.buttonHeight - this.constants.border * 2,
            width: this.constants.buttonWidth
        });
        this.clearButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiDateParamPopup.BUTTON_CLEAR_EVENT_CHANGE);
        });
        this.okButton = BI.createWidget({
            type: "bi.text_button",
            cls: 'bi-multidate-popup-button',
            text: BI.i18nText("BI-OK"),
            height: this.constants.buttonHeight - this.constants.border * 2,
            width: this.constants.buttonWidth
        });
        this.okButton.on(BI.TextButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiDateParamPopup.BUTTON_OK_EVENT_CHANGE);
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
        this.param = BI.createWidget({
            type: "bi.multi_date_param_pane"
        });
        this.dateTab = BI.createWidget({
            type: 'bi.tab',
            tab: {
                cls: "bi-multidate-popup-tab",
                height: this.constants.tabHeight,
                items: BI.createItems([{
                    el: {
                        text: BI.i18nText("BI-Multi_Date_YMD"),
                        value: BICst.MULTI_DATE_YMD_CARD,
                        width: this.constants.ymdWidth
                    },
                    lgap: this.constants.lgap
                }, {

                    text: BI.i18nText("BI-Multi_Date_Year"),
                    value: BICst.MULTI_DATE_YEAR_CARD
                },
                    {
                        text: BI.i18nText("BI-Multi_Date_Quarter"),
                        value: BICst.MULTI_DATE_QUARTER_CARD
                    },
                    {
                        text: BI.i18nText("BI-Multi_Date_Month"),
                        value: BICst.MULTI_DATE_MONTH_CARD
                    },
                    {
                        text: BI.i18nText("BI-Multi_Date_Week"),
                        value: BICst.MULTI_DATE_WEEK_CARD
                    },
                    {
                        text: BI.i18nText("BI-Multi_Date_Day"),
                        value: BICst.MULTI_DATE_DAY_CARD
                    }, {
                        text: BI.i18nText("BI-Parameter_Simple"),
                        value: BICst.MULTI_DATE_PARAM_CARD
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
                    case BICst.MULTI_DATE_PARAM_CARD:
                        return self.param;
                }
            },
            width: this.constants.popupWidth - this.constants.border * 2,
            height: this.constants.cardHeight - this.constants.border * 2
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
            self.fireEvent(BI.MultiDateParamPopup.CALENDAR_EVENT_CHANGE);
        });
        this.param.on(BI.MultiDateParamPane.EVENT_CHANGE, function(){
            self.fireEvent(BI.MultiDateParamPopup.EVENT_PARAM_CHANGE);
        });
        this.dateTab.on(BI.Tab.EVENT_CHANGE, function () {
            var v = self.dateTab.getSelect();
            switch (v) {
                case BICst.MULTI_DATE_YMD_CARD:
                    self.ymd.setValue(self.ymd.getValue());
                    self._setInnerValue(self.ymd);
                    self.textButton.setVisible(true);
                    break;
                case BICst.MULTI_DATE_YEAR_CARD:
                    self.year.setValue(self.storeValue);
                    self._setInnerValue(self.year);
                    self.textButton.setVisible(true);
                    break;
                case BICst.MULTI_DATE_QUARTER_CARD:
                    self.quarter.setValue(self.storeValue);
                    self._setInnerValue(self.quarter);
                    self.textButton.setVisible(true);
                    break;
                case BICst.MULTI_DATE_MONTH_CARD:
                    self.month.setValue(self.storeValue);
                    self._setInnerValue(self.month);
                    self.textButton.setVisible(true);
                    break;
                case BICst.MULTI_DATE_WEEK_CARD:
                    self.week.setValue(self.storeValue);
                    self._setInnerValue(self.week);
                    self.textButton.setVisible(true);
                    break;
                case BICst.MULTI_DATE_DAY_CARD:
                    self.day.setValue(self.storeValue);
                    self._setInnerValue(self.day);
                    self.textButton.setVisible(true);
                    break;
                case BICst.MULTI_DATE_PARAM_CARD:
                    self.param.setValue(self.param.getValue());
                    self.textButton.setVisible(false);
                    break;
            }
        });
        this.dateButton = BI.createWidget({
            type: "bi.border",
            items: {
                west: {
                    el: this.clearButton,
                    width: this.constants.buttonWidth
                },
                center: {
                    type: "bi.absolute",
                    cls: "bi-multidate-popup-label",
                    items: [{
                        el: this.textButton,
                        top: 0,
                        left: 0,
                        bottom: 0,
                        right: 0
                    }],
                    height: this.constants.buttonHeight - 2
                },
                east: {
                    el: this.okButton,
                    width: this.constants.buttonWidth
                }
            }
        });
        BI.createWidget({
            element: this.element,
            type: "bi.vertical",
            items: [this.dateTab, this.dateButton],
            height: this.constants.popupHeight - this.constants.border * 2,
            width: this.constants.popupWidth - this.constants.border * 2
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
                this.textButton.setVisible(true);
                this.year.setValue({type: type, value: value});
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
                this.textButton.setVisible(true);
                this.quarter.setValue({type: type, value: value});
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
                this.textButton.setVisible(true);
                this.month.setValue({type: type, value: value});
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
                this.textButton.setVisible(true);
                this.week.setValue({type: type, value: value});
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
                this.textButton.setVisible(true);
                this.day.setValue({type: type, value: value});
                self._setInnerValue(this.day);
                date = this.day.getCalculationValue();
                this.ymd.setValue({
                    year: date.getFullYear(),
                    month: date.getMonth(),
                    day: date.getDate()
                });
                break;
            case BICst.MULTI_DATE_PARAM:
                this.dateTab.setSelect(BICst.MULTI_DATE_PARAM_CARD);
                this.textButton.setVisible(false);
                this.param.setValue(value);
                break;
            default:
                if (BI.isNull(v)) {
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
                this.textButton.setVisible(true);
                break;
        }
    },
    getValue: function () {
        var tab = this.dateTab.getSelect();
        switch (tab) {
            case BICst.MULTI_DATE_YMD_CARD:
                return {type: BICst.MULTI_DATE_CALENDAR, value: this.ymd.getValue()};
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
            case BICst.MULTI_DATE_PARAM_CARD:
                return {type: BICst.MULTI_DATE_PARAM, value: this.param.getValue()[0]};
        }
    }
});
BI.MultiDateParamPopup.BUTTON_OK_EVENT_CHANGE = "BUTTON_OK_EVENT_CHANGE";
BI.MultiDateParamPopup.BUTTON_lABEL_EVENT_CHANGE = "BUTTON_lABEL_EVENT_CHANGE";
BI.MultiDateParamPopup.BUTTON_CLEAR_EVENT_CHANGE = "BUTTON_CLEAR_EVENT_CHANGE";
BI.MultiDateParamPopup.CALENDAR_EVENT_CHANGE = "CALENDAR_EVENT_CHANGE";
BI.MultiDateParamPopup.EVENT_PARAM_CHANGE = "EVENT_PARAM_CHANGE";
$.shortcut('bi.multidate_param_popup', BI.MultiDateParamPopup);
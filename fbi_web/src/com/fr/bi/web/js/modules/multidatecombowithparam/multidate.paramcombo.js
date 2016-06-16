BICst.MULTI_DATE_YMD_CARD = 1;
BICst.MULTI_DATE_YEAR_CARD = 2;
BICst.MULTI_DATE_QUARTER_CARD = 3;
BICst.MULTI_DATE_MONTH_CARD = 4;
BICst.MULTI_DATE_WEEK_CARD = 5;
BICst.MULTI_DATE_DAY_CARD = 6;
BICst.MULTI_DATE_PARAM_CARD = 7;
BICst.MULTI_DATE_SEGMENT_NUM = {};
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_PREV] = BI.i18nText("BI-Multi_Date_Year_Prev");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_AFTER] = BI.i18nText("BI-Multi_Date_Year_Next");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_BEGIN] = BI.i18nText("BI-Multi_Date_Year_Begin");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_END] = BI.i18nText("BI-Multi_Date_Year_End");

BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_PREV] = BI.i18nText("BI-Multi_Date_Quarter_Prev");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_AFTER] = BI.i18nText("BI-Multi_Date_Quarter_Next");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_BEGIN] = BI.i18nText("BI-Multi_Date_Quarter_Begin");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_END] = BI.i18nText("BI-Multi_Date_Quarter_End");

BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_PREV] = BI.i18nText("BI-Multi_Date_Month_Prev");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_AFTER] = BI.i18nText("BI-Multi_Date_Month_Next");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_BEGIN] = BI.i18nText("BI-Multi_Date_Month_Begin");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_END] = BI.i18nText("BI-Multi_Date_Month_End");

BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_WEEK_PREV] = BI.i18nText("BI-Multi_Date_Week_Prev");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_WEEK_AFTER] = BI.i18nText("BI-Multi_Date_Week_Next");

BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_DAY_PREV] = BI.i18nText("BI-Multi_Date_Day_Prev");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_DAY_AFTER] = BI.i18nText("BI-Multi_Date_Day_Next");
BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_DAY_TODAY] = BI.i18nText("BI-Multi_Date_Today");

/**
 * 带参数的日期控件
 * @class BI.MultiDateParamCombo
 * @extends BI.Widget
 */
BI.MultiDateParamCombo = BI.inherit(BI.Single, {
    constants: {
        popupHeight: 259,
        popupWidth: 310,
        comboAdjustHeight: 1,
        border: 1,
        DATE_MIN_VALUE: "1900-01-01",
        DATE_MAX_VALUE: "2099-12-31"
    },
    _defaultConfig: function () {
        return BI.extend(BI.MultiDateParamCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: 'bi-multidate-param-combo'
        });
    },
    _init: function () {
        BI.MultiDateParamCombo.superclass._init.apply(this, arguments);
        var self = this, opts = this.options;
        this.storeTriggerValue = "";
        var date = new Date();
        this.storeValue = {
            year: date.getFullYear(),
            month: date.getMonth()
        };
        this.trigger = BI.createWidget({
            type: 'bi.multidate_param_trigger',
            min: this.constants.DATE_MIN_VALUE,
            max: this.constants.DATE_MAX_VALUE
        });
        this.trigger.on(BI.DateTrigger.EVENT_TRIGGER_CLICK, function () {
            self.combo.toggle();
        });
        this.trigger.on(BI.DateTrigger.EVENT_FOCUS, function () {
            self.storeTriggerValue = self.trigger.getKey();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_FOCUS);
        });
        this.trigger.on(BI.DateTrigger.EVENT_CHANGE, function () {
            self.combo.isViewVisible() && self.combo.hideView();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CHANGE);
        });
        this.trigger.on(BI.DateTrigger.EVENT_ERROR, function () {
            self.fireEvent(BI.MultiDateParamCombo.EVENT_ERROR);
        });
        this.trigger.on(BI.DateTrigger.EVENT_VALID, function () {
            self.fireEvent(BI.MultiDateParamCombo.EVENT_VALID);
        });
        this.trigger.on(BI.DateTrigger.EVENT_START, function () {
            self.combo.hideView();
        });
        this.trigger.on(BI.DateTrigger.EVENT_CONFIRM, function () {
            var dateStore = self.storeTriggerValue;
            var dateObj = self.trigger.getKey();
            if (BI.isNotEmptyString(dateObj) && !BI.isEqual(dateObj, dateStore)) {
                self.storeValue = self.trigger.getValue();
                self.setValue(self.trigger.getValue());
            } else if (BI.isEmptyString(dateObj)) {
                self.storeValue = {
                    year: date.getFullYear(),
                    month: date.getMonth()
                };
                self.trigger.setValue();
            }
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CONFIRM);
        });
        this.popup = BI.createWidget({
            type: "bi.multidate_param_popup",
            min: this.constants.DATE_MIN_VALUE,
            max: this.constants.DATE_MAX_VALUE
        });
        this.popup.on(BI.MultiDatePopup.BUTTON_CLEAR_EVENT_CHANGE, function () {
            self.setValue();
            self.combo.hideView();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CHANGE);
        });
        this.popup.on(BI.MultiDatePopup.BUTTON_lABEL_EVENT_CHANGE, function () {
            var date = new Date();
            self.setValue({
                year: date.getFullYear(),
                month: date.getMonth(),
                day: date.getDate()
            });
            self.combo.hideView();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CHANGE);
        });
        this.popup.on(BI.MultiDatePopup.BUTTON_OK_EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.combo.hideView();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CHANGE);
        });
        this.popup.on(BI.MultiDatePopup.CALENDAR_EVENT_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.combo.hideView();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CHANGE);
        });
        this.popup.on(BI.MultiDateParamPopup.EVENT_PARAM_CHANGE, function () {
            self.setValue(self.popup.getValue());
            self.combo.hideView();
            self.fireEvent(BI.MultiDateParamCombo.EVENT_CHANGE);
        });
        this.combo = BI.createWidget({
            element: this.element,
            type: 'bi.combo',
            toggle: false,
            isNeedAdjustHeight: false,
            isNeedAdjustWidth: false,
            el: this.trigger,
            adjustLength: this.constants.comboAdjustHeight,
            popup: {
                el: this.popup,
                maxHeight: this.constants.popupHeight,
                width: this.constants.popupWidth,
                stopPropagation: false
            }
        });
        this.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.popup.setValue(self.storeValue);
            self.fireEvent(BI.MultiDateParamCombo.EVENT_BEFORE_POPUPVIEW);
        });
    },
    setValue: function (v) {
        this.storeValue = v;
        this.popup.setValue(v);
        this.trigger.setValue(v);
    },
    getValue: function () {
        return this.trigger.getValue();
    },
    getKey: function () {
        return this.trigger.getKey();
    },
    hidePopupView: function () {
        this.combo.hideView();
    }
});

BI.MultiDateParamCombo.EVENT_CONFIRM = "EVENT_CONFIRM";
BI.MultiDateParamCombo.EVENT_FOCUS = "EVENT_FOCUS";
BI.MultiDateParamCombo.EVENT_CHANGE = "EVENT_CHANGE";
BI.MultiDateParamCombo.EVENT_VALID = "EVENT_VALID";
BI.MultiDateParamCombo.EVENT_ERROR = "EVENT_ERROR";
BI.MultiDateParamCombo.EVENT_BEFORE_POPUPVIEW = "BI.MultiDateParamCombo.EVENT_BEFORE_POPUPVIEW";
$.shortcut('bi.multidate_param_combo', BI.MultiDateParamCombo);
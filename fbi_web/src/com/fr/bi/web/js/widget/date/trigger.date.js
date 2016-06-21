BI.DateTrigger = BI.inherit(BI.Trigger, {
    _const: {
        hgap: 4,
        vgap: 2,
        triggerWidth: 30,
        watermark: BI.i18nText("BI-Unrestricted"),
        yearLength: 4,
        yearMonthLength: 7
    },

    _defaultConfig: function () {
        return BI.extend(BI.DateTrigger.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-date-trigger",
            min: '1900-01-01', //最小日期
            max: '2099-12-31', //最大日期
            height: 30
        });
    },
    _init: function () {
        BI.DateTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options, c = this._const;
        this.editor = BI.createWidget({
            type: "bi.sign_editor",
            height: o.height,
            validationChecker: function (v) {
                var date = v.match(/\d+/g);
                self._autoAppend(v, date);
                return self._dateCheck(v) && Date.checkLegal(v) && self._checkVoid({
                        year: date[0],
                        month: date[1],
                        day: date[2]
                    });
            },
            quitChecker: function () {
                return false;
            },
            hgap: c.hgap,
            vgap: c.vgap,
            allowBlank: true,
            watermark: c.watermark,
            errorText: function () {
                if (self.editor.isEditing()) {
                    return BI.i18nText("BI-Date_Trigger_Error_Text");
                }
                return BI.i18nText("BI-Year_Trigger_Invalid_Text");
            }
        });
        this.editor.on(BI.SignEditor.EVENT_KEY_DOWN, function(){
            self.fireEvent(BI.DateTrigger.EVENT_KEY_DOWN)
        });
        this.editor.on(BI.SignEditor.EVENT_FOCUS, function () {
            self.fireEvent(BI.DateTrigger.EVENT_FOCUS);
        });
        this.editor.on(BI.SignEditor.EVENT_VALID, function () {
            self.fireEvent(BI.DateTrigger.EVENT_VALID);
        });
        this.editor.on(BI.SignEditor.EVENT_ERROR, function () {
            self.fireEvent(BI.DateTrigger.EVENT_ERROR);
        });
        this.editor.on(BI.SignEditor.EVENT_CONFIRM, function () {
            var value = self.editor.getState();
            if (BI.isNotNull(value)) {
                self.editor.setState(value);
            }
            self.fireEvent(BI.DateTrigger.EVENT_CONFIRM);
        });
        this.editor.on(BI.SignEditor.EVENT_SPACE, function () {
            if (self.editor.isValid()) {
                self.editor.blur();
            }
        });
        this.editor.on(BI.SignEditor.EVENT_START, function () {
            self.fireEvent(BI.DateTrigger.EVENT_START);
        });
        this.editor.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.DateTrigger.EVENT_CHANGE);
        });

        var triggerBtn = BI.createWidget({
            type: "bi.icon_button",
            stopPropagation: true,
            cls: "bi-trigger-date-button chart-date-font",
            width: c.triggerWidth
        });
        triggerBtn.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.DateTrigger.EVENT_TRIGGER_CLICK);
        });
        this.changeIcon = BI.createWidget({
            type: "bi.icon_button",
            cls: "bi-trigger-date-change widget-date-h-change-font",
            width: c.triggerWidth
        });
        this.items = [{
            el: triggerBtn,
            width: c.triggerWidth
        },
            {
                el: this.editor,
                width: 'fill'
            }, {
                el: this.changeIcon,
                width: 0
            }];
        this.layout = BI.createWidget({
            element: this.element,
            type: 'bi.htape',
            items: this.items
        });
        this._setChangeIconVisible(false);
    },
    _dateCheck: function (date) {
        return Date.parseDateTime(date, "%Y-%x-%d").print("%Y-%x-%d") == date || Date.parseDateTime(date, "%Y-%X-%d").print("%Y-%X-%d") == date || Date.parseDateTime(date, "%Y-%x-%e").print("%Y-%x-%e") == date || Date.parseDateTime(date, "%Y-%X-%e").print("%Y-%X-%e") == date;
    },
    _checkVoid: function (obj) {
        return !Date.checkVoid(obj.year, obj.month, obj.day, this.options.min, this.options.max)[0];
    },
    _autoAppend: function (v, dateObj) {
        var self = this;
        var date = Date.parseDateTime(v, "%Y-%X-%d").print("%Y-%X-%d");
        var yearCheck = function (v) {
            return Date.parseDateTime(v, "%Y").print("%Y") == v && date >= self.options.min && date <= self.options.max;
        };
        var monthCheck = function (v) {
            return Date.parseDateTime(v, "%Y-%X").print("%Y-%X") == v && date >= self.options.min && date <= self.options.max;
        };
        if (BI.isNotNull(dateObj) && Date.checkLegal(v)) {
            switch (v.length) {
                case this._const.yearLength:
                    if (yearCheck(v)) {
                        this.editor.setValue(v + "-");
                    }
                    break;
                case this._const.yearMonthLength:
                    if (monthCheck(v)) {
                        this.editor.setValue(v + "-");
                    }
                    break;
            }
        }
    },

    setValue: function (v) {
        var type, value, self = this;
        var date = new Date();
        this.store_value = v;
        if (BI.isNotNull(v)) {
            type = v.type, value = v.value;
        }
        var _setInnerValue = function (date, text) {
            var dateStr = date.print("%Y-%x-%e");
            self.editor.setState(dateStr);
            self.editor.setValue(dateStr);
            self.setTitle(text + ":" + dateStr);
            self._setChangeIconVisible(true);
        };
        switch (type) {
            case BICst.MULTI_DATE_YEAR_PREV:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_PREV];
                date = new Date((date.getFullYear() - 1 * value), date.getMonth(), date.getDate());
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_YEAR_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_AFTER];
                date = new Date((date.getFullYear() + 1 * value), date.getMonth(), date.getDate());
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_YEAR_BEGIN:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_BEGIN];
                date = new Date(date.getFullYear(), 0, 1);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_YEAR_END:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_END];
                date = new Date(date.getFullYear(), 11, 31);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_PREV:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_PREV];
                date = this._getBeforeMulQuarter(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_AFTER];
                date = this._getAfterMulQuarter(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_BEGIN:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_BEGIN];
                date = this._getQuarterStartDate();
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_END:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_END];
                date = this._getQuarterEndDate();
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_MONTH_PREV:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_PREV];
                date = this._getBeforeMultiMonth(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_MONTH_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_AFTER];
                date = this._getAfterMultiMonth(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_MONTH_BEGIN:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_BEGIN];
                date = new Date(date.getFullYear(), date.getMonth(), 1);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_MONTH_END:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_END];
                date = new Date(date.getFullYear(), date.getMonth(), (date.getLastDateOfMonth()).getDate());
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_WEEK_PREV:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_WEEK_PREV];
                date = date.getOffsetDate(-7 * value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_WEEK_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_WEEK_AFTER];
                date = date.getOffsetDate(7 * value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_DAY_PREV:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_DAY_PREV];
                date = date.getOffsetDate(-1 * value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_DAY_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_DAY_AFTER];
                date = date.getOffsetDate(1 * value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_DAY_TODAY:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_DAY_TODAY];
                date = new Date();
                _setInnerValue(date, text);
                break;
            default:
                if (BI.isNull(v) || BI.isNull(v.day)) {
                    this.editor.setState("");
                    this.editor.setValue("");
                    this.setTitle("");
                } else {
                    var dateStr = v.year + "-" + (v.month + 1) + "-" + v.day;
                    this.editor.setState(dateStr);
                    this.editor.setValue(dateStr);
                    this.setTitle(dateStr);
                }
                this._setChangeIconVisible(false);
                break;
        }
    },
    //获得n个季度后的日期
    _getAfterMulQuarter: function (n) {
        var dt = new Date();
        dt.setMonth(dt.getMonth() + n * 3);
        return dt;
    },
    //获得n个季度前的日期
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
    _getAfterMultiMonth: function (n) {
        var dt = new Date();
        dt.setMonth(dt.getMonth() + n | 0);
        return dt;
    },
    _getBeforeMultiMonth: function (n) {
        var dt = new Date();
        dt.setMonth(dt.getMonth() - n | 0);
        return dt;
    },

    getKey: function () {
        return this.editor.getValue();
    },
    getValue: function () {
        var dateStr = this.editor.getValue();
        if (BI.isNotEmptyString(dateStr) && BI.isNotNull(this.store_value)) {
            var date = dateStr.split("-");
            return {
                year: date[0] | 0,
                month: date[1] - 1,
                day: date[2] | 0
            }
        }
        return this.store_value;
    },

    _setChangeIconVisible: function (v) {
        this.changeIcon.setVisible(v);
        if (v === true) {
            this.items[2].width = this._const.triggerWidth;
            this.layout.resize();
        } else {
            this.items[2].width = 0;
            this.layout.resize();
        }
    }
});
BI.DateTrigger.EVENT_FOCUS = "EVENT_FOCUS";
BI.DateTrigger.EVENT_START = "EVENT_START";
BI.DateTrigger.EVENT_CONFIRM = "EVENT_CONFIRM";
BI.DateTrigger.EVENT_CHANGE = "EVENT_CHANGE";
BI.DateTrigger.EVENT_VALID = "EVENT_VALID";
BI.DateTrigger.EVENT_ERROR = "EVENT_ERROR";
BI.DateTrigger.EVENT_TRIGGER_CLICK = "EVENT_TRIGGER_CLICK";
BI.DateTrigger.EVENT_KEY_DOWN = "EVENT_KEY_DOWN";
$.shortcut("bi.date_trigger", BI.DateTrigger);
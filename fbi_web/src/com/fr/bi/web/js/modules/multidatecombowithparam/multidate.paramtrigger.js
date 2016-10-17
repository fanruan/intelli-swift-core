BI.MultiDateParamTrigger = BI.inherit(BI.Trigger, {
    _const: {
        hgap: 4,
        vgap: 2,
        triggerWidth: 30,
        watermark: BI.i18nText("BI-Unrestricted"),
        errorText: BI.i18nText("BI-Date_Trigger_Error_Text"),
        yearLength: 4,
        yearMonthLength: 7
    },

    _defaultConfig: function () {
        return BI.extend(BI.MultiDateParamTrigger.superclass._defaultConfig.apply(this, arguments), {
            extraCls: "bi-multidate-param-trigger",
            min: '1900-01-01', //最小日期
            max: '2099-12-31', //最大日期
            height: 24
        });
    },
    _init: function () {
        BI.MultiDateParamTrigger.superclass._init.apply(this, arguments);
        var self = this, o = this.options, c = this._const;
        this.editor = BI.createWidget({
            type: "bi.sign_editor",
            height: o.height,
            validationChecker: function (v) {
                var date = v.match(/\d+/g);
                self._autoAppend(v, date);
                var value = self.getValue();
                if(BI.has(value, "type") && value.type === BICst.MULTI_DATE_PARAM){
                    return true;
                }
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
            errorText: c.errorText
        });
        this.editor.on(BI.SignEditor.EVENT_KEY_DOWN, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_KEY_DOWN);
        });
        this.editor.on(BI.SignEditor.EVENT_FOCUS, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_FOCUS);
        });
        this.editor.on(BI.SignEditor.EVENT_VALID, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_VALID);
        });
        this.editor.on(BI.SignEditor.EVENT_ERROR, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_ERROR);
        });
        this.editor.on(BI.SignEditor.EVENT_CONFIRM, function () {
            var value = self.editor.getState();
            if (BI.isNotNull(value)) {
                self.editor.setState(value);
            }

            if (BI.isNotEmptyString(value)) {
                var date = value.split("-");
                self.stored_value = {
                    type: BICst.MULTI_DATE_CALENDAR,
                    value:{
                        year: date[0] | 0,
                        month: date[1] - 1,
                        day: date[2] | 0
                    }
                };
            }
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_CONFIRM);
        });
        this.editor.on(BI.SignEditor.EVENT_SPACE, function () {
            if (self.editor.isValid()) {
                self.editor.blur();
            }
        });
        this.editor.on(BI.SignEditor.EVENT_START, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_START);
        });
        this.editor.on(BI.SignEditor.EVENT_STOP, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_STOP);
        });
        this.editor.on(BI.SignEditor.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_CHANGE);
        });

        var triggerBtn = BI.createWidget({
            type: "bi.icon_button",
            stopPropagation: true,
            cls: "bi-trigger-date-button chart-date-font",
            width: c.triggerWidth
        });
        triggerBtn.on(BI.IconButton.EVENT_CHANGE, function () {
            self.fireEvent(BI.MultiDateParamTrigger.EVENT_TRIGGER_CLICK);
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
    parseComplexDate: function (v) {
        if (v.type === BICst.MULTI_DATE_PARAM) {
            return parseComplexDateForParam(v.value);
        } else {
            return parseComplexDateCommon(v);
        }
        function parseComplexDateForParam(value) {
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
                    paramdate = parseComplexDateCommon(wWValue.start);
                }
                if (se === BI.MultiDateParamPane.end && BI.isNotNull(wWValue.end)) {
                    paramdate = parseComplexDateCommon(wWValue.end);
                }
            } else {
                if (BI.isNull(widgetInfo.wId) || BI.isNull(BI.Utils.getWidgetValueByID(widgetInfo.wId))) {
                    return;
                }
                paramdate = parseComplexDateCommon(BI.Utils.getWidgetValueByID(widgetInfo.wId));
            }
            if (BI.isNotNull(paramdate)) {
                return parseComplexDateCommon(offset, new Date(paramdate));
            }
        }

        function parseComplexDateCommon(v, consultedDate) {
            var type = v.type, value = v.value;
            var date = BI.isNull(consultedDate) ? new Date() : consultedDate;
            var currY = date.getFullYear(), currM = date.getMonth(), currD = date.getDate();
            date = new Date(date.getFullYear(), date.getMonth(), date.getDate());
            if (BI.isNull(type) && BI.isNotNull(v.year)) {
                return new Date(v.year, v.month, v.day).getTime();
            }
            switch (type) {
                case BICst.MULTI_DATE_YEAR_PREV:
                    return new Date(currY - 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_AFTER:
                    return new Date(currY + 1 * value, currM, currD).getTime();
                case BICst.MULTI_DATE_YEAR_BEGIN:
                    return new Date(currY, 0, 1).getTime();
                case BICst.MULTI_DATE_YEAR_END:
                    return new Date(currY, 11, 31).getTime();

                case BICst.MULTI_DATE_MONTH_PREV:
                    return BI.Utils.getBeforeMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_AFTER:
                    return BI.Utils.getAfterMultiMonth(value).getTime();
                case BICst.MULTI_DATE_MONTH_BEGIN:
                    return new Date(currY, currM, 1).getTime();
                case BICst.MULTI_DATE_MONTH_END:
                    return new Date(currY, currM, (date.getLastDateOfMonth()).getDate()).getTime();

                case BICst.MULTI_DATE_QUARTER_PREV:
                    return BI.Utils.getBeforeMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_AFTER:
                    return BI.Utils.getAfterMulQuarter(value).getTime();
                case BICst.MULTI_DATE_QUARTER_BEGIN:
                    return BI.Utils.getQuarterStartDate().getTime();
                case BICst.MULTI_DATE_QUARTER_END:
                    return BI.Utils.getQuarterEndDate().getTime();

                case BICst.MULTI_DATE_WEEK_PREV:
                    return date.getOffsetDate(-7 * value).getTime();
                case BICst.MULTI_DATE_WEEK_AFTER:
                    return date.getOffsetDate(7 * value).getTime();

                case BICst.MULTI_DATE_DAY_PREV:
                    return date.getOffsetDate(-1 * value).getTime();
                case BICst.MULTI_DATE_DAY_AFTER:
                    return date.getOffsetDate(1 * value).getTime();
                case BICst.MULTI_DATE_DAY_TODAY:
                    return date.getTime();
                case BICst.MULTI_DATE_CALENDAR:
                    return new Date(value.year, value.month, value.day).getTime();
            }
        }
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
        this.stored_value = v;
        var date = new Date();
        if (BI.isNotNull(v)) {
            if(BI.has(v, "value")){
                type = v.type, value = v.value;
                if(BI.has(v.value, "widgetInfo")){
                    var widgetInfo = value.widgetInfo;
                    var name = BI.Utils.getWidgetNameByID(widgetInfo.wId);
                    if(BI.isNull(name) && type === BICst.MULTI_DATE_PARAM){
                        return;
                    }
                }
                this.stored_value = v;
            }else{
                value = v;
            }
        }
        var _setInnerValue = function (date, text) {
            var dateStr = date.print("%Y-%x-%e");
            self.editor.setState(dateStr);
            self.editor.setValue(dateStr);
            self.setTitle(text + ":" + dateStr);
            self._setChangeIconVisible(true);
        };
        var _setInnerValueForParam = function(){
            var widgetInfo = value.widgetInfo;
            var name = BI.Utils.getWidgetNameByID(widgetInfo.wId);
            var showText = BI.isNull(widgetInfo.wId) ? name : BI.i18nText("BI-Relative") + name + BI.i18nText("BI-De") + (widgetInfo.startOrEnd ? "(" + BI.i18nText("BI-End_Time") + ")": "(" + BI.i18nText("BI-Start_Time")) + ")" + BI.i18nText("BI-De");
            switch(value.offset.type){
                case BICst.MULTI_DATE_YEAR_PREV:
                    showText += value.offset.value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_PREV];
                    break;
                case BICst.MULTI_DATE_YEAR_BEGIN:
                    showText += BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_YEAR_BEGIN];
                    break;
            }
            var calcDate = self.parseComplexDate({type: type, value: value});
            showText += ":" + BI.isNull(calcDate) ? "" : new Date(calcDate).print("%Y-%X-%d");
            self.editor.setState(showText);
            self.editor.setValue(showText);
            self.setTitle(showText);
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
                date = BI.Utils.getBeforeMulQuarter(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_AFTER];
                date = BI.Utils.getAfterMulQuarter(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_BEGIN:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_BEGIN];
                date = BI.Utils.getQuarterStartDate();
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_QUARTER_END:
                var text = BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_QUARTER_END];
                date = BI.Utils.getQuarterEndDate();
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_MONTH_PREV:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_PREV];
                date = BI.Utils.getBeforeMultiMonth(value);
                _setInnerValue(date, text);
                break;
            case BICst.MULTI_DATE_MONTH_AFTER:
                var text = value + BICst.MULTI_DATE_SEGMENT_NUM[BICst.MULTI_DATE_MONTH_AFTER];
                date = BI.Utils.getAfterMultiMonth(value);
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
            case BICst.MULTI_DATE_PARAM:
                _setInnerValueForParam();
                break;
            default:
                if (BI.isNull(value) || BI.isNull(value.day)) {
                    this.editor.setState("");
                    this.editor.setValue("");
                } else {
                    var dateStr = value.year + "-" + (value.month + 1) + "-" + value.day;
                    this.editor.setState(dateStr);
                    this.editor.setValue(dateStr);
                }
                this.setTitle("");
                this._setChangeIconVisible(false);
                break;
        }
    },

    getKey: function () {
        return this.editor.getValue();
    },
    getValue: function () {
        return this.stored_value;
    },

    _setChangeIconVisible: function (v) {
        this.changeIcon.setVisible(v);
        if (v === true) {
            this.items[2].width = this._const.triggerWidth;
            this.layout.resize();
        }else{
            this.items[2].width = 0;
            this.layout.resize();
        }
    }
});
BI.MultiDateParamTrigger.EVENT_FOCUS = "EVENT_FOCUS";
BI.MultiDateParamTrigger.EVENT_KEY_DOWN = "EVENT_KEY_DOWN";
BI.MultiDateParamTrigger.EVENT_START = "EVENT_START";
BI.MultiDateParamTrigger.EVENT_STOP = "EVENT_STOP";
BI.MultiDateParamTrigger.EVENT_CONFIRM = "EVENT_CONFIRM";
BI.MultiDateParamTrigger.EVENT_CHANGE = "EVENT_CHANGE";
BI.MultiDateParamTrigger.EVENT_VALID = "EVENT_VALID";
BI.MultiDateParamTrigger.EVENT_ERROR = "EVENT_ERROR";
BI.MultiDateParamTrigger.EVENT_TRIGGER_CLICK = "EVENT_TRIGGER_CLICK";
$.shortcut("bi.multidate_param_trigger", BI.MultiDateParamTrigger);
/**
 * Created by 小灰灰 on 2016/5/21.
 */
BI.ETLDateFilterCombo = BI.inherit(BI.Single, {
    constants: {
        DATE_MIN_VALUE: "1900-01-01",
        DATE_MAX_VALUE: "2099-12-31"
    },
    _defaultConfig: function () {
        return BI.extend(BI.ETLDateFilterCombo.superclass._defaultConfig.apply(this, arguments), {
            baseCls: "bi-etl-date-combo",
            height: 30
        });
    },
    _init: function () {
        BI.ETLDateFilterCombo.superclass._init.apply(this, arguments);
        var self = this, o = this.options;
        this.storeTriggerValue = "";
        var date = new Date();
        this.storeValue = {
            year: date.getFullYear(),
            month: date.getMonth(),
            day : date.getDate()
        };
        this.trigger = BI.createWidget({
            type: "bi.date_trigger",
            min: this.constants.DATE_MIN_VALUE,
            max: this.constants.DATE_MAX_VALUE
        });
        this.trigger.on(BI.DateTrigger.EVENT_KEY_DOWN, function(){
            if(self.combo.isViewVisible()){
                self.combo.hideView();
            }
        });
        this.trigger.on(BI.DateTrigger.EVENT_TRIGGER_CLICK, function () {
            self.combo.toggle();
        });
        this.trigger.on(BI.DateTrigger.EVENT_FOCUS, function () {
            self.storeTriggerValue = self.trigger.getKey();
            if(!self.combo.isViewVisible()){
                self.combo.showView();
            }
            self.fireEvent(BI.ETLDateFilterCombo.EVENT_FOCUS);
        });
        this.trigger.on(BI.DateTrigger.EVENT_ERROR, function () {
            self.fireEvent(BI.ETLDateFilterCombo.EVENT_ERROR);
        });
        this.trigger.on(BI.DateTrigger.EVENT_VALID, function () {
            self.fireEvent(BI.ETLDateFilterCombo.EVENT_VALID);
        });
        this.trigger.on(BI.DateTrigger.EVENT_CONFIRM, function () {
            var dateStore = self.storeTriggerValue;
            var dateObj = self.trigger.getKey();
            if (BI.isNotEmptyString(dateObj) && !BI.isEqual(dateObj, dateStore)) {
                self.storeValue = self.trigger.getValue();
                self._setOBJValue(self.trigger.getValue());
            } else if (BI.isEmptyString(dateObj)) {
                self.storeValue = {
                    year: date.getFullYear(),
                    month: date.getMonth(),
                    day : date.getDate()
                };
                self.trigger.setValue();
            }
            self.fireEvent(BI.ETLDateFilterCombo.EVENT_CHANGE);
        });
        this.popup = BI.createWidget({
            type: "bi.date_calendar_popup",
            min: this.constants.DATE_MIN_VALUE,
            max: this.constants.DATE_MAX_VALUE
        });
        this.popup.on(BI.DateCalendarPopup.EVENT_CHANGE, function () {
            self._setOBJValue(self.popup.getValue());
            self.combo.hideView();
            self.fireEvent(BI.ETLDateFilterCombo.EVENT_CHANGE);
        });
        this.combo = BI.createWidget({
            type: "bi.combo",
            toggle: false,
            isNeedAdjustHeight: false,
            isNeedAdjustWidth: false,
            el: this.trigger,
            popup: {
                width: 270,
                el: this.popup,
                stopPropagation: false
            }
        })
        this.combo.on(BI.Combo.EVENT_BEFORE_POPUPVIEW, function () {
            self.popup.setValue(self.storeValue);
            self.fireEvent(BI.ETLDateFilterCombo.EVENT_BEFORE_POPUPVIEW);
        });
        var triggerBtn = BI.createWidget({
            type: "bi.trigger_icon_button",
            cls: "bi-trigger-date-button chart-date-normal-font",
            width: 30,
            height: 23
        });
        triggerBtn.on(BI.TriggerIconButton.EVENT_CHANGE, function () {
            if (self.combo.isViewVisible()) {
                self.combo.hideView();
            } else {
                self.combo.showView();
            }
        });

        BI.createWidget({
            type: "bi.absolute",
            element: this.element,
            items: [{
                el: this.combo,
                top: 0,
                left: 0,
                right: 0,
                bottom: 0
            }, {
                el: triggerBtn,
                top: 0,
                left: 0
            }]
        })
    },

    _convertTime : function (v) {
        var d = BI.isNull(v) ? new Date() : new Date(v);
        return {
            year : d.getFullYear(),
            month : d.getMonth(),
            day : d.getDate()
        }
    },

    _convertOBJ : function (ob) {
        return BI.isNull(ob) ? ob : new Date(ob.year, ob.month, ob.day).getTime()
    },

    _setOBJValue: function (v) {
        this.storeValue = v;
        this.popup.setValue(v);
        this.trigger.setValue(v);
    },

    setValue: function (v) {
        v = this._convertTime(v);
        this.storeValue = v;
        this.popup.setValue(v);
        this.trigger.setValue(v);
    },
    getValue: function () {
        return this._convertOBJ(this.trigger.getValue());
    },

    hidePopupView: function () {
        this.combo.hideView();
    }
});
BI.ETLDateFilterCombo.EVENT_VALID = 'ETLDateFilterCombo.EVENT_VALID';
BI.ETLDateFilterCombo.EVENT_FOCUS = 'ETLDateFilterCombo.EVENT_FOCUS';
BI.ETLDateFilterCombo.EVENT_ERROR = 'ETLDateFilterCombo.EVENT_ERROR';
BI.ETLDateFilterCombo.EVENT_BEFORE_POPUPVIEW = 'ETLDateFilterCombo.EVENT_BEFORE_POPUPVIEW';
BI.ETLDateFilterCombo.EVENT_CHANGE = 'ETLDateFilterCombo.EVENT_CHANGE';
$.shortcut('bi.date_filter_combo_etl', BI.ETLDateFilterCombo);

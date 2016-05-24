BI.ETLDateRangePane = BI.inherit(BI.Single, {
    _constants: {
        height: 30,
        width: 30,
        gap : 15,
        timeErrorCls: "time-error",
        DATE_MIN_VALUE: "1900-01-01",
        DATE_MAX_VALUE: "2099-12-31"
    },
    _init: function () {
        var self = this;
        BI.ETLDateRangePane.superclass._init.apply(this, arguments);
        this.up = this._createCombo();
        this.down = this._createCombo();
        this.uplabel = BI.createWidget({
            type: 'bi.label',
            height: this._constants.height,
            width: this._constants.width,
            text: BI.i18nText("BI-Later_Than")
        });
        this.dowmlabel = BI.createWidget({
            type: 'bi.label',
            height: this._constants.height,
            width: this._constants.width,
            text:  BI.i18nText("BI-Sooner_Than")
        });
        BI.createWidget({
            element: self.element,
            type: "bi.vertical",
            scrolly : false,
            tgap : self._constants.gap,
            items: [{
                        type: "bi.htape",
                        height:this._constants.height,
                        items:[
                                {
                                    el : self.uplabel,
                                    width : this._constants.width
                                },
                                {
                                    el : self.up,
                                    width : 'fill'
                                }
                        ]
                     },
                    {
                        type: "bi.htape",
                        height:this._constants.height,
                        items:[
                            {
                                el : self.dowmlabel,
                                width : this._constants.width
                            },
                            {
                                el : self.down,
                                width : 'fill'
                            }
                        ]
                    }
                    ]
        });
    },

    _createCombo: function () {
        var self = this;
        var combo = BI.createWidget({
            type: 'bi.date_filter_combo_etl'
        });
        combo.on(BI.ETLDateFilterCombo.EVENT_ERROR, function () {
            self._clearTitle();
            self.element.removeClass(self._constants.timeErrorCls);
        });
        combo.on(BI.ETLDateFilterCombo.EVENT_BEFORE_POPUPVIEW, function () {
            self.up.hidePopupView();
            self.down.hidePopupView();
        });
        combo.on(BI.ETLDateFilterCombo.EVENT_CHANGE, function () {
            var smallDate = self.up.getValue(), bigDate = self.down.getValue();
            if (self._compare(smallDate, bigDate)) {
                self._setTitle(BI.i18nText("BI-Time_Interval_Error_Text"));
                self.element.addClass(self._constants.timeErrorCls);
            } else {
                self._clearTitle();
                self.element.removeClass(self._constants.timeErrorCls);
            }
        });

        combo.on(BI.ETLDateFilterCombo.EVENT_VALID, function () {
            var smallDate = self.up.getValue(), bigDate = self.down.getValue();
            if (self._compare(smallDate, bigDate)) {
                self._setTitle(BI.i18nText("BI-Time_Interval_Error_Text"));
                self.element.addClass(self._constants.timeErrorCls);
            } else {
                self._clearTitle();
                self.element.removeClass(self._constants.timeErrorCls);
                self.fireEvent(BI.ETLDateRangePane.EVENT_CHANGE);
            }
        });
        return combo;
    },

    _compare: function (smallDate, bigDate) {
       return smallDate > bigDate;
    },
    _setTitle: function (v) {
        this.up.setTitle(v);
        this.down.setTitle(v);
    },
    _clearTitle: function () {
        this.up.setTitle("");
        this.down.setTitle("");
    },
    setValue: function (date) {
        this.up.setValue(date.start);
        this.down.setValue(date.end);
    },
    getValue: function () {
        return {start: this.up.getValue(), end: this.down.getValue()};
    }
});
BI.ETLDateRangePane.EVENT_CHANGE = "EVENT_CHANGE";
$.shortcut("bi.date_range_pane_etl", BI.ETLDateRangePane);

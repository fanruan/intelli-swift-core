/**
 * Created by 小灰灰 on 2016/5/24.
 */
BI.ETLSingleGroupDateRangePane = BI.inherit(BI.Single, {
    _constants: {
        height: 30,
        width: 30,
        lgap: 15,
        offset: -15,
        timeErrorCls: "time-error",
        DATE_MIN_VALUE: "1900-01-01",
        DATE_MAX_VALUE: "2099-12-31"
    },
    _defaultConfig: function () {
        var conf = BI.ETLSingleGroupDateRangePane.superclass._defaultConfig.apply(this, arguments);
        return BI.extend(conf, {
            extraCls: "bi-filter-time-interval"
        })
    },
    _init: function () {
        var self = this;
        BI.ETLSingleGroupDateRangePane.superclass._init.apply(this, arguments);
        this.left = this._createCombo();
        this.right = this._createCombo();
        this.label = BI.createWidget({
            type: 'bi.label',
            height: this._constants.height,
            width: this._constants.width,
            text: "-"
        });
        BI.createWidget({
            element: self.element,
            type: "bi.center",
            hgap: 15,
            height: this._constants.height,
            items: [{
                type: "bi.absolute",
                items: [{
                    el: self.left,
                    left: this._constants.offset,
                    right: 0,
                    top: 0,
                    bottom: 0
                }]
            }, {
                type: "bi.absolute",
                items: [{
                    el: self.right,
                    left: 0,
                    right: this._constants.offset,
                    top: 0,
                    bottom: 0
                }]
            }]
        });
        BI.createWidget({
            type: "bi.horizontal_auto",
            element: this.element,
            items: [
                self.label
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
            self.left.hidePopupView();
            self.right.hidePopupView();
        });
        combo.on(BI.ETLDateFilterCombo.EVENT_CHANGE, function () {
            var smallDate = self.left.getValue(), bigDate = self.right.getValue();
            if (self._compare(smallDate, bigDate)) {
                self._setTitle(BI.i18nText("BI-Time_Interval_Error_Text"));
                self.element.addClass(self._constants.timeErrorCls);
                self.fireEvent(BI.ETLSingleGroupDateRangePane.EVENT_INVALID);
            } else {
                self._clearTitle();
                self.element.removeClass(self._constants.timeErrorCls);
                self.fireEvent(BI.ETLSingleGroupDateRangePane.EVENT_CHANGE);
            }
        });

        combo.on(BI.ETLDateFilterCombo.EVENT_VALID, function () {
            var smallDate = self.left.getValue(), bigDate = self.right.getValue();
            if (self._compare(smallDate, bigDate)) {
                self._setTitle(BI.i18nText("BI-Time_Interval_Error_Text"));
                self.element.addClass(self._constants.timeErrorCls);
                self.fireEvent(BI.ETLSingleGroupDateRangePane.EVENT_INVALID);
            } else {
                self._clearTitle();
                self.element.removeClass(self._constants.timeErrorCls);
                self.fireEvent(BI.ETLSingleGroupDateRangePane.EVENT_CHANGE);
            }
        });
        return combo;
    },

    _compare: function (smallDate, bigDate) {
        return smallDate > bigDate;
    },
    _setTitle: function (v) {
        this.left.setTitle(v);
        this.right.setTitle(v);
    },
    _clearTitle: function () {
        this.left.setTitle("");
        this.right.setTitle("");
    },
    setValue: function (date) {
        this.left.setValue(date.start);
        this.right.setValue(date.end);
    },
    getValue: function () {
        return {start: this.left.getValue(), end: this.right.getValue()};
    }
});
BI.ETLSingleGroupDateRangePane.EVENT_CHANGE = "EVENT_CHANGE";
BI.ETLSingleGroupDateRangePane.EVENT_INVALID = "EVENT_INVALID";
$.shortcut("bi.date_group_range_pane_etl", BI.ETLSingleGroupDateRangePane);
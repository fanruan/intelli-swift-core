BI.DateETLFilterItem = BI.inherit(BI.AbstractETLFilterItem, {
    _defaultConfig: function () {
        return BI.extend(BI.DateETLFilterItem.superclass._defaultConfig.apply(this, arguments), {
            filterTypes : BICst.ETL_FILTER_DATE_COMBO,
            defaultType : BICst.FILTER_DATE.BELONG_DATE_RANGE
        })
    },

    _init: function () {
        BI.DateETLFilterItem.superclass._init.apply(this, arguments);
    },

    _refreshFilterWidget: function (value) {
        switch (this.filter_type) {
            case BICst.FILTER_DATE.CONTAINS:
                this._createMultiChooserPane();
                break;
            case BICst.FILTER_DATE.BELONG_DATE_RANGE:
                 this._createRange();
                break;
            case BICst.FILTER_DATE.MORE_THAN:
            case BICst.FILTER_DATE.LESS_THAN:
            case BICst.FILTER_DATE.EQUAL_TO:
            case BICst.FILTER_DATE.NOT_EQUAL_TO:
                this._createDate();
                break;
            case BICst.FILTER_TYPE.FORMULA:
                this._createFormular();
                break;
        }
        BI.isNotNull(value) && this.filterWidget.setValue(value);
        if (BI.isFunction(this.filterWidget.populate)){
            this.filterWidget.populate.apply(this.filterWidget, arguments);
        }
        this.filterValueContainer.empty();
        this.filterValueContainer.addItem(this.filterWidget);
    },

    _createRange: function () {
        var self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.date_range_pane_etl"
        });
        this.filterWidget.on(BI.ETLDateRangePane.EVENT_CHANGE, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    },

    _createDate: function () {
        var self = this;
        this.filterWidget = BI.createWidget({
            type: "bi.multidate_combo"
        });
        this.filterWidget.on(BI.MultiDateParamCombo.EVENT_VALID, function () {
            self.fireEvent(BI.AbstractETLFilterItem.EVENT_VALUE_CHANGED);
        })
        return this.filterWidget;
    }
});
$.shortcut('bi.date_filter_item_etl', BI.DateETLFilterItem)